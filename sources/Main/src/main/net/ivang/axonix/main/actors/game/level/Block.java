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

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.3
 */
public class Block extends Actor {

    private TextureRegion region;
    private Type type;

    public Block(float x, float y, Type type, Skin skin) {
        this.region = skin.getRegion("block");
        setX(x); setY(y);
        setWidth(1f);
        setHeight(1f);
        setType(type);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        switch (getType()) {
            case BLUE:
                batch.setColor(1, 1, 1, 1);
                break;
            case GREEN:
                batch.setColor(0, 1, 0.3f, 1);
                break;
            case TAIL:
                batch.setColor(0.3f, 0.3f, 1f, 1);
                break;
            default:
                return;
        }
        batch.draw(region, getX(), getY(), getWidth(), getHeight());
    }

    public boolean hasType(Type type) {
        return this.type == type;
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public enum Type {
        EMPTY, GREEN, BLUE, TAIL
    }

}
