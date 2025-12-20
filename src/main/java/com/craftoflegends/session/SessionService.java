package com.craftoflegends.session;

import com.craftoflegends.champion.ChampionService;
import com.craftoflegends.persistence.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionService {

    private final ChampionService championService;
    private final DataStorage dataStorage;
    private final Map<UUID, SessionState> state = new HashMap<>();

    public SessionService(ChampionService championService, DataStorage dataStorage) {
        this.championService = championService;
        this.dataStorage = dataStorage;
    }

    public void loadSession(UUID playerId) {
        dataStorage.loadSession(playerId).ifPresent(session -> {
            championService.assignChampion(playerId, session.getChampion());
            state.put(playerId, SessionState.IDLE);
        });
    }

    public void startSession(UUID playerId) {
        state.put(playerId, SessionState.IN_QUEUE);
    }

    public void markActive(UUID playerId) {
        state.put(playerId, SessionState.IN_MATCH);
    }

    public void markCompleted(UUID playerId) {
        state.put(playerId, SessionState.COMPLETED);
        dataStorage.saveSession(new PlayerSession(playerId, championService.getSelectedChampion(playerId)
                .map(champion -> champion.getName()).orElse("unknown")));
    }

    public void clear(UUID playerId) {
        state.remove(playerId);
    }

    public Optional<SessionState> getState(UUID playerId) {
        return Optional.ofNullable(state.get(playerId));
    }

    public Optional<Player> getOnlinePlayer(UUID playerId) {
        return Optional.ofNullable(Bukkit.getPlayer(playerId));
    }
}
