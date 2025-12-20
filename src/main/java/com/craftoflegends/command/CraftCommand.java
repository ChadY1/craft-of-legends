package com.craftoflegends.command;

import com.craftoflegends.champion.ChampionService;
import com.craftoflegends.map.MapService;
import com.craftoflegends.match.MatchService;
import com.craftoflegends.match.PartyService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CraftCommand implements CommandExecutor, TabCompleter {

    private final ChampionService championService;
    private final MatchService matchService;
    private final PartyService partyService;
    private final MapService mapService;

    public CraftCommand(ChampionService championService, MatchService matchService, PartyService partyService,
                        MapService mapService) {
        this.championService = championService;
        this.matchService = matchService;
        this.partyService = partyService;
        this.mapService = mapService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Players only");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /col <join|leave|party|ability|map>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "join":
                matchService.joinSolo(player);
                sender.sendMessage(ChatColor.GREEN + "Joined matchmaking queue. Current size: " + matchService.getQueueSize());
                matchService.tryCreateMatch().ifPresent(match -> sender.sendMessage(ChatColor.GRAY + "Match " + match.getId() + " started."));
                break;
            case "leave":
                partyService.leaveParty(player.getUniqueId());
                matchService.cancelQueue(player.getUniqueId());
                sender.sendMessage(ChatColor.YELLOW + "Left queue and party");
                break;
            case "party":
                handlePartySubcommand(player, args);
                break;
            case "ability":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /col ability <champion>");
                    break;
                }
                if (!championService.getChampion(args[1]).isPresent()) {
                    sender.sendMessage(ChatColor.RED + "Unknown champion: " + args[1]);
                    break;
                }
                championService.assignChampion(player, args[1]);
                sender.sendMessage(ChatColor.GREEN + "Selected champion " + args[1]);
                break;
            case "map":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /col map <name>");
                    break;
                }
                Optional.ofNullable(mapService.getMap(args[1]).orElse(mapService.getDefaultMap()))
                        .ifPresent(map -> {
                            mapService.teleportToMap(player, map);
                            sender.sendMessage(ChatColor.GREEN + "Teleported to map " + map.getName());
                        });
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand");
        }
        return true;
    }

    private void handlePartySubcommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /col party <create|invite|leave>");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "create":
                partyService.createParty(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Party created. Invite friends with /col party invite <player>");
                break;
            case "invite":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /col party invite <player>");
                    break;
                }
                Player target = player.getServer().getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    break;
                }
                boolean added = partyService.addMember(player.getUniqueId(), target.getUniqueId());
                player.sendMessage(added ? ChatColor.GREEN + target.getName() + " joined your party" : ChatColor.RED + "Party is full");
                break;
            case "leave":
                partyService.leaveParty(player.getUniqueId());
                player.sendMessage(ChatColor.YELLOW + "Left your party");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown party action");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("join", "leave", "party", "ability", "map");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("party")) {
            return Arrays.asList("create", "invite", "leave");
        }
        return new ArrayList<>();
    }
}
