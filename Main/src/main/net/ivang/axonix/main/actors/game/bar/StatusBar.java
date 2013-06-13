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

package net.ivang.axonix.main.actors.game.bar;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.events.facts.level.LevelIndexFact;
import net.ivang.axonix.main.events.facts.level.LevelProgressFact;
import net.ivang.axonix.main.events.facts.level.LevelScoreFact;
import net.ivang.axonix.main.events.facts.LivesNumberFact;

import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class StatusBar extends Table {

    private Label livesLabel;
    private Label livesValue;

    private Label scoreLabel;
    private Label scoreValue;

    private Label levelLabel;
    private Label levelValue;

    private Label progress;

    private Skin skin;

    public StatusBar(EventBus eventBus, Skin skin, String fontName) {
        eventBus.register(this);
        this.skin = skin;

        livesLabel = new Label("Lives: ", skin, fontName, "white");
        scoreLabel = new Label("Score: ", skin, fontName, "white");
        levelLabel = new Label("Level: ", skin, fontName, "white");

        livesValue = new Label("n/a", skin, fontName, "yellow");
        scoreValue = new Label("n/a", skin, fontName, "yellow");
        levelValue = new Label("n/a", skin, fontName, "yellow");
        progress = new Label("n/a", skin, fontName, "yellow");

        // lives
        add(livesLabel).padLeft(5);
        add(livesValue).padRight(5);
        // level score
        add(scoreLabel).padLeft(5);
        add(scoreValue).padRight(5);
        // level
        add(levelLabel).padLeft(5);
        add(levelValue).padRight(5);
        // progress
        add(progress).padLeft(5).padRight(5);
    }

    public void setFont(String fontName) {
        setFont(skin.getFont(fontName));

    }

    public void setFont(BitmapFont font) {
        LabelStyle whiteStyle = new LabelStyle(font, skin.getColor("white"));

        livesLabel.setStyle(whiteStyle);
        scoreLabel.setStyle(whiteStyle);
        levelLabel.setStyle(whiteStyle);

        LabelStyle yellowStyle = new LabelStyle(font, skin.getColor("yellow"));

        livesValue.setStyle(yellowStyle);
        scoreValue.setStyle(yellowStyle);
        levelValue.setStyle(yellowStyle);
        progress.setStyle(yellowStyle);
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onLivesNumberChange(LivesNumberFact fact) {
        String lives = Integer.toString(fact.getLivesNumber());
        livesValue.setText(lives);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLevelScoreChange(LevelScoreFact fact) {
        String score = Integer.toString(fact.getScore());
        scoreValue.setText(score);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLevelLoad(LevelIndexFact fact) {
        String level = Integer.toString(fact.getLevelIndex());
        levelValue.setText(level);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLevelProgressChange(LevelProgressFact fact) {
        String percent = Byte.toString(fact.getPercentComplete());
        progress.setText("(" + percent + "/80%)");
    }

}
