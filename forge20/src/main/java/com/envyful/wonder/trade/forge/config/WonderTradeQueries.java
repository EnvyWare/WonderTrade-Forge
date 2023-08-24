package com.envyful.wonder.trade.forge.config;

public class WonderTradeQueries {

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

}
