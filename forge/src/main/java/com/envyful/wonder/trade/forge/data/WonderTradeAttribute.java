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
import java.util.concurrent.TimeUnit;

public class WonderTradeAttribute extends AbstractForgeAttribute<WonderTradeForge> {

    private long lastTrade = -1;
    private int selected = -1;

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
}
