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

package net.ivang.xonix.main;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Notification extends Label {

    private GameScreen gameScreen;
    private Skin skin;

    public Notification(CharSequence text, GameScreen gameScreen, Skin skin, String fontName) {
        super(text, skin, fontName, "white");
        setVisible(false);
        setFillParent(true);
        setAlignment(Align.center);
        this.gameScreen = gameScreen;
        this.skin = skin;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        switch (gameScreen.getState()) {
            case PAUSED:
                setText("PAUSE");
                setVisible(true);
                break;
            case LEVEL_COMPLETED:
                setText("LEVEL COMPLETED");
                setVisible(true);
                break;
            case GAME_OVER:
                setText("GAME OVER");
                setVisible(true);
                break;
            case WIN:
                setText("WIN!!!");
                setVisible(true);
                break;
            default:
                if (getActions().size == 0) {
                    setVisible(false);
                }
                break;
        }
    }

    public void setFont(String fontName) {
        setFont(skin.getFont(fontName));
    }

    public void setFont(BitmapFont font) {
        setStyle(new Label.LabelStyle(font, skin.getColor("white")));
    }

}
