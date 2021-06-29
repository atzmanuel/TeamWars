package com.zmanuel.teamwars.tasks;

import com.zmanuel.teamwars.Main;
import org.bukkit.GameMode;

public class RespawnTask implements Runnable {

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
    @Override
    public void run() {
        Main.getInstance().getPlayerManager().getDataList().forEach(data -> {
            if(data.isRespawn() && System.currentTimeMillis() > data.getRespawnTime()) {
                data.setRespawn(false);
                data.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        });
    }
}
