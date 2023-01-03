package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.player.SaveMode;
import com.envyful.wonder.trade.forge.data.event.WonderTradeEvent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@ConfigPath("config/WonderTradeForge/config.yml")
@ConfigSerializable
public class WonderTradeConfig extends AbstractYamlConfig {

    private SaveMode saveMode = SaveMode.JSON;
    private BroadcastSettings broadcastSettings = new BroadcastSettings();
    private GenerationSettings defaultGeneratorSettings = new GenerationSettings();
    private SQLDatabaseDetails databaseDetails = new SQLDatabaseDetails("WonderTrade", "0.0.0.0",
            3306, "admin", "password", "WonderTrade");

    private Map<String, WebHookTriggers> webHooks = ImmutableMap.of(
            "one", new WebHookTriggers("config/WonderTradeForge/leg_web_hook.json", "legendary")
    );

    private int cooldownSeconds = 3600;
    private int minRequiredLevel = 30;
    private int numberInPool = 30;
    private boolean persistentPool = true;
    private boolean disableUI = false;

    public WonderTradeConfig() {}

    public SaveMode getSaveMode() {
        return this.saveMode;
    }

    public boolean isPersistentPool() {
        return this.persistentPool;
    }

    public GenerationSettings getDefaultGeneratorSettings() {
        return this.defaultGeneratorSettings;
    }

    public int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public int getMinRequiredLevel() {
        return this.minRequiredLevel;
    }

    public int getNumberInPool() {
        return this.numberInPool;
    }

    public SQLDatabaseDetails getDatabaseDetails() {
        return this.databaseDetails;
    }

    public BroadcastSettings getBroadcastSettings() {
        return this.broadcastSettings;
    }

    public boolean isDisableUI() {
        return this.disableUI;
    }

    public List<WebHookTriggers> getTriggers() {
        return Lists.newArrayList(this.webHooks.values());
    }

    @ConfigSerializable
    public static class GenerationSettings {

        private List<String> blockedSpecs = Lists.newArrayList("hoopa");
        private transient List<PokemonSpecification> blockSpecsCache = null;
        private boolean allowLegends = true;
        private boolean allowUltraBeasts = true;
        private double shinyChance = 0.05;

        public GenerationSettings() {
        }

        public Pokemon build() {
            Pokemon randomSpecies = this.getRandomPokemon();

            while (this.isBlockedSpec(randomSpecies)) {
                randomSpecies = this.getRandomPokemon();
            }

            return randomSpecies;
        }

        private Pokemon getRandomPokemon() {
            return PokemonBuilder.builder()
                    .randomSpecies(!this.allowLegends, !this.allowLegends, !this.allowUltraBeasts)
                    .shiny(ThreadLocalRandom.current().nextDouble() < this.shinyChance)
                    .level(UtilRandom.randomInteger(1, 50))
                    .ivs(IVStore.createRandomNewIVs().getArray())
                    .build();
        }

        private boolean isBlockedSpec(Pokemon pokemon) {
            if (this.blockSpecsCache == null) {
                List<PokemonSpecification> blockSpecsCache = Lists.newArrayList();

                for (String blockedSpec : this.blockedSpecs) {
                    blockSpecsCache.add(PokemonSpecificationProxy.create(blockedSpec));
                }

                this.blockSpecsCache = blockSpecsCache;
            }

            for (PokemonSpecification pokemonSpec : this.blockSpecsCache) {
                if (pokemonSpec.matches(pokemon)) {
                    return true;
                }
            }

            return false;
        }
    }

    @ConfigSerializable
    public static class BroadcastSettings {

        private boolean alwaysBroadcast = true;
        private boolean broadcastLegends = true;
        private boolean broadcastUltraBeasts = true;
        private boolean broadcastShinies = true;

        public boolean isAlwaysBroadcast() {
            return this.alwaysBroadcast;
        }

        public boolean isBroadcastLegends() {
            return this.broadcastLegends;
        }

        public boolean isBroadcastUltraBeasts() {
            return this.broadcastUltraBeasts;
        }

        public boolean isBroadcastShinies() {
            return this.broadcastShinies;
        }
    }

    @ConfigSerializable
    public static class WebHookTriggers {

        private String webHookPath;
        private String triggerSpec;
        private transient PokemonSpecification spec = null;
        private transient String webHookJson = null;

        public WebHookTriggers(String webHookPath, String triggerSpec) {
            this.webHookPath = webHookPath;
            this.triggerSpec = triggerSpec;
        }

        public WebHookTriggers() {
        }

        public String getWebHookPath() {
            return this.webHookPath;
        }

        public DiscordWebHook getWebHook(WonderTradeEvent event) {
            if (this.webHookJson == null) {
                try {
                    this.webHookJson = String.join(System.lineSeparator(), Files.readAllLines(Paths.get(this.webHookPath), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (this.webHookJson == null) {
                return null;
            }

            return DiscordWebHook.fromJson(this.webHookJson.replace("%received%", event.getReceived().getDisplayName())
                    .replace("%given%", event.getGiven().getDisplayName())
                    .replace("%player%", event.getPlayer().getName()));
        }

        public PokemonSpecification getSpec() {
            if (this.spec == null) {
                this.spec = PokemonSpecificationProxy.create(this.triggerSpec);
            }

            return this.spec;
        }
    }
}
