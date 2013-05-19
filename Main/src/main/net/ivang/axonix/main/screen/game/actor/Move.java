/*
 * Copyright 2012-2013 Ivan Gadzhega
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.ivang.axonix.main.screen.game.actor;

import java.util.Random;

/**
 * @author Ivan Gadzhega
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
