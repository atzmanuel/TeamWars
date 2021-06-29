package com.zmanuel.teamwars.command.commands;

import com.zmanuel.teamwars.Main;
import com.zmanuel.teamwars.command.PCommand;
import com.zmanuel.teamwars.data.PlayerData;
import com.zmanuel.teamwars.utils.LocationUtil;
import com.zmanuel.teamwars.utils.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand extends PCommand {

    public SetHomeCommand() {
        super("sethome", false);
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
            sender.sendMessage(StringUtil.translate("&cYou can't use /sethome in the enemy territory."));
            return;
        }
        playerData.getTeam().setHome(player.getLocation());
        playerData.getTeam().sendTeamMessage("&e" + player.getName() + " ha impostato nuovamente la home del team!");
        playerData.getTeam().save();
    }
}
