package com.envyful.wonder.trade.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigPath("config/WonderTradeForge/locale.yml")
@ConfigSerializable
public class WonderTradeLocale extends AbstractYamlConfig {

    public WonderTradeLocale() {
        super();
    }
}
