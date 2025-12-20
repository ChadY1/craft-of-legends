package com.craftoflegends.match;

import com.craftoflegends.map.GameLoop;
import com.craftoflegends.map.GameMap;
import com.craftoflegends.map.MapService;
import com.craftoflegends.persistence.DataStorage;
import com.craftoflegends.session.SessionService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.UUID;

public class MatchService {

    private final MapService mapService;
    private final PartyService partyService;
    private final SessionService sessionService;
    private final DataStorage dataStorage;
    private final int queueTimeoutSeconds;
    private final Deque<Party> queue = new ArrayDeque<>();

    public MatchService(MapService mapService, PartyService partyService, SessionService sessionService,
                        DataStorage dataStorage, FileConfiguration configuration) {
        this.mapService = mapService;
        this.partyService = partyService;
        this.sessionService = sessionService;
        this.dataStorage = dataStorage;
        this.queueTimeoutSeconds = configuration.getInt("matchmaking.queue-timeout-seconds", 90);
    }

    public void enqueueParty(UUID leader) {
        partyService.getParty(leader).ifPresent(party -> {
            queue.offer(party);
            party.getMembers().forEach(sessionService::startSession);
        });
    }

    public Optional<Match> tryCreateMatch() {
        if (queue.isEmpty()) {
            return Optional.empty();
        }
        Party party = queue.poll();
        GameMap map = mapService.getDefaultMap();
        if (map == null) {
            Bukkit.getLogger().warning("No maps configured; cannot create match for queued party.");
            return Optional.empty();
        }
        Match match = new Match(map);
        party.getMembers().forEach(match::addParticipant);
        startMatch(match);
        return Optional.of(match);
    }

    public void joinSolo(Player player) {
        Party solo = new Party(player.getUniqueId());
        queue.offer(solo);
        sessionService.startSession(player.getUniqueId());
    }

    public void startMatch(Match match) {
        match.start();
        if (match.getMap() == null) {
            Bukkit.getLogger().warning("Match " + match.getId() + " has no map configured; skipping start.");
            return;
        }
        GameLoop loop = new GameLoop(mapService, match, sessionService, dataStorage);
        loop.runTaskTimer(mapService.getPlugin(), 0L, mapService.getTickInterval());
        match.getParticipants().forEach(sessionService::markActive);
    }

    public void cancelQueue(UUID leader) {
        queue.removeIf(party -> party.getOwner().equals(leader));
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getQueueTimeoutSeconds() {
        return queueTimeoutSeconds;
    }

    public void finishMatch(Match match) {
        match.complete();
        dataStorage.recordMatchResult(match);
        match.getParticipants().forEach(sessionService::markCompleted);
    }
}
