package com.zmanuel.warzone.command.commands;

import com.zmanuel.warzone.Main;
import com.zmanuel.warzone.command.PCommand;
import com.zmanuel.warzone.data.PlayerData;
import com.zmanuel.warzone.utils.LocationUtil;
import com.zmanuel.warzone.utils.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends PCommand {

    public SpawnCommand() {
        super("spawn", false);
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

        if(!LocationUtil.inRegion(player, playerData.getTeam().getPos1(), playerData.getTeam().getPos2())) {
            sender.sendMessage(StringUtil.translate("&cYou can't use /spawn in the enemy territory. If you are trapped type /suicide."));
            return;
        }

        player.teleport(playerData.getTeam().getSpawn());
        sender.sendMessage(StringUtil.translate("&aHere you are."));
    }
}
