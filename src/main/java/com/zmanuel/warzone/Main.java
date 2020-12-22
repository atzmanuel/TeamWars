package com.zmanuel.warzone;

import com.bizarrealex.aether.Aether;
import com.zmanuel.warzone.adapters.ScoreboardAdapter;
import com.zmanuel.warzone.command.PCommand;
import com.zmanuel.warzone.command.commands.*;
import com.zmanuel.warzone.configuration.Config;
import com.zmanuel.warzone.data.Game;
import com.zmanuel.warzone.data.Settings;
import com.zmanuel.warzone.database.Database;
import com.zmanuel.warzone.listeners.PlayerListener;
import com.zmanuel.warzone.listeners.WorldHeightLimit;
import com.zmanuel.warzone.managers.PlayerManager;
import com.zmanuel.warzone.managers.TeamManager;
import com.zmanuel.warzone.tasks.RespawnTask;
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

        new Aether(this, new ScoreboardAdapter());

        RespawnTask respawnTask = new RespawnTask();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, respawnTask, 0L, 10L);
    }

}
