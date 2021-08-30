package com.envyful.wonder.trade.forge.data.event;

import com.envyful.api.player.EnvyPlayer;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class WonderTradeEvent extends Event {

    private final EnvyPlayer<EntityPlayerMP> player;

    private Pokemon given;
    private Pokemon received;

    public WonderTradeEvent(EnvyPlayer<EntityPlayerMP> player, Pokemon given, Pokemon received) {
        this.player = player;
        this.given = given;
        this.received = received;
    }

    public EnvyPlayer<EntityPlayerMP> getPlayer() {
        return this.player;
    }

    public Pokemon getGiven() {
        return this.given;
    }

    public void setGiven(Pokemon given) {
        this.given = given;
    }

    public Pokemon getReceived() {
        return this.received;
    }

    public void setReceived(Pokemon received) {
        this.received = received;
    }
}
