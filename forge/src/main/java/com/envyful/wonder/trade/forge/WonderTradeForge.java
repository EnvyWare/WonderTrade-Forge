package com.envyful.wonder.trade.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.wonder.trade.forge.command.WonderTradeCommand;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import com.envyful.wonder.trade.forge.config.WonderTradeLocale;
import com.envyful.wonder.trade.forge.config.WonderTradeQueries;
import com.envyful.wonder.trade.forge.data.WonderTradeAttribute;
import com.envyful.wonder.trade.forge.data.WonderTradeManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Mod(
        modid = "wondertrade",
        name = "WonderTrade Forge",
        version = WonderTradeForge.VERSION,
        acceptableRemoteVersions = "*",
        updateJSON = "https://ogn.pixelmonmod.com/update/sm-wt/update.json"
)
public class WonderTradeForge {
    public static final String VERSION = "7.0.0";

    private static WonderTradeForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private Database database;
    private WonderTradeConfig config;
    private WonderTradeLocale locale;
    private WonderTradeManager manager;

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        instance = this;

        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

        this.playerManager.registerAttribute(this, WonderTradeAttribute.class);
        this.loadConfig();

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

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(WonderTradeConfig.class);
            this.locale = YamlConfigFactory.getInstance(WonderTradeLocale.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.commandFactory.registerCommand(event.getServer(), new WonderTradeCommand());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        UtilConcurrency.runAsync(() -> this.manager = new WonderTradeManager(this));
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStoppingEvent event) {
        if (this.config.isPersistentPool()) {
            this.manager.saveFile();
        }
    }

    public static WonderTradeForge getInstance() {
        return instance;
    }

    public WonderTradeConfig getConfig() {
        return this.config;
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

    public WonderTradeLocale getLocale() {
        return this.locale;
    }
}
