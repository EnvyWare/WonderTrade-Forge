package com.envyful.wonder.trade.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AdminViewUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        openUI(player, 0);
    }

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int page) {
        WonderTradeConfig.AdminUISettings config = WonderTradeForge.getInstance().getConfig().getAdminUI();

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

        List<Pokemon> tradePool = WonderTradeForge.getInstance().getManager().getTradePool();

        for (int i = (page * config.getPagePositions().size()); i < Math.min(tradePool.size(), ((page + 1) * config.getPagePositions().size())); i++) {
            int pos = config.getPagePositions().get(i % config.getPagePositions().size());
            int posX = pos % 9;
            int posY = pos / 9;

            Pokemon poke = tradePool.get(i);

            pane.set(posX, posY, GuiFactory.displayableBuilder(UtilSprite.getPokemonElement(poke, config.getSprites()))
                    .clickHandler((envyPlayer, clickType) -> UtilForgeConcurrency.runSync(() -> {
                        WonderTradeForge.getInstance().getManager().removePokemon(poke);
                        UtilPixelmonPlayer.getParty(player.getParent()).add(poke);
                        player.message(UtilChatColour.translateColourCodes(
                                '&',
                                WonderTradeForge.getInstance().getLocale().getRemovedPokemon()
                        ));
                        openUI(player, page);
                    })).build());
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
