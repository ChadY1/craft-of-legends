package com.craftoflegends.map;

import com.craftoflegends.CraftOfLegendsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapService {
    private final CraftOfLegendsPlugin plugin;
    private final Map<String, GameMap> maps = new HashMap<>();
    private final String defaultMapName;
    private final long tickInterval;

    public MapService(CraftOfLegendsPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.defaultMapName = config.getString("maps.default-map", "rift");
        this.tickInterval = config.getLong("maps.tick-interval", 20L);
        loadMaps();
    }

    private void loadMaps() {
        Location spawn;
        if (Bukkit.getWorlds().isEmpty()) {
            plugin.getLogger().warning("No worlds loaded; using placeholder spawn until a world becomes available.");
            spawn = new Location(null, 0, 80, 0);
        } else {
            spawn = new Location(Bukkit.getWorlds().get(0), 0, 80, 0);
        }
        GameMap map = new GameMap(defaultMapName, spawn.getWorld() != null ? spawn.getWorld().getName() : "world", spawn);
        maps.put(map.getName().toLowerCase(), map);
    }

    public Optional<GameMap> getMap(String name) {
        return Optional.ofNullable(maps.get(name.toLowerCase()));
    }

    public GameMap getDefaultMap() {
        return maps.getOrDefault(defaultMapName.toLowerCase(), maps.values().stream().findFirst().orElse(null));
    }

    public void teleportToMap(Player player, GameMap map) {
        if (map == null) {
            plugin.getLogger().warning("Cannot teleport " + player.getName() + "; map not configured.");
            return;
        }
        Location spawn = map.getSpawn();
        if (spawn.getWorld() == null) {
            plugin.getLogger().warning("Spawn world not loaded for map " + map.getName());
            return;
        }
        player.teleport(spawn);
    }

    public CraftOfLegendsPlugin getPlugin() {
        return plugin;
    }

    public long getTickInterval() {
        return tickInterval;
    }
}
