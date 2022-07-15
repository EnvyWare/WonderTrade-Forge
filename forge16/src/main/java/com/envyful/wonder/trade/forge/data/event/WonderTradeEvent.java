package com.envyful.wonder.trade.forge.data.event;

import com.envyful.api.player.EnvyPlayer;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class WonderTradeEvent extends Event {

    private final EnvyPlayer<ServerPlayerEntity> player;

    private Pokemon given;
    private Pokemon received;

    public WonderTradeEvent(EnvyPlayer<ServerPlayerEntity> player, Pokemon given, Pokemon received) {
        this.player = player;
        this.given = given;
        this.received = received;
    }

    public EnvyPlayer<ServerPlayerEntity> getPlayer() {
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
