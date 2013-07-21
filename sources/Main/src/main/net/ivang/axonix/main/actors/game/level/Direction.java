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

package net.ivang.axonix.main.actors.game.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public enum Direction {
    UP(0, 1),
    UP_RIGHT(1, 1),
    RIGHT(1, 0),
    DOWN_RIGHT(1, -1),
    DOWN(0, -1),
    DOWN_LEFT(-1, -1),
    LEFT(-1, 0),
    UP_LEFT(-1, 1),
    IDLE(0, 0);

    private static final Direction[] VALUES = values();
    private static final Direction[] VALUES_DIAGONAL = new Direction[] {UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT};

    private final Vector2 unitVector;

    private Direction(float x, float y) {
        this.unitVector = new Vector2(x, y).nor();
    }

    public static Direction getRandom() {
        return VALUES[(MathUtils.random(VALUES.length - 1))];
    }

    public static Direction getRandomDiagonal() {
        return VALUES_DIAGONAL[(MathUtils.random(VALUES_DIAGONAL.length - 1))];
    }

    public Vector2 getUnitVector() {
        return unitVector.cpy();
    }
}
