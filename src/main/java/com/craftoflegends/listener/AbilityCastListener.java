package com.craftoflegends.listener;

import com.craftoflegends.champion.Ability;
import com.craftoflegends.champion.Champion;
import com.craftoflegends.champion.ChampionService;
import com.craftoflegends.cooldown.CooldownService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class AbilityCastListener implements Listener {

    private final CooldownService cooldownService;
    private final ChampionService championService;

    public AbilityCastListener(CooldownService cooldownService, ChampionService championService) {
        this.cooldownService = cooldownService;
        this.championService = championService;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }
        String displayName = ChatColor.stripColor(meta.getDisplayName());
        Optional<Champion> champion = championService.getSelectedChampion(player.getUniqueId());
        if (!champion.isPresent()) {
            return;
        }
        Ability ability = champion.get().getAbility(displayName.toLowerCase());
        if (ability == null) {
            return;
        }
        if (cooldownService.isOnCooldown(player.getUniqueId(), ability.getName())) {
            long seconds = cooldownService.getSecondsLeft(player.getUniqueId(), ability.getName());
            player.sendMessage(ChatColor.RED + "Ability on cooldown for " + seconds + "s");
            return;
        }
        ability.execute(player, event);
        cooldownService.startCooldown(player.getUniqueId(), ability.getName(), ability.getCooldownSeconds());
        player.sendMessage(ChatColor.GRAY + "Cooldown started for " + ability.getCooldownSeconds() + "s");
    }
}
