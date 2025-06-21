package com.mcbdaily.data;

import com.mcbdaily.MCBDaily;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    
    private final MCBDaily plugin;
    private final File dataFile;
    private final FileConfiguration dataConfig;
    private final Map<UUID, Long> lastClaimed;
    
    public PlayerDataManager(MCBDaily plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        this.lastClaimed = new HashMap<>();
        
        // Create data file if it doesn't exist
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml file!");
                e.printStackTrace();
            }
        }
        
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadData();
    }
    
    private void loadData() {
        for (String uuidString : dataConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                long timestamp = dataConfig.getLong(uuidString);
                lastClaimed.put(uuid, timestamp);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in playerdata.yml: " + uuidString);
            }
        }
        plugin.getLogger().info("Loaded data for " + lastClaimed.size() + " players");
    }
    
    public void saveAll() {
        for (Map.Entry<UUID, Long> entry : lastClaimed.entrySet()) {
            dataConfig.set(entry.getKey().toString(), entry.getValue());
        }
        
        try {
            dataConfig.save(dataFile);
            plugin.getLogger().info("Saved player data successfully");
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
            e.printStackTrace();
        }
    }
    
    public boolean canClaim(UUID playerUuid) {
        // Check bypass permission
        if (plugin.getServer().getPlayer(playerUuid) != null && 
            plugin.getServer().getPlayer(playerUuid).hasPermission("mcbdaily.bypass")) {
            return true;
        }
        
        Long lastClaimTime = lastClaimed.get(playerUuid);
        if (lastClaimTime == null) {
            return true; // Never claimed before
        }
        
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastClaimTime;
        long cooldownTime = getCooldownTime();
        
        return timeDifference >= cooldownTime;
    }
    
    public void setClaimed(UUID playerUuid) {
        lastClaimed.put(playerUuid, System.currentTimeMillis());
        dataConfig.set(playerUuid.toString(), System.currentTimeMillis());
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player claim data!");
            e.printStackTrace();
        }
    }
    
    public long getTimeUntilNextClaim(UUID playerUuid) {
        Long lastClaimTime = lastClaimed.get(playerUuid);
        if (lastClaimTime == null) {
            return 0; // Can claim immediately
        }
        
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastClaimTime;
        long cooldownTime = getCooldownTime();
        
        return Math.max(0, cooldownTime - timeDifference);
    }
    
    public void resetCooldown(UUID playerUuid) {
        lastClaimed.remove(playerUuid);
        dataConfig.set(playerUuid.toString(), null);
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data after reset!");
            e.printStackTrace();
        }
    }
    
    private long getCooldownTime() {
        // Default 24 hours in milliseconds
        int hours = plugin.getConfig().getInt("cooldown-hours", 24);
        return hours * 60L * 60L * 1000L;
    }
} 