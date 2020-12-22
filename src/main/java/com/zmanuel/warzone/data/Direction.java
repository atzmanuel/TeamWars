package com.zmanuel.warzone.data;

import java.util.Arrays;

public enum Direction {

    EAST_WEST,
    NORTH_SOUTH;

    public static Direction getDirection(String name){
        return Arrays.stream(Direction.values()).filter(direction -> direction.toString().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
