package com.zmanuel.teamwars;

import com.bizarrealex.aether.Aether;
import com.zmanuel.teamwars.adapters.ScoreboardAdapter;
import com.zmanuel.teamwars.command.PCommand;
import com.zmanuel.teamwars.command.commands.*;
import com.zmanuel.teamwars.configuration.Config;
import com.zmanuel.teamwars.data.Game;
import com.zmanuel.teamwars.data.Settings;
import com.zmanuel.teamwars.database.Database;
import com.zmanuel.teamwars.listeners.PlayerListener;
import com.zmanuel.teamwars.listeners.WorldHeightLimit;
import com.zmanuel.teamwars.managers.PlayerManager;
import com.zmanuel.teamwars.managers.TeamManager;
import com.zmanuel.teamwars.tasks.RespawnTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    @Getter private static Main instance;

    @Getter private PlayerManager playerManager;
    @Getter private TeamManager teamManager;

    @Getter private FileConfiguration config;

    @Getter private Game game;
    @Getter private Settings settings;

    @Getter
    @Setter
    private Map<String, PCommand> commands;


    @Override
    public void onEnable() {
        instance = this;
        this.config = new Config(this, "config", true).getConfig();
        new Database();
        this.settings = new Settings();
        this.playerManager = new PlayerManager();
        this.teamManager = new TeamManager();
        this.teamManager.loadTeams();
        this.game = new Game();
        this.commands = new HashMap<>();
        new WorldHeightLimit();
        new PlayerListener();

        new TeamWarsCommand();
        new SpawnCommand();
        new SetHomeCommand();
        new HomeCommand();
        new BackCommand();
        new SuicideCommand();

        new Aether(this, new ScoreboardAdapter());

        RespawnTask respawnTask = new RespawnTask();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, respawnTask, 0L, 10L);
    }

}
