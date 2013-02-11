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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class DebugBar extends Table {

    private Label fpsLabel;
    private Label sizeLabel;

    public DebugBar(Skin skin, String fontName) {
        this.setFillParent(true);
        this.right().top();

        fpsLabel = new Label(null, skin, fontName, "white");
        sizeLabel = new Label(null, skin, fontName, "white");

        add(fpsLabel).padRight(5);
        add(sizeLabel).padRight(5);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        String fps = Integer.toString(Gdx.graphics.getFramesPerSecond());
        String width = Integer.toString(Gdx.graphics.getWidth());
        String height = Integer.toString(Gdx.graphics.getHeight());
        fpsLabel.setText(fps + "fps");
        sizeLabel.setText(width + "x" + height);
    }

}
