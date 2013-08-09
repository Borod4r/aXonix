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

package net.ivang.axonix.main.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * @author Ivan Gadzhega
 * @since 0.3
 */
public abstract class Effect {

    private float duration, time;
    private boolean complete;

    public Effect(float duration) {
        this.duration = duration;
    }

    public boolean act(float delta) {
        complete = complete || time >= duration;
        if (!complete) {
            if (time == 0) {
                begin();
            } else {
                update(delta);
            }
            time += delta;
        } else {
            end();
        }

        return complete;
    }

    public void complete() {
        complete = true;
    }

    public abstract void draw(SpriteBatch batch);

    protected abstract void begin();

    protected abstract void update(float delta);

    protected abstract void end();

}
