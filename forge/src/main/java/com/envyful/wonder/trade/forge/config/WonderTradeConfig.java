package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.math.UtilRandom;
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

    private GenerationSettings defaultGeneratorSettings = new GenerationSettings();
    private DatabaseDetails databaseDetails = new DatabaseDetails();

    private int cooldownSeconds = 3600;
    private int minRequiredLevel = 30;

    public WonderTradeConfig() {}

    public GenerationSettings getDefaultGeneratorSettings() {
        return this.defaultGeneratorSettings;
    }

    public int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public int getMinRequiredLevel() {
        return this.minRequiredLevel;
    }

    public DatabaseDetails getDatabaseDetails() {
        return this.databaseDetails;
    }

    @ConfigSerializable
    public static class GenerationSettings {

        private Set<EnumSpecies> blockedTypes = Sets.newHashSet(EnumSpecies.Hoopa);
        private boolean allowLegends = true;
        private boolean allowUltraBeasts = true;
        private double shinyChance = 0.05;
        private int minLevel = 10;
        private int maxLevel = 70;

        public GenerationSettings() {
        }

        public Pokemon build() {
            EnumSpecies species = this.getRandomSpecies();
            PokemonSpec spec = new PokemonSpec();

            spec.name = species.name;
            spec.shiny = ThreadLocalRandom.current().nextDouble() < this.shinyChance;
            spec.level = UtilRandom.randomInteger(this.minLevel, this.maxLevel);

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
    public static class DatabaseDetails {

        private String poolName = "WonderTrade";
        private String ip = "0.0.0.0";
        private int port = 3306;
        private String username = "admin";
        private String password = "admin";
        private String database = "database";

        public DatabaseDetails() {
        }

        public String getPoolName() {
            return this.poolName;
        }

        public String getIp() {
            return this.ip;
        }

        public int getPort() {
            return this.port;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        public String getDatabase() {
            return this.database;
        }
    }
}
