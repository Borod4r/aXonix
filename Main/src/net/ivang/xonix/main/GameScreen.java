package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

import static java.lang.Math.floor;
import static java.lang.Math.min;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class GameScreen implements Screen {

    public enum State {
        PLAYING, PAUSED, LOST_LIFE, LEVEL_COMPLETED, GAME_OVER
    }

    private XonixGame game;
    private State state;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    // TODO: TextureRegion
    private Texture texture;
    private Texture enemyT;
    private BitmapFont font;

    private Level level;
    private int levelIndex;

    // other
    int lives;
    float lostLifeLabelDelay;

    public GameScreen(XonixGame game) {
        this.game = game;
        this.state = State.PAUSED; // init?
        this.lives = 3;
        // input event handling
        Gdx.input.setInputProcessor(new GameScreenInputProcessor(game, this));
        /* Textures */
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("data/tile.png"));
        enemyT = new Texture(Gdx.files.internal("data/bomb.png"));
        /* BitmapFont */
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setUseIntegerPositions(false) ;
        font.setScale(1/15f);
    }

    public void setLevel(int index) {
        this.levelIndex = index;

        Pixmap pixmap = new Pixmap(game.getLevelsFiles().get(index));
        this.level = new Level(pixmap);
        this.state = State.PLAYING;

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        int gmWidth = level.getGameMap().getWidth();
        int gmHeight = level.getGameMap().getHeight();

        int blockSize = calculateBlockSize(width, height, gmWidth, gmHeight);

        camera = new OrthographicCamera(width/blockSize, height/blockSize);
        camera.translate(gmWidth/2, gmHeight/2);
    }

    @Override
    public void render(float delta) {
        GameMap gameMap = level.getGameMap();
        Protagonist protagonist = level.getProtagonist();
        List<Enemy> enemies = level.getEnemies();

        check(level);
        update(level, delta);

        // RENDERING
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderMap(gameMap);
        renderProtagonist(protagonist);
        renderEnemies(enemies);

        int gmWidth = gameMap.getWidth();
        int gmHeight = gameMap.getHeight();

        // TODO: Move strings to bundles
        String livesLabel = "Lives: " + lives;
        String scoreLabel = "Score: " + gameMap.mapScore;
        String percentLabel = "Level: " + (levelIndex + 1) +" (" + gameMap.percentComplete + "/80%)";
        font.draw(batch, livesLabel + "   " + scoreLabel + "   " + percentLabel, 1, gmHeight + 1);

        switch (state) {
            case PAUSED:
                drawStringAtCenter(batch, font, "PAUSE", gmWidth, gmHeight);
                break;
            case GAME_OVER:
                drawStringAtCenter(batch, font, "GAME OVER", gmWidth, gmHeight);
                break;
            case LEVEL_COMPLETED:
                drawStringAtCenter(batch, font, "LEVEL COMPLETED", gmWidth, gmHeight);
                break;
        }

        if (lostLifeLabelDelay > 0) {
            drawStringAtCenter(batch, font, "LIFE LEFT!!!", gmWidth, gmHeight);
            lostLifeLabelDelay -= delta;
        }

        batch.end();
    }

    private void drawStringAtCenter(SpriteBatch batch, BitmapFont font, String str, int gmWidth, int gmHeight) {
        BitmapFont.TextBounds bounds = font.getBounds(str);
        font.draw(batch, str, (gmWidth/2) - bounds.width/2, (gmHeight/2 + 0.5f) - bounds.height/2);
    }

    @Override
    public void resize(int width, int height) {
        this.setState(State.PAUSED);

        int gmWidth = level.getGameMap().getWidth();
        int gmHeight = level.getGameMap().getHeight();
        int blockSize = calculateBlockSize(width, height, gmWidth, gmHeight);

        camera = new OrthographicCamera(width/blockSize, height/blockSize);
        camera.translate(level.getGameMap().getWidth()/2, level.getGameMap().getHeight()/2 + 0.5f);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void check(Level level) {
        GameMap gameMap = level.getGameMap();
        List<Enemy> enemies = level.getEnemies();

        // check win
        if (gameMap.percentComplete > 80) {
            setState(State.LEVEL_COMPLETED);
        }
        // check lives
        if (lives <= 0) state = State.GAME_OVER;
        // check collisions
        for (Enemy enemy : enemies) {
            if (gameMap.getBlockState(enemy.pos.x, enemy.pos.y) == GameMap.BS_TAIL) {
                lives--;
                Protagonist protagonist = new Protagonist(level.getProtStartPos().cpy(), level.getGameMap());
                level.setProtagonist(protagonist);

                for(int i = 1; i < gameMap.getWidth() - 1; i++) {
                    for(int j = 1; j < gameMap.getHeight() - 1; j++) {
                        if (gameMap.getBlockState(i, j) == GameMap.BS_TAIL) {
                            gameMap.setBlockState(i, j, GameMap.BS_WATER);
                        }
                    }
                }

                setState(State.LOST_LIFE);
            }
        }
    }

    private void update(Level level, float deltaTime) {
        GameMap gameMap = level.getGameMap();
        Protagonist protagonist = level.getProtagonist();
        List<Enemy> enemies = level.getEnemies();

        switch (state) {
            case PLAYING:
                protagonist.update(deltaTime);
                for (Enemy enemy : enemies) {
                    enemy.update(deltaTime);
                }
                gameMap.update(deltaTime, protagonist, enemies);
                break;
//            case PAUSED:
//                break;
//            case GAME_OVER:
//                break;
            case LOST_LIFE:
                if (lives > 0) lostLifeLabelDelay = 2;
                setState(State.PLAYING);
                break;
        }
    }

    private void renderMap(GameMap gameMap) {
        for(int i = 0; i < gameMap.getWidth(); i++) {
            for(int j = 0; j < gameMap.getHeight(); j++) {
                switch (gameMap.getBlockState(i, j)) {
                    case GameMap.BS_WATER:
                        batch.setColor(0, 0, 0.07f, 1);
                        break;
                    case GameMap.BS_EARTH:
                        batch.setColor(0.1f, 0.1f, 0.8f, 1);
                        break;
                    case GameMap.BS_TAIL:
                        batch.setColor(0.3f, 0.3f, 1f, 1);
                        break;
                }
                batch.draw(texture, i, j, 1, 1);
            }
        }
    }

    private void renderProtagonist(Protagonist protagonist) {
        batch.setColor(1, 0, 0, 1);
        batch.draw(texture, protagonist.pos.x - 0.5f, protagonist.pos.y - 0.5f, 1, 1);
    }

    private void renderEnemies(List<Enemy> enemies) {
        batch.setColor(1, 1, 1, 1);
        for (Enemy enemy : enemies) {
            batch.draw(enemyT, enemy.pos.x - 0.75f, enemy.pos.y - 0.75f, 1.5f, 1.5f);
        }

    }

    private int calculateBlockSize(int width, int height, int gmWidth, int gmHeight) {
        return (int) min(floor(width / gmWidth), floor(height / (gmHeight + 1)));
    }

    private void gameOver() {
        game.getScreen().dispose();
        game.setScreen(new GameScreen(game));
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

    public Level getLevel() {
        return level;
    }
}
