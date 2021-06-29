package com.zmanuel.teamwars.data;

import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.zmanuel.teamwars.Main;
import com.zmanuel.teamwars.database.Database;
import com.zmanuel.teamwars.utils.LocationUtil;
import com.zmanuel.teamwars.utils.StringUtil;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@Data
public class Team {

    private String name;
    private Location home;
    private Location spawn;
    private List<String> members;
    private Location pos1;
    private Location pos2;
    private ChatColor color;

    public Team(String name, Location pos1, Location pos2, ChatColor color) {
        if(Main.getInstance().getTeamManager().findByName(name) != null) return;
        this.name = name;
        this.members = Lists.newArrayList();
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.color = color;
        Main.getInstance().getTeamManager().getTeams().add(this);
    }

    public void save() {

        Document document = new Document();
        document.put("name", name);
        if(home != null) document.put("home", LocationUtil.fromLocation(home));
        if(spawn != null) document.put("spawn", LocationUtil.fromLocation(spawn));
        document.put("pos1", LocationUtil.fromLocation(pos1));
        document.put("pos2", LocationUtil.fromLocation(pos2));
        document.put("color", color.getChar());
        document.put("members", members);
        Database.getInstance().getTeams().replaceOne(Filters.eq("name", name), document, new ReplaceOptions().upsert(true));

    }

    public void addMember(String name) {
        getMembers().add(name);
    }

    public void removeMember(String name) {
        getMembers().remove(name);
    }

    public void sendTeamMessage(String msg) {
        members.forEach(member ->{
            Player memberPlayer = Bukkit.getPlayer(member);
            if(memberPlayer != null) {
                memberPlayer.sendMessage(StringUtil.translate(msg));
            }
        });
    }

    public int getOnline() {
        return (int) members.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).count();
    }
}
