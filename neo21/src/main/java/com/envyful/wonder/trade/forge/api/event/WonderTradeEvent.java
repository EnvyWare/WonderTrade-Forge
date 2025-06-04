package com.envyful.wonder.trade.forge.api.event;

import com.envyful.api.player.EnvyPlayer;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class WonderTradeEvent extends Event implements ICancellableEvent {

    private final EnvyPlayer<ServerPlayer> player;

    private Pokemon given;
    private Pokemon received;

    public WonderTradeEvent(EnvyPlayer<ServerPlayer> player, Pokemon given, Pokemon received) {
        this.player = player;
        this.given = given;
        this.received = received;
    }

    public EnvyPlayer<ServerPlayer> getPlayer() {
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
