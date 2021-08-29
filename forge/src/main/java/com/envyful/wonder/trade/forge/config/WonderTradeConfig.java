package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Set;

@ConfigPath("config/WonderTradeForge/config.yml")
@ConfigSerializable
public class WonderTradeConfig extends AbstractYamlConfig {

    private GenerationSettings defaultGeneratorSettings = new GenerationSettings();

    public WonderTradeConfig() {
    }

    public GenerationSettings getDefaultGeneratorSettings() {
        return this.defaultGeneratorSettings;
    }

    @ConfigSerializable
    public static class GenerationSettings {

        private Set<EnumSpecies> blockedTypes = Sets.newHashSet(EnumSpecies.Hoopa);
        private boolean allowLegends = true;
        private boolean allowUltraBeasts = true;

        public GenerationSettings() {
        }

        public Pokemon build() {
            EnumSpecies species = this.getRandomSpecies();
            PokemonSpec spec = new PokemonSpec();
            spec.name = species.name;
            return spec.create();
        }

        private EnumSpecies getRandomSpecies() {
            EnumSpecies species = EnumSpecies.randomPoke(this.allowLegends);

            while (blockedTypes.contains(species) || (!this.allowLegends && species.isUltraBeast())) {
                species = EnumSpecies.randomPoke(this.allowLegends);
            }

            return species;
        }

    }
}
