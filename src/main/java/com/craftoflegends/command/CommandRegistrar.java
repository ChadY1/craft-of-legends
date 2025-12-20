package com.craftoflegends.command;

import com.craftoflegends.CraftOfLegendsPlugin;
import com.craftoflegends.champion.ChampionService;
import com.craftoflegends.map.MapService;
import com.craftoflegends.match.MatchService;
import com.craftoflegends.match.PartyService;
import org.bukkit.command.PluginCommand;

public class CommandRegistrar {

    private final CraftOfLegendsPlugin plugin;
    private final ChampionService championService;
    private final MatchService matchService;
    private final PartyService partyService;
    private final MapService mapService;

    public CommandRegistrar(CraftOfLegendsPlugin plugin, ChampionService championService, MatchService matchService,
                            PartyService partyService, MapService mapService) {
        this.plugin = plugin;
        this.championService = championService;
        this.matchService = matchService;
        this.partyService = partyService;
        this.mapService = mapService;
    }

    public void register() {
        PluginCommand command = plugin.getCommand("col");
        if (command != null) {
            CraftCommand executor = new CraftCommand(championService, matchService, partyService, mapService);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }
}
