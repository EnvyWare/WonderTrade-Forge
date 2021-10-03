package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.math.UtilRandom;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@ConfigPath("config/WonderTradeForge/config.yml")
@ConfigSerializable
public class WonderTradeConfig extends AbstractYamlConfig {

    private BroadcastSettings broadcastSettings = new BroadcastSettings();
    private GenerationSettings defaultGeneratorSettings = new GenerationSettings();
    private SQLDatabaseDetails databaseDetails = new SQLDatabaseDetails("WonderTrade", "0.0.0.0",
            3306, "admin", "password", "WonderTrade");

    private ConfigInterface guiSettings = new ConfigInterface("WonderTrade", 5, "BLOCK", ImmutableMap.of("one", new ConfigItem(
            "minecraft:stained_glass_pane", 1, (byte) 15, " ",
            Lists.newArrayList(), Maps.newHashMap()
    )));

    private int cooldownSeconds = 3600;
    private int minRequiredLevel = 30;
    private int numberInPool = 30;
    private boolean persistentPool = true;

    public WonderTradeConfig() {}

    public ConfigInterface getGuiSettings() {
        return this.guiSettings;
    }

    public boolean isPersistentPool() {
        return this.persistentPool;
    }

    public GenerationSettings getDefaultGeneratorSettings() {
        return this.defaultGeneratorSettings;
    }

    public int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public int getMinRequiredLevel() {
        return this.minRequiredLevel;
    }

    public int getNumberInPool() {
        return this.numberInPool;
    }

    public SQLDatabaseDetails getDatabaseDetails() {
        return this.databaseDetails;
    }

    public BroadcastSettings getBroadcastSettings() {
        return this.broadcastSettings;
    }

    @ConfigSerializable
    public static class GenerationSettings {

        private Set<EnumSpecies> blockedTypes = Sets.newHashSet(EnumSpecies.Hoopa);
        private boolean allowLegends = true;
        private boolean allowUltraBeasts = true;
        private double shinyChance = 0.05;

        public GenerationSettings() {
        }

        public Pokemon build() {
            EnumSpecies species = this.getRandomSpecies();
            PokemonSpec spec = new PokemonSpec();

            spec.name = species.name;
            spec.shiny = ThreadLocalRandom.current().nextDouble() < this.shinyChance;
            spec.level = species.getBaseStats().getSpawnLevel() + UtilRandom.randomInteger(0,
                    Math.max(1, species.getBaseStats().getSpawnLevelRange()));

            return spec.create();
        }

        private EnumSpecies getRandomSpecies() {
            EnumSpecies species = EnumSpecies.randomPoke(this.allowLegends);

            while (blockedTypes.contains(species) || (!this.allowUltraBeasts && species.isUltraBeast())) {
                species = EnumSpecies.randomPoke(this.allowLegends);
            }

            return species;
        }
    }

    @ConfigSerializable
    public static class BroadcastSettings {

        private boolean alwaysBroadcast = true;
        private boolean broadcastLegends = true;
        private boolean broadcastUltraBeasts = true;
        private boolean broadcastShinies = true;

        public boolean isAlwaysBroadcast() {
            return this.alwaysBroadcast;
        }

        public boolean isBroadcastLegends() {
            return this.broadcastLegends;
        }

        public boolean isBroadcastUltraBeasts() {
            return this.broadcastUltraBeasts;
        }

        public boolean isBroadcastShinies() {
            return this.broadcastShinies;
        }
    }
}
