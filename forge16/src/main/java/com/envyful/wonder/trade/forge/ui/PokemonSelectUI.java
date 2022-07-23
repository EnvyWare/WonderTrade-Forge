package com.envyful.wonder.trade.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.envyful.wonder.trade.forge.data.WonderTradeAttribute;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;

public class PokemonSelectUI {

    public static void openUI(EnvyPlayer<ServerPlayerEntity> player) {
        WonderTradeConfig config = WonderTradeForge.getInstance().getConfig();

        Pane pane = GuiFactory.paneBuilder()
                .height(config.getGuiSettings().getHeight())
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        PlayerPartyStorage party = StorageProxy.getParty(player.getParent());
        Pokemon[] all = party.getAll();

        for (int i = 0; i < all.length; i++) {
            Pokemon pokemon = all[i];
            int yPos = 1 + (i / 2);
            int xPos = 1 + (i % 2);

            if (pokemon == null) {
                pane.set(xPos, yPos, GuiFactory.displayable(new ItemBuilder()
                        .type(Items.BARRIER)
                        .name(UtilChatColour.colour("&c&l ")).build())
                );
                continue;
            }

            if (pokemon.getPokemonLevel() < config.getMinRequiredLevel()) {
                pane.set(xPos, yPos, GuiFactory.displayable(UtilConfigItem.fromConfigItem(config.getLevelTooLowItem())));
            } else if (pokemon.isUntradeable() || pokemon.isInRanch()) {
                pane.set(xPos, yPos, GuiFactory.displayable(UtilConfigItem.fromConfigItem(config.getUntradeableItem())));
            } else {
                int finalI = i;
                pane.set(xPos, yPos, GuiFactory.displayableBuilder(UtilSprite.getPokemonElement(pokemon, config.getSpriteConfig()))
                        .clickHandler((envyPlayer, clickType) -> {
                            WonderTradeAttribute attribute = player.getAttribute(WonderTradeForge.class);

                            if (attribute != null) {
                                int selectedSpritePosX = config.getSelectedSpritePos() % 9;
                                int selectedSpritePosY = config.getSelectedSpritePos() / 9;

                                attribute.setSelected(finalI);
                                pane.set(selectedSpritePosX, selectedSpritePosY,
                                         GuiFactory.displayable(UtilSprite.getPokemonElement(all[attribute.getSelected()], config.getSpriteConfig())));
                            }
                        })
                        .build());
            }
        }

        WonderTradeAttribute attribute = player.getAttribute(WonderTradeForge.class);

        if (attribute == null) {
            return;
        }

        if (attribute.getSelected() != -1) {
            int selectedSpritePosX = config.getSelectedSpritePos() % 9;
            int selectedSpritePosY = config.getSelectedSpritePos() / 9;

            pane.set(selectedSpritePosX, selectedSpritePosY,
                     GuiFactory.displayable(UtilSprite.getPokemonElement(all[attribute.getSelected()], config.getSpriteConfig())));
        } else {
            UtilConfigItem.addConfigItem(pane, config.getNoneSelectedItem());
        }

        pane.set(7, 2, GuiFactory.displayableBuilder(UtilConfigItem.fromConfigItem(config.getClickToConfirmButton()))
                .clickHandler((envyPlayer, clickType) -> {
                    WonderTradeAttribute att = player.getAttribute(WonderTradeForge.class);

                    if (att.getSelected() == -1) {
                        return;
                    }

                    Pokemon pokemon = all[att.getSelected()];

                    UtilForgeConcurrency.runSync(() -> {
                        WonderTradeForge.getInstance().getManager()
                                .replaceRandomPokemon((EnvyPlayer<ServerPlayerEntity>) envyPlayer, pokemon);
                    });
                }).build());

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .height(config.getGuiSettings().getHeight())
                .setPlayerManager(WonderTradeForge.getInstance().getPlayerManager())
                .build()
                .open(player);
    }
}
