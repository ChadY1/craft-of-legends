package com.craftoflegends.cooldown;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownService {

    private final Map<UUID, Map<String, Instant>> cooldowns = new HashMap<>();

    public boolean isOnCooldown(UUID playerId, String ability) {
        Map<String, Instant> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) {
            return false;
        }
        Instant expiry = playerCooldowns.get(ability.toLowerCase());
        return expiry != null && expiry.isAfter(Instant.now());
    }

    public long getSecondsLeft(UUID playerId, String ability) {
        Map<String, Instant> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) {
            return 0;
        }
        Instant expiry = playerCooldowns.get(ability.toLowerCase());
        if (expiry == null) {
            return 0;
        }
        return Math.max(0, expiry.getEpochSecond() - Instant.now().getEpochSecond());
    }

    public void startCooldown(UUID playerId, String ability, int seconds) {
        cooldowns.computeIfAbsent(playerId, k -> new HashMap<>())
                .put(ability.toLowerCase(), Instant.now().plusSeconds(seconds));
    }

    public void clear(UUID playerId) {
        cooldowns.remove(playerId);
    }
}
