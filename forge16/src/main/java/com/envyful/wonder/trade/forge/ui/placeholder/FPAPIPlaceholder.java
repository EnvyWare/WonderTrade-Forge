package com.envyful.wonder.trade.forge.ui.placeholder;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import net.minecraft.entity.player.ServerPlayerEntity;

public class FPAPIPlaceholder implements SimplePlaceholder {

    private final ServerPlayerEntity player;

    public FPAPIPlaceholder(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public String replace(String line) {
        if (!WonderTradeForge.isPlaceholderAPIEnabled()) {
            return line;
        }

        return this.useFPAPI(line);
    }

    private String useFPAPI(String json) {
        return UtilPlaceholder.replaceIdentifiers(this.player, json);
    }
}
