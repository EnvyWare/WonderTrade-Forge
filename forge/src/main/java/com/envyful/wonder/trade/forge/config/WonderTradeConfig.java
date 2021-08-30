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

import com.envyful.api.config.type.SQLDatabaseDetails;

@ConfigPath("config/WonderTradeForge/config.yml")
@ConfigSerializable
public class WonderTradeConfig extends AbstractYamlConfig {

    private GenerationSettings defaultGeneratorSettings = new GenerationSettings();
    private SQLDatabaseDetails databaseDetails = new SQLDatabaseDetails();

    private int cooldownSeconds = 3600;
    private int minRequiredLevel = 30;
    private int numberInPool = 30;

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

    public int getNumberInPool() {
        return this.numberInPool;
    }

    public SQLDatabaseDetails getDatabaseDetails() {
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
}
