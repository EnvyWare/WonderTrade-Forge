package com.envyful.wonder.trade.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class ListViewUI {

    public static void openUI(EnvyPlayer<ServerPlayerEntity> player) {
        openUI(player, 0);
    }

    public static void openUI(EnvyPlayer<ServerPlayerEntity> player, int page) {
        WonderTradeConfig.ListUI config = WonderTradeForge.getInstance().getConfig().getListUI();

        Pane pane = GuiFactory.paneBuilder()
                .height(config.getGuiSettings().getHeight())
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        List<Pokemon> tradePool = WonderTradeForge.getInstance().getManager().getTradePool();

        for (int i = (page * config.getPagePositions().size()); i < Math.min(tradePool.size(), ((page + 1) * config.getPagePositions().size())); i++) {
            int pos = config.getPagePositions().get(i % config.getPagePositions().size());
            int posX = pos % 9;
            int posY = pos / 9;

            Pokemon poke = tradePool.get(i);

            pane.set(posX, posY, GuiFactory.displayableBuilder(UtilSprite.getPokemonElement(poke, config.getSprites())).build());
        }

        UtilConfigItem.addConfigItem(pane, config.getNextPageButton(), (envyPlayer, clickType) -> {
            if (((page + 1) * config.getPagePositions().size()) > tradePool.size()) {
                openUI(player, 0);
            } else {
                openUI(player, page + 1);
            }
        });

        UtilConfigItem.addConfigItem(pane, config.getPreviousPageButton(), (envyPlayer, clickType) -> {
            if (page == 0) {
                openUI(player, (tradePool.size() / config.getPagePositions().size()));
            } else {
                openUI(player, 0);
            }
        });


        UtilConfigItem.builder()
                        .asyncClick(false)
                        .combinedClickHandler(config.getSelectUIButton(), (envyPlayer, clickType) -> PokemonSelectUI.openUI(player))
                        .singleClick()
                        .extendedConfigItem((ForgeEnvyPlayer) player, pane, config.getSelectUIButton());

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .height(config.getGuiSettings().getHeight())
                .setPlayerManager(WonderTradeForge.getInstance().getPlayerManager())
                .build()
                .open(player);
    }
}
