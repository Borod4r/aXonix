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

package net.ivang.axonix.main;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class StatusBar extends Table {

    private GameScreen gameScreen;

    private Label livesLabel;
    private Label livesValue;

    private Label scoreLabel;
    private Label scoreValue;

    private Label levelLabel;
    private Label levelValue;

    private Skin skin;

    public StatusBar(GameScreen gameScreen, Skin skin, String fontName) {
        this.gameScreen = gameScreen;
        this.skin = skin;

        livesLabel = new Label("Lives: ", skin, fontName, "white");
        scoreLabel = new Label("Score: ", skin, fontName, "white");
        levelLabel = new Label("Level: ", skin, fontName, "white");

        livesValue = new Label("n/a", skin, fontName, "yellow");
        scoreValue = new Label("n/a", skin, fontName, "yellow");
        levelValue = new Label("n/a", skin, fontName, "yellow");

        // lives
        add(livesLabel).padLeft(5);
        add(livesValue).padRight(5);
        // score
        add(scoreLabel).padLeft(5);
        add(scoreValue).padRight(5);
        // level
        add(levelLabel).padLeft(5);
        add(levelValue).padRight(5);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // lives
        String lives = Integer.toString(gameScreen.getLives());
        livesValue.setText(lives);
        // score
        String score = Integer.toString(gameScreen.getLevel().getScore());
        scoreValue.setText(score);
        // level
        String level = Integer.toString(gameScreen.getLevelIndex() + 1);
        String percent = Byte.toString(gameScreen.getLevel().getPercentComplete());
        levelValue.setText(level + " (" + percent + "/80%)");
    }

    public void setFont(String fontName) {
        setFont(skin.getFont(fontName));

    }

    public void setFont(BitmapFont font) {
        livesLabel.setStyle(new Label.LabelStyle(font, skin.getColor("white")));
        livesValue.setStyle(new Label.LabelStyle(font, skin.getColor("yellow")));
        scoreLabel.setStyle(new Label.LabelStyle(font, skin.getColor("white")));
        scoreValue.setStyle(new Label.LabelStyle(font, skin.getColor("yellow")));
        levelLabel.setStyle(new Label.LabelStyle(font, skin.getColor("white")));
        levelValue.setStyle(new Label.LabelStyle(font, skin.getColor("yellow")));
    }
}
