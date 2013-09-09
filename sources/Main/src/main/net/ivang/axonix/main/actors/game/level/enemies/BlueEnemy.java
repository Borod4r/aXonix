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

package net.ivang.axonix.main.actors.game.level.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;

/**
 * @author Ivan Gadzhega
 * @since 0.4
 */
public class BlueEnemy extends Enemy {

    public BlueEnemy(float x, float y, Skin skin, Vector2 direction, EventBus eventBus) {
        super(x, y, 0.5f, direction, eventBus);
        particleEffect.load(Gdx.files.internal("data/particles/enemy_blue.p"), skin.getAtlas());
    }

}
