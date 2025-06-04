package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.type.UtilParse;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.api.WonderTradeAttribute;
import com.envyful.wonder.trade.forge.ui.PokemonSelectUI;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = {
                "wondertrade",
                "wt"
        }
)
@SubCommands({ReloadCommand.class, ResetCooldownCommand.class, ReGeneratePoolCommand.class, AdminCommand.class, ListCommand.class, AddCommand.class})
public class WonderTradeCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer sender, String[] args) {
        ForgeEnvyPlayer player = WonderTradeForge.getInstance().getPlayerManager().getPlayer(sender);
        WonderTradeAttribute attribute = player.getAttributeNow(WonderTradeAttribute.class);

        if (attribute == null) {
            return;
        }

        if (!attribute.canTrade()) {
            player.message(UtilChatColour.colour(
                    UtilPlaceholder.replaceIdentifiers(
                            sender,
                            WonderTradeForge.getLocale().getCooldownMessage()
                                    .replace("%cooldown%", attribute.getCooldownFormatted())
                    )
            ));
            return;
        }

        if (args.length == 0 && !WonderTradeForge.getConfig().isDisableUI()) {
            this.openUI(player);
            return;
        }

        if (args.length != 1) {
            player.message(UtilChatColour.colour(WonderTradeForge.getLocale().getCommandError()));
            return;
        }

        PlayerPartyStorage party = StorageProxy.getPartyNow(sender);

        if (party.getTeam().size() <= 1) {
            player.message(UtilChatColour.colour(UtilPlaceholder.replaceIdentifiers(sender, WonderTradeForge.getLocale().getMinimumPartySize())));
            return;
        }

        int slot = UtilParse.parseInt(args[0]).orElse(-1);

        if (slot < 1 || slot > 6) {
            player.message(UtilChatColour.colour(WonderTradeForge.getLocale().getCommandError()));
            return;
        }

        if (!attribute.isConfirming(slot)) {
            player.message(UtilChatColour.colour(
                    WonderTradeForge.getLocale().getConfirmSell()
                            .replace("%slot%", slot + "")
            ));
            attribute.setConfirm(slot);
            return;
        }

        Pokemon pokemon = party.getAll()[slot - 1];

        if (pokemon.isUntradeable()) {
            player.message(UtilChatColour.colour(
                    WonderTradeForge.getLocale().getUntradeablePokemon()
                            .replace("%slot%", slot + "")
            ));
            return;
        }

        if (pokemon.getPokemonLevel() <= WonderTradeForge.getConfig().getMinRequiredLevel()) {
            player.message(UtilChatColour.colour(WonderTradeForge.getLocale().getLevelTooLow()));
            return;
        }

        StorageProxy.getPartyNow(sender).retrieveAll("WONDER_TRADE");
        WonderTradeForge.getInstance().getManager().replaceRandomPokemon(player, pokemon);
        attribute.setConfirm(-1);
    }

    private void openUI(ForgeEnvyPlayer player) {
        ServerPlayer sender = player.getParent();

        if (StorageProxy.getPartyNow(sender).getTeam().size() <= 1) {
            player.message(UtilChatColour.colour(UtilPlaceholder.replaceIdentifiers(sender,
                    WonderTradeForge.getLocale().getMinimumPartySize())));
            return;
        }

        StorageProxy.getPartyNow(sender).retrieveAll("WONDER_TRADE");
        PokemonSelectUI.openUI(WonderTradeForge.getInstance().getPlayerManager().getPlayer(sender));
        player.message(WonderTradeForge.getLocale().getOpeningUI());
    }
}
