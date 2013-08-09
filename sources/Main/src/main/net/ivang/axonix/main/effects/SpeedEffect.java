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

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.ivang.axonix.main.actors.game.KinematicActor;

/**
 * @author Ivan Gadzhega
 * @since 0.3
 */
public class SpeedEffect extends Effect {

    private KinematicActor actor;
    private float multiplier;

    private ParticleEffect particleEffect;

    public SpeedEffect(KinematicActor actor, float multiplier, float duration, ParticleEffect particleEffect) {
        super(duration);
        this.actor = actor;
        this.multiplier = multiplier;
        this.particleEffect = particleEffect;
    }

    public void draw(SpriteBatch batch) {
        particleEffect.draw(batch);
    }

    protected void begin() {
        actor.setSpeed(actor.getSpeed() * multiplier);
        particleEffect.start();
    }

    protected void update(float delta) {
        particleEffect.setPosition(actor.getX(), actor.getY());
        particleEffect.update(delta);
    }

    protected void end() {
        actor.setSpeed(actor.getSpeed() / multiplier);
    }

}
