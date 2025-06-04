package com.envyful.wonder.trade.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.api.event.WonderTradeEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.io.IOException;

public class WonderTradeListener {

    @SubscribeEvent
    public void onWonderTrade(WonderTradeEvent event) {
        for (var trigger : WonderTradeForge.getConfig().getTriggers()) {
            if (trigger.getSpec().matches(event.getGiven())) {
                DiscordWebHook webHook = trigger.getWebHook(event);

                if (webHook == null) {
                    continue;
                }

                UtilConcurrency.runAsync(() -> {
                    try {
                        webHook.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
