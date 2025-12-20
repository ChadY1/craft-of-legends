package com.craftoflegends.listener;

import com.craftoflegends.map.GameMap;
import com.craftoflegends.map.MapService;
import com.craftoflegends.match.MatchService;
import com.craftoflegends.match.PartyService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MatchmakingListener implements Listener {

    private final MatchService matchService;
    private final PartyService partyService;
    private final MapService mapService;

    public MatchmakingListener(MatchService matchService, PartyService partyService, MapService mapService) {
        this.matchService = matchService;
        this.partyService = partyService;
        this.mapService = mapService;
    }

    @EventHandler
    public void onQueueItem(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.COMPASS) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Join Queue")) {
            return;
        }
        Player player = event.getPlayer();
        partyService.getParty(player.getUniqueId()).ifPresentOrElse(party -> {
            if (party.getOwner().equals(player.getUniqueId())) {
                matchService.enqueueParty(player.getUniqueId());
                Bukkit.getLogger().info("Queued party led by " + player.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Only party leaders can queue the party.");
            }
        }, () -> matchService.joinSolo(player));
        matchService.tryCreateMatch();
        player.sendMessage(ChatColor.GREEN + "Queued for match using matchmaking compass.");
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (mapService == null) {
            return;
        }
        GameMap map = mapService.getDefaultMap();
        event.setRespawnLocation(map.getSpawn());
    }
}
