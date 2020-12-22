package com.zmanuel.warzone.data;

import java.util.Arrays;

public enum GameState {

    BEFORE_GAME,
    PREPARATION,
    WAR,
    ENDING;

    public static GameState getGameState(String name){
        return Arrays.stream(GameState.values()).filter(gameState -> gameState.toString().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
