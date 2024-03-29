package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@ConfigPath("config/WonderTradeForge/config.yml")
@ConfigSerializable
public class WonderTradeConfig extends AbstractYamlConfig {

    private BroadcastSettings broadcastSettings = new BroadcastSettings();
    private GenerationSettings defaultGeneratorSettings = new GenerationSettings();
    private SQLDatabaseDetails databaseDetails = new SQLDatabaseDetails("WonderTrade", "0.0.0.0",
            3306, "admin", "password", "WonderTrade");

    private AdminUISettings adminUI = new AdminUISettings();

    private ConfigInterface guiSettings = new ConfigInterface("WonderTrade", 5, "BLOCK", ImmutableMap.of("one", new ConfigItem(
            "minecraft:stained_glass_pane", 1, (byte) 15, " ",
            Lists.newArrayList(), Maps.newHashMap()
    )));

    private ConfigItem levelTooLowItem = new ConfigItem("minecraft:stained_glass_pane", 1, (byte) 14,
                                                        "&c&lLevel too low", Lists.newArrayList(), Maps.newHashMap());

    private ConfigItem untradeableItem = new ConfigItem("minecraft:stained_glass_pane", 1, (byte) 14,
                                                        "&c&lUntradeable", Lists.newArrayList(), Maps.newHashMap());

    private ConfigItem clickToConfirmButton = new ConfigItem("minecraft:stained_glass_pane", 1, (byte) 5,
                                                        "&a&lClick to confirm", Lists.newArrayList(), Maps.newHashMap());

    private ExtendedConfigItem noneSelectedItem = new ExtendedConfigItem("minecraft:barrier", 1, (byte) 0,
                                                                     "&c&lNone selected", Lists.newArrayList(),
                                                                                 5, 2, Maps.newHashMap());

    private SpriteConfig spriteConfig = new SpriteConfig();

    private int cooldownSeconds = 3600;
    private int minRequiredLevel = 30;
    private int numberInPool = 30;
    private int selectedSpritePos = 23;
    private boolean persistentPool = true;
    private boolean disableUI = false;

    public WonderTradeConfig() {}

    public ExtendedConfigItem getNoneSelectedItem() {
        return this.noneSelectedItem;
    }

    public ConfigItem getClickToConfirmButton() {
        return this.clickToConfirmButton;
    }

    public ConfigItem getUntradeableItem() {
        return this.untradeableItem;
    }

    public ConfigItem getLevelTooLowItem() {
        return this.levelTooLowItem;
    }

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

    public SpriteConfig getSpriteConfig() {
        return this.spriteConfig;
    }

    public int getSelectedSpritePos() {
        return this.selectedSpritePos;
    }

    public AdminUISettings getAdminUI() {
        return this.adminUI;
    }

    public boolean isDisableUI() {
        return this.disableUI;
    }

    @ConfigSerializable
    public static class GenerationSettings {

        private Set<EnumSpecies> blockedTypes = Sets.newHashSet(EnumSpecies.Hoopa);
        private List<String> blockedSpecs = Lists.newArrayList("hoopa");
        private transient List<PokemonSpec> blockSpecsCache = null;
        private boolean allowLegends = true;
        private boolean allowUltraBeasts = true;
        private double shinyChance = 0.05;

        public GenerationSettings() {
        }

        public Pokemon build() {
            Pokemon randomSpecies = this.getRandomPokemon();

            while (this.isBlockedSpec(randomSpecies)) {
                randomSpecies = this.getRandomPokemon();
            }

            return randomSpecies;
        }

        private Pokemon getRandomPokemon() {
            EnumSpecies species = EnumSpecies.randomPoke(this.allowLegends);

            while (blockedTypes.contains(species) || (!this.allowUltraBeasts && species.isUltraBeast())) {
                species = EnumSpecies.randomPoke(this.allowLegends);
            }

            PokemonSpec spec = new PokemonSpec();

            spec.name = species.name;
            spec.shiny = ThreadLocalRandom.current().nextDouble() < this.shinyChance;
            spec.level = species.getBaseStats().getSpawnLevel() + UtilRandom.randomInteger(0, Math.max(1, species.getBaseStats().getSpawnLevelRange()));
            return spec.create();
        }

        private boolean isBlockedSpec(Pokemon pokemon) {
            if (this.blockSpecsCache == null) {
                List<PokemonSpec> blockSpecsCache = Lists.newArrayList();

                for (String blockedSpec : this.blockedSpecs) {
                    blockSpecsCache.add(PokemonSpec.from(blockedSpec));
                }

                this.blockSpecsCache = blockSpecsCache;
            }

            for (PokemonSpec pokemonSpec : this.blockSpecsCache) {
                if (pokemonSpec.matches(pokemon)) {
                    return true;
                }
            }

            return false;
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

    @ConfigSerializable
    public static class AdminUISettings {

        private ConfigInterface guiSettings = new ConfigInterface("WonderTrade", 6, "BLOCK", ImmutableMap.of("one",
                                                                                                             new ConfigItem(
                "minecraft:stained_glass_pane", 1, (byte) 15, " ",
                Lists.newArrayList(), Maps.newHashMap()
        )));

        private List<Integer> pagePositions = Lists.newArrayList(
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
                25, 26, 27, 28, 29
        );

        private ExtendedConfigItem nextPageButton = new ExtendedConfigItem(
                "pixelmon:trade_holder_right", 1, (byte) 0, "&aNext Page",
                Lists.newArrayList(), 8, 5, Maps.newHashMap()
        );

        private ExtendedConfigItem previousPageButton = new ExtendedConfigItem(
                "pixelmon:trade_holder_left", 1, (byte) 0, "&aPrevious Page",
                Lists.newArrayList(), 0, 5, Maps.newHashMap()
        );

        private SpriteConfig sprites = new SpriteConfig();

        public AdminUISettings() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public List<Integer> getPagePositions() {
            return this.pagePositions;
        }

        public SpriteConfig getSprites() {
            return this.sprites;
        }

        public ExtendedConfigItem getNextPageButton() {
            return this.nextPageButton;
        }

        public ExtendedConfigItem getPreviousPageButton() {
            return this.previousPageButton;
        }
    }
}
