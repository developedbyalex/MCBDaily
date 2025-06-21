package com.mcbdaily.commands;

import com.mcbdaily.MCBDaily;
import com.mcbdaily.gui.DailyRewardGUI;
import com.mcbdaily.utils.BroadcastUtil;
import com.mcbdaily.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DailyCommand implements CommandExecutor, TabCompleter {
    
    private final MCBDaily plugin;
    
    public DailyCommand(MCBDaily plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.format("&cThis command can only be used by players!"));
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (!player.hasPermission("mcbdaily.claim")) {
            MessageUtil.sendMessage(player, "&cYou don't have permission to use this command!");
            return true;
        }
        
        // Handle admin commands
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("mcbdaily.admin")) {
                    plugin.reloadPluginConfig();
                    MessageUtil.sendMessage(player, "&aConfiguration reloaded successfully!");
                } else {
                    MessageUtil.sendMessage(player, "&cYou don't have permission to reload the configuration!");
                }
                return true;
            }
            
            if (args[0].equalsIgnoreCase("reset") && args.length == 2) {
                if (player.hasPermission("mcbdaily.admin")) {
                    Player targetPlayer = plugin.getServer().getPlayer(args[1]);
                    if (targetPlayer != null) {
                        plugin.getPlayerDataManager().resetCooldown(targetPlayer.getUniqueId());
                        MessageUtil.sendMessage(player, "&aReset daily cooldown for " + targetPlayer.getName());
                    } else {
                        MessageUtil.sendMessage(player, "&cPlayer not found!");
                    }
                } else {
                    MessageUtil.sendMessage(player, "&cYou don't have permission to reset cooldowns!");
                }
                return true;
            }
            
            if (args[0].equalsIgnoreCase("testbroadcast")) {
                if (player.hasPermission("mcbdaily.admin")) {
                    BroadcastUtil.broadcastClaim(plugin, player);
                    MessageUtil.sendMessage(player, "&aTest broadcast sent!");
                } else {
                    MessageUtil.sendMessage(player, "&cYou don't have permission to test broadcasts!");
                }
                return true;
            }
        }
        
        // Open daily reward GUI
        DailyRewardGUI gui = new DailyRewardGUI(plugin, player);
        gui.open();
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!(sender instanceof Player)) {
            return completions;
        }
        
        Player player = (Player) sender;
        
        // Check if player has basic permission
        if (!player.hasPermission("mcbdaily.claim")) {
            return completions;
        }
        
        if (args.length == 1) {
            // First argument completions
            List<String> subcommands = new ArrayList<>();
            
            // Add admin commands if player has permission
            if (player.hasPermission("mcbdaily.admin")) {
                subcommands.addAll(Arrays.asList("reload", "reset", "testbroadcast"));
            }
            
            // Filter based on what the player has typed so far
            String input = args[0].toLowerCase();
            completions = subcommands.stream()
                    .filter(cmd -> cmd.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            // Second argument for reset command - suggest online player names
            if (player.hasPermission("mcbdaily.admin")) {
                String input = args[1].toLowerCase();
                completions = plugin.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        }
        
        return completions;
    }
} 