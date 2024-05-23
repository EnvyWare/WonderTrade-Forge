package com.envyful.wonder.trade.forge.data;

import com.envyful.api.database.sql.SqlType;
import com.envyful.api.database.sql.UtilSql;
import com.envyful.api.forge.player.attribute.ManagedForgeAttribute;
import com.envyful.api.player.save.attribute.DataDirectory;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeQueries;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@DataDirectory("config/players/WonderTrade/")
public class WonderTradeAttribute extends ManagedForgeAttribute<WonderTradeForge> {

    private static final long SECONDS_PER_MINUTE = 60;
    private static final long MINUTES_PER_HOUR = 60;
    private static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    private long lastTrade = -1;
    private int selected = -1;
    private int confirmedSlot = -1;

    public WonderTradeAttribute() {
        super(WonderTradeForge.getInstance());
    }

    public boolean canTrade() {
        if (this.lastTrade == -1) {
            return true;
        }

        return (System.currentTimeMillis() - this.lastTrade) >
                TimeUnit.SECONDS.toMillis(WonderTradeForge.getConfig().getCooldownSeconds());
    }

    public void updateLastTrade() {
        this.lastTrade = System.currentTimeMillis();
    }

    public void resetCooldown() {
        this.lastTrade = -1;
    }

    public int getSelected() {
        return this.selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void setConfirm(int confirmedSlot) {
        this.confirmedSlot = confirmedSlot;
    }

    public boolean isConfirming(int slot) {
        return this.confirmedSlot == slot;
    }

    @Override
    public void load() {
        if (this.manager.getDatabase() == null) {
            return;
        }

        UtilSql.query(this.manager.getDatabase())
                .query(WonderTradeQueries.LOAD_USER)
                .data(SqlType.text(this.id.toString()))
                .converter(resultSet -> {
                    this.lastTrade = resultSet.getLong("last_trade");
                    return null;
                }).executeWithConverter();
    }

    @Override
    public void save() {
        if (this.manager.getDatabase() == null) {
            return;
        }

        UtilSql.update(this.manager.getDatabase())
                .query(WonderTradeQueries.ADD_AND_UPDATE_USER)
                .data(SqlType.text(this.id.toString()), SqlType.bigInt(this.lastTrade))
                .execute();
    }

    public String getCooldownFormatted() {
        long seconds = Duration.ofMillis(
                (this.lastTrade + TimeUnit.SECONDS.toMillis(this.manager.getConfig().getCooldownSeconds()))
                        - System.currentTimeMillis()).getSeconds();

        long hoursPart = (seconds / SECONDS_PER_HOUR) % 24;
        long minutesPart = (seconds / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
        long secondsPart = seconds % SECONDS_PER_MINUTE;

        StringBuilder builder = new StringBuilder();

        if (hoursPart > 0) {
            builder.append(hoursPart).append("h ");
        }

        if (minutesPart > 0) {
            builder.append(minutesPart).append("m ");
        }

        if (secondsPart > 0) {
            builder.append(secondsPart).append("s");
        }

        return builder.toString();
    }
}
