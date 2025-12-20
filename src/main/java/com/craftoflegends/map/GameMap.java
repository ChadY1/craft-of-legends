package com.craftoflegends.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class GameMap {
    private final String name;
    private final String worldName;
    private final Location spawn;

    public GameMap(String name, String worldName, Location spawn) {
        this.name = name;
        this.worldName = worldName;
        this.spawn = spawn;
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getSpawn() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return spawn;
        }
        return new Location(world, spawn.getX(), spawn.getY(), spawn.getZ());
    }
}
