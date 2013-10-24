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

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.actors.levels.LevelButton;
import net.ivang.axonix.main.events.intents.BackIntent;
import net.ivang.axonix.main.events.intents.DefaultIntent;
import net.ivang.axonix.main.events.intents.screen.GameScreenIntent;
import net.ivang.axonix.main.events.intents.screen.StartScreenIntent;
import net.ivang.axonix.main.preferences.PreferencesWrapper;
import net.ivang.axonix.main.utils.ScoreUtils;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class LevelsScreen extends BaseScreen {

    private final static int LEVELS_TABLE_COLS = 5;

    private Style style;
    private Table levelsTable;
    private int defaultLevelIndex;

    @Inject
    private PreferencesWrapper preferences;

    @Inject
    private LevelsScreen(final AxonixGame game, InputMultiplexer inputMultiplexer, EventBus eventBus) {
        super(game, inputMultiplexer, eventBus);

        levelsTable = new Table();

        for (int levelNumber = 1; levelNumber <= game.getLevelsFiles().size(); levelNumber++) {
            LevelButton button = new LevelButton(levelNumber, style.button, eventBus);
            levelsTable.add(button);
            if (levelNumber % LEVELS_TABLE_COLS == 0) {
                levelsTable.row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(levelsTable);
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);
    }

    @Override
    public void show() {
        super.show();
        for(Cell cell : levelsTable.getCells()) {
            updateButtonState((LevelButton) cell.getWidget());
        }
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void doDefaultAction(DefaultIntent intent) {
        eventBus.post(new GameScreenIntent(defaultLevelIndex));
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void doBacktAction(BackIntent intent) {
        eventBus.post(new StartScreenIntent());
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    @Override
    protected void setStyleByName(String styleName) {
        style = skin.get(styleName, Style.class);
    }

    @Override
    protected void applyStyle() {
        for(Cell cell : levelsTable.getCells()) {
            LevelButton button = (LevelButton) cell.getWidget();
            if (button != null) {
                button.setStyle(style.button);
            }
            cell.width(style.buttonWidth).height(style.buttonHeight).pad(style.buttonPad);
        }
    }

    private void updateButtonState(LevelButton button) {
        int levelIndex = button.getLevelIndex();
        // disable button if its level number isn't first and there is no prefs for previous levels
        if (levelIndex == 1 || preferences.containsLives(levelIndex - 1)) {
            button.setColor(1f, 1f, 1f, 1f);
            button.setDisabled(false);
            // update the "star" rating
            int levelRating = getRatingByScore(levelIndex);
            button.setRating(levelRating);
            // update default level index
            if (defaultLevelIndex < levelIndex) {
                defaultLevelIndex = levelIndex;
            }
        } else {
            button.setColor(1f, 1f, 1f, 0.35f);
            button.setDisabled(true);
            button.setRating(0);
        }
    }

    private int getRatingByScore(int levelIndex) {
        int score = preferences.getLevelScore(levelIndex);
        return ScoreUtils.getRatingByScore(score);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public static class Style {
        public LevelButton.Style button;
        public float buttonWidth;
        public float buttonHeight;
        public float buttonPad;
    }

}
