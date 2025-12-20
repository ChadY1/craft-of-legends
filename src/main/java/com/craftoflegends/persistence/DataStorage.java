package com.craftoflegends.persistence;

import com.craftoflegends.match.Match;
import com.craftoflegends.session.PlayerSession;

import java.util.Optional;
import java.util.UUID;

public interface DataStorage {
    void initialize();

    void close();

    void saveSession(PlayerSession session);

    Optional<PlayerSession> loadSession(UUID playerId);

    void recordMatchResult(Match match);

    void recordHeartbeat(int matchId, long tick);
}
