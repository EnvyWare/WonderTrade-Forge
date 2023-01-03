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
            "&e%forge_name%&7 added a &e%is_shiny%%is_ultra_beast%%is_legend%&b%pokemon%&7 to the &b&lWonderTrade",
            " "
    );

    private String shinyReplacement = "Shiny ";
    private String ultraBeastReplacement = "UltraBeast ";
    private String legendReplacement = "Legend ";
    private String tradeSuccessful = "&e&l(!) &eWonderTrade was successful! Check your party to see what you got &7(&b%species%&7)";
    private String cooldownMessage = "&c&l(!) &cYou cannot use the WonderTrade yet. You're still on cooldown";
    private String minimumPartySize = "&c&l(!) &cYou must have more than 1 pokemon in your party!";
    private String untradeablePokemon = "&c&l(!) &cThat pokemon is untradeable!";
    private String confirmSell = "&e&l(!) &ePlease type the command again to confirm you wish to sell the slot %slot%";

    private String removedPokemon = "&e&l(!) &eRemoved pokemon from the WonderTrade pool and replaced it with a new random one";
    private String commandError = "&c&l(!) &cIncorrect slot number! /wt <slot>";
    private String levelTooLow = "&c&l(!) &cLevel too low";

    private List<String> openingUI = Lists.newArrayList(
            "&e&l(!) &eOpening wonder trade"
    );

    public WonderTradeLocale() {
        super();
    }

    public String getMinimumPartySize() {
        return this.minimumPartySize;
    }

    public String getCooldownMessage() {
        return this.cooldownMessage;
    }

    public String getShinyReplacement() {
        return this.shinyReplacement;
    }

    public String getUltraBeastReplacement() {
        return this.ultraBeastReplacement;
    }

    public String getLegendReplacement() {
        return this.legendReplacement;
    }

    public String getTradeSuccessful() {
        return this.tradeSuccessful;
    }

    public List<String> getPokemonBroadcast() {
        return this.pokemonBroadcast;
    }

    public String getRemovedPokemon() {
        return this.removedPokemon;
    }

    public String getCommandError() {
        return this.commandError;
    }

    public String getConfirmSell() {
        return this.confirmSell;
    }

    public String getUntradeablePokemon() {
        return this.untradeablePokemon;
    }

    public String getLevelTooLow() {
        return this.levelTooLow;
    }

    public List<String> getOpeningUI() {
        return this.openingUI;
    }
}
