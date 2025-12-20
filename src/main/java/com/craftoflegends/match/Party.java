package com.craftoflegends.match;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Party {
    private final UUID owner;
    private final Set<UUID> members = new HashSet<>();

    public Party(UUID owner) {
        this.owner = owner;
        this.members.add(owner);
    }

    public UUID getOwner() {
        return owner;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean addMember(UUID playerId, int maxSize) {
        if (members.size() >= maxSize) {
            return false;
        }
        return members.add(playerId);
    }

    public void removeMember(UUID playerId) {
        members.remove(playerId);
    }
}
