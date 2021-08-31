package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.data.WonderTradeAttribute;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "resetcooldown",
        description = "Reset cooldown command",
        aliases = {
                "rc"
        }
)
@Permissible("wonder.trade.command.reset.cooldown")
@Child
public class ResetCooldownCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    "&c&l(!) &cNo player specified.")));
            return;
        }

        EnvyPlayer<EntityPlayerMP> target = WonderTradeForge.getInstance().getPlayerManager().getOnlinePlayer(args[0]);

        if (target == null) {
            sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    "&c&l(!) &cCannot find that player")));
            return;
        }

        WonderTradeAttribute attribute = target.getAttribute(WonderTradeForge.class);

        if (attribute == null) {
            sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    "&c&l(!) &cPlease wait! That player has not loaded yet")));
            return;
        }

        attribute.resetCooldown();
        sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                "&e&l(!) &eSuccessfully reset their cooldown")));

    }
}
