package com.zmanuel.warzone.tasks;

import com.zmanuel.warzone.Main;
import com.zmanuel.warzone.data.Game;
import com.zmanuel.warzone.data.GameState;
import com.zmanuel.warzone.utils.LocationUtil;
import com.zmanuel.warzone.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;

public class TimerTask implements Runnable {

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Getter
    @Setter
    private int id = 0;
    @Override
    public void run() {
        Game game = Main.getInstance().getGame();
        if(game.isStarted() && System.currentTimeMillis() > game.getTime()) {
            game.setGameState(GameState.WAR);
            game.save();
            Bukkit.getOnlinePlayers().forEach(p-> {
                p.setGameMode(GameMode.ADVENTURE);
                p.sendTitle(StringUtil.translate("&c&lTeam&9&lWars"), StringUtil.translate("&6Fase II: &6Che la guerra abbia inizio"), 10, 100, 10);
            });
            LocationUtil.set(Main.getInstance().getSettings().getWallPos1(), Main.getInstance().getSettings().getWallPos2(), Material.GLASS);
            Bukkit.getScheduler().cancelTask(getId());
        }
    }
}
