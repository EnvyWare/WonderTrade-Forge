package com.envyful.wonder.trade.forge.data;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.envyful.wonder.trade.forge.data.event.WonderTradeEvent;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WonderTradeManager {

    private final Path tradePoolFile = Paths.get("config/WonderTradeForge/pool.json");
    private final List<Pokemon> tradePool = Lists.newArrayList();

    private final WonderTradeForge mod;

    public WonderTradeManager(WonderTradeForge mod) {
        this.mod = mod;

        if (this.mod.getConfig().isPersistentPool()) {
            File file = tradePoolFile.toFile();

            if (!file.exists()) {
                this.createFile(file);
                this.generatePool();
                this.saveFile(file);
            } else {
                this.loadPool(file);
            }
        } else {
            this.generatePool();
        }
    }

    private void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            WonderTradeForge.getLogger().error("Failed to create WonderTrade pool file", e);
        }
    }

    public void generatePool() {
        this.tradePool.clear();

        for (int i = 0; i < WonderTradeForge.getConfig().getNumberInPool(); i++) {
            this.tradePool.add(WonderTradeForge.getConfig().getDefaultGeneratorSettings().build());
        }
    }

    public void saveFile() {
        if (!WonderTradeForge.getConfig().isPersistentPool()) {
            return;
        }

        UtilConcurrency.runAsync(() -> this.saveFile(this.tradePoolFile.toFile()));
    }

    private void saveFile(File file) {
        if (!this.mod.getConfig().isPersistentPool()) {
            return;
        }

        var nbt = new CompoundNBT();
        var tradePool = new ListNBT();

        for (Pokemon pokemon : this.tradePool) {
            tradePool.add(pokemon.writeToNBT(new CompoundNBT()));
        }

        nbt.put("trade_pool", tradePool);

        try (var writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(nbt.toString());
        } catch (IOException e) {
            WonderTradeForge.getLogger().error("Failed to save WonderTrade pool file", e);
        }
    }

    private void loadPool(File file) {
        try (var fileReader = new FileReader(file);
             var reader = new BufferedReader(fileReader)) {
            var builder = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }

            CompoundNBT nbt = JsonToNBT.parseTag(builder.toString());
            ListNBT list = nbt.getList("trade_pool", Constants.NBT.TAG_COMPOUND);
            for (INBT inbt : list) {
                Pokemon pokemon = PokemonFactory.create((CompoundNBT) inbt);
                this.tradePool.add(pokemon);
            }
        } catch (CommandSyntaxException | IOException e) {
            WonderTradeForge.getLogger().error("Failed to load WonderTrade pool file", e);
        }
    }

    public void replaceRandomPokemon(EnvyPlayer<ServerPlayerEntity> player, Pokemon newPoke) {
        var attribute = player.getAttributeNow(WonderTradeAttribute.class);

        if (this.tradePool.isEmpty()) {
            WonderTradeForge.getLogger().error("WonderTrade Pool is empty");
            return;
        }

        Pokemon pokemon = UtilRandom.getRandomElement(this.tradePool);

        attribute.setSelected(-1);

        WonderTradeEvent event = new WonderTradeEvent(player, newPoke, pokemon);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            return;
        }

        player.getParent().closeContainer();

        if (this.shouldBroadcast(newPoke)) {
            PlatformProxy.broadcastMessage(WonderTradeForge.getLocale().getPokemonBroadcast(), Placeholder.simple(s -> this.handlePlaceholders(player, newPoke, s)));
        }

        attribute.updateLastTrade();
        StorageProxy.getParty(player.getParent()).set(newPoke.getStorageAndPosition().getB(), pokemon);
        this.tradePool.remove(pokemon);
        this.tradePool.add(newPoke);

        player.message(UtilChatColour.colour(
                UtilPlaceholder.replaceIdentifiers(player.getParent(), WonderTradeForge.getLocale().getTradeSuccessful()
                        .replace("%species%", pokemon.getSpecies().getLocalizedName())
                        .replace("%is_shiny%", WonderTradeForge.getLocale().getShinyReplacement())
                        .replace("%is_ultrabeast%", WonderTradeForge.getLocale().getUltraBeastReplacement())
                        .replace("%is_legend%", WonderTradeForge.getLocale().getLegendReplacement()))));

        UtilConcurrency.runAsync(this::saveFile);
    }

    private boolean shouldBroadcast(Pokemon newPoke) {
        WonderTradeConfig.BroadcastSettings broadcastSettings = this.mod.getConfig().getBroadcastSettings();

        if (broadcastSettings.isAlwaysBroadcast()) {
            return true;
        }

        if (newPoke.isLegendary() && broadcastSettings.isBroadcastLegends()) {
            return true;
        }

        if (newPoke.isUltraBeast() && broadcastSettings.isBroadcastUltraBeasts()) {
            return true;
        }

        return newPoke.isShiny() && broadcastSettings.isBroadcastShinies();
    }

    public String handlePlaceholders(EnvyPlayer<ServerPlayerEntity> player, Pokemon newPoke, String line) {
        return UtilPlaceholder.replaceIdentifiers(player.getParent(), line
                .replace("%is_shiny%", newPoke.isShiny() ? WonderTradeForge.getLocale().getShinyReplacement() : "")
                .replace("%is_ultra_beast%", newPoke.isUltraBeast() ? WonderTradeForge.getLocale().getUltraBeastReplacement() : "")
                .replace("%is_legend%", newPoke.isLegendary() ? WonderTradeForge.getLocale().getLegendReplacement() : "")
                .replace("%species%", newPoke.getSpecies().getName())
                .replace("%pokemon%", newPoke.getFormattedDisplayName().getString()));
    }

    public List<Pokemon> getTradePool() {
        return this.tradePool;
    }

    public void removePokemon(Pokemon pokemon) {
        this.tradePool.remove(pokemon);
        this.tradePool.add(WonderTradeForge.getConfig().getDefaultGeneratorSettings().build());
    }
}
