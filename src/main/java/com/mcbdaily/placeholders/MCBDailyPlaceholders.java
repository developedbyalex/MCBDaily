package com.mcbdaily.placeholders;

import com.mcbdaily.MCBDaily;
import com.mcbdaily.utils.TimeFormatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class MCBDailyPlaceholders extends PlaceholderExpansion {
    
    private final MCBDaily plugin;
    
    public MCBDailyPlaceholders(MCBDaily plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "mcbdaily";
    }
    
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }
    
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }
        
        if (params.equalsIgnoreCase("countdown")) {
            if (plugin.getPlayerDataManager().canClaim(player.getUniqueId())) {
                return "Available now!";
            } else {
                long timeLeft = plugin.getPlayerDataManager().getTimeUntilNextClaim(player.getUniqueId());
                return TimeFormatter.formatTime(timeLeft);
            }
        }
        
        if (params.equalsIgnoreCase("can_claim")) {
            return plugin.getPlayerDataManager().canClaim(player.getUniqueId()) ? "true" : "false";
        }
        
        if (params.equalsIgnoreCase("time_left_millis")) {
            return String.valueOf(plugin.getPlayerDataManager().getTimeUntilNextClaim(player.getUniqueId()));
        }
        
        return null;
    }
} 