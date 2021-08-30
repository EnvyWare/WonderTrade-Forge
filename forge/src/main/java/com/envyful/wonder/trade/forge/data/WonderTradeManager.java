package com.envyful.wonder.trade.forge.data;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.json.UtilGson;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.data.event.WonderTradeEvent;
import com.google.common.collect.Lists;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WonderTradeManager {

    private final Path tradePoolFile = Paths.get("config/WonderTrade/pool.json");
    private final List<Pokemon> tradePool = Lists.newArrayList();

    private final WonderTradeForge mod;

    public WonderTradeManager(WonderTradeForge mod) {
        this.mod = mod;

        File file = tradePoolFile.toFile();

        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }

        if (!file.exists()) {
            this.createFile(file);
            this.generatePool();
            this.saveFile(file);
        } else {
            this.loadPool(file);
        }
    }

    private void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generatePool() {
        for (int i = 0; i < this.mod.getConfig().getNumberInPool(); i++) {
            this.tradePool.add(this.mod.getConfig().getDefaultGeneratorSettings().build());
        }
    }

    public void saveFile() {
        UtilConcurrency.runAsync(() -> this.saveFile(this.tradePoolFile.toFile()));
    }

    private void saveFile(File file) {
        try {
            UtilGson.GSON.toJson(this.tradePool, ArrayList.class, new JsonWriter(new FileWriter(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPool(File file) {
        try {
            this.tradePool.addAll(UtilGson.GSON.fromJson(new JsonReader(new FileReader(file)), ArrayList.class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void replaceRandomPokemon(EnvyPlayer<EntityPlayerMP> player, Pokemon newPoke) {
        WonderTradeAttribute attribute = player.getAttribute(WonderTradeForge.class);
        Pokemon pokemon = UtilRandom.getRandomElement(this.tradePool);

        WonderTradeEvent event = new WonderTradeEvent(player, newPoke, pokemon);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            return;
        }

        UtilPixelmonPlayer.getParty(player.getParent()).set(newPoke.getStorageAndPosition().getSecond(), pokemon);
        this.tradePool.remove(pokemon);
        this.tradePool.add(newPoke);
        //TODO: send message

        UtilConcurrency.runAsync(this::saveFile);
    }
}
