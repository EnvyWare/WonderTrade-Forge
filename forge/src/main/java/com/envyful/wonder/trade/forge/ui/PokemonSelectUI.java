package com.envyful.wonder.trade.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.data.WonderTradeAttribute;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PokemonSelectUI {

    public static final Displayable BACKGROUND_ITEM = GuiFactory.displayableBuilder(ItemStack.class)
            .itemStack(new ItemBuilder()
                    .type(Item.getByNameOrId("minecraft:stained_glass_pane"))
                    .name(" ")
                    .damage(15)
                    .build())
            .build();

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .height(5)
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .build();

        pane.fill(BACKGROUND_ITEM);

        PlayerPartyStorage party = UtilPixelmonPlayer.getParty(player.getParent());
        Pokemon[] all = party.getAll();

        for (int i = 0; i < all.length; i++) {
            Pokemon pokemon = all[i];
            int xPos = 1 + (i / 2);
            int yPos = 1 + (i % 2);

            if (pokemon == null) {
                pane.set(yPos, xPos, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(new ItemBuilder()
                                .type(Item.getByNameOrId("minecraft:barrier"))
                                .name(UtilChatColour.translateColourCodes('&', "&c&l "))
                                .lore( ).build())
                        .build());
                continue;
            }

            if (pokemon.getLevel() < WonderTradeForge.getInstance().getConfig().getMinRequiredLevel()) {
                pane.set(yPos, xPos, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(new ItemBuilder()
                                .type(Item.getByNameOrId("minecraft:stained_glass_pane"))
                                .damage(14)
                                .name(UtilChatColour.translateColourCodes('&', "&c&lLevel too low"))
                                .lore(
                                        ""
                                ).build())
                        .build());
            } else if (pokemon.hasSpecFlag("untradable")) {
                pane.set(yPos, xPos, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(new ItemBuilder()
                                .type(Item.getByNameOrId("minecraft:stained_glass_pane"))
                                .damage(14)
                                .name(UtilChatColour.translateColourCodes('&', "&c&lUntradeable"))
                                .lore(
                                        ""
                                ).build())
                        .build());
            } else {
                int finalI = i;
                pane.set(yPos, xPos, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilSprite.getPokemonElement(pokemon))
                        .clickHandler((envyPlayer, clickType) -> {
                            WonderTradeAttribute attribute = player.getAttribute(WonderTradeForge.class);

                            if (attribute != null) {
                                attribute.setSelected(finalI);
                                pane.set(5, 2, GuiFactory.displayableBuilder(ItemStack.class)
                                        .itemStack(UtilSprite.getPokemonElement(all[attribute.getSelected()]))
                                        .build());
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
            pane.set(5, 2, GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(UtilSprite.getPixelmonSprite(all[attribute.getSelected()]))
                    .build());
        } else {
            pane.set(5, 2, GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(new ItemBuilder()
                            .type(Item.getByNameOrId("minecraft:barrier"))
                            .name(UtilChatColour.translateColourCodes('&', "&c&lNone selected"))
                            .lore(
                                    ""
                            ).build())
                    .build());
        }

        pane.set(7, 2, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder()
                        .type(Item.getByNameOrId("minecraft:stained_glass_pane"))
                        .damage(5)
                        .name(UtilChatColour.translateColourCodes('&', "&a&lClick to confirm"))
                        .build())
                .clickHandler((envyPlayer, clickType) -> {
                    WonderTradeAttribute att = player.getAttribute(WonderTradeForge.class);

                    if (att.getSelected() == -1) {
                        return;
                    }

                    WonderTradeForge.getInstance().getManager()
                            .replaceRandomPokemon((EnvyPlayer<EntityPlayerMP>) envyPlayer, all[att.getSelected()]);
                }).build());

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title("WonderTrade")
                .height(5)
                .setPlayerManager(WonderTradeForge.getInstance().getPlayerManager())
                .build()
                .open(player);
    }
}
