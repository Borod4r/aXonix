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

    private State state;
    private List<Effect> effects;

    private float spawnX, spawnY;
    private float prevX, prevY;
    private Circle collisionCircle;

    private Vector2 nextDirection;
    boolean canChangeDirection;

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
        setDirection(Direction.IDLE);

        setWidth(1.5f);
        setHeight(1.5f);
        setOriginX(0.75f);
        setOriginY(0.75f);

        particleAlive = new ParticleEffect();
        particleAlive.load(Gdx.files.internal("data/particles/protagonist/protagonist_alive.p"), skin.getAtlas());

        particleDead = new ParticleEffect();
        particleDead.load(Gdx.files.internal("data/particles/protagonist/protagonist_dead.p"), skin.getAtlas());

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
                updateDirection();
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

    public void setState(State state) {
        if (!hasState(state)) {
            this.state = state;
            eventBus.post(state);
        }
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
                direction = Direction.IDLE;
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
        boolean isDraggedLeft = false;
        boolean isDraggedRight = false;
        boolean isDraggedDown = false;
        boolean isDraggedUp = false;

        if (Gdx.input.isTouched()) {
            int dx = Gdx.input.getDeltaX();
            int dy = Gdx.input.getDeltaY();
            float diff = Math.abs(dx) - Math.abs(dy);
            int deadZone = Gdx.graphics.getHeight() / 240;
            isDraggedLeft = dx < -deadZone && diff > 0;
            isDraggedRight = dx > deadZone && diff >= 0;
            isDraggedDown = dy < -deadZone && diff < 0;
            isDraggedUp =  dy > deadZone && diff <= 0;
        }

        Block block = level.getBlock(getX(), getY());
        boolean onFilledBlock = block.hasType(Type.BLUE) || block.hasType(Type.BLUE_HARD) || block.hasType(Type.GREEN);

        // DOWN
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || isDraggedUp) {
            if (onFilledBlock || direction != Direction.UP) {
                nextDirection = Direction.DOWN;
            }
        // UP
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || isDraggedDown) {
            if(onFilledBlock || direction != Direction.DOWN) {
                nextDirection = Direction.UP;
            }
        // LEFT
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || isDraggedLeft) {
            if (onFilledBlock || direction != Direction.RIGHT) {
                nextDirection = Direction.LEFT;
            }
        // RIGHT
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || isDraggedRight) {
            if (onFilledBlock || direction != Direction.LEFT) {
                nextDirection = Direction.RIGHT;
            }
        }
    }

    private void updateDirection() {
        if (direction == Direction.IDLE || isOnNewBlock()) {
            canChangeDirection = true;
        }

        if (shouldChangeDirection()) {
            direction = nextDirection;
            nextDirection = null;
            canChangeDirection = false;
        }
    }

    private boolean shouldChangeDirection() {
        if (canChangeDirection && nextDirection != null && direction != nextDirection) {
            double floorX = getX() - Math.floor(getX());
            double floorY = getY() - Math.floor(getY());
            if (direction == Direction.IDLE
                    || direction.x != 0 && floorX >= 0.25f && floorX <= 0.75f
                    || direction.y != 0 && floorY >= 0.25f && floorY <= 0.75f) {
                return true;
            }
        }
        return false;
    }

    private void updatePosition(float deltaTime) {
        if (direction != Direction.IDLE) {
            Vector2 position = new Vector2(getX(), getY());
            float distance = calculateDistance(deltaTime);

            updatePositon(position, distance);

            if (position.x == getX() && position.y == getY()) {
                direction = Direction.IDLE;
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

        // correct for smooth turns
        if (direction.x != 0) {
            position.y = roundPosition(position.y);
        } else if (direction.y != 0) {
            position.x = roundPosition(position.x);
        }
    }

    private float roundPosition(float position) {
        float step = 0.05f;
        // move for 0.5f to be able to round it
        float nearPos = position + 0.5f;
        float roundPos = Math.round(nearPos);

        if (Math.abs(nearPos - roundPos) < step) {
            // return rounded value (just move back for 0.5f)
            return roundPos - 0.5f;
        }

        if (roundPos > nearPos) {
            return position + step;
        } else {
            return position - step;
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
