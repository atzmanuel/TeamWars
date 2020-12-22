package com.zmanuel.warzone.command.commands;

import com.zmanuel.warzone.Main;
import com.zmanuel.warzone.command.PCommand;
import com.zmanuel.warzone.data.*;
import com.zmanuel.warzone.tasks.TimerTask;
import com.zmanuel.warzone.utils.ItemUtil;
import com.zmanuel.warzone.utils.LocationUtil;
import com.zmanuel.warzone.utils.StringUtil;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class TeamWarsCommand extends PCommand {


    public TeamWarsCommand() {
        super("teamwars", "teamwars.admin", false);
    }

    private Settings settings = Main.getInstance().getSettings();

    @Override
    public void execute(CommandSender sender, String[] args, String label) {

        Player player = (Player) sender;
        if(args.length == 0) {
            String help = "&9/teamwars setcenter\n&9/teamwars setradius <radius>\n&9/teamwars setdirection <EAST_WEST/NORTH_SOUTH>\n&9/teamwars settime <minutes>\n&9/teamwars ready\n&9/teamwars start\n&9/teamwars team setspawn <team>";
            for (String s : help.split("\\n")) {
                sender.sendMessage(StringUtil.translate(s));
            }
        } else if(args.length == 1) {
            switch(args[0].toLowerCase()){
                case "setcenter":
                    Location location = player.getLocation();
                    settings.setCenter(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                    settings.save();
                    sender.sendMessage(StringUtil.translate("&aMap Center Set"));
                    break;
                case "ready":

                    if(settings.getCenter() == null || settings.getDirection() == null || settings.getRadius() == 0) {
                        sender.sendMessage(StringUtil.translate("&cSettings not configured properly."));
                        return;
                    }

                    Bukkit.broadcastMessage(StringUtil.translate("&9Configuring worldborder..."));

                    WorldBorder wb = settings.getCenter().getWorld().getWorldBorder();
                    wb.setCenter(settings.getCenter());
                    wb.setSize(settings.getRadius() * 2);
                    Bukkit.broadcastMessage(StringUtil.translate("&9Creating bedrock wall..."));
                    if(settings.getDirection() == Direction.EAST_WEST) {
                        Location pos1 = new Location(settings.getCenter().getWorld(), settings.getCenter().getX() - settings.getRadius(), 0, settings.getCenter().getZ());
                        Location pos2 = new Location(settings.getCenter().getWorld(), settings.getCenter().getX() + settings.getRadius(), 256, settings.getCenter().getZ());
                        settings.setWallPos1(pos1);
                        settings.setWallPos2(pos2);
                        settings.save();
                        LocationUtil.set(pos1, pos2, Material.BEDROCK);
                    } else { //NORTH-SOUTH
                        Location pos1 = new Location(settings.getCenter().getWorld(), settings.getCenter().getX(), 0, settings.getCenter().getZ() - settings.getRadius());
                        Location pos2 = new Location(settings.getCenter().getWorld(), settings.getCenter().getX(), 256, settings.getCenter().getZ() + settings.getRadius());
                        settings.setWallPos1(pos1);
                        settings.setWallPos2(pos2);
                        settings.save();
                        LocationUtil.set(pos1, pos2, Material.BEDROCK);
                    }

                    Bukkit.broadcastMessage(StringUtil.translate("&9Creating teams..."));
                    //Teams: []
                    if(settings.getDirection() == Direction.EAST_WEST) {
                        new Team("Red", settings.getWallPos1().clone().subtract(0, 0, 1), settings.getWallPos2().clone().subtract(0, 0, settings.getRadius()), ChatColor.RED).save();
                        new Team("Blue", settings.getWallPos1().clone().add(0, 0, 1), settings.getWallPos2().clone().add(0, 0, settings.getRadius()), ChatColor.BLUE).save();
                    } else {
                        new Team("Red", settings.getWallPos1().clone().subtract(1, 0, 0), settings.getWallPos2().clone().subtract(settings.getRadius(), 0, 0), ChatColor.RED).save();
                        new Team("Blue", settings.getWallPos1().clone().add(1, 0, 0), settings.getWallPos2().clone().add(settings.getRadius(), 0, 0), ChatColor.BLUE).save();
                    }

                    Bukkit.broadcastMessage(StringUtil.translate("&9We're almost ready..."));

                    break;
                case "start":
                    if(Main.getInstance().getGame().isStarted()) {
                        sender.sendMessage(StringUtil.translate("&cGame already started"));
                        return;
                    }

                    if(settings.getCenter() == null || settings.getDirection() == null || settings.getRadius() == 0) {
                        sender.sendMessage(StringUtil.translate("&cSettings not configured properly."));
                        return;
                    }

                    if(Main.getInstance().getGame().getTeams().size() < 2) {
                        sender.sendMessage(StringUtil.translate("&cNot ready."));
                        return;
                    }

                        Bukkit.getOnlinePlayers().forEach(p -> {
                            PlayerData playerData = Main.getInstance().getPlayerManager().findBy(p);
                            if (playerData.getTeam() == null) {
                                Team random = Main.getInstance().getTeamManager().getTeams().get(ThreadLocalRandom.current().nextInt(Main.getInstance().getTeamManager().getTeams().size()));
                                playerData.joinTeam(random);
                            }
                        });

                    Team red = Main.getInstance().getTeamManager().findByName("Red");
                    Team blue = Main.getInstance().getTeamManager().findByName("Blue");
                    if(red.getMembers().size() != blue.getMembers().size()) {
                        if(red.getMembers().size() > blue.getMembers().size()) {
                            for(int i = 0; i < (red.getMembers().size() - blue.getMembers().size())/2; i++) {
                                PlayerData randomPlayer = Main.getInstance().getPlayerManager().findBy(Bukkit.getPlayer(red.getMembers().get(ThreadLocalRandom.current().nextInt(red.getMembers().size()))));
                                randomPlayer.leaveTeam();
                                randomPlayer.joinTeam(blue);
                            }
                        } else {
                            for(int i = 0; i < (blue.getMembers().size() - red.getMembers().size())/2; i++) {
                                PlayerData randomPlayer = Main.getInstance().getPlayerManager().findBy(Bukkit.getPlayer(blue.getMembers().get(ThreadLocalRandom.current().nextInt(blue.getMembers().size()))));
                                randomPlayer.leaveTeam();
                                randomPlayer.joinTeam(red);
                            }
                        }
                    }

                    TimerTask timerTask = new TimerTask();
                    timerTask.setId(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), timerTask, 0L, 10L));
                    Main.getInstance().getGame().setStarted(true);
                    Main.getInstance().getGame().setTime(System.currentTimeMillis() + Main.getInstance().getGame().getTime());
                    Main.getInstance().getGame().setGameState(GameState.PREPARATION);
                    Main.getInstance().getGame().save();
                    Main.getInstance().getTeamManager().getTeams().forEach(team -> {
                        Player pf = Bukkit.getPlayer(team.getMembers().get(ThreadLocalRandom.current().nextInt(team.getMembers().size())));
                        pf.getInventory().addItem(ItemUtil.flagItem(team));
                    });
                    Bukkit.getOnlinePlayers().forEach(p-> {
                        p.teleport(Main.getInstance().getPlayerManager().findBy(p).getTeam().getSpawn());
                        p.sendTitle(StringUtil.translate("&c&lTeam&9&lWars"), StringUtil.translate("&6Fase I: &6Preparazione"), 10, 100, 10);
                    });
                    break;
                default:

            }
        } else if(args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "setradius":
                    if(!StringUtil.isNumberic(args[1])) {
                        sender.sendMessage(StringUtil.translate("&cInvalid value"));
                        return;
                    }
                    settings.setRadius(Integer.parseInt(args[1]));
                    settings.save();
                    sender.sendMessage(StringUtil.translate("&aRadius set"));
                    break;
                case "setdirection":
                    Direction direction = Direction.getDirection(args[1]);
                    if(direction == null) {
                        sender.sendMessage(StringUtil.translate("&cInvalid direction"));
                        return;
                    }
                    settings.setDirection(direction);
                    settings.save();
                    sender.sendMessage(StringUtil.translate("&aDirection set"));
                    break;
                case "settime":
                    if(!StringUtil.isLong(args[1])) {
                        sender.sendMessage(StringUtil.translate("&cInvalid value"));
                        return;
                    }
                    long value = Long.parseLong(args[1]);
                    Main.getInstance().getGame().setTime((1000 * 60 * value));
                    Main.getInstance().getGame().save();
                    sender.sendMessage(StringUtil.translate("&aTime set"));
                    break;
            }
        } else if(args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "team":
                    switch (args[1].toLowerCase()) {
                        case "setspawn":
                            Team team = Main.getInstance().getTeamManager().findByName(args[2]);

                            if(team == null) {
                                sender.sendMessage(StringUtil.translate("&cTeam not found"));
                                return;
                            }

                            if(!LocationUtil.inRegion(player, team.getPos1(), team.getPos2())) {
                                sender.sendMessage(StringUtil.translate("&cYou cannot set spawn in another region!"));
                                return;
                            }

                            team.setSpawn(player.getLocation());
                            team.save();
                            sender.sendMessage(StringUtil.translate("&aSpawn set."));
                            break;
                    }

                    break;
            }
        } else if(args.length == 4) {
            switch (args[0].toLowerCase()) {
                case "team":
                    switch (args[1].toLowerCase()) {
                        case "join":
                            Team team = Main.getInstance().getTeamManager().findByName(args[2]);

                            if(team == null) {
                                sender.sendMessage(StringUtil.translate("&cTeam not found"));
                                return;
                            }

                            Player p = Bukkit.getPlayer(args[3]);
                            if(p == null) {
                                sender.sendMessage(StringUtil.translate("&cPlayer not online."));
                                return;
                            }

                            PlayerData playerData = Main.getInstance().getPlayerManager().findBy(p);
                            playerData.leaveTeam();
                            playerData.joinTeam(team);
                            sender.sendMessage(StringUtil.translate("&aTeam changed!"));
                            break;
                        case "leave":
                            Team t = Main.getInstance().getTeamManager().findByName(args[2]);

                            if(t == null) {
                                sender.sendMessage(StringUtil.translate("&cTeam not found"));
                                return;
                            }

                            Player p1 = Bukkit.getPlayer(args[3]);
                            if(p1 == null) {
                                sender.sendMessage(StringUtil.translate("&cPlayer not online."));
                                return;
                            }

                            PlayerData pData = Main.getInstance().getPlayerManager().findBy(p1);
                            pData.leaveTeam();
                            sender.sendMessage(StringUtil.translate("&aTeam removed!"));
                            break;
                    }
                break;
            }
        }

    }
}
