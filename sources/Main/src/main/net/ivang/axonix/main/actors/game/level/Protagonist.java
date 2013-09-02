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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.actors.game.KinematicActor;
import net.ivang.axonix.main.actors.game.level.blocks.Block;
import net.ivang.axonix.main.actors.game.level.bonuses.SpeedBonus;
import net.ivang.axonix.main.effects.Effect;
import net.ivang.axonix.main.effects.SpeedEffect;
import net.ivang.axonix.main.events.intents.game.LivesIntent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.ivang.axonix.main.actors.game.level.blocks.Block.Type;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Protagonist extends KinematicActor {

    private static final Vector2 IDLE = new Vector2(0, 0);
    private static final Vector2 UP = new Vector2(0, 1);
    private static final Vector2 RIGHT = new Vector2(1, 0);
    private static final Vector2 DOWN = new Vector2(0, -1);
    private static final Vector2 LEFT = new Vector2(-1, 0);

    private State state;
    private List<Effect> effects;

    private float spawnX, spawnY;
    private float prevX, prevY;
    private Circle collisionCircle;

    private Level level;

    private TextureRegion region;
    private ParticleEffect particleAlive;
    private ParticleEffect particleDead;

    private EventBus eventBus;

    public Protagonist(float x, float y, Level level, Skin skin, EventBus eventBus) {
        this.state = State.ALIVE;
        this.level = level;
        this.region = skin.getRegion("circular_flare");
        this.collisionCircle = new Circle(x, y, 0.4f);

        setX(x); setY(y);
        setSpawnX(x); setSpawnY(y);
        setPrevX(x); setPrevY(y);
        setSpeed(4f);
        setDirection(IDLE);

        setWidth(1.5f);
        setHeight(1.5f);
        setOriginX(0.75f);
        setOriginY(0.75f);

        particleAlive = new ParticleEffect();
        particleAlive.load(Gdx.files.internal("data/particles/protagonist_alive.p"), skin.getAtlas());

        particleDead = new ParticleEffect();
        particleDead.load(Gdx.files.internal("data/particles/protagonist_dead.p"), skin.getAtlas());

        effects = new ArrayList<Effect>();

        // register with the event bus
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        switch (state) {
            case ALIVE:
                processKeys();
                updatePosition(delta);
                particleAlive.setPosition(getX(), getY());
                particleAlive.update(delta);
                // effects
                Iterator<Effect> iterator = effects.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().act(delta)) {
                        iterator.remove();
                    }
                }
                break;
            case DYING:
                if (particleDead.isComplete()) {
                    this.setState(State.DEAD);
                } else {
                    particleDead.update(delta);
                    particleAlive.update(delta);
                }
                break;
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        switch (state) {
            case ALIVE:
                // draw particles
                particleAlive.draw(batch);
                // draw texture
                batch.setColor(1, 1, 1, 1);
                batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getOriginX(), getOriginY(),
                        getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
                // effects
                for (Effect effect : effects) {
                    effect.draw(batch);
                }
                break;
            case DYING:
                particleDead.draw(batch);
                break;
        }
    }

    public boolean isOnNewBlock() {
        return ((int) getX() - (int) getPrevX() != 0) || ((int) getY() - (int) getPrevY() != 0);
    }

    public boolean hasState(State state) {
        return this.state == state;
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onStateChange(State state) {
        switch (state) {
            case DYING:
                // init the "dying" particles
                particleDead.setPosition(getX(), getY());
                particleDead.start();
                // re-spawn
                direction = IDLE;
                setX(spawnX); setY(spawnY);
                setPrevX(spawnX); setPrevY(spawnY);
                particleAlive.setPosition(spawnX, spawnY);
                // remove all effects
                for (Effect effect : effects) {
                    effect.complete();
                }
                break;
            case DEAD:
                eventBus.post(new LivesIntent(-1));
                this.setState(State.ALIVE);
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onSpeedBonus(SpeedBonus bonus) {
        effects.add(new SpeedEffect(this, 2, 10, bonus.getParticleEffect()));
    }

    //---------------------------------------------------------------------
    // Helper Methods
    //---------------------------------------------------------------------

    private void processKeys() {
        int dx = Gdx.input.getDeltaX();
        int dy = Gdx.input.getDeltaY();
        float diff = Math.abs(dx) - Math.abs(dy);

        boolean isDraggedDown = (Gdx.input.isTouched() && dy < 0 && diff < 0);
        boolean isDraggedUp = (Gdx.input.isTouched() && dy > 0 && diff <= 0);
        boolean isDraggedLeft = (Gdx.input.isTouched() && dx < 0 && diff > 0);
        boolean isDraggedRight = (Gdx.input.isTouched() && dx > 0 && diff >= 0);

        Block block = level.getBlock(getX(), getY());
        boolean onFilledBlock = block.hasType(Type.BLUE) || block.hasType(Type.BLUE_HARD) || block.hasType(Type.GREEN);

        if((onFilledBlock || direction != UP)
                && (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || isDraggedUp)) {
            direction = DOWN;
        }
        if((onFilledBlock || direction != DOWN)
                && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || isDraggedDown)) {
            direction = UP;
        }
        if((onFilledBlock || direction != RIGHT)
                && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || isDraggedLeft)) {
            direction = LEFT;
        }
        if((onFilledBlock || direction != LEFT)
                && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || isDraggedRight)) {
            direction = RIGHT;
        }
    }

    private void updatePosition(float deltaTime) {
        if (direction != IDLE) {
            Vector2 position = new Vector2(getX(), getY());
            float distance = calculateDistance(deltaTime);

            updatePositon(position, distance);
            correctForSmoothTurns(position);

            if (position.x == getX() && position.y == getY()) {
                direction = IDLE;
            } else {
                // update previous coords
                prevX = getX();
                prevY = getY();
                // update current coords
                setX(position.x);
                setY(position.y);
            }
        }
    }

    private float calculateDistance(float deltaTime) {
        float distance = deltaTime * speed;
        return Math.min(distance, 1f);
    }

    private void updatePositon(Vector2 position, float distance) {
        position.x += distance * direction.x;
        position.y += distance * direction.y;

        // check the boundaries
        position.x = Math.max(0.5f, position.x);
        position.x = Math.min(position.x, level.getMapWidth() - 0.5f);
        position.y = Math.max(0.5f, position.y);
        position.y = Math.min(position.y, level.getMapHeight() - 0.5f);
    }

    private void correctForSmoothTurns(Vector2 position) {
        float step = 0.05f;
        if (direction.x != 0) {
            float ny = position.y + 0.5f;
            float ry = Math.round(ny);
            if (ry > ny) {
                position.y += step;
            } else if (ry < ny) {
                if (ry - ny < step) {
                    position.y = ry - 0.5f; // round y for smoother movement
                } else {
                    position.y -= step;
                }
            }
        } else if (direction.y != 0) {
            float nx = position.x + 0.5f;
            float rx = Math.round(nx);
            if (rx > nx) {
                position.x += step;
            } else if (rx < nx) {
                if (rx - nx < step) {
                    position.x = rx - 0.5f; // round x for smoother movement
                } else {
                    position.x -= step;
                }
            }
        }
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

    public float getPrevX() {
        return prevX;
    }

    public void setPrevX(float prevX) {
        this.prevX = prevX;
    }

    public float getPrevY() {
        return prevY;
    }

    public void setPrevY(float prevY) {
        this.prevY = prevY;
    }

    public void setSpawnX(float spawnX) {
        this.spawnX = spawnX;
    }

    public void setSpawnY(float spawnY) {
        this.spawnY = spawnY;
    }

    public void setState(State state) {
        this.state = state;
        eventBus.post(state);
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public float getSpeed() {
        return speed;
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public enum State {
        ALIVE, DYING, DEAD
    }

}
