package com.zmanuel.teamwars.managers;

import com.google.common.collect.Lists;
import com.zmanuel.teamwars.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerManager {

    private List<PlayerData> dataList = Lists.newArrayList();

    public PlayerData findBy(Player player) {
        return dataList.stream().filter(playerData -> playerData.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public List<PlayerData> getDataList() {
        return dataList;
    }
}
