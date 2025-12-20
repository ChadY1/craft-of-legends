package com.craftoflegends.persistence;

import com.zaxxer.hikari.HikariConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class MySqlStorage extends JdbcStorage {

    private final FileConfiguration config;

    public MySqlStorage(FileConfiguration config, Logger logger) {
        super(logger);
        this.config = config;
    }

    @Override
    protected String sessionUpsertSql() {
        return "INSERT INTO player_sessions(player_uuid, champion) VALUES (?, ?) ON DUPLICATE KEY UPDATE champion = VALUES(champion)";
    }

    @Override
    protected String matchUpsertSql() {
        return "INSERT INTO match_results(id, state, started_at, ended_at) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE state = VALUES(state), started_at = VALUES(started_at), ended_at = VALUES(ended_at)";
    }

    @Override
    protected HikariConfig buildConfig() {
        HikariConfig hikari = new HikariConfig();
        String host = config.getString("storage.mysql.host");
        int port = config.getInt("storage.mysql.port", 3306);
        String database = config.getString("storage.mysql.database");
        hikari.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC");
        hikari.setUsername(config.getString("storage.mysql.username"));
        hikari.setPassword(config.getString("storage.mysql.password"));
        hikari.setMaximumPoolSize(config.getInt("storage.mysql.poolSize", 10));
        hikari.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikari.setPoolName("CraftOfLegends-MySQL");
        return hikari;
    }
}
