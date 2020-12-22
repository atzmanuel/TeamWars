package com.zmanuel.warzone.listeners;

import com.zmanuel.warzone.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class WorldHeightLimit implements Listener {

    public WorldHeightLimit() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if(e.getBlockPlaced().getLocation().getY() > 200) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {

        Material bucket = e.getBucket();

        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if ((bucket.toString().contains("LAVA") || bucket.toString().contains("WATER")) && e.getBlock().getLocation().getY() > 200) {
            e.setCancelled(true);
        }

    }

}
