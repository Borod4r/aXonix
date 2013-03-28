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

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class NotificationLabel extends Label {

    private Skin skin;

    public NotificationLabel(CharSequence text, Skin skin, String fontName) {
        super(text, skin, fontName, "white");
        setVisible(false);
        this.skin = skin;
    }

    public void setFont(String fontName) {
        setFont(skin.getFont(fontName));
    }

    public void setFont(BitmapFont font) {
        setStyle(new Label.LabelStyle(font, skin.getColor("white")));
    }

}
