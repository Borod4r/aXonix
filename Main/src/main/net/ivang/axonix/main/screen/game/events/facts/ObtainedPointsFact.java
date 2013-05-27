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

package net.ivang.axonix.main.screen.game.events.facts;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class ObtainedPointsFact {
    private int points;
    private float x, y;
    private float deltaY;
    private boolean subtractBounds;

    public ObtainedPointsFact(int points, float x, float y, float deltaY, boolean subtractBounds) {
        this.points = points;
        this.x = x;
        this.y = y;
        this.deltaY = deltaY;
        this.subtractBounds = subtractBounds;
    }

    public int getPoints() {
        return points;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDeltaY() {
        return deltaY;
    }

    public boolean isSubtractBounds() {
        return subtractBounds;
    }
}
