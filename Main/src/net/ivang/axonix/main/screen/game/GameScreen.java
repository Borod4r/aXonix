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

package net.ivang.axonix.main.screen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.esotericsoftware.tablelayout.Cell;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.screen.BaseScreen;
import net.ivang.axonix.main.screen.game.actor.*;
import net.ivang.axonix.main.screen.game.input.AxonixGameGestureListener;
import net.ivang.axonix.main.screen.game.input.GameScreenInputProcessor;

import static java.lang.Math.min;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class GameScreen extends BaseScreen {

    public enum State {
        PLAYING, PAUSED, LEVEL_COMPLETED, GAME_OVER, WIN
    }

    private InputMultiplexer inputMultiplexer;

    private State state;

    private int lives;
    private long totalScore;
    private int levelIndex;
    Level level;

    private Skin skin;

    private StatusBar statusBar;
    private Cell levelCell;
    private Cell statusCell;
    private NotificationLabel pointsLabel;
    private NotificationLabel bigPointsLabel;
    private NotificationLabel notificationLabel;
    private NotificationWindow notificationWindow;
    private Background background;

    public GameScreen(AxonixGame game) {
        super(game);
        setState(State.PAUSED);
        setLives(3);

        // Input event handling
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new GameScreenInputProcessor(game, this));
        inputMultiplexer.addProcessor(new GestureDetector(new AxonixGameGestureListener(game, this)));
        inputMultiplexer.addProcessor(stage);

        // Look & feel
        TextureAtlas atlas = new TextureAtlas("data/atlas/game_screen.atlas");
        skin = new Skin(Gdx.files.internal("data/skin/game_screen.json"), atlas);
        Style style = getStyleByHeight(Gdx.graphics.getHeight());

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        // status bar
        statusBar = new StatusBar(this, skin, style.toString());
        statusCell = rootTable.add(statusBar);
        statusCell.height(skin.getFont(style.toString()).getLineHeight()).left();
        rootTable.row();
        // level cell
        levelCell = rootTable.add();

        // background
        background = new Background(skin);
        // floating notifications
        pointsLabel = new NotificationLabel(null, skin, style.toString());
        bigPointsLabel = new NotificationLabel(null, skin, style.getNext().toString());
        //notification label
        notificationLabel = new NotificationLabel(null, skin, style.toString());
        notificationLabel.setFillParent(true);
        notificationLabel.setAlignment(Align.center);
        // notification window
        notificationWindow = new NotificationWindow(null, skin, style.toString());
        // debug bar
        DebugBar debugBar = new DebugBar(skin, Style.SMALL.toString());

        stage.addActor(background);
        stage.addActor(rootTable);
        stage.addActor(pointsLabel);
        stage.addActor(bigPointsLabel);
        stage.addActor(notificationLabel);
        stage.addActor(notificationWindow);
        stage.addActor(debugBar);
    }

    public void setLevel(int index) {
        this.levelIndex = index;
        Pixmap pixmap = new Pixmap(game.getLevelsFiles().get(index - 1));
        level = new Level(this, pixmap, skin);
        float scale = calculateScaling(stage, level, statusCell.getMaxHeight());
        level.setScale(scale);
        levelCell.setWidget(level).width(level.getWidth() * scale).height(level.getHeight() * scale);
        setState(State.PLAYING);
    }

    @Override
    public void render(float delta) {
        this.act(delta);
        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, false);
        float scale = calculateScaling(stage, level, statusCell.getMaxHeight());
        level.setScale(scale);
        levelCell.width(level.getWidth() * scale).height(level.getHeight() * scale);
        background.update(true);

        Style style = getStyleByHeight(height);
        BitmapFont font = skin.getFont(style.toString());

        statusCell.height(font.getLineHeight());
        statusBar.setFont(font);
        pointsLabel.setFont(font);
        bigPointsLabel.setFont(style.getNext().toString());
        notificationLabel.setFont(font);
        notificationWindow.setStyle(style.toString());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void pause() {
        setState(State.PAUSED);
    }

    public boolean isInState(State state) {
        return this.state == state;
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void act(float delta) {
        check();
        showNotifications();
    }

    private void check() {
        // check lives
        if (lives <= 0 && !isInState(State.GAME_OVER)) {
            setState(State.GAME_OVER);
        }
    }

    private void showNotifications() {
        switch (state) {
            case PLAYING:
                // hide notifications
                if (notificationLabel.getActions().size == 0 || notificationWindow.isVisible()) {
                    notificationLabel.clearActions();
                    notificationLabel.setVisible(false);
                }
                if (notificationWindow.getActions().size == 0) {
                    notificationWindow.setVisible(false);
                }
                break;
            case PAUSED:
                notificationWindow.setTitle("PAUSE");
                notificationWindow.setScores(getLevel().getLevelScore(), getTotalScore() + getLevel().getLevelScore());
                notificationWindow.setVisible(true);
                break;
            case LEVEL_COMPLETED:
                notificationWindow.setTitle("LEVEL COMPLETED");
                notificationWindow.setScores(getLevel().getLevelScore(), getTotalScore());
                notificationWindow.setVisible(true);
                break;
            case GAME_OVER:
                notificationWindow.setTitle("GAME OVER");
                notificationWindow.setScores(getLevel().getLevelScore(), getTotalScore());
                notificationWindow.setVisible(true);
                break;
            case WIN:
                notificationWindow.setTitle("YOU WIN!");
                notificationWindow.setScores(getLevel().getLevelScore(), getTotalScore());
                notificationWindow.setVisible(true);
                break;
        }
    }

    private void onStateChanged() {
        switch (state) {
            case LEVEL_COMPLETED:
            case GAME_OVER:
                setTotalScore(getTotalScore() + level.getLevelScore());
                break;
        }
    }

    private float calculateScaling(Stage stage, Level level, float statusBarHeight) {
        int padding = 5;
        float wScaling = (stage.getWidth() - padding)/ level.getWidth();
        float hScaling = (stage.getHeight() - statusBarHeight - padding) / level.getHeight();
        return min(wScaling, hScaling);
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        onStateChanged();
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public NotificationLabel getNotificationLabel() {
        return notificationLabel;
    }

    public void setNotificationLabel(NotificationLabel notificationLabel) {
        this.notificationLabel = notificationLabel;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(long totalScore) {
        this.totalScore = totalScore;
    }

    public NotificationLabel getPointsLabel() {
        return pointsLabel;
    }

    public void setPointsLabel(NotificationLabel pointsLabel) {
        this.pointsLabel = pointsLabel;
    }

    public NotificationLabel getBigPointsLabel() {
        return bigPointsLabel;
    }

    public void setBigPointsLabel(NotificationLabel bigPointsLabel) {
        this.bigPointsLabel = bigPointsLabel;
    }

}