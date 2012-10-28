package net.ivang.xonix.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

    // TODO: review this one
    public static int blockSize;

    private int width;
    private int height;

    private Game game;

    private State state;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    // TODO: TextureRegion
    private Texture texture;
    private Texture enemyT;
    private BitmapFont font;

    private GameMap gameMap;
    private Protagonist protagonist;
    private Enemy enemy;

    private Point shift;

    public GameScreen(Game game) {
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();

        this.game = game;

        this.state = State.PLAYING;
        Gdx.input.setInputProcessor(new GameScreenInputProcessor(this));

        camera = new OrthographicCamera(width, height);
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("data/tile.png"));
        enemyT = new Texture(Gdx.files.internal("data/bomb.png"));


        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        blockSize = calculateBlockSize(width, height);

        gameMap = new GameMap();
        protagonist = new Protagonist(0.5f * blockSize, (GameMap.HEIGHT - 0.5f) * blockSize, gameMap);
        protagonist.setLives(2);
        enemy = new Enemy(100,100, gameMap);

        // adapt to the screen resolution
        resize(width, height);
    }

    @Override
    public void render(float delta) {
        // CHECKING
        // check win
        if (gameMap.percentComplete > 80) {
            setState(State.LEVEL_COMPLETED);
        }
        // check lives
        if (protagonist.getLives() < 0) state = State.GAME_OVER;
        // check collisions
        if (gameMap.getBlockStateByPx(enemy.pos.x, enemy.pos.y) == GameMap.BS_TAIL) {
            protagonist.setLives(protagonist.getLives() - 1);
            // TODO: Bull Shit
            protagonist.pos.x = 0.5f * blockSize;
            protagonist.pos.y = (GameMap.HEIGHT - 0.5f) * blockSize;
            protagonist.prev.x = 0.5f * blockSize;
            protagonist.prev.y = (GameMap.HEIGHT - 0.5f) * blockSize;

            for(int i = 1; i < GameMap.WIDTH - 1; i++) {
                for(int j = 1; j < GameMap.HEIGHT - 1; j++) {
                    if (gameMap.getBlockState(i, j) == GameMap.BS_TAIL) {
                        gameMap.setBlockState(i, j, GameMap.BS_WATER);
                    }
                }
            }

            this.state = State.PAUSED;
        }

        // UPDATING
        switch (state) {
            case PLAYING:
                protagonist.update(delta);
                enemy.update(delta);
                gameMap.update(delta, protagonist, enemy);
                break;
            case PAUSED:
                break;
            case GAME_OVER:
                break;
        }

        // RENDERING
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderMap(shift);
        renderProtagonist(shift);
        renderEnemy(delta, enemy, shift);

        // TODO: Move strings to bundles
        String lives = "Lives: " + protagonist.getLives();
        String score = "Score: " + gameMap.mapScore;
        String percent = "Percent: " + gameMap.percentComplete;
        font.draw(batch, lives + "   " + score + "   " + percent, blockSize + shift.x, (GameMap.HEIGHT + 1)* blockSize + shift.y);

        switch (state) {
            case PAUSED: {
                String text = "PAUSE";
                BitmapFont.TextBounds bounds = font.getBounds(text);
                font.draw(batch, text, (GameMap.WIDTH/2)*blockSize + shift.x - bounds.width/2, (GameMap.HEIGHT/2 + 0.5f)*blockSize + shift.y - bounds.height/2);
                break;
            }
            case GAME_OVER: {
                String text = "GAME OVER";
                BitmapFont.TextBounds bounds = font.getBounds(text);
                font.draw(batch, text, (GameMap.WIDTH/2)*blockSize + shift.x - bounds.width/2, (GameMap.HEIGHT/2 + 0.5f)*blockSize + shift.y - bounds.height/2);
                break;
            }
            case LEVEL_COMPLETED: {
                String text = "LEVEL COMPLETED";
                BitmapFont.TextBounds bounds = font.getBounds(text);
                font.draw(batch, text, (GameMap.WIDTH/2)*blockSize + shift.x - bounds.width/2, (GameMap.HEIGHT/2 + 0.5f)*blockSize + shift.y - bounds.height/2);
                break;
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);

        blockSize = calculateBlockSize(width, height);
        shift = calculateShift(width, height);
        font.setScale((float) blockSize / 15);

        float deltaX = (float) width / (float) this.width;
        float deltaY = (float) height / (float) this.height;

        protagonist.pos.x = (int)(protagonist.pos.x * deltaX);
        protagonist.pos.y = (int)(protagonist.pos.y * deltaY);

        enemy.pos.x = (int)(enemy.pos.x * deltaX);
        enemy.pos.y = (int)(enemy.pos.y * deltaY);

        this.width = width;
        this.height = height;
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
//        Gdx.app.log("Pause"," aha!");
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
    // Getters & Setters
    //---------------------------------------------------------------------

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void renderMap(Point shift) {
        for(int i = 0; i < GameMap.WIDTH; i++) {
            for(int j = 0; j < GameMap.HEIGHT; j++) {
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

                batch.draw(texture, i*blockSize + shift.x, j*blockSize + shift.y, blockSize, blockSize);
            }
        }
    }

    private void renderProtagonist(Point shift) {
        batch.setColor(1, 0, 0, 1);
        batch.draw(texture, protagonist.pos.x + shift.x - (blockSize * 0.5f), protagonist.pos.y + shift.y - (blockSize * 0.5f), blockSize, blockSize);
    }

    private void renderEnemy(float deltaTime, Enemy enemy, Point shift) {
        batch.setColor(1, 1, 1, 1);
        batch.draw(enemyT, enemy.pos.x  + shift.x - (blockSize * 0.75f), enemy.pos.y + shift.y - (blockSize * 0.75f), blockSize * 1.5f, blockSize * 1.5f);

    }

    private int calculateBlockSize(int width, int height) {
        return (int) min(floor(width / GameMap.WIDTH), floor(height / (GameMap.HEIGHT + 1)));
    }

    private Point calculateShift(int width, int height) {
        int sx = (width - ((GameMap.WIDTH) * blockSize)) / 2;
        int sy = (height - ((GameMap.HEIGHT + 1) * blockSize)) / 2;

        return new Point(sx, sy);
    }

    private void gameOver() {
        game.getScreen().dispose();
        game.setScreen(new GameScreen(game));
    }

}
