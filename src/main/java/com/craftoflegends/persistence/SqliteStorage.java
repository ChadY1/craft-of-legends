package com.craftoflegends.persistence;

import com.zaxxer.hikari.HikariConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.logging.Logger;

public class SqliteStorage extends JdbcStorage {

    private final FileConfiguration config;

    public SqliteStorage(FileConfiguration config, Logger logger) {
        super(logger);
        this.config = config;
    }

    @Override
    protected String sessionUpsertSql() {
        return "INSERT INTO player_sessions(player_uuid, champion) VALUES (?, ?) ON CONFLICT(player_uuid) DO UPDATE SET champion = excluded.champion";
    }

    @Override
    protected String matchUpsertSql() {
        return "INSERT INTO match_results(id, state, started_at, ended_at) VALUES (?, ?, ?, ?) ON CONFLICT(id) DO UPDATE SET state = excluded.state, started_at = excluded.started_at, ended_at = excluded.ended_at";
    }

    @Override
    protected HikariConfig buildConfig() {
        HikariConfig hikari = new HikariConfig();
        String filePath = config.getString("storage.sqlite.file", "plugins/CraftOfLegends/data.db");
        File dbFile = new File(filePath);
        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }
        hikari.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        hikari.setMaximumPoolSize(config.getInt("storage.sqlite.poolSize", 5));
        hikari.setDriverClassName("org.sqlite.JDBC");
        hikari.setPoolName("CraftOfLegends-SQLite");
        return hikari;
    }
}
