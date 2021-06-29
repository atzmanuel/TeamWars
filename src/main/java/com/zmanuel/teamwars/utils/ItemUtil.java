package com.zmanuel.teamwars.utils;

import com.zmanuel.teamwars.data.Team;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

    public static ItemStack flagItem(Team team) {
        ItemStack item = new ItemStack(Material.SHULKER_SHELL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtil.translate(team.getColor() + "Flag"));
        item.setItemMeta(meta);
        return item;
    }

}
