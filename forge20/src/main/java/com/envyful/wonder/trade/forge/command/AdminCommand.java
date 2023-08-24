package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.type.UtilParse;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.ui.AdminViewUI;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = "admin",
        description = "Views the UI as an admin",
        aliases = {
                "view",
                "adminview",
                "av"
        }
)
@Child
@Permissible("wonder.trade.forge.command.admin")
public class AdminCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer player, String[] args) {
        int page = 0;

        if (args.length >= 1) {
            page = UtilParse.parseInt(args[0]).orElse(0);
        }

        AdminViewUI.openUI(WonderTradeForge.getInstance().getPlayerManager().getPlayer(player), page);
    }
}
