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

package net.ivang.axonix.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.eventbus.EventBus;
import net.ivang.axonix.main.AxonixGame;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public abstract class BaseScreen implements Screen {

    protected final AxonixGame game;
    protected Stage stage;
    protected Skin skin;

    protected EventBus eventBus;
    protected InputMultiplexer inputMultiplexer;

    protected BaseScreen(final AxonixGame game, InputMultiplexer inputMultiplexer, EventBus eventBus) {
        this.game = game;
        this.stage = new Stage();
        this.skin = game.getSkin();
        this.inputMultiplexer = inputMultiplexer;
        this.eventBus = eventBus;

        String styleName = getStyleNameByHeight();
        setStyleByName(styleName, false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        Table.drawDebug(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, false);

        String styleName = getStyleNameByHeight(height);
        setStyleByName(styleName, true);
    }

    @Override
    public void show() {
        inputMultiplexer.addProcessor(stage);
        eventBus.register(this);
    }

    @Override
    public void hide() {
        inputMultiplexer.removeProcessor(stage);
        eventBus.unregister(this);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    protected String getStyleNameByHeight() {
        return getStyleNameByHeight(Gdx.graphics.getHeight());
    }

    protected String getStyleNameByHeight(int height) {
        if (height < 480) {
            return StyleName.SMALL.toString();
        } else if (height < 720) {
            return StyleName.NORMAL.toString();
        } else {
            return StyleName.LARGE.toString();
        }
    }

    protected abstract void setStyleByName(String styleName);

    protected abstract void applyStyle();

    protected void setStyleByName(String styleName, boolean apply) {
        setStyleByName(styleName);
        if (apply) {
            applyStyle();
        }
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private enum StyleName {
        SMALL("small"),
        NORMAL("normal"),
        LARGE("large"),
        XLARGE("xlarge");

        private final String styleName;

        private StyleName(String styleName) {
            this.styleName = styleName;
        }

        public String toString() {
            return styleName;
        }
    }

}
