package com.craftoflegends.champion;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SimpleAbility implements Ability {
    private final String name;
    private final int cooldownSeconds;

    public SimpleAbility(String name, int cooldownSeconds) {
        this.name = name;
        this.cooldownSeconds = cooldownSeconds;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    @Override
    public void execute(Player player, PlayerInteractEvent event) {
        player.sendMessage("You used ability: " + name);
    }
}
