package com.mcbdaily.utils;

import com.mcbdaily.MCBDaily;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class BroadcastUtil {
    
    private static final Random RANDOM = new Random();
    
    /**
     * Sends a claim success message to the player
     */
    public static void sendClaimMessage(MCBDaily plugin, Player player) {
        String message = plugin.getConfig().getString("messages.claim-success", 
            "&a&lDaily reward claimed successfully!");
        
        MessageUtil.sendMessage(player, message);
    }
    
    /**
     * Broadcasts a claim message to all online players if enabled
     */
    public static void broadcastClaim(MCBDaily plugin, Player player) {
        // Check if broadcasting is enabled
        if (!plugin.getConfig().getBoolean("messages.broadcast.enabled", true)) {
            return;
        }
        
        String broadcastMessage = getBroadcastMessage(plugin, player);
        
        if (broadcastMessage != null && !broadcastMessage.isEmpty()) {
            boolean excludeClaimer = plugin.getConfig().getBoolean("messages.broadcast.exclude-claimer", false);
            
            // Send to all online players
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                // Skip the claiming player if exclude-claimer is enabled
                if (excludeClaimer && onlinePlayer.equals(player)) {
                    continue;
                }
                MessageUtil.sendMessage(onlinePlayer, broadcastMessage);
            }
            
            // Log to console if enabled (show original format, not converted)
            if (plugin.getConfig().getBoolean("messages.broadcast.console-log", true)) {
                plugin.getLogger().info("Daily claim broadcast: " + broadcastMessage);
            }
        }
    }
    
    /**
     * Gets a broadcast message, either the main one or a random alternative
     */
    private static String getBroadcastMessage(MCBDaily plugin, Player player) {
        List<String> randomMessages = plugin.getConfig().getStringList("messages.broadcast.random-messages");
        
        String message;
        
        // Use random message if available, otherwise use main message
        if (randomMessages != null && !randomMessages.isEmpty()) {
            message = randomMessages.get(RANDOM.nextInt(randomMessages.size()));
        } else {
            message = plugin.getConfig().getString("messages.broadcast.message", 
                "&6&l%player% &ehas claimed their daily reward!");
        }
        
        // Replace player placeholder
        return message.replace("%player%", player.getName());
    }
    
    /**
     * Sends both claim message and broadcast
     */
    public static void handleClaimMessages(MCBDaily plugin, Player player) {
        sendClaimMessage(plugin, player);
        broadcastClaim(plugin, player);
    }
} 