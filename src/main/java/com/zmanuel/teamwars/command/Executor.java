package com.zmanuel.teamwars.command;

import com.zmanuel.teamwars.Main;
import com.zmanuel.teamwars.utils.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class Executor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {



        if(Main.getInstance().getCommands().containsKey(label)){

            PCommand pCommand = Main.getInstance().getCommands().get(label);

            if(commandSender instanceof ConsoleCommandSender && !pCommand.isConsole()){
                commandSender.sendMessage(StringUtil.translate("&cThe command /" + label + " is disabled from console!"));
                return true;
            }

            if(pCommand.getPerm() != null){
                if(!commandSender.hasPermission(pCommand.getPerm())){
                    commandSender.sendMessage(StringUtil.translate("&cYou don't have the permission to execute this command!"));
                    return true;
                }
            }

            pCommand.execute(commandSender, args, label);

        }

        return true;
    }
}
