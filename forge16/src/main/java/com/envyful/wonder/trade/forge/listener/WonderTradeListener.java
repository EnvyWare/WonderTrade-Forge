package com.envyful.wonder.trade.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.envyful.wonder.trade.forge.data.event.WonderTradeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;

public class WonderTradeListener {

    @SubscribeEvent
    public void onWonderTrade(WonderTradeEvent event) {
        for (WonderTradeConfig.WebHookTriggers trigger : WonderTradeForge.getInstance().getConfig().getTriggers()) {
            if (trigger.getSpec().matches(event.getGiven())) {
                DiscordWebHook webHook = trigger.getWebHook(event);
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
