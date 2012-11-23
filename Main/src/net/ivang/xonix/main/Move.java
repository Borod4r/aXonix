package net.ivang.xonix.main;

import java.util.Random;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public enum Move {
    UP,
    UP_RIGHT,
    RIGHT,
    DOWN_RIGHT,
    DOWN,
    DOWN_LEFT,
    LEFT,
    UP_LEFT,
    IDLE;

    private static final Move[] VALUES = values();
    private static final Move[] VALUES_DIAGONAL = new Move[] {UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT};
    private static final Random RANDOM = new Random();

    public static Move getRandom() {
        return VALUES[(RANDOM.nextInt(VALUES.length))];
    }

    public static Move getRandomDiagonal() {
        return VALUES_DIAGONAL[(RANDOM.nextInt(VALUES_DIAGONAL.length))];
    }

}
