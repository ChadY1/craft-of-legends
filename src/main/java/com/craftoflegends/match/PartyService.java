package com.craftoflegends.match;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PartyService {

    private final int maxPartySize;
    private final Map<UUID, Party> parties = new HashMap<>();

    public PartyService(FileConfiguration configuration) {
        this.maxPartySize = configuration.getInt("matchmaking.max-party-size", 5);
    }

    public Party createParty(UUID leader) {
        Party party = new Party(leader);
        parties.put(leader, party);
        return party;
    }

    public Optional<Party> getParty(UUID playerId) {
        return parties.values().stream()
                .filter(party -> party.getMembers().contains(playerId))
                .findFirst();
    }

    public void leaveParty(UUID playerId) {
        getParty(playerId).ifPresent(party -> {
            party.removeMember(playerId);
            if (party.getMembers().isEmpty()) {
                parties.remove(party.getOwner());
            }
        });
    }

    public boolean addMember(UUID owner, UUID invitee) {
        Party party = parties.get(owner);
        if (party == null) {
            party = createParty(owner);
        }
        return party.addMember(invitee, maxPartySize);
    }

    public int getMaxPartySize() {
        return maxPartySize;
    }
}
