package com.envyful.wonder.trade.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
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

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .height(WonderTradeForge.getInstance().getConfig().getGuiSettings().getHeight())
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .build();

        for (ConfigItem fillerItem : WonderTradeForge.getInstance().getConfig().getGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem)).build());
        }

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
                        .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(pokemon))
                                           .name("§b" + pokemon.getSpecies().getLocalizedName() + (!pokemon.getDisplayName().isEmpty() ? " (" + pokemon.getDisplayName() + ")" : "") + "")
                                           .build())
                        .clickHandler((envyPlayer, clickType) -> {
                            WonderTradeAttribute attribute = player.getAttribute(WonderTradeForge.class);

                            if (attribute != null) {
                                attribute.setSelected(finalI);
                                pane.set(5, 2, GuiFactory.displayableBuilder(ItemStack.class)
                                        .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(all[attribute.getSelected()]))
                                                           .name("§b" + pokemon.getSpecies().getLocalizedName() + (!pokemon.getDisplayName().isEmpty() ? " (" + pokemon.getDisplayName() + ")" : "") + "")
                                                           .build())
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
                .title(WonderTradeForge.getInstance().getConfig().getGuiSettings().getTitle())
                .height(WonderTradeForge.getInstance().getConfig().getGuiSettings().getHeight())
                .setPlayerManager(WonderTradeForge.getInstance().getPlayerManager())
                .setCloseConsumer(envyPlayer -> {})
                .build()
                .open(player);
    }
}
