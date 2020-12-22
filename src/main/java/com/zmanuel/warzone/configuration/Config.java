package com.zmanuel.warzone.configuration;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {
    private static List<Config> configurations = Lists.newArrayList();

    public static Config getConfiguration(String filename){
        return configurations.stream().filter(configuration -> configuration.file.getName().equals(filename)).findFirst().orElse(null);
    }

    @Getter private FileConfiguration config;
    @Getter private File file;

    public Config(JavaPlugin instance, String filename, boolean saveResource) {
        file = new File(instance.getDataFolder(), filename + ".yml");
        if(!file.exists()){
            file.getParentFile().mkdir();
            if(saveResource) instance.saveResource(filename + ".yml", true);
        }
        load();
        configurations.add(this);
    }

    public void load(){
        try {
            config = YamlConfiguration.loadConfiguration(file);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void save(){
        try {
            config.save(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}