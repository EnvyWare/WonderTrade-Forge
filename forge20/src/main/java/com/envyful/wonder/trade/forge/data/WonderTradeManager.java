package com.envyful.wonder.trade.forge.data;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.envyful.wonder.trade.forge.data.event.WonderTradeEvent;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.ServerLifecycleHooks;

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

        if (WonderTradeForge.getConfig().isPersistentPool()) {
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
            e.printStackTrace();
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
        if (!WonderTradeForge.getConfig().isPersistentPool()) {
            return;
        }

        CompoundTag nbt = new CompoundTag();
        ListTag tradePool = new ListTag();

        for (Pokemon pokemon : this.tradePool) {
            tradePool.add(pokemon.writeToNBT(new CompoundTag()));
        }

        nbt.put("trade_pool", tradePool);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(nbt.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPool(File file) {
        try (FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader)) {
            StringBuilder builder = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }

            CompoundTag nbt = TagParser.parseTag(builder.toString());
            ListTag list = nbt.getList("trade_pool", Tag.TAG_COMPOUND);
            for (Tag inbt : list) {
                Pokemon pokemon = PokemonFactory.create((CompoundTag)inbt);
                this.tradePool.add(pokemon);
            }
        } catch (CommandSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void replaceRandomPokemon(EnvyPlayer<ServerPlayer> player, Pokemon newPoke) {
        WonderTradeAttribute attribute = player.getAttribute(WonderTradeAttribute.class);

        if (this.tradePool.isEmpty()) {
            System.out.println("ERROR: Trade Pool is empty");
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
            for (String broadcast : this.mod.getLocale().getPokemonBroadcast()) {
                ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(
                        this.getFormattedLine(player, newPoke, broadcast), true);
            }
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

    private Component getFormattedLine(EnvyPlayer<ServerPlayer> player, Pokemon newPoke, String line) {
        return UtilChatColour.colour(this.handlePlaceholders(player, newPoke, line));
    }

    public String handlePlaceholders(EnvyPlayer<ServerPlayer> player, Pokemon newPoke, String line) {
        return UtilPlaceholder.replaceIdentifiers(player.getParent(), line
                .replace("%is_shiny%", newPoke.isShiny() ? WonderTradeForge.getLocale().getShinyReplacement() : "")
                .replace("%is_ultra_beast%", newPoke.isUltraBeast() ? WonderTradeForge.getLocale().getUltraBeastReplacement() : "")
                .replace("%is_legend%", newPoke.isLegendary() ? WonderTradeForge.getLocale().getLegendReplacement() : "")
                .replace("%species%", newPoke.getSpecies().getName())
                .replace("%pokemon%", newPoke.getDisplayName()));
    }

    public List<Pokemon> getTradePool() {
        return this.tradePool;
    }

    public void removePokemon(Pokemon pokemon) {
        this.tradePool.remove(pokemon);
        this.tradePool.add(this.mod.getConfig().getDefaultGeneratorSettings().build());
    }
}
