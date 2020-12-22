package com.zmanuel.warzone.adapters;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.zmanuel.warzone.Main;
import com.zmanuel.warzone.data.GameState;
import com.zmanuel.warzone.data.PlayerData;
import com.zmanuel.warzone.utils.StringUtil;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScoreboardAdapter implements BoardAdapter {

    @Override
    public String getTitle(Player player) {
        return StringUtil.translate("&c&lTeam&9&lWars");
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> scores = new ArrayList<>();
        PlayerData playerData = Main.getInstance().getPlayerManager().findBy(player);

        scores.add("&7&m--------------------");
        scores.add("&fTeam: " + (playerData.getTeam() == null ? "None" : playerData.getTeam().getColor() + playerData.getTeam().getName()));
        if(Main.getInstance().getGame().getGameState() == GameState.PREPARATION) scores.add("&fTime Left: " + ((Main.getInstance().getGame().getTime() - System.currentTimeMillis()) > 0 ? DurationFormatUtils.formatDuration(Main.getInstance().getGame().getTime() - System.currentTimeMillis(), "dd:HH:mm:ss") : "War started"));
        scores.add("&7&m-------------------");

        return scores;
    }
}
