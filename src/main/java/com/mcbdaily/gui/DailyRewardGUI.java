package com.mcbdaily.gui;

import com.mcbdaily.MCBDaily;
import com.mcbdaily.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class DailyRewardGUI implements Listener {
    
    private final MCBDaily plugin;
    private final Player player;
    private final Inventory inventory;
    
    public DailyRewardGUI(MCBDaily plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 27, "§6Daily Rewards");
        
        // Register event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        setupInventory();
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
        meta.setDisplayName("§a§lDaily Reward");
        meta.setLore(Arrays.asList(
            "§7Click to claim your daily reward!",
            "",
            "§eRewards:",
            getRewardList()
        ));
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createCooldownItem() {
        ItemStack item = new ItemStack(Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lDaily Reward");
        
        long timeLeft = plugin.getPlayerDataManager().getTimeUntilNextClaim(player.getUniqueId());
        String timeString = TimeFormatter.formatTime(timeLeft);
        
        meta.setLore(Arrays.asList(
            "§7You can claim again in:",
            "§c" + timeString,
            "",
            "§eRewards:",
            getRewardList()
        ));
        item.setItemMeta(meta);
        return item;
    }
    
    private String getRewardList() {
        List<String> rewards = plugin.getConfig().getStringList("rewards");
        if (rewards.isEmpty()) {
            return "§7No rewards configured";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(rewards.size(), 3); i++) {
            sb.append("§7- ").append(rewards.get(i));
            if (i < Math.min(rewards.size(), 3) - 1) {
                sb.append("\n");
            }
        }
        
        if (rewards.size() > 3) {
            sb.append("\n§7... and ").append(rewards.size() - 3).append(" more!");
        }
        
        return sb.toString();
    }
    
    public void open() {
        player.openInventory(inventory);
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
                player.sendMessage("§cYou cannot claim your daily reward yet!");
            }
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
        
        // Close inventory and send success message
        player.closeInventory();
        player.sendMessage("§a§lDaily reward claimed successfully!");
        
        // Optional: Play sound effect
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // Log the claim
        plugin.getLogger().info("Player " + player.getName() + " claimed their daily reward");
    }
} 