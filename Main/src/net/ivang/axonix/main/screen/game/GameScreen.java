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
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.screen.BaseScreen;
import net.ivang.axonix.main.screen.game.actor.*;

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

    int lives;
    private int levelIndex;
    Level level;

    private Skin skin;

    private StatusBar statusBar;
    private Cell levelCell;
    private Cell statusCell;
    private Notification notification;
    private Background background;

    public GameScreen(AxonixGame game) {
        super(game);
        this.state = State.PAUSED;
        this.lives = 3;

        // Input event handling
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new GameScreenInputProcessor(game, this));
        inputMultiplexer.addProcessor(stage);

        // Look & feel
        TextureAtlas atlas = new TextureAtlas("data/atlas/game_screen.atlas");
        skin = new Skin(Gdx.files.internal("data/skin/game_screen.json"), atlas);
        String fontName = getFontNameByHeight(Gdx.graphics.getHeight());

        statusBar = new StatusBar(this, skin, fontName);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        statusCell = rootTable.add(statusBar).height(skin.getFont(fontName).getLineHeight()).left();
        rootTable.row();
        levelCell = rootTable.add();

        background = new Background(skin);
        notification = new Notification(null, this, skin, fontName);
        DebugBar debugBar = new DebugBar(skin, FONT_NAME_SMALL);

        stage.addActor(background);
        stage.addActor(rootTable);
        stage.addActor(notification);
        stage.addActor(debugBar);
    }

    public void setLevel(int index) {
        this.levelIndex = index;
        Pixmap pixmap = new Pixmap(game.getLevelsFiles().get(index));
        level = new Level(this, pixmap, skin);
        float scale = calculateScaling(stage, level, statusCell.getMaxHeight());
        level.setScale(scale);
        levelCell.setWidget(level).width(level.getWidth() * scale).height(level.getHeight() * scale);
        this.state = State.PLAYING;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, false);
        float scale = calculateScaling(stage, level, statusCell.getMaxHeight());
        level.setScale(scale);
        levelCell.width(level.getWidth() * scale).height(level.getHeight() * scale);
        background.update(true);

        String fontName = getFontNameByHeight(height);
        BitmapFont font = skin.getFont(fontName);

        statusCell.height(font.getLineHeight());
        statusBar.setFont(font);
        notification.setFont(font);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void pause() {
        setState(State.PAUSED);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void act(float delta) {
        check();
        stage.act();
    }

    private void check() {
        // check lives
        if (lives <= 0) state = State.GAME_OVER;
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

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
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
}