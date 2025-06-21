package com.mcbdaily.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
            LegacyComponentSerializer.legacyAmpersand();
    
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
                // Parse as MiniMessage and convert to legacy format for compatibility
                Component component = MINI_MESSAGE.deserialize(input);
                return LEGACY_SERIALIZER.serialize(component);
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
    public static void sendMessage(org.bukkit.entity.Player player, String message) {
        String formatted = format(message);
        player.sendMessage(formatted);
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
                input.contains("<br>")
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