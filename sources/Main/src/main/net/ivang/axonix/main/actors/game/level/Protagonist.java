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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.events.intents.bonus.SpeedBonusIntent;
import net.ivang.axonix.main.events.intents.game.LivesIntent;

import static net.ivang.axonix.main.actors.game.level.Block.Type;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Protagonist extends Actor {

    public enum State {
        ALIVE, DYING, DEAD
    }

    private State state;
    private EventBus eventBus;

    private float spawnX, spawnY;
    private float prevX, prevY;
    private float speed;
    private float boost;
    private Direction direction;

    private Circle collisionCircle;

    private Level level;

    private TextureRegion region;
    private ParticleEffect particleAlive;
    private ParticleEffect particleDead;

    public Protagonist(EventBus eventBus, float x, float y, Level level, Skin skin) {
        this.eventBus = eventBus;
        eventBus.register(this);

        this.state = State.ALIVE;
        this.speed = 4;
        this.direction = Direction.IDLE;
        this.level = level;
        this.region = skin.getRegion("circular_flare");
        this.collisionCircle = new Circle(x, y, 0.4f);

        setX(x); setY(y);
        setSpawnX(x); setSpawnY(y);
        setPrevX(x); setPrevY(y);

        setWidth(1.5f);
        setHeight(1.5f);
        setOriginX(0.75f);
        setOriginY(0.75f);

        particleAlive = new ParticleEffect();
        particleAlive.load(Gdx.files.internal("data/particles/protagonist_alive.p"), skin.getAtlas());

        particleDead = new ParticleEffect();
        particleDead.load(Gdx.files.internal("data/particles/protagonist_dead.p"), skin.getAtlas());
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
                this.direction = Direction.IDLE;
                this.boost = 0;
                setX(spawnX); setY(spawnY);
                setPrevX(spawnX); setPrevY(spawnY);
                particleAlive.setPosition(spawnX, spawnY);
                break;
            case DEAD:
                eventBus.post(new LivesIntent(-1));
                this.setState(State.ALIVE);
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onSpeedBonus(SpeedBonusIntent intent) {
        boost = 10;
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
        boolean onFilledBlock = (block.hasType(Type.BLUE)) || ((block.hasType(Type.GREEN)));

        if((onFilledBlock || direction != Direction.UP)
                && (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || isDraggedUp)) {
            direction = Direction.DOWN;
        }
        if((onFilledBlock || direction != Direction.DOWN)
                && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || isDraggedDown)) {
            direction = Direction.UP;
        }
        if((onFilledBlock || direction != Direction.RIGHT)
                && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || isDraggedLeft)) {
            direction = Direction.LEFT;
        }
        if((onFilledBlock || direction != Direction.LEFT)
                && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || isDraggedRight)) {
            direction = Direction.RIGHT;
        }
    }

    private void updatePosition(float deltaTime) {
        if (direction != Direction.IDLE) {
            Vector2 position = new Vector2(getX(), getY());
            float distance = calculateDistance(deltaTime);

            updatePositon(position, distance);
            correctForSmoothTurns(position);

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
        // apply boost
        if (boost > 0) {
            boost -= deltaTime;
            distance *= 2;
        }
        return Math.min(distance, 1f);
    }

    private void updatePositon(Vector2 position, float distance) {
        Vector2 unitVector = direction.getUnitVector();

        position.x += distance * unitVector.x;
        position.y += distance * unitVector.y;

        // check the boundaries
        position.x = Math.max(0.5f, position.x);
        position.x = Math.min(position.x, level.getMapWidth() - 0.5f);
        position.y = Math.max(0.5f, position.y);
        position.y = Math.min(position.y, level.getMapHeight() - 0.5f);
    }

    private void correctForSmoothTurns(Vector2 position) {
        float step = 0.05f;
        switch (direction) {
            case UP:
            case DOWN:
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
                break;
            case RIGHT:
            case LEFT:
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
                break;

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
}
