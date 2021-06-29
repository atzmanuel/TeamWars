package com.zmanuel.teamwars.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.zmanuel.teamwars.database.Database;
import com.zmanuel.teamwars.utils.LocationUtil;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Location;

@Data
public class Settings {

    private Location center;
    private int radius;
    private Direction direction;
    private Location wallPos1;
    private Location wallPos2;

    public Settings() {
        load();
    }

    public void load() {
        Document document = Database.getInstance().getSettings().find(Filters.eq("name", "settings")).first();

        if(document == null) {
            save();
            return;
        }
        if(document.getString("center") != null)this.center = LocationUtil.fromString(document.getString("center"));
        this.radius = document.getInteger("radius");
        if(document.getString("direction") != null)this.direction = Direction.getDirection(document.getString("direction"));
        if(document.getString("wallPos1") != null)this.wallPos1 = LocationUtil.fromString(document.getString("wallPos1"));
        if(document.getString("wallPos2") != null) this.wallPos2 = LocationUtil.fromString(document.getString("wallPos2"));
    }


    public void save() {
        if(center == null || direction == null) return;
        Document document = new Document();
        document.put("name", "settings");
        document.put("center", LocationUtil.fromLocation(center));
        document.put("radius", radius);
        document.put("direction", direction.toString());
        if(wallPos1 != null) document.put("wallPos1", LocationUtil.fromLocation(wallPos1));
        if(wallPos2 != null) document.put("wallPos2", LocationUtil.fromLocation(wallPos2));
        Database.getInstance().getSettings().replaceOne(Filters.eq("name", "settings"), document, new ReplaceOptions().upsert(true));
    }
}
