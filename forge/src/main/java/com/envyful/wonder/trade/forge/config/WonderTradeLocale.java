package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/WonderTradeForge/locale.yml")
@ConfigSerializable
public class WonderTradeLocale extends AbstractYamlConfig {

    private List<String> pokemonBroadcast = Lists.newArrayList(
            " ",
            "&e%forge_name%&7 added a &e%is_shiny%%is_ultra_beasts%%is_legend%&7 to the &b&lWonderTrade",
            " "
    );

    private String tradeSuccessful = "&e&l(!) &eWonderTrade was successful! Check your party to see what you got";

    public WonderTradeLocale() {
        super();
    }

    public String getTradeSuccessful() {
        return this.tradeSuccessful;
    }

    public List<String> getPokemonBroadcast() {
        return this.pokemonBroadcast;
    }
}
