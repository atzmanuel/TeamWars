package com.zmanuel.warzone.command.commands;

import com.zmanuel.warzone.Main;
import com.zmanuel.warzone.command.PCommand;
import com.zmanuel.warzone.data.PlayerData;
import com.zmanuel.warzone.utils.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand extends PCommand {

    public BackCommand() {
        super("back", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args, String label) {
        Player player = (Player) sender;
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(player);
        if(playerData.getLastLocation() == null) {
            sender.sendMessage(StringUtil.translate("&cNope"));
            return;
        }
        player.teleport(playerData.getLastLocation());
        sender.sendMessage(StringUtil.translate("&aTeleporting..."));
    }
}
