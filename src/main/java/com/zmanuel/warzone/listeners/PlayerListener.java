package com.zmanuel.warzone.listeners;

import com.zmanuel.warzone.Main;
import com.zmanuel.warzone.data.Game;
import com.zmanuel.warzone.data.GameState;
import com.zmanuel.warzone.data.PlayerData;
import com.zmanuel.warzone.utils.ItemUtil;
import com.zmanuel.warzone.utils.LocationUtil;
import com.zmanuel.warzone.utils.StringUtil;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    public PlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler(priority= EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e){
        new PlayerData(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(e.getPlayer());
        if(Main.getInstance().getGame().isStarted() && Main.getInstance().getGame().getGameState() == GameState.WAR) {
            if(e.getPlayer().getInventory().contains(ItemUtil.flagItem(playerData.getOpponent()))) {
                e.getPlayer().getInventory().removeItem(ItemUtil.flagItem(playerData.getOpponent()));
                if(playerData.getBlock().getState() instanceof Chest) {
                    Chest chest = (Chest) playerData.getBlock().getState();
                    chest.getBlockInventory().addItem(ItemUtil.flagItem(playerData.getOpponent()));
                    playerData.setBlock(null);
                    Bukkit.getOnlinePlayers().forEach(p-> p.sendTitle(StringUtil.translate("&cAlert!"), StringUtil.translate(playerData.getOpponent().getColor() + playerData.getOpponent().getName() + " &eflag lost!"), 5, 40, 5));
                }
            }
        }
        Main.getInstance().getPlayerManager().getDataList().remove(playerData);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            e.setCancelled(false);
            return;
        }
        Game game = Main.getInstance().getGame();
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(e.getPlayer());
        if(playerData.isRespawn()) {
            e.setCancelled(true);
            return;
        }

        if(!game.isStarted()) {
            e.setCancelled(true);
            return;
        }
        if(!LocationUtil.inRegion(e.getPlayer(), playerData.getTeam().getPos1(), playerData.getTeam().getPos2())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent e){
        if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            e.setCancelled(false);
            return;
        }
        Game game = Main.getInstance().getGame();
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(e.getPlayer());
        if(playerData.isRespawn()) {
            e.setCancelled(true);
        }
        if(!game.isStarted()) {
            e.setCancelled(true);
            return;
        }
        if(!LocationUtil.inRegion(e.getPlayer(), playerData.getTeam().getPos1(), playerData.getTeam().getPos2())) {
            e.setCancelled(true);
            return;
        }
        if(e.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {
            e.setCancelled(false);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()-> e.getBlock().setType(Material.NETHER_QUARTZ_ORE), 100);
            return;
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e){
        Game game = Main.getInstance().getGame();
        if((e.getDamager() instanceof Player || (e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player)) && e.getEntity() instanceof Player) {
            if(!game.isStarted()) {
                e.setCancelled(true);
                return;
            }
            Player damagerPlayer;
            if(e.getDamager() instanceof Player) {
                damagerPlayer = (Player) e.getDamager();
            } else {
                Arrow arrow = (Arrow) e.getDamager();
                damagerPlayer = (Player) arrow.getShooter();
            }
            PlayerData damager = Main.getInstance().getPlayerManager().findBy(damagerPlayer);
            PlayerData player = Main.getInstance().getPlayerManager().findBy((Player)e.getEntity());

            if(damager.getTeam() == player.getTeam()) {
                e.setCancelled(true);
            } else if(game.isStarted() && game.getGameState() != GameState.WAR) {
                if(!LocationUtil.inRegion(damager.getPlayer(), damager.getTeam().getPos1(), damager.getTeam().getPos2())) {
                    e.setCancelled(true);
                }
            }
        }else if(e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            PlayerData playerData = Main.getInstance().getPlayerManager().findBy(player);
            if(playerData.isRespawn()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(player);
        if(!Main.getInstance().getGame().isStarted()) return;
        if(playerData.getTeam() == null) return;
        if(LocationUtil.inRegion(player, playerData.getOpponent().getPos1(), playerData.getOpponent().getPos2())) {
            if(playerData.getOpponent().getOnline() < 1) {
                e.setTo(e.getFrom());
                player.sendMessage(StringUtil.translate("&cThere are no opponent players online, you can't go in their territory. To go back at home type /suicide or wait at least 1 opponent player online!"));
            }
            if(Main.getInstance().getGame().getGameState() == GameState.WAR && player.getGameMode() == GameMode.SURVIVAL) {
                player.setGameMode(GameMode.ADVENTURE);
            }
        } else if(LocationUtil.inRegion(player, playerData.getTeam().getPos1(), playerData.getTeam().getPos2())) {
            if(Main.getInstance().getGame().getGameState() == GameState.WAR) {
                if(player.getInventory().contains(ItemUtil.flagItem(playerData.getOpponent()))) {
                    Main.getInstance().getGame().setGameState(GameState.ENDING);
                    Bukkit.getOnlinePlayers().forEach(p-> {
                        p.setGameMode(GameMode.CREATIVE);
                        p.sendTitle(StringUtil.translate(playerData.getTeam().getColor() + playerData.getTeam().getName() + " &ewins!"), "", 5, 40, 5);
                    });
                }
            }
            if(Main.getInstance().getGame().getGameState() == GameState.WAR && player.getGameMode() == GameMode.ADVENTURE) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
        if(playerData.isRespawn()) {
            e.setTo(e.getFrom());
        }
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e){
        Player player = e.getEntity();
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(player);
        playerData.setLastLocation(null);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()->{
            player.spigot().respawn();
            if(playerData.getTeam() == null) return;
            if(playerData.getTeam().getHome() != null) player.teleport(playerData.getTeam().getHome());
            else player.teleport(playerData.getTeam().getSpawn());
        }, 5);
        if(Main.getInstance().getGame().getGameState() == GameState.WAR) {
            if(player.getInventory().contains(ItemUtil.flagItem(playerData.getOpponent()))) {
                player.getInventory().removeItem(ItemUtil.flagItem(playerData.getOpponent()));
                if(playerData.getBlock().getState() instanceof Chest) {
                    Chest chest = (Chest) playerData.getBlock().getState();
                    chest.getBlockInventory().addItem(ItemUtil.flagItem(playerData.getOpponent()));
                    playerData.setBlock(null);
                    Bukkit.getOnlinePlayers().forEach(p-> p.sendTitle(StringUtil.translate("&cAlert!"), StringUtil.translate(playerData.getOpponent().getColor() + playerData.getOpponent().getName() + " &eflag lost!"), 5, 40, 5));
                }
            }
            playerData.setRespawn(true);
            playerData.setRespawnTime(System.currentTimeMillis() + 10000);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTornerai in vita tra 10 secondi!"));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(e.getPlayer());
        if(Main.getInstance().getGame().isStarted()) {
            if (e.getItemDrop().getItemStack().equals(ItemUtil.flagItem(playerData.getOpponent())) || e.getItemDrop().getItemStack().equals(ItemUtil.flagItem(playerData.getTeam()))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(e.getPlayer());
        if(!Main.getInstance().getGame().isStarted() || Main.getInstance().getGame().getGameState() == GameState.ENDING) return;
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getClickedBlock().getState() instanceof Chest) {
                if (LocationUtil.inRegion(e.getPlayer(), playerData.getTeam().getPos1(), playerData.getTeam().getPos2())){
                    e.setCancelled(false);
                    return;
                }

                Chest c = (Chest) e.getClickedBlock().getState();
                if (!c.getBlockInventory().contains(ItemUtil.flagItem(playerData.getOpponent()))) {
                    e.setCancelled(true);
                }
            } else if(e.getClickedBlock().getState() instanceof Dispenser){
                if (LocationUtil.inRegion(e.getPlayer(), playerData.getTeam().getPos1(), playerData.getTeam().getPos2())){
                    e.setCancelled(false);
                    return;
                }
                e.setCancelled(true);
            } else if(e.getClickedBlock().getState() instanceof Furnace) {
                if (LocationUtil.inRegion(e.getPlayer(), playerData.getTeam().getPos1(), playerData.getTeam().getPos2())){
                    e.setCancelled(false);
                    return;
                }
                e.setCancelled(true);
            } else if(e.getClickedBlock().getState() instanceof Dropper) {
                if (LocationUtil.inRegion(e.getPlayer(), playerData.getTeam().getPos1(), playerData.getTeam().getPos2())){
                    e.setCancelled(false);
                    return;
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            PlayerData playerData = Main.getInstance().getPlayerManager().findBy((Player)e.getWhoClicked());
            if(Main.getInstance().getGame().isStarted()) {
                if (LocationUtil.inRegion((Player) e.getWhoClicked(), playerData.getTeam().getPos1(), playerData.getTeam().getPos2())) {
                    e.setCancelled(false);
                    return;
                }
                if (Main.getInstance().getGame().getGameState() != GameState.WAR && e.getInventory().getType() == InventoryType.CHEST) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            PlayerData playerData = Main.getInstance().getPlayerManager().findBy(player);
            if(!Main.getInstance().getGame().isStarted()) return;
            if(Main.getInstance().getGame().getGameState() != GameState.WAR) return;
            if(e.getInventory().getType() == InventoryType.CHEST && LocationUtil.inRegion(player, playerData.getOpponent().getPos1(), playerData.getOpponent().getPos2())) {
                if(player.getInventory().contains(ItemUtil.flagItem(playerData.getOpponent()))) {
                    playerData.setBlock(player.getLocation().getWorld().getBlockAt(e.getInventory().getLocation()));
                    Bukkit.getOnlinePlayers().forEach(p-> p.sendTitle(StringUtil.translate("&cAlert!"), StringUtil.translate(playerData.getOpponent().getColor() + playerData.getOpponent().getName() + " &eflag taken!"), 5, 40, 5));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {

        Material bucket = e.getBucket();

        if (bucket.toString().contains("LAVA") || bucket.toString().contains("WATER")) {
            if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                e.setCancelled(false);
                return;
            }
            Game game = Main.getInstance().getGame();
            PlayerData playerData = Main.getInstance().getPlayerManager().findBy(e.getPlayer());
            if(playerData.isRespawn()) {
                e.setCancelled(true);
                return;
            }

            if(!game.isStarted()) {
                e.setCancelled(true);
                return;
            }
            if(!LocationUtil.inRegion(e.getPlayer(), playerData.getTeam().getPos1(), playerData.getTeam().getPos2())) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN || e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            Player player = e.getPlayer();
            PlayerData playerData = Main.getInstance().getPlayerManager().findBy(player);
            playerData.setLastLocation(player.getLocation());
        }
    }

}
