package com.envyful.wonder.trade.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeGraphics;
import com.envyful.wonder.trade.forge.ui.placeholder.FPAPIPlaceholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;

import java.util.List;

public class AdminViewUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 0);
    }

    public static void openUI(ForgeEnvyPlayer player, int page) {
        WonderTradeGraphics.AdminUISettings config = WonderTradeForge.getGraphics().getAdminUI();

        Pane pane = GuiFactory.paneBuilder()
                .height(config.getGuiSettings().getHeight())
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings(), new FPAPIPlaceholder(player.getParent()));

        List<Pokemon> tradePool = WonderTradeForge.getInstance().getManager().getTradePool();

        for (int i = (page * config.getPagePositions().size()); i < Math.min(tradePool.size(), ((page + 1) * config.getPagePositions().size())); i++) {
            int pos = config.getPagePositions().get(i % config.getPagePositions().size());
            int posX = pos % 9;
            int posY = pos / 9;

            Pokemon poke = tradePool.get(i);

            pane.set(posX, posY, GuiFactory.displayableBuilder(UtilSprite.getPokemonElement(poke, config.getSprites()))
                    .asyncClick(false)
                    .delayTicks(1)
                    .clickHandler((envyPlayer, clickType) -> {
                        WonderTradeForge.getInstance().getManager().removePokemon(poke);
                        StorageProxy.getParty(player.getParent()).add(poke);
                        player.message(UtilChatColour.colour(
                                WonderTradeForge.getLocale().getRemovedPokemon()
                        ));
                        openUI(player, page);
                    }).build());
        }

        UtilConfigItem.builder()
                .asyncClick()
                .clickHandler((envyPlayer, clickType) -> {
                    if (((page + 1) * config.getPagePositions().size()) > tradePool.size()) {
                        openUI(player, 0);
                    } else {
                        openUI(player, page + 1);
                    }
                })
                .extendedConfigItem(player, pane, config.getNextPageButton(), new FPAPIPlaceholder(player.getParent()));


        UtilConfigItem.builder()
                .asyncClick()
                .clickHandler((envyPlayer, clickType) -> {
                    if (page == 0) {
                        openUI(player, (tradePool.size() / config.getPagePositions().size()));
                    } else {
                        openUI(player, 0);
                    }
                })
                .extendedConfigItem(player, pane, config.getPreviousPageButton(), new FPAPIPlaceholder(player.getParent()));

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .height(config.getGuiSettings().getHeight())
                .setPlayerManager(WonderTradeForge.getInstance().getPlayerManager())
                .build()
                .open(player);
    }
}
