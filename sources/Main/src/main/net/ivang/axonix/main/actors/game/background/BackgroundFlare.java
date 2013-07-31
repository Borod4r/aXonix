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

package net.ivang.axonix.main.actors.game.background;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import net.ivang.axonix.main.actors.game.KinematicActor;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class BackgroundFlare extends KinematicActor {

    private final static int BASE_SPEED = 50;

    private TextureRegion texture;
    private float maxScale;

    public BackgroundFlare(TextureRegion texture) {
        this.texture = texture;
        setX(-texture.getRegionWidth() - 1);
        setY(-texture.getRegionHeight() - 1);
        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    public void randomize(float width, float height, float scaleCorrection) {
        setX(width * MathUtils.random());
        setY(height * MathUtils.random());

        setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 0.2f);

        setScale(0);
        this.maxScale = MathUtils.random(0.1f, 1f) * scaleCorrection;

        float angle = MathUtils.random(359);
        direction.x = MathUtils.cosDeg(angle);
        direction.y = MathUtils.sinDeg(angle);
        speed = BASE_SPEED * maxScale;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setX(getX() + direction.x * speed * delta);
        setY(getY() + direction.y * speed * delta);
        if (getScaleX() < maxScale) {
            setScale(getScaleX() + 0.01f);
        }

    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

}
