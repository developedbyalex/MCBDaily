package com.mcbdaily;

import com.mcbdaily.commands.DailyCommand;
import com.mcbdaily.data.PlayerDataManager;
import com.mcbdaily.placeholders.MCBDailyPlaceholders;
import org.bukkit.plugin.java.JavaPlugin;

public class MCBDaily extends JavaPlugin {
    
    private PlayerDataManager playerDataManager;
    private MCBDailyPlaceholders placeholders;
    
    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        
        // Initialize data manager
        playerDataManager = new PlayerDataManager(this);
        
        // Register command and tab completer
        DailyCommand dailyCommand = new DailyCommand(this);
        getCommand("daily").setExecutor(dailyCommand);
        getCommand("daily").setTabCompleter(dailyCommand);
        
        // Register PlaceholderAPI expansion if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholders = new MCBDailyPlaceholders(this);
            placeholders.register();
            getLogger().info("PlaceholderAPI integration enabled!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholder functionality will be disabled.");
        }
        
        getLogger().info("MCBDaily plugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Save player data
        if (playerDataManager != null) {
            playerDataManager.saveAll();
        }
        
        // Unregister placeholders
        if (placeholders != null) {
            placeholders.unregister();
        }
        
        getLogger().info("MCBDaily plugin has been disabled!");
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public void reloadPluginConfig() {
        reloadConfig();
        getLogger().info("MCBDaily configuration reloaded!");
    }
} 