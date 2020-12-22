package com.zmanuel.warzone.data;

import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.zmanuel.warzone.Main;
import com.zmanuel.warzone.database.Database;
import com.zmanuel.warzone.tasks.TimerTask;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Game {

    private List<Team> teams;
    private GameState gameState;
    private boolean started;
    private long time;

    public Game() {
        this.teams = Main.getInstance().getTeamManager().getTeams();
        this.gameState = GameState.BEFORE_GAME;
        this.started = false;
        this.time = 0;
        load();
    }

    public void load() {
        Document document = Database.getInstance().getGames().find(Filters.eq("game", 0)).first();

        if(document == null) {
            save();
            return;
        }

        ((List<String>) document.get("teams")).forEach(team -> teams.add(Main.getInstance().getTeamManager().findByName(team)));
        this.started = document.getBoolean("started");
        this.time = document.getLong("time");
        this.gameState = GameState.getGameState(document.getString("gameState"));
        TimerTask timerTask = new TimerTask();
        timerTask.setId(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), timerTask, 0L, 10L));

    }

    public void save() {
        Document document = new Document();
        document.put("game", 0);
        document.put("teams", teams.stream().map(Team::getName).collect(Collectors.toList()));
        document.put("started", started);
        document.put("gameState", gameState.toString());
        document.put("time", time);
        Database.getInstance().getGames().replaceOne(Filters.eq("game", 0), document, new ReplaceOptions().upsert(true));
    }
}
