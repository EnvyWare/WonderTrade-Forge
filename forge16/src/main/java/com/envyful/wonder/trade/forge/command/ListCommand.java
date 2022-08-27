package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.ui.ListViewUI;
import net.minecraft.entity.player.ServerPlayerEntity;

@Command(
        value = "list",
        description = "Shows the user a list of Pokemon in the WT",
        aliases = {
                "l"
        }
)
@Permissible("wonder.trade.forge.command.list")
@Child
public class ListCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayerEntity player, String[] args) {
        ListViewUI.openUI(WonderTradeForge.getInstance().getPlayerManager().getPlayer(player));
    }
}
