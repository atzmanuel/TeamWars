package com.zmanuel.teamwars.command.commands;

import com.zmanuel.teamwars.command.PCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuicideCommand extends PCommand {

    public SuicideCommand() {
        super("suicide", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args, String label) {

        Player player = (Player) sender;
        player.setHealth(0);
    }
}
