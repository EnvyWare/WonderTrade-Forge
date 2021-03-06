package com.envyful.wonder.trade.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.envyful.wonder.trade.forge.data.WonderTradeAttribute;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PokemonSelectUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        WonderTradeConfig config = WonderTradeForge.getInstance().getConfig();

        Pane pane = GuiFactory.paneBuilder()
                .height(config.getGuiSettings().getHeight())
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .build();

        for (ConfigItem fillerItem : config.getGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem)).build());
        }

        PlayerPartyStorage party = UtilPixelmonPlayer.getParty(player.getParent());
        Pokemon[] all = party.getAll();

        for (int i = 0; i < all.length; i++) {
            Pokemon pokemon = all[i];
            int yPos = 1 + (i / 2);
            int xPos = 1 + (i % 2);

            if (pokemon == null) {
                pane.set(xPos, yPos, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(new ItemBuilder()
                                .type(Item.getByNameOrId("minecraft:barrier"))
                                .name(UtilChatColour.translateColourCodes('&', "&c&l "))
                                .lore( ).build())
                        .build());
                continue;
            }

            if (pokemon.getLevel() < config.getMinRequiredLevel()) {
                pane.set(xPos, yPos, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilConfigItem.fromConfigItem(config.getLevelTooLowItem()))
                        .build());
            } else if (pokemon.hasSpecFlag("untradeable") || pokemon.isInRanch()) {
                pane.set(xPos, yPos, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilConfigItem.fromConfigItem(config.getUntradeableItem()))
                        .build());
            } else {
                int finalI = i;
                pane.set(xPos, yPos, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilSprite.getPokemonElement(pokemon, config.getSpriteConfig()))
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
                                .replaceRandomPokemon((EnvyPlayer<EntityPlayerMP>) envyPlayer, pokemon);
                    });
                }).build());

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title(UtilChatColour.translateColourCodes('&', config.getGuiSettings().getTitle()))
                .height(config.getGuiSettings().getHeight())
                .setPlayerManager(WonderTradeForge.getInstance().getPlayerManager())
                .setCloseConsumer(envyPlayer -> {})
                .build()
                .open(player);
    }
}
