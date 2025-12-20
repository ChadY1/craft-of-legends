package com.craftoflegends.persistence;

public enum StorageType {
    SQLITE,
    MYSQL;

    public static StorageType fromConfig(String name) {
        if (name == null) {
            return SQLITE;
        }
        try {
            return StorageType.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return SQLITE;
        }
    }
}
