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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Background extends Group {

    private final static String TEXTURE_FLARE = "circular_flare";
    private final static byte ACTORS_NUM = 50;
    private final static int MAX_HEIGHT = 1080;

    public Background(Skin skin) {
        // init flares
        TextureRegion texture = skin.getRegion(TEXTURE_FLARE);
        for (int i = 0; i < ACTORS_NUM; i++) {
            addActor(new BackgroundFlare(texture));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update(false);
    }

    public void update(boolean force) {
        float width = getStage().getWidth();
        float height = getStage().getHeight();
        float scaleCorrection = height / MAX_HEIGHT;
        for (Actor flare : getChildren()) {
            boolean outX = flare.getX() > width || flare.getX() < -flare.getWidth();
            boolean outY = flare.getY() > height || flare.getY() < -flare.getHeight();
            if ( force || outX || outY) {
                ((BackgroundFlare) flare).randomize(width, height, scaleCorrection);
            }
        }
    }

}
