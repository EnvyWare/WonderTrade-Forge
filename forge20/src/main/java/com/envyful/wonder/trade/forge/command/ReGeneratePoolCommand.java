package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import net.minecraft.commands.CommandSource;

@Command(
        value = "regenpool",
        description = "Regenerate pool command",
        aliases = {
                "rp"
        }
)
@Permissible("wonder.trade.command.regenerate.pool")
@Child
public class ReGeneratePoolCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender, String[] args) {
        WonderTradeForge.getInstance().getManager().generatePool();
        sender.sendSystemMessage(UtilChatColour.colour("&e&l(!) &eRegenerated the pool"));
    }
}
