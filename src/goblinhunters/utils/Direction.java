package goblinhunters.utils;

import java.util.Random;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    private static final Random RND = new Random();

    public static Direction getRandom() {
        return values()[RND.nextInt(values().length)];
    }
}
