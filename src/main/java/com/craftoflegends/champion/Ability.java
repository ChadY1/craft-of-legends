package com.craftoflegends.champion;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface Ability {
    String getName();

    int getCooldownSeconds();

    void execute(Player player, PlayerInteractEvent event);
}
