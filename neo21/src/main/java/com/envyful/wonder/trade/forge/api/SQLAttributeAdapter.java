package com.envyful.wonder.trade.forge.api;

import com.envyful.api.database.sql.SqlType;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.wonder.trade.forge.WonderTradeForge;

import java.util.concurrent.CompletableFuture;

public class SQLAttributeAdapter implements AttributeAdapter<WonderTradeAttribute> {

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `wonder_trade_users`(" +
            "id         INT         UNSIGNED        NOT NULL        AUTO_INCREMENT, " +
            "uuid       VARCHAR(64) NOT NULL, " +
            "last_trade LONG        NOT NULL, " +
            "UNIQUE(uuid), " +
            "PRIMARY KEY(id));";

    public static final String LOAD_USER = "SELECT last_trade FROM `wonder_trade_users` WHERE uuid = ?;";

    public static final String ADD_AND_UPDATE_USER = "INSERT INTO `wonder_trade_users`(uuid, last_trade) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "last_trade = VALUES(`last_trade`);";

    @Override
    public CompletableFuture<Void> save(WonderTradeAttribute wonderTradeAttribute) {
        return WonderTradeForge.getInstance().getDatabase()
                .update(ADD_AND_UPDATE_USER)
                .data(
                        SqlType.text(wonderTradeAttribute.getUniqueId().toString()),
                        SqlType.bigInt(wonderTradeAttribute.lastTrade)
                )
                .executeAsync().thenApply(integer -> null);
    }

    @Override
    public void load(WonderTradeAttribute attribute) {
        WonderTradeForge.getInstance().getDatabase()
                .query(LOAD_USER)
                .data(SqlType.text(attribute.getUniqueId().toString()))
                .converter(resultSet -> {
                    attribute.lastTrade = resultSet.getLong("last_trade");
                    return null;
                })
                .executeAsyncWithConverter();
    }

    @Override
    public CompletableFuture<Void> delete(WonderTradeAttribute wonderTradeAttribute) {
        return null; //TODO:
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        return null; //TODO:
    }

    @Override
    public void initialize() {
        WonderTradeForge.getInstance().getDatabase().update(CREATE_TABLE).executeAsync();
    }
}
