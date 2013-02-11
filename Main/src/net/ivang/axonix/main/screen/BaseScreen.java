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

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import net.ivang.axonix.main.AxonixGame;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public abstract class BaseScreen implements Screen {

    protected final static String FONT_NAME_SMALL = "small";
    protected final static String FONT_NAME_NORMAL = "normal";
    protected final static String FONT_NAME_LARGE = "large";

    protected AxonixGame game;
    protected Stage stage;

    public BaseScreen(AxonixGame game) {
        this.game = game;
        this.stage = new Stage();
    }

    @Override
    public void render(float delta) {
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

    protected String getFontNameByHeight(int height) {
        if (height < 480) {
            return FONT_NAME_SMALL;
        } else if (height < 720) {
            return FONT_NAME_NORMAL;
        } else {
            return FONT_NAME_LARGE;
        }
    }

}
