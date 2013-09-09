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

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.actors.game.KinematicActor;
import net.ivang.axonix.main.actors.game.level.bonuses.SlowBonus;
import net.ivang.axonix.main.effects.Effect;
import net.ivang.axonix.main.effects.SpeedEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public abstract class Enemy extends KinematicActor {

    protected boolean bouncingOffBlocks;
    protected boolean destroyingBlocks;

    protected TextureRegion region;
    protected ParticleEffect particleEffect;

    protected Circle collisionCircle;
    private List<Effect> effects;

    public Enemy(float x, float y, float radius, Vector2 direction, EventBus eventBus) {
        this.collisionCircle = new Circle(x, y, radius - 0.05f);
        this.effects = new ArrayList<Effect>();
        setX(x); setY(y);
        setWidth(radius * 2);
        setHeight(radius * 2);
        setOriginX(radius);
        setOriginY(radius);
        setSpeed(4f);
        setDirection(direction);
        particleEffect = new ParticleEffect();
        particleEffect.setPosition(x, y);
        // register with the event bus
        eventBus.register(this);
    }

    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);
        // position
        setX(getX() + direction.x * speed * deltaTime);
        setY(getY() + direction.y * speed * deltaTime);
        // particles
        particleEffect.setPosition(getX(), getY());
        particleEffect.update(deltaTime);
        // effects
        Iterator<Effect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().act(deltaTime)) {
                iterator.remove();
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        //draw particles
        particleEffect.draw(batch);
        // draw texture
        if (region != null) {
            batch.setColor(getColor());
            batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getWidth(), getHeight());
        }
        // draw effects
        for (Effect effect : effects) {
            effect.draw(batch);
        }
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onSlowBonus(SlowBonus bonus) {
        ParticleEffect particles = new ParticleEffect(bonus.getParticleEffect());
        effects.add(new SpeedEffect(this, 0.5f, 10, particles));
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    @Override
    public void setX(float x) {
        super.setX(x);
        collisionCircle.x = x;
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        collisionCircle.y = y;
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public boolean isBouncingOffBlocks() {
        return bouncingOffBlocks;
    }

    public boolean isDestroyingBlocks() {
        return destroyingBlocks;
    }
}
