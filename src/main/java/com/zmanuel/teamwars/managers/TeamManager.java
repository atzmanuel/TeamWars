package com.zmanuel.teamwars.managers;

import com.google.common.collect.Lists;
import com.mongodb.Block;
import com.zmanuel.teamwars.data.Team;
import com.zmanuel.teamwars.database.Database;
import com.zmanuel.teamwars.utils.LocationUtil;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.List;

public class TeamManager {

    @Getter private List<Team> teams = Lists.newArrayList();

    public Team findByName(String name) {
        return teams.stream().filter(team -> team.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void loadTeams() {
        Database.getInstance().getTeams().find().forEach((Block<? super Document>) (Document document) ->{
            Team team = new Team(document.getString("name"), LocationUtil.fromString(document.getString("pos1")), LocationUtil.fromString(document.getString("pos2")), ChatColor.getByChar(document.getString("color")));
            if(document.getString("home") != null) team.setHome(LocationUtil.fromString(document.getString("home")));
            if(document.getString("spawn") != null) team.setSpawn(LocationUtil.fromString(document.getString("spawn")));
            team.setMembers((List<String>) document.get("members"));
        });
    }

}
