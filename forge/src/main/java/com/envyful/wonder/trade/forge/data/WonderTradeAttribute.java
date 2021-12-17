package com.envyful.wonder.trade.forge.data;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.wonder.trade.forge.WonderTradeForge;
import com.envyful.wonder.trade.forge.config.WonderTradeQueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class WonderTradeAttribute extends AbstractForgeAttribute<WonderTradeForge> {

    private static final long SECONDS_PER_MINUTE = 60;
    private static final long MINUTES_PER_HOUR = 60;
    private static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    private long lastTrade = -1;
    private int selected = -1;
    private boolean confirm = false;

    public WonderTradeAttribute(WonderTradeForge manager, EnvyPlayer<?> parent) {
        super(manager, (ForgeEnvyPlayer) parent);
    }

    public boolean canTrade() {
        if (this.lastTrade == -1) {
            return true;
        }

        return (System.currentTimeMillis() - this.lastTrade) >
                TimeUnit.SECONDS.toMillis(this.manager.getConfig().getCooldownSeconds());
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

    public boolean isConfirm() {
        return this.confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    @Override
    public void load() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(WonderTradeQueries.LOAD_USER)) {
            preparedStatement.setString(1, this.parent.getUuid().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return;
            }

            this.lastTrade = resultSet.getLong("last_trade");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(WonderTradeQueries.ADD_AND_UPDATE_USER)) {
            preparedStatement.setString(1, this.parent.getUuid().toString());
            preparedStatement.setLong(2, this.lastTrade);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
