package com.envyful.wonder.trade.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.config.database.DatabaseDetailsRegistry;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.neoforge.chat.ComponentTextFormatter;
import com.envyful.api.neoforge.command.ForgeCommandFactory;
import com.envyful.api.neoforge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.neoforge.gui.factory.ForgeGuiFactory;
import com.envyful.api.neoforge.platform.ForgePlatformHandler;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.neoforge.player.ForgePlayerManager;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.Attribute;
import com.envyful.api.sqlite.config.SQLiteDatabaseDetailsConfig;
import com.envyful.wonder.trade.forge.api.SQLAttributeAdapter;
import com.envyful.wonder.trade.forge.api.SQLiteAttributeAdapter;
import com.envyful.wonder.trade.forge.api.WonderTradeAttribute;
import com.envyful.wonder.trade.forge.api.WonderTradeManager;
import com.envyful.wonder.trade.forge.command.WonderTradeCommand;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.envyful.wonder.trade.forge.config.WonderTradeGraphics;
import com.envyful.wonder.trade.forge.config.WonderTradeLocale;
import com.envyful.wonder.trade.forge.listener.WonderTradeListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("wondertrade")
public class WonderTradeForge {

    private static final Logger LOGGER = LogManager.getLogger("wondertrade");

    private static WonderTradeForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, playerManager);

    private Database database;
    private WonderTradeConfig config;
    private WonderTradeLocale locale;
    private WonderTradeGraphics graphics;
    private WonderTradeManager manager;
    private boolean placeholderAPI = false;

    public WonderTradeForge() {
        UtilLogger.setLogger(LOGGER);

        SQLiteDatabaseDetailsConfig.register();
        PlatformProxy.setHandler(ForgePlatformHandler.getInstance());
        PlatformProxy.setPlayerManager(this.playerManager);
        PlatformProxy.setTextFormatter(ComponentTextFormatter.getInstance());
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

        instance = this;
        NeoForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event) {
        this.loadConfig();

        this.playerManager.registerAttribute(Attribute.builder(WonderTradeAttribute.class, ForgeEnvyPlayer.class)
                .constructor(WonderTradeAttribute::new)
                .registerAdapter(SQLDatabaseDetails.ID, new SQLAttributeAdapter())
                .registerAdapter(SQLiteDatabaseDetailsConfig.ID, new SQLiteAttributeAdapter())
        );

        this.playerManager.setGlobalSaveMode(DatabaseDetailsRegistry.getRegistry().getKey((Class<DatabaseDetailsConfig>) this.getConfig().getDatabaseDetails().getClass()));

        NeoForge.EVENT_BUS.register(new WonderTradeListener());

        this.placeholderAPI = this.hasPlaceholderSupport();
        database = this.config.getDatabaseDetails().createDatabase();
        this.playerManager.getAdapter(WonderTradeAttribute.class).initialize();
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(WonderTradeConfig.class);
            this.locale = YamlConfigFactory.getInstance(WonderTradeLocale.class);
            this.graphics = YamlConfigFactory.getInstance(WonderTradeGraphics.class);
        } catch (IOException e) {
            LOGGER.error("Failed to load WonderTrade config files", e);
        }
    }

    protected boolean hasPlaceholderSupport() {
        try {
            Class.forName("com.envyful.papi.forge.ForgePlaceholderAPI");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new WonderTradeCommand()));
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        UtilConcurrency.runAsync(() -> this.manager = new WonderTradeManager(this));
    }

    @SubscribeEvent
    public void onServerStarted(ServerStoppingEvent event) {
        if (this.config.isPersistentPool()) {
            this.manager.saveFile();
        }
    }

    public static WonderTradeForge getInstance() {
        return instance;
    }

    public static WonderTradeConfig getConfig() {
        return instance.config;
    }

    public Database getDatabase() {
        return this.database;
    }

    public ForgePlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public WonderTradeManager getManager() {
        return this.manager;
    }

    public static WonderTradeLocale getLocale() {
        return instance.locale;
    }

    public static WonderTradeGraphics getGraphics() {
        return instance.graphics;
    }

    public static boolean isPlaceholderAPIEnabled() {
        return instance.placeholderAPI;
    }
}
