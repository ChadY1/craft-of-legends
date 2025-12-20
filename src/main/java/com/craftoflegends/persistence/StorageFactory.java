package com.craftoflegends.persistence;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public final class StorageFactory {

    private StorageFactory() {
    }

    public static DataStorage createStorage(FileConfiguration config, StorageType type, Logger logger) {
        switch (type) {
            case MYSQL:
                return new MySqlStorage(config, logger);
            case SQLITE:
            default:
                return new SqliteStorage(config, logger);
        }
    }
}
