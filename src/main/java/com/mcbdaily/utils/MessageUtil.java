package com.mcbdaily.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MessageUtil {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
            LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer SECTION_SERIALIZER = 
            LegacyComponentSerializer.legacySection();
    
    private static Boolean hasAdventureSupport = null;
    
    /**
     * Formats a string supporting both MiniMessage and legacy color codes
     * Automatically detects the format type and applies appropriate formatting
     */
    public static String format(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Check if the string contains MiniMessage tags
        if (containsMiniMessageTags(input)) {
            try {
                Component component = MINI_MESSAGE.deserialize(input);
                // Always use section serializer for better compatibility
                return SECTION_SERIALIZER.serialize(component);
            } catch (Exception e) {
                // If MiniMessage parsing fails, fall back to legacy color codes
                return ChatColor.translateAlternateColorCodes('&', input);
            }
        } else {
            // Use legacy color code formatting
            return ChatColor.translateAlternateColorCodes('&', input);
        }
    }
    
    /**
     * Formats a list of strings, each supporting MiniMessage and legacy color codes
     */
    public static List<String> format(List<String> input) {
        if (input == null) {
            return new ArrayList<>();
        }
        
        List<String> formatted = new ArrayList<>();
        for (String line : input) {
            formatted.add(format(line));
        }
        return formatted;
    }
    
    /**
     * Sends a message to a player using Adventure API if available, otherwise legacy
     */
    public static void sendMessage(Player player, String message) {
        if (hasAdventureAPI() && containsMiniMessageTags(message)) {
            try {
                // Try to use Adventure API directly
                Component component = MINI_MESSAGE.deserialize(message);
                sendAdventureMessage(player, component);
                return;
            } catch (Exception e) {
                // Fall back to legacy if Adventure fails
            }
        }
        
        // Use legacy formatting
        String formatted = format(message);
        player.sendMessage(formatted);
    }
    
    /**
     * Checks if Adventure API is available on this server
     */
    private static boolean hasAdventureAPI() {
        if (hasAdventureSupport == null) {
            try {
                // Check if Player has the sendMessage(Component) method
                Player.class.getMethod("sendMessage", Component.class);
                hasAdventureSupport = true;
            } catch (NoSuchMethodException e) {
                hasAdventureSupport = false;
            }
        }
        return hasAdventureSupport;
    }
    
    /**
     * Send Adventure message using reflection for compatibility
     */
    private static void sendAdventureMessage(Player player, Component component) {
        try {
            Method sendMessageMethod = Player.class.getMethod("sendMessage", Component.class);
            sendMessageMethod.invoke(player, component);
        } catch (Exception e) {
            // Fall back to legacy if reflection fails
            String legacy = SECTION_SERIALIZER.serialize(component);
            player.sendMessage(legacy);
        }
    }
    
    /**
     * Checks if a string contains MiniMessage tags
     */
    private static boolean containsMiniMessageTags(String input) {
        // Common MiniMessage patterns
        return input.contains("<") && input.contains(">") && (
                input.contains("<gradient:") ||
                input.contains("<rainbow") ||
                input.contains("<hover:") ||
                input.contains("<click:") ||
                input.contains("<color:") ||
                input.contains("<#") ||
                input.contains("<bold>") ||
                input.contains("<italic>") ||
                input.contains("<underlined>") ||
                input.contains("<strikethrough>") ||
                input.contains("<obfuscated>") ||
                input.contains("<reset>") ||
                input.contains("<br>") ||
                input.contains("<yellow>") ||
                input.contains("<red>") ||
                input.contains("<green>") ||
                input.contains("<blue>") ||
                input.contains("<aqua>") ||
                input.contains("<light_purple>") ||
                input.contains("<dark_") ||
                input.contains("</gradient>") ||
                input.contains("</rainbow>") ||
                input.contains("</bold>") ||
                input.contains("</italic>")
        );
    }
    
    /**
     * Creates a Component from a string (for future Adventure API usage)
     */
    public static Component createComponent(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        
        if (containsMiniMessageTags(input)) {
            try {
                return MINI_MESSAGE.deserialize(input);
            } catch (Exception e) {
                // Fall back to legacy parsing
                return LEGACY_SERIALIZER.deserialize(ChatColor.translateAlternateColorCodes('&', input));
            }
        } else {
            return LEGACY_SERIALIZER.deserialize(ChatColor.translateAlternateColorCodes('&', input));
        }
    }
} 