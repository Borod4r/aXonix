package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;

import static java.lang.Math.min;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class GameScreen implements Screen {

    public enum State {
        PLAYING, PAUSED, LEVEL_COMPLETED, GAME_OVER, WIN
    }

    private final int STATUS_BAR_HEIGHT = 20;

    private XonixGame game;
    private Stage stage;
    private InputMultiplexer inputMultiplexer;

    private State state;

    int lives;
    private int levelIndex;
    Level level;

    private TextureAtlas atlas;
    private Skin skin;

    private StatusBar statusBar;
    private Cell levelCell;
    private Label notification;

    public GameScreen(XonixGame game) {
        this.game = game;
        this.stage = new Stage();
        this.state = State.PAUSED;
        this.lives = 3;

        // Input event handling
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new GameScreenInputProcessor(game, this));
        inputMultiplexer.addProcessor(stage);

        // Looks & feel
        atlas = new TextureAtlas("data/atlas/game_screen.atlas");
        skin = new Skin(Gdx.files.internal("data/skin/game_screen.json"), atlas);

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        statusBar = new StatusBar(this, skin);
        rootTable.add(statusBar).height(STATUS_BAR_HEIGHT).left();
        rootTable.row();
        levelCell = rootTable.add();

        notification = new Notification(null, this, skin);

        DebugBar debugBar = new DebugBar(skin);

        stage.addActor(rootTable);
        stage.addActor(notification);
        stage.addActor(debugBar);

    }

    public void setLevel(int index) {
        this.levelIndex = index;
        Pixmap pixmap = new Pixmap(game.getLevelsFiles().get(index));
        level = new Level(this, pixmap, skin);
        float scale = calculateScaling(stage, level);
        level.setScale(scale);
        levelCell.setWidget(level).width(level.getWidth() * scale).height(level.getHeight() * scale);
        this.state = State.PLAYING;
    }

    @Override
    public void render(float delta) {
        act(delta);
        // draw
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, false);
        float scale = calculateScaling(stage, level);
        level.setScale(scale);
        levelCell.width(level.getWidth() * scale).height(level.getHeight() * scale);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void hide() {
        // do nothing
    }

    @Override
    public void pause() {
        setState(State.PAUSED);
    }

    @Override
    public void resume() {
        // do nothing
    }

    @Override
    public void dispose() {
        // do nothing
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

    private float calculateScaling(Stage stage, Level level) {
        float wScaling = (stage.getWidth() - 5)/ level.getWidth();
        float hScaling = (stage.getHeight() - STATUS_BAR_HEIGHT - 5) / level.getHeight();
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

    public Label getNotification() {
        return notification;
    }

    public void setNotification(Label notification) {
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
