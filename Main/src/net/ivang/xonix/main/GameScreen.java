package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

//    private final static int STATUS_BAR_HEIGHT = 20;
    private final static String FONT_NAME_SMALL = "small";
    private final static String FONT_NAME_NORMAL = "normal";
    private final static String FONT_NAME_LARGE = "large";

    private XonixGame game;
    private Stage stage;
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

    public GameScreen(XonixGame game) {
        this.game = game;
        this.stage = new Stage();
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
        act(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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

    private float calculateScaling(Stage stage, Level level, float statusBarHeight) {
        int padding = 5;
        float wScaling = (stage.getWidth() - padding)/ level.getWidth();
        float hScaling = (stage.getHeight() - statusBarHeight - padding) / level.getHeight();
        return min(wScaling, hScaling);
    }

    private String getFontNameByHeight(int height) {
        if (height < 480) {
            return FONT_NAME_SMALL;
        } else if (height < 720) {
            return FONT_NAME_NORMAL;
        } else {
            return FONT_NAME_LARGE;
        }
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
