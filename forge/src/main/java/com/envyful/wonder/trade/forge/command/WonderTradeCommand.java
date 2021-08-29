package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import net.minecraft.command.ICommandSender;

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
    public void onCommand(@Sender ICommandSender sender, String[] args) {
        //TODO: send message
    }
}
