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
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Enemy extends Actor {

    private Move moveDirection;
    private TextureRegion region;
    private ParticleEffect particleEffect;

    Enemy(float x, float y, Skin skin) {
        setX(x); setY(y);
        setWidth(1f);
        setHeight(1f);
        setOriginX(0.5f);
        setOriginY(0.5f);
        this.moveDirection = Move.getRandomDiagonal();
        this.region = skin.getRegion("circular_flare");
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("data/particles/enemy.p"), skin.getAtlas());
        particleEffect.setPosition(x, y);

    }

    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);

        float radius = getWidth() / 2;
        float deltaPx = deltaTime * 4f;

        float x = getX();
        float y = getY();

        Level level = (Level) getParent();

        switch (moveDirection) {
            case UP_LEFT:
                x -= deltaPx;
                y += deltaPx;
                if (level.getBlock(x, y + radius) == Level.Block.BLUE) {
                    if (level.getBlock(x - radius, y) == Level.Block.BLUE) {
                        moveDirection = Move.DOWN_RIGHT;
                    } else {
                        moveDirection = Move.DOWN_LEFT;
                    }
                } else if (level.getBlock(x - radius, y) == Level.Block.BLUE) {
                    moveDirection = Move.UP_RIGHT;
                }
                break;
            case UP_RIGHT:
                x += deltaPx;
                y += deltaPx;
                if (level.getBlock(x, y + radius) == Level.Block.BLUE) {
                    if (level.getBlock(x + radius, y) == Level.Block.BLUE) {
                        moveDirection = Move.DOWN_LEFT;
                    } else {
                        moveDirection = Move.DOWN_RIGHT;
                    }
                } else if (level.getBlock(x + radius, y)== Level.Block.BLUE) {
                    moveDirection = Move.UP_LEFT;
                }
                break;
            case DOWN_LEFT:
                x -= deltaPx;
                y -= deltaPx;
                if (level.getBlock(x, y - radius) == Level.Block.BLUE) {
                    if (level.getBlock(x - radius, y) == Level.Block.BLUE) {
                        moveDirection = Move.UP_RIGHT;
                    } else {
                        moveDirection = Move.UP_LEFT;
                    }
                } else if (level.getBlock(x - radius, y) == Level.Block.BLUE) {
                    moveDirection = Move.DOWN_RIGHT;
                }
                break;
            case DOWN_RIGHT:
                x += deltaPx;
                y -= deltaPx;
                if (level.getBlock(x, y - radius) == Level.Block.BLUE) {
                    if (level.getBlock(x + radius, y) == Level.Block.BLUE) {
                        moveDirection = Move.UP_LEFT;
                    } else {
                        moveDirection = Move.UP_RIGHT;
                    }
                } else if (level.getBlock(x + radius, y) == Level.Block.BLUE) {
                    moveDirection = Move.DOWN_LEFT;
                }
                break;
        }

        setX(x);
        setY(y);

        particleEffect.update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        //draw particles
        particleEffect.setPosition(getX(), getY());
        particleEffect.draw(batch);
        // draw texture
        batch.setColor(1, 0.2f, 0.1f, 1);
        batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getWidth(), getHeight());
    }

}
