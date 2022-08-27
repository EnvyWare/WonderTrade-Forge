package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
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
    public void onCommand(ServerPlayerEntity player, String[] args) {

    }
}
