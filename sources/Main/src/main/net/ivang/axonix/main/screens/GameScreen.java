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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.actors.game.level.Level;
import net.ivang.axonix.main.actors.game.background.Background;
import net.ivang.axonix.main.actors.game.bar.DebugBar;
import net.ivang.axonix.main.actors.game.bar.StatusBar;
import net.ivang.axonix.main.actors.game.dialog.AlertDialog;
import net.ivang.axonix.main.actors.game.dialog.ScreenStateDialog;
import net.ivang.axonix.main.events.facts.LivesNumberFact;
import net.ivang.axonix.main.events.facts.ObtainedPointsFact;
import net.ivang.axonix.main.events.facts.TotalScoreFact;
import net.ivang.axonix.main.events.facts.level.LevelIndexFact;
import net.ivang.axonix.main.events.intents.BackIntent;
import net.ivang.axonix.main.events.intents.DefaultIntent;
import net.ivang.axonix.main.events.intents.game.*;
import net.ivang.axonix.main.events.intents.screen.LevelsScreenIntent;
import net.ivang.axonix.main.events.intents.screen.StartScreenIntent;
import net.ivang.axonix.main.preferences.PreferencesWrapper;

import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import static java.lang.Math.min;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class GameScreen extends BaseScreen {

    public enum State {
        PLAYING, PAUSED, LEVEL_COMPLETED, GAME_OVER, WIN
    }

    @Inject
    private PreferencesWrapper preferences;

    private State state;

    private int lives;
    private int totalScore;
    private int levelIndex;
    Level level;

    private Style style;
    private StatusBar statusBar;
    private Cell levelCell;
    private Cell statusCell;
    private Label pointsLabel;
    private Label bigPointsLabel;
    private Label notificationLabel;
    private AlertDialog stateDialog;
    private Background background;

    @Inject
    private GameScreen(AxonixGame game, InputMultiplexer inputMultiplexer, EventBus eventBus) {
        super(game, inputMultiplexer, eventBus);

        // init sub-components
        Table rootTable = initRootTable(style);
        DebugBar debugBar = initDebugBar(style);
        initBackground();
        initPointsLabels(style);
        initNotificationLabel(style);
        initStateDialog(style);
        // add sub-components to stage
        stage.addActor(background);
        stage.addActor(rootTable);
        stage.addActor(pointsLabel);
        stage.addActor(bigPointsLabel);
        stage.addActor(notificationLabel);
        stage.addActor(stateDialog);
        stage.addActor(debugBar);
    }

    public void nextLevel() {
        setLevel(levelIndex + 1, false);
    }

    public void loadLevel(int index) {
        setLevel(index, true);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (level != null) {
            float scale = calculateScaling(stage, level, statusCell.getMaxHeight());
            level.setScale(scale);
            levelCell.width(level.getWidth() * scale).height(level.getHeight() * scale);
        }
        background.update(true);
    }

    @Override
    public void pause() {
        setState(State.PAUSED);
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onStateChange(State state) {
        switch (state) {
            case LEVEL_COMPLETED:
                saveLevelInfoToPrefs();
                break;
            case GAME_OVER:
            case WIN:
                saveGameInfoToPrefs();
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void loadLevel(LoadLevelIntent intent) {
        loadLevel(intent.getLevelIndex());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void replayLevel(ReplayLevelIntent intent) {
        loadLevel(levelIndex);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLevelStateChange(Level.State levelState) {
        switch (levelState) {
            case LEVEL_COMPLETED:
                setState(State.LEVEL_COMPLETED);
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLevelScoreChange(LevelScoreIntent intent) {
        setTotalScore(getTotalScore() + intent.getScoreDelta());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void changeLivesNumber(LivesIntent intent) {
        setLives(getLives() + intent.getLivesDelta());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLivesNumberChange(LivesNumberFact fact) {
        if (lives <= 0 && !hasState(State.GAME_OVER)) {
            setState(State.GAME_OVER);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void showNotification(NotificationIntent intent) {
        Action action = Actions.sequence(Actions.delay(intent.getShowDelay()),
                                         Actions.show(),
                                         Actions.delay(intent.getHideDelay()),
                                         Actions.hide());
        getNotificationLabel().setText(intent.getMessage());
        getNotificationLabel().addAction(action);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void showObtainedPoints(ObtainedPointsFact fact) {
        // text
        int points = fact.getPoints();
        Label label = (points <= ObtainedPointsFact.QUANTITY_3) ? getPointsLabel(): getBigPointsLabel();
        label.setText(Integer.toString(points));
        // position
        float x = fact.getX();
        float y = fact.getY();
        if (fact.isSubtractBounds()) {
            x -= label.getTextBounds().width;
        }
        label.setPosition(x, y);
        // events
        SequenceAction fadeInFadeOut = Actions.sequence(Actions.visible(true), Actions.fadeIn(0.2f), Actions.delay(1f),
                Actions.fadeOut(0.3f), Actions.visible(false));
        ParallelAction fadeAndMove = Actions.parallel(Actions.moveTo(x, y + fact.getDeltaY(), 1.5f), fadeInFadeOut);
        label.clearActions();
        label.addAction(fadeAndMove);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void doDefaultAction(DefaultIntent intent) {
        switch (getState()) {
            case PLAYING:
                setState(State.PAUSED);
                break;
            case PAUSED:
                setState(State.PLAYING);
                break;
            case LEVEL_COMPLETED:
                int nextIndex = getLevelIndex() + 1;
                if (nextIndex <= game.getLevelsFiles().size()) {
                    nextLevel();
                } else {
                    setState(GameScreen.State.WIN);
                }
                break;
            case GAME_OVER:
            case WIN:
                eventBus.post(new StartScreenIntent());
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void doBacktAction(BackIntent intent) {
        switch (getState()) {
            case PLAYING:
                setState(State.PAUSED);
                break;
            case PAUSED:
            case LEVEL_COMPLETED:
            case GAME_OVER:
            case WIN:
                eventBus.post(new LevelsScreenIntent());
                break;
        }
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
        statusBar.setStyle(style.statusBar);
        statusCell.height(style.statusCellHeight);
        pointsLabel.setStyle(style.points);
        bigPointsLabel.setStyle(style.bigPoints);
        notificationLabel.setStyle(style.notification);
        stateDialog.setStyle(style.stateDialog);
    }

    private void initBackground() {
        background = new Background(skin);
    }

    private Table initRootTable(Style style) {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        // status bar
        statusBar = new StatusBar(eventBus, style.statusBar);
        statusCell = rootTable.add(statusBar);
        statusCell.height(style.statusCellHeight).left();
        rootTable.row();
        // level cell
        levelCell = rootTable.add();
        return  rootTable;
    }

    private void initPointsLabels(Style style) {
        pointsLabel = new Label(null, style.points);
        pointsLabel.setVisible(false);
        bigPointsLabel = new Label(null, style.bigPoints);
        bigPointsLabel.setVisible(false);
    }

    private void initNotificationLabel(Style style) {
        notificationLabel = new Label(null, style.notification);
        notificationLabel.setFillParent(true);
        notificationLabel.setAlignment(Align.center);
        notificationLabel.setVisible(false);
    }

    private void initStateDialog(Style style) {
        stateDialog = new ScreenStateDialog(null, style.stateDialog, eventBus);
    }

    private DebugBar initDebugBar(Style style) {
        return new DebugBar(style.debugBar);
    }

    private void setLevel(int index, boolean loadFromPrefs) {
        if (level != null) level.unregister();
        // init level structure from pixmap
        Pixmap pixmap = new Pixmap(game.getLevelsFiles().get(index - 1));
        level = new Level(index, pixmap, skin, eventBus);
        levelIndex = index;
        // set widget size
        float scale = calculateScaling(stage, level, statusCell.getMaxHeight());
        level.setScale(scale);
        levelCell.setWidget(level).width(level.getWidth() * scale).height(level.getHeight() * scale);
        // get level info from preferences
        if (loadFromPrefs) {
            loadLevelInfoFromPrefs(index - 1);
            setTotalScore(0);
        }
        // go play
        setState(State.PLAYING);
        // announce the fact
        eventBus.post(new LevelIndexFact(index));
    }

    private void loadLevelInfoFromPrefs(int levelIndex) {
        if (levelIndex == 0) {
            setLives(3);
        } else if (preferences.containsLives(levelIndex)) {
            int savedLivesNumber = preferences.getLives(levelIndex);
            setLives(savedLivesNumber);
        } else {
            throw new IllegalArgumentException("Preferences do not contain values for index:" + levelIndex);
        }
    }

    private void saveLevelInfoToPrefs() {
        int savedLivesNumber = preferences.getLives(levelIndex);
        int savedLevelScore = preferences.getLevelScore(levelIndex);

        int newLivesNumber = getLives();
        int newLevelScore = level.getScore();

        if (newLivesNumber > savedLivesNumber) {
            preferences.setLives(levelIndex, newLivesNumber);
        }
        if (newLevelScore > savedLevelScore) {
            preferences.setLevelScore(levelIndex, newLevelScore);
        }

        preferences.flush();
    }

    private void saveGameInfoToPrefs() {
        int savedTotalScore = preferences.getTotalScore();
        int newTotalScore = getTotalScore();
        if (newTotalScore > savedTotalScore) {
            preferences.setTotalScore(newTotalScore);
            preferences.flush();
        }
    }

    private float calculateScaling(Stage stage, Level level, float statusBarHeight) {
        int padding = 5;
        float wScaling = (stage.getWidth() - padding)/ level.getWidth();
        float hScaling = (stage.getHeight() - statusBarHeight - padding) / level.getHeight();
        return min(wScaling, hScaling);
    }

    private boolean hasState(State state) {
        return this.state == state;
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        eventBus.post(state);
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
        eventBus.post(new LivesNumberFact(lives));
    }

    public Label getNotificationLabel() {
        return notificationLabel;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public Level getLevel() {
        return level;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
        eventBus.post(new TotalScoreFact(totalScore));
    }

    public Label getPointsLabel() {
        return pointsLabel;
    }

    public Label getBigPointsLabel() {
        return bigPointsLabel;
    }

    public AlertDialog getStateDialog() {
        return stateDialog;
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public static class Style {
        public StatusBar.Style statusBar;
        public float statusCellHeight;
        public LabelStyle points;
        public LabelStyle bigPoints;
        public LabelStyle notification;
        public AlertDialog.Style stateDialog;
        public DebugBar.Style debugBar;
    }

}