package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.player.SaveMode;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.data.event.WonderTradeEvent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@ConfigPath("config/WonderTradeForge/config.yml")
@ConfigSerializable
public class WonderTradeConfig extends AbstractYamlConfig {

    @Comment("""
            The setting to tell the mod how to save the player data.
            The options are:
            - JSON
            - MYSQL
            """)
    private SaveMode saveMode = SaveMode.JSON;

    private BroadcastSettings broadcastSettings = new BroadcastSettings();
    private GenerationSettings defaultGeneratorSettings = new GenerationSettings();


    @Comment("""
            The MySQL database details.
            This will only be used if the save mode is set to MYSQL
            
            NOTE: DO NOT SHARE THESE WITH ANYONE YOU DO NOT TRUST
            """)
    private SQLDatabaseDetails databaseDetails = new SQLDatabaseDetails("WonderTrade", "0.0.0.0",
            3306, "admin", "password", "WonderTrade");

    private Map<String, WebHookTriggers> webHooks = ImmutableMap.of(
            "one", new WebHookTriggers("config/WonderTradeForge/leg_web_hook.json", "legendary")
    );

    @Comment("""
            The cooldown, in seconds, between when people can submit pokemon to the pool
            """)
    private int cooldownSeconds = 3600;

    @Comment("""
            The minimum level required for entry
            """)
    private int minRequiredLevel = 30;

    @Comment("""
            The number of Pokemon that appear in the wonder trade pool
            """)
    private int numberInPool = 30;

    @Comment("""
            If the pool resets after server restarts
            """)
    private boolean persistentPool = true;

    @Comment("""
            Setting this to 'true' means that the GUI will not open
            """)
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

        @Comment("""
                Pokemon that won't show up in the wonder trade pool
                """)
        private List<String> blockedSpecs = Lists.newArrayList("hoopa");
        private transient List<PokemonSpecification> blockSpecsCache = null;

        @Comment("""
                If legendary pokemon can show up in the wonder trade pool
                """)
        private boolean allowLegends = true;

        @Comment("""
                If ultra beast pokemon can show up in the wonder trade pool
                """)
        private boolean allowUltraBeasts = true;

        @Comment("""
                The chance of a shiny pokemon appearing in the wonder trade pool
                """)
        private double shinyChance = 0.05;

        public GenerationSettings() {
        }

        public Pokemon build() {
            Pokemon randomSpecies = this.getRandomPokemon();

            while (this.isBlockedSpec(randomSpecies)) {
                randomSpecies = this.getRandomPokemon();
            }

            randomSpecies.setOriginalTrainer(UUID.randomUUID(), "WonderTrade");

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
                    blockSpecsCache.add(PokemonSpecificationProxy.create(blockedSpec).get());
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

        @Comment("""
                If it should broadcast every pokemon added
                """)
        private boolean alwaysBroadcast = true;

        @Comment("""
                If it should broadcast legendary pokemon that are added
                """)
        private boolean broadcastLegends = true;

        @Comment("""
                if it should broadcast ultra best pokemon that are added
                """)
        private boolean broadcastUltraBeasts = true;

        @Comment("""
                if it should broadcast shiny pokemon that are added
                """)
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

        @Comment("""
                The path to the webhook JSON file
                """)
        private String webHookPath;

        @Comment("""
                The spec that triggers the webhook to fire
                """)
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

            String modifiedJson = this.handlePlaceholders(this.webHookJson, "given_", event.getGiven());
            modifiedJson = this.handlePlaceholders(modifiedJson, "", event.getGiven());
            modifiedJson = this.handlePlaceholders(modifiedJson, "received_", event.getReceived());

            if (WonderTradeForge.isPlaceholderAPIEnabled()) {
                modifiedJson = this.useFPAPI(event.getPlayer().getParent(), modifiedJson);
            }

            return DiscordWebHook.fromJson(modifiedJson
                    .replace("%received%", event.getReceived().getDisplayName())
                    .replace("%given%", event.getGiven().getDisplayName())
                    .replace("%player%", event.getPlayer().getName()));
        }

        private String handlePlaceholders(String json, String speciesPrefix, Pokemon pokemon) {
            return json.replace("%" + speciesPrefix + "species%", pokemon.getSpecies().getLocalizedName())
                    .replace("%" + speciesPrefix + "species_lower%", pokemon.getSpecies().getLocalizedName().toLowerCase(Locale.ROOT))
                    .replace("%" + speciesPrefix + "evs%", getFormattedIntegerArray(pokemon.getEVs().getArray()))
                    .replace("%" + speciesPrefix + "ivs%", getFormattedIntegerArray(pokemon.getIVs().getArray()))
                    .replace("%" + speciesPrefix + "growth%", pokemon.getGrowth().getLocalizedName())
                    .replace("%" + speciesPrefix + "nature%", pokemon.getNature().getLocalizedName());
        }

        private String getFormattedIntegerArray(int[] array) {
            StringJoiner joiner = new StringJoiner(", ");

            for (int i : array) {
                joiner.add(String.valueOf(i));
            }

            return joiner.toString();
        }

        private String useFPAPI(ServerPlayer player, String json) {
            return UtilPlaceholder.replaceIdentifiers(player, json);
        }

        public PokemonSpecification getSpec() {
            if (this.spec == null) {
                this.spec = PokemonSpecificationProxy.create(this.triggerSpec).get();
            }

            return this.spec;
        }
    }
}
