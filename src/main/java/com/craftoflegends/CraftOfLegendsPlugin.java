package com.craftoflegends;

import com.craftoflegends.champion.ChampionService;
import com.craftoflegends.command.CommandRegistrar;
import com.craftoflegends.cooldown.CooldownService;
import com.craftoflegends.listener.AbilityCastListener;
import com.craftoflegends.listener.SessionListener;
import com.craftoflegends.listener.MatchmakingListener;
import com.craftoflegends.map.MapService;
import com.craftoflegends.match.MatchService;
import com.craftoflegends.match.PartyService;
import com.craftoflegends.persistence.DataStorage;
import com.craftoflegends.persistence.StorageFactory;
import com.craftoflegends.persistence.StorageType;
import com.craftoflegends.session.SessionService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftOfLegendsPlugin extends JavaPlugin {

    private DataStorage dataStorage;
    private ChampionService championService;
    private CooldownService cooldownService;
    private PartyService partyService;
    private MatchService matchService;
    private MapService mapService;
    private SessionService sessionService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        StorageType storageType = StorageType.fromConfig(config.getString("storage.type"));

        this.dataStorage = StorageFactory.createStorage(config, storageType, getLogger());
        this.dataStorage.initialize();

        this.cooldownService = new CooldownService();
        this.championService = new ChampionService(cooldownService, dataStorage);
        this.partyService = new PartyService(config);
        this.mapService = new MapService(this, config);
        this.sessionService = new SessionService(championService, dataStorage);
        this.matchService = new MatchService(mapService, partyService, sessionService, dataStorage, config);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if (dataStorage != null) {
            dataStorage.close();
        }
    }

    private void registerCommands() {
        CommandRegistrar registrar = new CommandRegistrar(this, championService, matchService, partyService, mapService);
        registrar.register();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new AbilityCastListener(cooldownService, championService), this);
        Bukkit.getPluginManager().registerEvents(new SessionListener(sessionService, matchService), this);
        Bukkit.getPluginManager().registerEvents(new MatchmakingListener(matchService, partyService, mapService), this);
    }
}
