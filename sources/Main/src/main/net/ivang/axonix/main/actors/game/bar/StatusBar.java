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

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.events.facts.LivesNumberFact;
import net.ivang.axonix.main.events.facts.level.LevelIndexFact;
import net.ivang.axonix.main.events.facts.level.LevelProgressFact;
import net.ivang.axonix.main.events.facts.level.LevelScoreFact;

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

    public StatusBar(EventBus eventBus, Style style) {
        eventBus.register(this);

        livesLabel = new Label("Lives: ", style.label);
        scoreLabel = new Label("Score: ", style.label);
        levelLabel = new Label("Level: ", style.label);

        livesValue = new Label("n/a", style.value);
        scoreValue = new Label("n/a", style.value);
        levelValue = new Label("n/a", style.value);
        progress = new Label("n/a", style.value);

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

    public void setStyle(Style style) {
        if (style != null) {
            // names
            livesLabel.setStyle(style.label);
            scoreLabel.setStyle(style.label);
            levelLabel.setStyle(style.label);
            // values
            livesValue.setStyle(style.value);
            scoreValue.setStyle(style.value);
            levelValue.setStyle(style.value);
            progress.setStyle(style.value);
        } else {
            throw new IllegalArgumentException("style cannot be null.");
        }
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

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    static public class Style {
        public Label.LabelStyle label;
        public Label.LabelStyle value;
    }

}
