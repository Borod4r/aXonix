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

package net.ivang.axonix.main.screen.game.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Protagonist extends Actor {

    private float px, py;
    private float speed;
    private Move move;

    private Level level;

    private TextureRegion region;
    ParticleEffect particleEffect;

    public Protagonist(float x, float y, Level level, Skin skin) {
        this.speed = 8;
        this.move = Move.IDLE;
        this.level = level;
        this.region = skin.getRegion("circular_flare");
        setX(x); setY(y);
        setPx(x); setPy(y);

        setWidth(1.5f);
        setHeight(1.5f);
        setOriginX(0.75f);
        setOriginY(0.75f);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("data/particles/protagonist.p"), skin.getAtlas());
        particleEffect.setPosition(x, y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        processKeys();
        updatePosition(delta);
        particleEffect.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        // draw particles
        particleEffect.setPosition(getX(), getY());
        particleEffect.draw(batch);
        // draw texture
        batch.setColor(1, 1, 1, 1);
        batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

    }

    public boolean isOnNewBlock() {
        return ((int) getX() - (int) getPx() != 0) || ((int) getY() - (int) getPy() != 0);
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

        byte blockState = level.getBlockState(getX(), getY());
        boolean onFilledBlock = (blockState == Level.BS_BLUE) || ((blockState == Level.BS_GREEN));

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
            px = getX();
            py = getY();
        }

        setX(x); setY(y);
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public float getPx() {
        return px;
    }

    public void setPx(float px) {
        this.px = px;
    }

    public float getPy() {
        return py;
    }

    public void setPy(float py) {
        this.py = py;
    }
}
