package com.envyful.wonder.trade.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.items.ItemBuilder;
import com.envyful.api.neoforge.items.ItemFlag;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.api.WonderTradeAttribute;
import com.envyful.wonder.trade.forge.config.WonderTradeGraphics;
import com.envyful.wonder.trade.forge.ui.placeholder.FPAPIPlaceholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class PokemonSelectUI {

    public static void openUI(ForgeEnvyPlayer player) {
        WonderTradeAttribute attribute = player.getAttributeNow(WonderTradeAttribute.class);

        if (attribute == null) {
            return;
        }

        WonderTradeGraphics.SelectPokemonUI config = WonderTradeForge.getGraphics().getSelectPokemonUI();

        var pane = config.getGuiSettings().toPane();

        PlayerPartyStorage party = StorageProxy.getPartyNow(player.getParent());
        Pokemon[] all = party.getAll();

        for (int i = 0; i < all.length; i++) {
            Pokemon pokemon = all[i];
            int yPos = 1 + (i / 2);
            int xPos = 1 + (i % 2);

            if (pokemon == null) {
                pane.set(xPos, yPos, GuiFactory.displayable(UtilConfigItem.fromConfigItem(config.getNoPokemonInSlotItem())));
                continue;
            }

            if (pokemon.getPokemonLevel() < WonderTradeForge.getConfig().getMinRequiredLevel()) {
                pane.set(xPos, yPos, GuiFactory.displayable(UtilConfigItem.fromConfigItem(config.getLevelTooLowItem())));
            } else if (pokemon.isUntradeable()) {
                pane.set(xPos, yPos, GuiFactory.displayable(UtilConfigItem.fromConfigItem(config.getUntradeableItem())));
            } else {
                int finalI = i;
                ItemStack sprite = UtilSprite.getPokemonElement(pokemon, config.getSpriteConfig());
                setPokemonDisplayItem(pane, xPos, yPos, sprite, player, finalI, config);
            }
        }

        if (attribute.getSelected() != -1) {
            int selectedSpritePosX = config.getSelectedSpritePos() % 9;
            int selectedSpritePosY = config.getSelectedSpritePos() / 9;

            pane.set(selectedSpritePosX, selectedSpritePosY, GuiFactory.displayable(UtilSprite.getPokemonElement(all[attribute.getSelected()], config.getSpriteConfig())));
        } else {
            UtilConfigItem.builder().extendedConfigItem(player, pane, config.getNoneSelectedItem(), new FPAPIPlaceholder(player.getParent()));
        }

        UtilConfigItem.builder()
                .asyncClick(true)
                .clickHandler((envyPlayer, clickType) -> {
                    WonderTradeAttribute att = player.getAttributeNow(WonderTradeAttribute.class);

                    if (att.getSelected() == -1) {
                        return;
                    }

                    Pokemon pokemon = all[att.getSelected()];

                    PlatformProxy.runSync(() -> WonderTradeForge.getInstance().getManager()
                            .replaceRandomPokemon((EnvyPlayer<ServerPlayer>)envyPlayer, pokemon));
                })
                .extendedConfigItem(player, pane, config.getClickToConfirmButton(), new FPAPIPlaceholder(player.getParent()));

        pane.open(player, config.getGuiSettings());
    }

    private static void setPokemonDisplayItem(Pane pane, int xPos, int yPos, ItemStack sprite, ForgeEnvyPlayer player, int finalI, WonderTradeGraphics.SelectPokemonUI config) {
        Pokemon[] all = StorageProxy.getPartyNow(player.getParent()).getAll();
        var enchants = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        pane.set(xPos, yPos,
                GuiFactory.displayableBuilder(sprite)
                        .clickHandler((envyPlayer, clickType) -> {
                            WonderTradeAttribute attribute = player.getAttributeNow(WonderTradeAttribute.class);

                            int selectedSpritePosX = config.getSelectedSpritePos() % 9;
                            int selectedSpritePosY = config.getSelectedSpritePos() / 9;

                            if (config.isEnchantSelectedPokemon()) {
                                setPokemonDisplayItem(pane, xPos, yPos, new ItemBuilder(sprite).enchant(enchants.getHolderOrThrow(Enchantments.UNBREAKING), 1).itemFlag(ItemFlag.HIDE_ENCHANTS).build(), player, finalI, config);
                            }

                            attribute.setSelected(finalI);
                            pane.set(selectedSpritePosX, selectedSpritePosY,
                                    GuiFactory.displayable(UtilSprite.getPokemonElement(all[attribute.getSelected()], config.getSpriteConfig())));
                            openUI(player);
                        })
                        .build());
    }
}
