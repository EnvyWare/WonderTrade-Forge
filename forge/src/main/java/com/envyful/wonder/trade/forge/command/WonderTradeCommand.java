package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.ui.PokemonSelectUI;
import net.minecraft.command.ICommandSender;
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
        PokemonSelectUI.openUI(WonderTradeForge.getInstance().getPlayerManager().getPlayer(sender));
    }
}
