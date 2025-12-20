package com.craftoflegends.map;

import com.craftoflegends.match.Match;
import com.craftoflegends.persistence.DataStorage;
import com.craftoflegends.session.SessionService;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameLoop extends BukkitRunnable {

    private final MapService mapService;
    private final Match match;
    private final SessionService sessionService;
    private final DataStorage dataStorage;
    private int ticksElapsed = 0;

    public GameLoop(MapService mapService, Match match, SessionService sessionService, DataStorage dataStorage) {
        this.mapService = mapService;
        this.match = match;
        this.sessionService = sessionService;
        this.dataStorage = dataStorage;
    }

    @Override
    public void run() {
        ticksElapsed++;
        match.getParticipants().forEach(playerId -> sessionService.getOnlinePlayer(playerId).ifPresent(player -> {
            GameMap map = match.getMap();
            if (!player.getWorld().getName().equalsIgnoreCase(map.getWorldName())) {
                mapService.teleportToMap(player, map);
            }
        }));

        if (ticksElapsed % (20 * 60) == 0) {
            dataStorage.recordHeartbeat(match.getId(), ticksElapsed);
            Bukkit.getLogger().info("Match " + match.getId() + " heartbeat at tick " + ticksElapsed);
        }
    }
}
