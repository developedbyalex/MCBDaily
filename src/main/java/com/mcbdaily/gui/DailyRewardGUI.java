package com.mcbdaily.gui;

import com.mcbdaily.MCBDaily;
import com.mcbdaily.utils.BroadcastUtil;
import com.mcbdaily.utils.MessageUtil;
import com.mcbdaily.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class DailyRewardGUI implements Listener {
    
    private final MCBDaily plugin;
    private final Player player;
    private final Inventory inventory;
    private BukkitTask refreshTask;
    
    public DailyRewardGUI(MCBDaily plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        
        String title = MessageUtil.format(
            plugin.getConfig().getString("gui.title", "&6Daily Rewards"));
        this.inventory = Bukkit.createInventory(null, 27, title);
        
        // Register event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        setupInventory();
        startAutoRefresh();
    }
    
    private void setupInventory() {
        boolean canClaim = plugin.getPlayerDataManager().canClaim(player.getUniqueId());
        
        ItemStack rewardItem;
        if (canClaim) {
            rewardItem = createClaimableItem();
        } else {
            rewardItem = createCooldownItem();
        }
        
        // Set the item in slot 13 (center of 27-slot inventory)
        inventory.setItem(13, rewardItem);
        
        // Fill empty slots with glass panes for decoration
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        
        for (int i = 0; i < 27; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }
    
    private ItemStack createClaimableItem() {
        ItemStack item = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        
        // Get configurable display name
        String displayName = plugin.getConfig().getString("gui.claimable.display-name", "&a&lDaily Reward");
        meta.setDisplayName(MessageUtil.format(displayName));
        
        // Get configurable lore
        List<String> configLore = plugin.getConfig().getStringList("gui.claimable.lore");
        List<String> lore = new ArrayList<>();
        
        for (String line : configLore) {
            if (line.contains("%rewards%")) {
                lore.add(getRewardList());
            } else {
                lore.add(MessageUtil.format(line));
            }
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createCooldownItem() {
        ItemStack item = new ItemStack(Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        
        // Get configurable display name
        String displayName = plugin.getConfig().getString("gui.cooldown.display-name", "&c&lDaily Reward");
        meta.setDisplayName(MessageUtil.format(displayName));
        
        // Get configurable lore
        List<String> configLore = plugin.getConfig().getStringList("gui.cooldown.lore");
        List<String> lore = new ArrayList<>();
        
        long timeLeft = plugin.getPlayerDataManager().getTimeUntilNextClaim(player.getUniqueId());
        String countdown = TimeFormatter.formatTime(timeLeft);
        
        for (String line : configLore) {
            if (line.contains("%countdown%")) {
                lore.add(MessageUtil.format(line.replace("%countdown%", countdown)));
            } else if (line.contains("%rewards%")) {
                lore.add(getRewardList());
            } else {
                lore.add(MessageUtil.format(line));
            }
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private String getRewardList() {
        List<String> rewards = plugin.getConfig().getStringList("rewards");
        if (rewards.isEmpty()) {
            return ChatColor.GRAY + "No rewards configured";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(rewards.size(), 3); i++) {
            sb.append(ChatColor.GRAY).append("- ").append(rewards.get(i));
            if (i < Math.min(rewards.size(), 3) - 1) {
                sb.append("\n");
            }
        }
        
        if (rewards.size() > 3) {
            sb.append("\n").append(ChatColor.GRAY).append("... and ").append(rewards.size() - 3).append(" more!");
        }
        
        return sb.toString();
    }
    
    private void startAutoRefresh() {
        int refreshInterval = plugin.getConfig().getInt("gui.auto-refresh-seconds", 5);
        
        if (refreshInterval > 0) {
            refreshTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (player.getOpenInventory().getTopInventory().equals(inventory)) {
                    refreshInventory();
                }
            }, 20L * refreshInterval, 20L * refreshInterval);
        }
    }
    
    private void refreshInventory() {
        // Only refresh the center item to avoid flickering
        boolean canClaim = plugin.getPlayerDataManager().canClaim(player.getUniqueId());
        
        ItemStack newItem;
        if (canClaim) {
            newItem = createClaimableItem();
        } else {
            newItem = createCooldownItem();
        }
        
        inventory.setItem(13, newItem);
    }
    
    public void open() {
        player.openInventory(inventory);
    }
    
    public void close() {
        if (refreshTask != null) {
            refreshTask.cancel();
        }
        
        // Unregister listener to prevent memory leaks
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player clickedPlayer = (Player) event.getWhoClicked();
        if (!clickedPlayer.equals(player)) {
            return;
        }
        
        if (event.getSlot() == 13) {
            if (plugin.getPlayerDataManager().canClaim(player.getUniqueId())) {
                claimReward();
            } else {
                MessageUtil.sendMessage(player, "&cYou cannot claim your daily reward yet!");
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            close();
        }
    }
    
    private void claimReward() {
        // Execute reward commands
        List<String> rewards = plugin.getConfig().getStringList("rewards");
        for (String command : rewards) {
            String processedCommand = command.replace("%player%", player.getName());
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), processedCommand);
        }
        
        // Set claim timestamp
        plugin.getPlayerDataManager().setClaimed(player.getUniqueId());
        
        // Close inventory
        player.closeInventory();
        
        // Send claim message and broadcast
        BroadcastUtil.handleClaimMessages(plugin, player);
        
        // Optional: Play sound effect
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // Log the claim
        plugin.getLogger().info("Player " + player.getName() + " claimed their daily reward");
    }
} 