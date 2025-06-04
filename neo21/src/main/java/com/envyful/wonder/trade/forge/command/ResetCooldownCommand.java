package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.api.WonderTradeAttribute;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = {
                "resetcooldown",
                "rc"
        }
)
@Permissible("wonder.trade.command.reset.cooldown")
public class ResetCooldownCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender, String[] args) {
        if (args.length != 1) {
            sender.sendSystemMessage(UtilChatColour.colour("&c&l(!) &cNo player specified."));
            return;
        }

        EnvyPlayer<ServerPlayer> target = WonderTradeForge.getInstance().getPlayerManager().getOnlinePlayerCaseInsensitive(args[0]);

        if (target == null) {
            sender.sendSystemMessage(UtilChatColour.colour("&c&l(!) &cCannot find that player"));
            return;
        }

        WonderTradeAttribute attribute = target.getAttributeNow(WonderTradeAttribute.class);

        if (attribute == null) {
            sender.sendSystemMessage(UtilChatColour.colour("&c&l(!) &cPlease wait! That player has not loaded yet"));
            return;
        }

        attribute.resetCooldown();
        sender.sendSystemMessage(UtilChatColour.colour("&e&l(!) &eSuccessfully reset their cooldown"));

    }
}
