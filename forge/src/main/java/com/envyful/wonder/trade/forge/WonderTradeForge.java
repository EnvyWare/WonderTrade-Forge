package com.envyful.wonder.trade.forge;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.wonder.trade.forge.config.WonderTradeConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;

@Mod(
        modid = "wondertrade",
        name = "WonderTrade Forge",
        version = WonderTradeForge.VERSION,
        acceptableRemoteVersions = "*"
)
public class WonderTradeForge {
    public static final String VERSION = "0.1.0";

    private static WonderTradeForge instance;

    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private WonderTradeConfig config;

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        instance = this;

        this.loadConfig();
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(WonderTradeConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    public static WonderTradeForge getInstance() {
        return instance;
    }
}
