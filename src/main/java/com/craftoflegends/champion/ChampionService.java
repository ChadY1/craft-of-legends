package com.craftoflegends.champion;

import com.craftoflegends.cooldown.CooldownService;
import com.craftoflegends.persistence.DataStorage;
import com.craftoflegends.session.PlayerSession;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChampionService {

    private final CooldownService cooldownService;
    private final DataStorage dataStorage;
    private final Map<String, Champion> champions = new HashMap<>();
    private final Map<UUID, Champion> selectedChampion = new HashMap<>();

    public ChampionService(CooldownService cooldownService, DataStorage dataStorage) {
        this.cooldownService = cooldownService;
        this.dataStorage = dataStorage;
        registerDefaultChampions();
    }

    private void registerDefaultChampions() {
        Map<String, Ability> archerAbilities = new HashMap<>();
        archerAbilities.put("arrow volley", new SimpleAbility("Arrow Volley", 8));
        archerAbilities.put("dash", new SimpleAbility("Dash", 12));
        champions.put("archer", new Champion("Archer", archerAbilities));

        Map<String, Ability> guardianAbilities = new HashMap<>();
        guardianAbilities.put("shield wall", new SimpleAbility("Shield Wall", 10));
        guardianAbilities.put("ground slam", new SimpleAbility("Ground Slam", 15));
        champions.put("guardian", new Champion("Guardian", guardianAbilities));
    }

    public Optional<Champion> getChampion(String name) {
        return Optional.ofNullable(champions.get(name.toLowerCase()));
    }

    public void assignChampion(Player player, String name) {
        assignChampion(player.getUniqueId(), name);
    }

    public void assignChampion(UUID playerId, String name) {
        Champion champion = champions.getOrDefault(name.toLowerCase(), champions.get("archer"));
        selectedChampion.put(playerId, champion);
        dataStorage.saveSession(new PlayerSession(playerId, champion.getName()));
    }

    public Optional<Champion> getSelectedChampion(UUID playerId) {
        return Optional.ofNullable(selectedChampion.get(playerId));
    }

    public CooldownService getCooldownService() {
        return cooldownService;
    }
}
