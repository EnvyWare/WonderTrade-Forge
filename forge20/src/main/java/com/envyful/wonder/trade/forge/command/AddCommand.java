package com.envyful.wonder.trade.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.commands.CommandSource;

@Command(
        value = "add",
        description = "Adds a pokemon to the pool"
)
@Child
@Permissible("wonder.trade.forge.command.add")
public class AddCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender, String[] args) {
        String specs = String.join(" ", args);

        if (specs.isEmpty()) {
            sender.sendSystemMessage(UtilChatColour.colour("&c&l(!) &cNo specs were provided for the pokemon to be added"));
            return;
        }

        Pokemon pokemon = PokemonSpecificationProxy.create(specs).create();
        WonderTradeForge.getInstance().getManager().getTradePool().add(pokemon);
        sender.sendSystemMessage(UtilChatColour.colour("&e&l(!) &eSuccessfully added " + specs + " as " + pokemon.getLocalizedName() + " to the pool"));
    }
}
