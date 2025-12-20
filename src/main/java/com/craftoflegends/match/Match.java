package com.craftoflegends.match;

import com.craftoflegends.map.GameMap;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Match {
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final int id;
    private final GameMap map;
    private final Set<UUID> participants = new HashSet<>();
    private MatchState state;
    private Instant startedAt;
    private Instant endedAt;

    public Match(GameMap map) {
        this.id = COUNTER.incrementAndGet();
        this.map = map;
        this.state = MatchState.QUEUED;
    }

    public int getId() {
        return id;
    }

    public GameMap getMap() {
        return map;
    }

    public Set<UUID> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public MatchState getState() {
        return state;
    }

    public void addParticipant(UUID playerId) {
        participants.add(playerId);
    }

    public void start() {
        this.startedAt = Instant.now();
        this.state = MatchState.IN_PROGRESS;
    }

    public void complete() {
        this.state = MatchState.COMPLETED;
        this.endedAt = Instant.now();
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }
}
