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

package net.ivang.axonix.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.screen.game.GameScreen;
import net.ivang.axonix.main.screen.game.actor.dialog.AlertDialog;
import net.ivang.axonix.test.util.Screenshot;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class TestAxonixGame extends AxonixGame {

    private static final String DIR_TEST = "aXonix/Test/";
    private static final String DIR_SCREENSHOTS = DIR_TEST + "Screenshots/";

    @Override
    public void render() {
        super.render();

        deleteScreenshots();

        test_Preferences();
        test_alertDialog();
        test_actionButtons();

        Gdx.app.exit();
    }

    //---------------------------------------------------------------------
    // Test Methods
    //---------------------------------------------------------------------

    private void test_Preferences() {
        clearPreferences();

        subTest_saveLevelInfo();
        subTest_saveGameInfo();
        subTest_loadLevelInfo();
    }

    private void test_alertDialog() {
        subTest_alertDialog_playing();
        subTest_alertDialog_paused();
        subTest_alertDialog_levelCompleted();
        subTest_alertDialog_win();
    }

    private void test_actionButtons() {
        // levels
        subTest_actionButtonLevels();
        // repeat
        subTest_actionButtonRepeat_firstLevel();
        subTest_actionButtonRepeat_someLevel();
        // forward
        subTest_actionButtonForward_paused();
        subTest_actionButtonForward_levelCompleted();
        subTest_actionButtonForward_win();
    }

    //---------------------------------------------------------------------
    // Sub-test Methods
    //---------------------------------------------------------------------

    /* Preferences */

    private void subTest_saveLevelInfo() {
        final int[] livesNumber = {2, 1, 1};
        final int[] levelScore = {1500, 2222, 2750};

        for (int levelIndex = 1; levelIndex <= 3; levelIndex++) {
            setGameScreen(levelIndex);

            getGameScreen().setLives(livesNumber[levelIndex-1]);
            getGameScreen().getLevel().setScore(levelScore[levelIndex - 1]);
            getGameScreen().setState(GameScreen.State.LEVEL_COMPLETED);
            super.render();

            int savedLivesNumber = getPreferences().getInteger(AxonixGame.PREF_KEY_LIVES + levelIndex);
            assertThat(savedLivesNumber).isEqualTo(livesNumber[levelIndex-1]);

            int savedLevelScore = getPreferences().getInteger(AxonixGame.PREF_KEY_LVL_SCORE + levelIndex);
            assertThat(savedLevelScore).isEqualTo(levelScore[levelIndex-1]);
        }
    }

    private void subTest_saveGameInfo() {
        final int totalScore = 55555;

        getGameScreen().setTotalScore(totalScore);
        getGameScreen().setState(GameScreen.State.WIN);
        super.render();

        int savedTotalScore = getPreferences().getInteger(AxonixGame.PREF_KEY_TTL_SCORE);
        assertThat(savedTotalScore).isEqualTo(totalScore + getGameScreen().getLevel().getScore());
    }

    private void subTest_loadLevelInfo() {
        final int[] livesNumber = {3, 2, 1};
        for (int levelIndex = 1; levelIndex <= 3; levelIndex++) {
            setGameScreen(levelIndex);
            super.render();
            assertThat(getGameScreen().getLives()).isEqualTo(livesNumber[levelIndex-1]);
            assertThat(getGameScreen().getLevel().getScore()).isZero();
            assertThat(getGameScreen().getTotalScore()).isZero();
        }
    }

    /* Alert Dialog */

    private void subTest_alertDialog_playing() {
        setGameScreen(1);
        getGameScreen().setState(GameScreen.State.PLAYING);
        super.render(); saveScreenshot("alertDialog/playing");
        assertThat(getAlertDialog().isVisible()).isFalse();
    }

    private void subTest_alertDialog_paused() {
        getGameScreen().setState(GameScreen.State.PAUSED);
        super.render(); saveScreenshot("alertDialog/paused");
        assertThat(getAlertDialog().isVisible()).isTrue();
        assertThat(getAlertDialog().getTitle().getText().toString()).isEqualTo("PAUSE");
    }


    private void subTest_alertDialog_levelCompleted() {
        getGameScreen().setState(GameScreen.State.LEVEL_COMPLETED);
        super.render(); saveScreenshot("alertDialog/level_completed");
        assertThat(getAlertDialog().isVisible()).isTrue();
        assertThat(getAlertDialog().getTitle().getText().toString()).isEqualTo("LEVEL COMPLETED");
    }

    private void subTest_alertDialog_win() {
        setGameScreen(3);
        getGameScreen().setState(GameScreen.State.WIN);
        super.render(); saveScreenshot("alertDialog/win");
        assertThat(getAlertDialog().isVisible()).isTrue();
        assertThat(getAlertDialog().getTitle().getText().toString()).isEqualTo("YOU WIN!");
    }

    /* Action Buttons */

    private void subTest_actionButtonLevels() {
        setGameScreen(2);
        getGameScreen().setState(GameScreen.State.PAUSED);
        super.render(); saveScreenshot("actionButtons/levels/paused");
        getLevelsButton().fire(new ChangeListener.ChangeEvent());
        super.render(); saveScreenshot("actionButtons/levels/paused_click");
        assertThat(getScreen()).isEqualTo(getLevelsScreen());
    }

    private void subTest_actionButtonRepeat_firstLevel() {
        setGameScreen(1);
        getGameScreen().setLives(1);
        getGameScreen().getLevel().setScore(258);
        getGameScreen().setTotalScore(1595);
        getGameScreen().setState(GameScreen.State.PAUSED);
        super.render(); saveScreenshot("actionButtons/repeat/paused_firstLevel");
        getRepeatButton().fire(new ChangeListener.ChangeEvent());
        super.render(); saveScreenshot("actionButtons/repeat/paused_firstLevel_click");
        assertThat(getGameScreen().getLevelIndex()).isEqualTo(1);
        assertThat(getGameScreen().getLives()).isEqualTo(3);
        assertThat(getGameScreen().getLevel().getScore()).isZero();
        assertThat(getGameScreen().getTotalScore()).isZero();
    }

    private void subTest_actionButtonRepeat_someLevel() {
        clearPreferences();
        setGameScreen(1);
        getGameScreen().setLives(1);
        getGameScreen().getLevel().setScore(423);
        getGameScreen().setTotalScore(7512);
        getGameScreen().setState(GameScreen.State.LEVEL_COMPLETED);

        setGameScreen(2);
        getGameScreen().setLives(1);
        getGameScreen().getLevel().setScore(258);
        getGameScreen().setTotalScore(1595);
        getGameScreen().setState(GameScreen.State.PAUSED);
        super.render(); saveScreenshot("actionButtons/repeat/paused_someLevel");
        getRepeatButton().fire(new ChangeListener.ChangeEvent());
        super.render(); saveScreenshot("actionButtons/repeat/paused_someLevel_click");
        assertThat(getGameScreen().getLevelIndex()).isEqualTo(2);
        assertThat(getGameScreen().getLives()).isEqualTo(1);
        assertThat(getGameScreen().getLevel().getScore()).isZero();
        assertThat(getGameScreen().getTotalScore()).isZero();
    }

    private void subTest_actionButtonForward_paused() {
        // when paused
        setGameScreen(1);
        getGameScreen().setState(GameScreen.State.PAUSED);
        super.render(); saveScreenshot("actionButtons/forward/paused");
        getForwardButton().fire(new ChangeListener.ChangeEvent());
        super.render(); saveScreenshot("actionButtons/forward/paused_click");
        assertThat(getGameScreen().getState()).isEqualTo(GameScreen.State.PLAYING);
    }

    private void subTest_actionButtonForward_levelCompleted() {
        // when level completed
        getGameScreen().getLevel().setScore(654);
        getGameScreen().setTotalScore(5432);
        getGameScreen().setState(GameScreen.State.LEVEL_COMPLETED);
        super.render(); saveScreenshot("actionButtons/forward/completed");
        assertThat(getGameScreen().getLevel().getScore()).isEqualTo(654);
        assertThat(getGameScreen().getTotalScore()).isEqualTo(5432 + 654);

        getForwardButton().fire(new ChangeListener.ChangeEvent());
        super.render(); saveScreenshot("actionButtons/forward/completed_click");
        assertThat(getGameScreen().getState()).isEqualTo(GameScreen.State.PLAYING);
        assertThat(getGameScreen().getLevelIndex()).isEqualTo(2);
        assertThat(getGameScreen().getLives()).isEqualTo(3);
        assertThat(getGameScreen().getLevel().getScore()).isZero();
        assertThat(getGameScreen().getTotalScore()).isEqualTo(5432 + 654);
    }

    private void subTest_actionButtonForward_win() {
        clearPreferences();
        getGameScreen().setState(GameScreen.State.LEVEL_COMPLETED);
        // when game completed
        setGameScreen(3);
        getGameScreen().getLevel().setScore(654);
        getGameScreen().setTotalScore(5432);
        getGameScreen().setState(GameScreen.State.LEVEL_COMPLETED);
        super.render(); saveScreenshot("actionButtons/forward/lvl_completed_last");
        assertThat(getGameScreen().getLevel().getScore()).isEqualTo(654);
        assertThat(getGameScreen().getTotalScore()).isEqualTo(5432 + 654);

        getForwardButton().fire(new ChangeListener.ChangeEvent());
        super.render(); saveScreenshot("actionButtons/forward/lvl_completed_last_click");
        assertThat(getGameScreen().getLevel().getScore()).isEqualTo(654);
        // TODO: Fix this bug with total score
//        assertThat(gameScreen.getTotalScore()).isEqualTo(5432 + 654);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void clearPreferences() {
        getPreferences().clear();
        getPreferences().flush();
    }

    private void deleteScreenshots() {
        Gdx.files.external(DIR_SCREENSHOTS).deleteDirectory();
    }

    private void saveScreenshot(String fileName) {
        Screenshot.saveTo(DIR_SCREENSHOTS + fileName);
    }

    //---------------------------------------------------------------------
    // Short cuts
    //---------------------------------------------------------------------

    private AlertDialog getAlertDialog() {
        return getGameScreen().getAlertDialog();
    }

    private Button getLevelsButton() {
        return getAlertDialog().getActionsGroup().getButton1();
    }

    private Button getRepeatButton() {
        return getAlertDialog().getActionsGroup().getButton2();
    }

    private Button getForwardButton() {
        return getAlertDialog().getActionsGroup().getButton3();
    }

}
