package com.envyful.wonder.trade.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Mod("wondertrade")
public class WonderTradeForge {

    private static WonderTradeForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private Database database;
    private WonderTradeConfig config;
    private WonderTradeLocale locale;
    private WonderTradeGraphics graphics;
    private WonderTradeManager manager;

    public WonderTradeForge() {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerAboutToStartEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

        this.loadConfig();

        if (this.config.getSaveMode() == SaveMode.JSON) {
            this.playerManager.setSaveManager(new JsonSaveManager<>());
        }

        this.playerManager.registerAttribute(this, WonderTradeAttribute.class);
        MinecraftForge.EVENT_BUS.register(new WonderTradeListener());

        if (this.config.getSaveMode() == SaveMode.MYSQL) {
            UtilConcurrency.runAsync(() -> {
                this.database = new SimpleHikariDatabase(this.config.getDatabaseDetails());

                try (Connection connection = this.database.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(WonderTradeQueries.CREATE_TABLE)) {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(WonderTradeConfig.class);
            this.locale = YamlConfigFactory.getInstance(WonderTradeLocale.class);
            this.graphics = YamlConfigFactory.getInstance(WonderTradeGraphics.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), new WonderTradeCommand());
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
}
