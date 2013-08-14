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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
public class Enemy extends KinematicActor {

    private static final Vector2 UP_RIGHT = new Vector2(1, 1).nor();
    private static final Vector2 DOWN_RIGHT = new Vector2(1, -1).nor();
    private static final Vector2 DOWN_LEFT = new Vector2(-1, -1).nor();
    private static final Vector2 UP_LEFT = new Vector2(-1, 1).nor();

    private static final Vector2[] DIAGONALS = new Vector2[] {UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT};

    private static Vector2 getRandomDiagonal() {
        return DIAGONALS[(MathUtils.random(DIAGONALS.length - 1))];
    }

    //---------------------------------------------------------------------
    // Instance
    //---------------------------------------------------------------------

    private Circle collisionCircle;
    private List<Effect> effects;

    private TextureRegion region;
    private ParticleEffect particleEffect;

    public Enemy(float x, float y, Skin skin, EventBus eventBus) {
        this.collisionCircle = new Circle(x, y, 0.45f);
        this.effects = new ArrayList<Effect>();
        setX(x); setY(y);
        setWidth(1f);
        setHeight(1f);
        setOriginX(0.5f);
        setOriginY(0.5f);
        setSpeed(4f);
        setDirection(getRandomDiagonal());
        // appearance
        this.region = skin.getRegion("circular_flare");
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("data/particles/enemy.p"), skin.getAtlas());
        particleEffect.setPosition(x, y);
        // register with the event bus
        eventBus.register(this);
    }

    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);

        setX(getX() + direction.x * speed * deltaTime);
        setY(getY() + direction.y * speed * deltaTime);

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
        particleEffect.setPosition(getX(), getY());
        particleEffect.draw(batch);
        // draw texture
        batch.setColor(1, 0.2f, 0.1f, 1);
        batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getWidth(), getHeight());
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

}
