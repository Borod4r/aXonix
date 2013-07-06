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

package net.ivang.axonix.main.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.events.intents.game.LivesIntent;

import static net.ivang.axonix.main.actors.game.Level.Block;

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
    private Move move;

    private Level level;

    private TextureRegion region;
    private ParticleEffect particleAlive;
    private ParticleEffect particleDead;

    public Protagonist(EventBus eventBus, float x, float y, Level level, Skin skin) {
        this.eventBus = eventBus;
        eventBus.register(this);

        this.state = State.ALIVE;
        this.speed = 8;
        this.move = Move.IDLE;
        this.level = level;
        this.region = skin.getRegion("circular_flare");

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

    @Subscribe
    @SuppressWarnings("unused")
    public void onStateChange(State state) {
        switch (state) {
            case DYING:
                // init the "dying" particles
                particleDead.setPosition(getX(), getY());
                particleDead.start();
                // re-spawn
                this.move = Move.IDLE;
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
        boolean onFilledBlock = (block == Block.BLUE) || ((block == Block.GREEN));

        if((onFilledBlock || move != Move.DOWN)
                && (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || isDraggedUp)) {
            move = Move.UP;
        }
        if((onFilledBlock || move != Move.UP)
                && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || isDraggedDown)) {
            move = Move.DOWN;
        }
        if((onFilledBlock || move != Move.RIGHT)
                && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || isDraggedLeft)) {
            move = Move.LEFT;
        }
        if((onFilledBlock || move != Move.LEFT)
                && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || isDraggedRight)) {
            move = Move.RIGHT;
        }
    }

    private void updatePosition(float deltaTime) {
        float deltaPx = deltaTime * speed;
        float x = getX();
        float y = getY();

        switch (move) {
            case UP:
                if (y > 0.5) {
                    y -= deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case DOWN:
                if (y < level.getHeight() - 0.5) {
                    y += deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case LEFT:
                if (x > 0.5) {
                    x -= deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case RIGHT:
                if (x < level.getWidth() - 0.5) {
                    x += deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
        }

        float step = 0.05f;
        switch (move) {
            case UP:
            case DOWN:
                float nx = x + 0.5f;
                float rx = Math.round(nx);
                if (rx > nx) {
                    x += step;
                } else if (rx < nx) {
                    if (rx - nx < step) {
                        x = rx - 0.5f; // round x for smoother movement
                    } else {
                        x -= step;
                    }
                }
                break;
            case RIGHT:
            case LEFT:
                float ny = y + 0.5f;
                float ry = Math.round(ny);
                if (ry > ny) {
                    y += step;
                } else if (ry < ny) {
                    if (ry - ny < step) {
                        y = ry - 0.5f; // round y for smoother movement
                    } else {
                        y -= step;
                    }
                }
                break;

        }

        // update previous coords
        if (move != Move.IDLE) {
            prevX = getX();
            prevY = getY();
        }

        setX(x); setY(y);
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

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

    public float getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(float spawnX) {
        this.spawnX = spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(float spawnY) {
        this.spawnY = spawnY;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        eventBus.post(state);
    }
}
