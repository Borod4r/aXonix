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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.3
 */
public class Bonus extends Actor {

    private Circle collisionCircle;

    private TextureRegion region;
    private ParticleEffect particleEffect;

    public Bonus(float x, float y, Skin skin) {
        setX(x); setY(y);
        setWidth(1.5f);
        setHeight(1.5f);
        setOriginX(0.75f);
        setOriginY(0.75f);
        setColor(1,1,1,1);

        this.collisionCircle = new Circle(x, y, 0.5f);
        this.region = skin.getRegion("speed_bonus");

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("data/particles/bonus.p"), skin.getAtlas());
        particleEffect.setPosition(x, y);
    }

    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);
        particleEffect.update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        // draw texture
        batch.setColor(getColor());
        batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getWidth(), getHeight());
        //draw particles
        particleEffect.draw(batch);
    }

    public void removeSmoothly() {
        particleEffect.allowCompletion();
        Action sequence = Actions.sequence(Actions.fadeOut(0.35f), Actions.delay(0.15f), Actions.removeActor());
        addAction(sequence);
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public Circle getCollisionCircle() {
        return collisionCircle;
    }
}
