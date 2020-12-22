package com.zmanuel.warzone.utils;

import org.bukkit.ChatColor;

public class StringUtil {

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static boolean isNumberic(String s) {
        try{
            Integer.parseInt(s);
        }catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(String s) {
        try{
            Long.parseLong(s);
        }catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}