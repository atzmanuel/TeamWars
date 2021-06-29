package com.zmanuel.teamwars.command.commands;

import com.zmanuel.teamwars.Main;
import com.zmanuel.teamwars.command.PCommand;
import com.zmanuel.teamwars.data.PlayerData;
import com.zmanuel.teamwars.utils.LocationUtil;
import com.zmanuel.teamwars.utils.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand extends PCommand {

    public HomeCommand() {
        super("home", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args, String label) {
        if(!Main.getInstance().getGame().isStarted()) {
            sender.sendMessage(StringUtil.translate("&cGame not started."));
            return;
        }
        Player player = (Player) sender;
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(player);

        if(playerData.getTeam() == null) {
            sender.sendMessage(StringUtil.translate("&cYou're team is null. Contact an administrator"));
            return;
        }

        if(playerData.getTeam().getHome() == null) {
            sender.sendMessage(StringUtil.translate("&cTeam home not set."));
            return;
        }

        if(!LocationUtil.inRegion(player, playerData.getTeam().getPos1(), playerData.getTeam().getPos2())) {
            sender.sendMessage(StringUtil.translate("&cYou can't use /home in the enemy territory. If you are trapped type /suicide."));
            return;
        }

        player.teleport(playerData.getTeam().getHome());
        sender.sendMessage(StringUtil.translate("&aHome sweet home!"));
    }
}
