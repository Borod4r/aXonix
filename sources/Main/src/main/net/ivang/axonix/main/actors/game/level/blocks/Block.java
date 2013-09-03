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

package net.ivang.axonix.main.actors.game.level.blocks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.3
 */
public class Block extends Actor {

    private Skin skin;
    private Type type;
    private TextureRegion region;
    private Rectangle collisionRectangle;

    public Block(float x, float y, Type type, Skin skin) {
        this.skin = skin;
        setX(x); setY(y);
        setWidth(1f);
        setHeight(1f);
        setOriginX(0.5f);
        setOriginY(0.5f);
        setType(type);
        this.collisionRectangle = new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        if (!isEmpty()) {
            batch.setColor(getColor());
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }

    public void setType(Type type) {
        this.type = type;
        switch (getType()) {
            case RED:
                setColor(1, 1, 1, 1);
                setRegion(skin.getRegion("block_red"));
                break;
            case GREEN:
                setColor(0, 1, 0.3f, 1);
                setRegion(skin.getRegion("block_blue"));
                applyVisualEffects();
                break;
            case BLUE:
                setColor(1, 1, 1, 1);
                setRegion(skin.getRegion("block_blue"));
                break;
            case BLUE_HARD:
                setColor(1, 1, 1, 1);
                setRegion(skin.getRegion("block_blue_hard"));
                break;
            case TAIL:
                setColor(0.3f, 0.3f, 1, 1);
                setRegion(skin.getRegion("block_blue"));
                break;
            default:
                setColor(1, 1, 1, 1);
                setRegion(null);
        }
    }

    public boolean hasType(Type type) {
        return this.type == type;
    }

    public boolean isEmpty() {
        return hasType(Type.EMPTY);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void applyVisualEffects() {
        float duration1 = 0.1f;
        float duration2 = 0.15f;
        // scale actions
        Action scaleDown = Actions.scaleTo(0.5f, 0.5f, duration1);
        Action scaleUp = Actions.scaleTo(1, 1, duration2);
        // rotate actions
        int angle = MathUtils.random(-45, 45);
        Action rotate = Actions.rotateBy(angle, duration1);
        Action rotateBack = Actions.rotateBy(-angle, duration2);
        // sequence
        Action act1 = Actions.parallel(scaleDown, rotate);
        Action act2 = Actions.parallel(scaleUp, rotateBack);
        addAction(Actions.sequence(act1, act2));
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public Type getType() {
        return type;
    }

    public Rectangle getCollisionRectangle() {
        return collisionRectangle;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public enum Type {
        EMPTY, RED, GREEN, BLUE, BLUE_HARD, TAIL
    }

}
