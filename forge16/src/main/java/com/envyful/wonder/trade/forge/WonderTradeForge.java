package com.envyful.wonder.trade.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.database.sql.UtilSql;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.platform.ForgePlatformHandler;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.SaveMode;
import com.envyful.api.player.save.impl.JsonSaveManager;
import com.envyful.wonder.trade.forge.command.WonderTradeCommand;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.envyful.wonder.trade.forge.config.WonderTradeGraphics;
import com.envyful.wonder.trade.forge.config.WonderTradeLocale;
import com.envyful.wonder.trade.forge.config.WonderTradeQueries;
import com.envyful.wonder.trade.forge.data.WonderTradeAttribute;
import com.envyful.wonder.trade.forge.data.WonderTradeManager;
import com.envyful.wonder.trade.forge.listener.WonderTradeListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("wondertrade")
public class WonderTradeForge {

    private static final Logger LOGGER = LogManager.getLogger("WonderTradeForge");

    private static WonderTradeForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private Database database;
    private WonderTradeConfig config;
    private WonderTradeLocale locale;
    private WonderTradeGraphics graphics;
    private WonderTradeManager manager;
    private boolean placeholderAPI = false;

    public WonderTradeForge() {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);

        PlatformProxy.setHandler(ForgePlatformHandler.getInstance());
        PlatformProxy.setPlayerManager(this.playerManager);

        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        GuiFactory.setPlayerManager(this.playerManager);

        UtilLogger.setLogger(LOGGER);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerAboutToStartEvent event) {
        this.loadConfig();

        if (this.config.getSaveMode() == SaveMode.JSON) {
            this.playerManager.setSaveManager(new JsonSaveManager<>(this.playerManager));
        }

        this.playerManager.registerAttribute(WonderTradeAttribute.class, WonderTradeAttribute::new);
        MinecraftForge.EVENT_BUS.register(new WonderTradeListener());

        this.placeholderAPI = this.hasPlaceholderSupport();

        if (this.config.getSaveMode() == SaveMode.MYSQL) {
            this.database = new SimpleHikariDatabase(this.config.getDatabaseDetails());

            UtilSql.update(this.getDatabase())
                            .query(WonderTradeQueries.CREATE_TABLE)
                                    .executeAsync();
        }
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(WonderTradeConfig.class);
            this.locale = YamlConfigFactory.getInstance(WonderTradeLocale.class);
            this.graphics = YamlConfigFactory.getInstance(WonderTradeGraphics.class);
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
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
    public void onServerStarted(FMLServerStartedEvent event) {
        UtilConcurrency.runAsync(() -> this.manager = new WonderTradeManager(this));
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStoppingEvent event) {
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

    public static Logger getLogger() {
        return LOGGER;
    }
}
