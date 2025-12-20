package com.craftoflegends.listener;

import com.craftoflegends.match.MatchService;
import com.craftoflegends.session.SessionService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SessionListener implements Listener {

    private final SessionService sessionService;
    private final MatchService matchService;

    public SessionListener(SessionService sessionService, MatchService matchService) {
        this.sessionService = sessionService;
        this.matchService = matchService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        sessionService.loadSession(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        matchService.cancelQueue(event.getPlayer().getUniqueId());
        sessionService.clear(event.getPlayer().getUniqueId());
    }
}
