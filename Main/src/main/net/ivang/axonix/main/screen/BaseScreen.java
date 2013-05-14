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

package net.ivang.axonix.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import net.ivang.axonix.main.AxonixGame;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public abstract class BaseScreen implements Screen {

    protected final AxonixGame game;
    protected Stage stage;
    protected Skin skin;

    public BaseScreen(final AxonixGame game) {
        this.game = game;
        this.stage = new Stage();
        this.skin = game.getSkin();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    protected Style getStyleByHeight() {
        return getStyleByHeight(Gdx.graphics.getHeight());
    }

    protected Style getStyleByHeight(int height) {
        if (height < 480) {
            return Style.SMALL;
        } else if (height < 720) {
            return Style.NORMAL;
        } else {
            return Style.LARGE;
        }
    }

    protected float getScaleByStyle(Style style) {
        float scale = 1f;
        switch (style) {
            case SMALL:
                scale = 0.44f; // 320/720
                break;
            case NORMAL:
                scale = 0.67f; // 480/720
                break;
        }
        return scale;
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    protected enum Style {
        SMALL("small"),
        NORMAL("normal"),
        LARGE("large"),
        XLARGE("xlarge");

        private final String styleName;

        private Style(String styleName) {
            this.styleName = styleName;
        }

        public Style getNext() {
            int next = (ordinal() + 1) % values().length;
            return values()[next];
        }

        public String toString() {
            return styleName;
        }
    }

}
