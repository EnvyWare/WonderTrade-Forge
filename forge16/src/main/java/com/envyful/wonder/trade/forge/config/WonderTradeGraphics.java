package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.type.Pair;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
@ConfigPath("config/WonderTradeForge/guis.yml")
public class WonderTradeGraphics extends AbstractYamlConfig {

    private SelectPokemonUI selectPokemonUI = new SelectPokemonUI();
    private AdminUISettings adminUI = new AdminUISettings();
    private ListUI listUI = new ListUI();

    public WonderTradeGraphics() {
        super();
    }

    public SelectPokemonUI getSelectPokemonUI() {
        return this.selectPokemonUI;
    }

    public AdminUISettings getAdminUI() {
        return this.adminUI;
    }

    public ListUI getListUI() {
        return this.listUI;
    }

    @ConfigSerializable
    public static class SelectPokemonUI {

        private ConfigInterface guiSettings = new ConfigInterface(
                "WonderTrade", 5, "BLOCK", ImmutableMap.of("one", ConfigItem.builder()
                .type("minecraft:black_stained_glass_pane").amount(1).name(" ").build()));

        private ConfigItem levelTooLowItem = ConfigItem.builder()
                .type("minecraft:red_stained_glass_pane")
                .amount(1)
                .name("&c&lLevel too low")
                .build();

        private ConfigItem untradeableItem = ConfigItem.builder()
                .type("minecraft:red_stained_glass_pane")
                .amount(1)
                .name("&c&lUntradeable")
                .build();

        private ExtendedConfigItem clickToConfirmButton = ExtendedConfigItem.builder()
                .type("minecraft:lime_stained_glass_pane")
                .amount(1)
                .positions(Pair.of(7, 2))
                .name("&a&lClick to confirm")
                .build();

        private ConfigItem noPokemonInSlotItem = ConfigItem.builder()
                .type("minecraft:barrier")
                .name("&c&lNo Pokemon in this slot")
                .amount(1)
                .build();

        private ExtendedConfigItem noneSelectedItem = ExtendedConfigItem.builder()
                .type("minecraft:barrier")
                .amount(1)
                .name("&c&lNone selected")
                .positions(Pair.of(5, 2))
                .build();

        private SpriteConfig spriteConfig = new SpriteConfig();

        private boolean enchantSelectedPokemon = true;

        private int selectedSpritePos = 23;

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ConfigItem getLevelTooLowItem() {
            return this.levelTooLowItem;
        }

        public ConfigItem getUntradeableItem() {
            return this.untradeableItem;
        }

        public ExtendedConfigItem getClickToConfirmButton() {
            return this.clickToConfirmButton;
        }

        public ConfigItem getNoPokemonInSlotItem() {
            return this.noPokemonInSlotItem;
        }

        public ExtendedConfigItem getNoneSelectedItem() {
            return this.noneSelectedItem;
        }

        public SpriteConfig getSpriteConfig() {
            return this.spriteConfig;
        }

        public boolean isEnchantSelectedPokemon() {
            return this.enchantSelectedPokemon;
        }

        public int getSelectedSpritePos() {
            return this.selectedSpritePos;
        }
    }

    @ConfigSerializable
    public static class AdminUISettings {

        private ConfigInterface guiSettings = new ConfigInterface("WonderTrade", 6, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                "minecraft:black_stained_glass_pane", 1, " ", Lists.newArrayList(), Maps.newHashMap(), Maps.newHashMap()
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

    @ConfigSerializable
    public static class ListUI {

        private ConfigInterface guiSettings = new ConfigInterface("WonderTrade", 6, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                "minecraft:black_stained_glass_pane", 1, " ", Lists.newArrayList(), Maps.newHashMap(), Maps.newHashMap()
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

        private ExtendedConfigItem selectUIButton = new ExtendedConfigItem(
                "minecraft:clock", 1, (byte) 0, "&aWT a Poke",
                Lists.newArrayList(), 5, 5, Maps.newHashMap()
        );

        private SpriteConfig sprites = new SpriteConfig();

        public ListUI() {
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

        public ExtendedConfigItem getSelectUIButton() {
            return this.selectUIButton;
        }
    }
}
