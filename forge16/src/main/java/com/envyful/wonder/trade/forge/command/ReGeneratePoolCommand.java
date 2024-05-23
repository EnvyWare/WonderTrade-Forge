package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;

@Command(
        value = {
                "regenpool",
                "rp"
        }
)
@Permissible("wonder.trade.command.regenerate.pool")
public class ReGeneratePoolCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        WonderTradeForge.getInstance().getManager().generatePool();
        sender.sendMessage(UtilChatColour.colour("&e&l(!) &eRegenerated the pool"), Util.NIL_UUID);
    }
}
