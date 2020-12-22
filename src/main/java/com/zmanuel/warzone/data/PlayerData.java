package com.zmanuel.warzone.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.zmanuel.warzone.Main;
import com.zmanuel.warzone.database.Database;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

@Data
public class PlayerData {

    private Player player;
    private boolean respawn;
    private long respawnTime;
    private Team team;
    private Block block;
    private Location lastLocation;

    public PlayerData(Player player) {
        this.player = player;
        this.respawn = false;
        Main.getInstance().getPlayerManager().getDataList().add(this);
        load();
    }

    public void load() {

        Document profile = Database.getInstance().getPlayers().find(Filters.eq("name", player.getName())).first();

        if(profile == null) {
            save();
            chooseTeam();
            return;
        }

        if(profile.getString("team") != null) this.setTeam(Main.getInstance().getTeamManager().findByName(profile.getString("team")));
        chooseTeam();
        loadTeamColor();

    }

    public void save() {
        Document profile = new Document();
        profile.put("name", player.getName());
        if(team != null) profile.put("team", team.getName());
        Database.getInstance().getPlayers().replaceOne(Filters.eq("name", player.getName()), profile, new ReplaceOptions().upsert(true));
    }

    public void chooseTeam() {
        if(team == null && Main.getInstance().getGame().isStarted()) {
            Team red = Main.getInstance().getTeamManager().findByName("Red");
            Team blue = Main.getInstance().getTeamManager().findByName("Blue");
            if(red.getMembers().size() == blue.getMembers().size()) {
                Team random = Main.getInstance().getTeamManager().getTeams().get(ThreadLocalRandom.current().nextInt(Main.getInstance().getTeamManager().getTeams().size()));
                joinTeam(random);
            } else if(red.getMembers().size() > blue.getMembers().size()){
                joinTeam(blue);
            } else {
                joinTeam(red);
            }
        }
    }

    public void joinTeam(Team team) {
        this.team = team;
        team.addMember(this.player.getName());
        team.save();
        save();
        loadTeamColor();
    }

    public void leaveTeam() {
        this.team.removeMember(this.player.getName());
        team.save();
        this.team = null;
        save();
        loadTeamColor();
    }

    public void loadTeamColor() {
        if(team == null) {
            player.setPlayerListName(player.getName());
            return;
        }
        player.setPlayerListName(team.getColor() + player.getName());
    }

    public Team getOpponent() {
        return Main.getInstance().getTeamManager().getTeams().stream().filter(team1 -> !team1.getName().equalsIgnoreCase(team.getName())).findFirst().orElse(null);
    }
}
