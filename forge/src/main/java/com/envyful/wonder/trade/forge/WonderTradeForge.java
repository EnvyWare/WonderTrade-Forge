package com.envyful.wonder.trade.forge;

import com.envyful.api.forge.command.ForgeCommandFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

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

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        instance = this;
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    public static WonderTradeForge getInstance() {
        return instance;
    }
}
