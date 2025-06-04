package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.ui.ListViewUI;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = {
                "list",
                "l"
        }
)
@Permissible("wonder.trade.forge.command.list")
public class ListCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer player, String[] args) {
        ListViewUI.openUI(WonderTradeForge.getInstance().getPlayerManager().getPlayer(player));
    }
}
