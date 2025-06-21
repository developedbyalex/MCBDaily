package com.mcbdaily.commands;

import com.mcbdaily.MCBDaily;
import com.mcbdaily.gui.DailyRewardGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyCommand implements CommandExecutor {
    
    private final MCBDaily plugin;
    
    public DailyCommand(MCBDaily plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (!player.hasPermission("mcbdaily.claim")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        // Handle admin commands
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("mcbdaily.admin")) {
                    plugin.reloadPluginConfig();
                    player.sendMessage("§aConfiguration reloaded successfully!");
                } else {
                    player.sendMessage("§cYou don't have permission to reload the configuration!");
                }
                return true;
            }
            
            if (args[0].equalsIgnoreCase("reset") && args.length == 2) {
                if (player.hasPermission("mcbdaily.admin")) {
                    Player targetPlayer = plugin.getServer().getPlayer(args[1]);
                    if (targetPlayer != null) {
                        plugin.getPlayerDataManager().resetCooldown(targetPlayer.getUniqueId());
                        player.sendMessage("§aReset daily cooldown for " + targetPlayer.getName());
                    } else {
                        player.sendMessage("§cPlayer not found!");
                    }
                } else {
                    player.sendMessage("§cYou don't have permission to reset cooldowns!");
                }
                return true;
            }
        }
        
        // Open daily reward GUI
        DailyRewardGUI gui = new DailyRewardGUI(plugin, player);
        gui.open();
        
        return true;
    }
} 