package com.craftoflegends.session;

import java.util.UUID;

public class PlayerSession {
    private final UUID playerId;
    private final String champion;

    public PlayerSession(UUID playerId, String champion) {
        this.playerId = playerId;
        this.champion = champion;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getChampion() {
        return champion;
    }
}
