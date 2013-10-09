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

package net.ivang.axonix.main.actors.game.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.events.facts.ButtonClickFact;
import net.ivang.axonix.main.events.facts.TotalScoreFact;
import net.ivang.axonix.main.events.facts.level.LevelScoreFact;
import net.ivang.axonix.main.events.intents.DefaultIntent;
import net.ivang.axonix.main.events.intents.game.ReplayLevelIntent;
import net.ivang.axonix.main.events.intents.screen.LevelsScreenIntent;
import net.ivang.axonix.main.screens.GameScreen;
import net.ivang.axonix.main.utils.ScoreUtils;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class ScreenStateDialog extends AlertDialog {

    private DialogRatingTable ratingTable;
    private DialogScoresTable scoresTable;

    private Cell lineCell1, lineCell2;
    private Cell ratingTableCell;

    private int levelScore;
    private int totalScore;

    private Style style;

    public ScreenStateDialog(String titleText, Style style, final EventBus eventBus) {
        super(titleText, style.dialog);
        eventBus.register(this);

        // line 1
        Image line = new Image(style.line);
        lineCell1 = getWindow().add(line).fill();
        getWindow().row();
        // rating
        ratingTable = new DialogRatingTable(style.starTable);
        ratingTableCell = getWindow().add(ratingTable);
        getWindow().row();
        // line2
        Image line2 = new Image(style.line);
        lineCell2 = getWindow().add(line2).fill();
        getWindow().row();
        // scores
        scoresTable = new DialogScoresTable(style.scoresTable);
        getWindow().add(scoresTable);

        // levels button listener
        addButtonListener(1, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new ButtonClickFact());
                eventBus.post(new LevelsScreenIntent());
            }
        });
        // replay button listener
        addButtonListener(2, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new ButtonClickFact());
                eventBus.post(new ReplayLevelIntent());
            }
        });
        // forward button listener
        addButtonListener(3, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new ButtonClickFact());
                eventBus.post(new DefaultIntent());
            }
        });
    }

    public void setStyle(Style style) {
        this.style = style;
        // dialog window
        super.setStyle(style.dialog);
        // line 1
        lineCell1.pad(style.linePad1, 0, style.linePad1, 0);
        // rating
        ratingTable.setStyle(style.starTable);
        // line 2
        if (lineCell2.getMinHeight() > 0) {
            lineCell2.pad(style.linePad2, 0, style.linePad1, 0);
        }
        // scores
        scoresTable.setStyle(style.scoresTable);
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameScreenStateChanged(GameScreen.State state) {
        switch (state) {
            case PLAYING:
                if (getActions().size == 0) hide();
                break;
            case PAUSED:
                show("PAUSE", false);
                break;
            case LEVEL_COMPLETED:
                show("LEVEL COMPLETED", true);
                break;
            case GAME_OVER:
                show("GAME OVER", false);
                break;
            case WIN:
                show("YOU WIN!", false);
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLevelScoreChange(LevelScoreFact event) {
        levelScore = event.getScore();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onTotalScoreChange(TotalScoreFact event) {
        totalScore = event.getScore();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void show(String title, boolean showStars) {
        setTitle(title);
        // rating
        if (showStars) {
            int starsNum = ScoreUtils.getRatingByScore(levelScore);
            showStars(starsNum);
        } else {
            hideStars();
        }
        // scores
        scoresTable.setLevelScore(levelScore);
        scoresTable.setTotalScore(totalScore);
        // make visible
        setVisible(true);
    }

    private void hide() {
        setVisible(false);
    }

    @SuppressWarnings("unchecked")
    protected void showStars(int stars) {
        ratingTable.setRating(stars);
        ratingTableCell.setWidget(ratingTable);
        lineCell2.height(4);
        lineCell2.pad(style.linePad2, 0, style.linePad1, 0);
    }

    @SuppressWarnings("unchecked")
    protected void hideStars() {
        ratingTableCell.setWidget(null);
        lineCell2.height(0);
        lineCell2.pad(0);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public static class Style {
        public AlertDialog.Style dialog;
        public DialogRatingTable.Style starTable;
        public DialogScoresTable.Style scoresTable;
        public Drawable line;
        public float linePad1, linePad2;
    }

}
