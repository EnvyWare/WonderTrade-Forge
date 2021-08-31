package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.data.WonderTradeAttribute;
import com.envyful.wonder.trade.forge.ui.PokemonSelectUI;
import net.minecraft.entity.player.EntityPlayerMP;

@Command(
        value = "wondertrade",
        description = "Root wonder trade command",
        aliases = {
                "wt"
        }
)
@SubCommands(ReloadCommand.class)
public class WonderTradeCommand {

    @CommandProcessor
    public void onCommand(@Sender EntityPlayerMP sender, String[] args) {
        ForgeEnvyPlayer player = WonderTradeForge.getInstance().getPlayerManager().getPlayer(sender);
        WonderTradeAttribute attribute = player.getAttribute(WonderTradeForge.class);

        if (attribute == null) {
            return;
        }

        if (!attribute.canTrade()) {
            player.message(UtilChatColour.translateColourCodes('&', UtilPlaceholder.replaceIdentifiers(player,
                    WonderTradeForge.getInstance().getLocale().getCooldownMessage())));
            return;
        }

        PokemonSelectUI.openUI(WonderTradeForge.getInstance().getPlayerManager().getPlayer(sender));
    }
}
