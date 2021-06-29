package com.zmanuel.teamwars.command;

import com.zmanuel.teamwars.Main;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
public abstract class PCommand {

    private String label, perm;
    private boolean console;

    public PCommand(String label, boolean console) {
        this.label = label;
        this.console = console;
        Main.getInstance().getCommand(label).setExecutor(new Executor());
        Main.getInstance().getCommands().put(label, this);
        Main.getInstance().getCommand(label).getAliases().forEach(s -> Main.getInstance().getCommands().put(s, this));
    }

    public PCommand(String label, String perm, boolean console) {
        this.label = label;
        this.perm = perm;
        this.console = console;
        Main.getInstance().getCommand(label).setExecutor(new Executor());
        Main.getInstance().getCommands().put(label, this);
        Main.getInstance().getCommand(label).getAliases().forEach(s -> Main.getInstance().getCommands().put(s, this));
    }

    public abstract void execute(CommandSender sender, String[] args, String label);

}
