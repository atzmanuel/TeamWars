package com.zmanuel.warzone.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.zmanuel.warzone.Main;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

@Getter
public class Database {

    @Getter
    private static Database instance;
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> players;
    private MongoCollection<Document> teams;
    private MongoCollection<Document> settings;
    private MongoCollection<Document> games;

    public Database() {
        if(instance != null){
            throw new RuntimeException("The mongo database has already been instantiated.");
        }

        instance = this;

        FileConfiguration config = Main.getInstance().getConfig();
        if(config.getBoolean("MONGO.AUTH.ENABLED")){
            final String USERNAME = config.getString("MONGO.AUTH.USERNAME");
            final String PASSWORD = config.getString("MONGO.AUTH.PASSWORD");
            final String DATABASE = config.getString("MONGO.AUTH.DATABASE");
            MongoCredential credential = MongoCredential.createCredential(USERNAME, DATABASE, PASSWORD.toCharArray());
            this.client =  new MongoClient(new ServerAddress(config.getString("MONGO.HOST"), config.getInt("MONGO.PORT")), Collections.singletonList(credential));
        }else{
            this.client = new MongoClient(new ServerAddress(config.getString("MONGO.HOST"), config.getInt("MONGO.PORT")));
        }
        this.database = this.client.getDatabase("teamwars");
        this.players = this.database.getCollection("players");
        this.teams = this.database.getCollection("teams");
        this.settings = this.database.getCollection("settings");
        this.games = this.database.getCollection("games");
    }

}
