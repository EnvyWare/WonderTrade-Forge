package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.type.Pair;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@ConfigPath("config/WonderTradeForge/guis.yml")
public class WonderTradeGraphics extends AbstractYamlConfig {

    private SelectPokemonUI selectPokemonUI = new SelectPokemonUI();

    public WonderTradeGraphics() {
        super();
    }

    public SelectPokemonUI getSelectPokemonUI() {
        return this.selectPokemonUI;
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
}
