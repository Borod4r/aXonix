package net.ivang.xonix.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import javax.swing.text.Position;

import static java.lang.Math.floor;
import static java.lang.Math.min;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class GameScreen implements Screen {

    // TODO: review this one
    public static int blockSize;

    private int width;
    private int height;

    Game game;

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
        this.width = 800;
        this.height = 480;

        this.game = game;

        camera = new OrthographicCamera(width, height);
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("data/tile.png"));
        enemyT = new Texture(Gdx.files.internal("data/bomb.png"));


        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        gameMap = new GameMap();
        protagonist = new Protagonist(0, GameMap.HEIGHT - 1, gameMap);
        protagonist.setLives(2);
        enemy = new Enemy(100,100, gameMap);

        // adapt to the screen resolution
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        if (protagonist.pos.equals(enemy.pos) || gameMap.getTileState((int) enemy.pos.x/blockSize, (int) enemy.pos.y/blockSize) == GameMap.TS_TAIL) {
            protagonist.setLives(protagonist.getLives() - 1);
            // TODO: Bull Shit
            protagonist.pos.x = 0;
            protagonist.pos.y = 0;
            protagonist.prev.x = 0;
            protagonist.prev.y = 0;

            for(int i = 1; i < GameMap.WIDTH - 1; i++) {
                for(int j = 1; j < GameMap.HEIGHT - 1; j++) {
                    if (gameMap.getTileState(i,j) == GameMap.TS_TAIL) {
                        gameMap.setTileState(i,j, GameMap.TS_WATER);
                    }
                }
            }


        }

        if (protagonist.getLives() < 0) gameOver();

        protagonist.update(delta);
        enemy.update(delta);
        gameMap.update(delta, protagonist, enemy);

        // rendering part
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

    private void renderMap(Point shift) {
        for(int i = 0; i < GameMap.WIDTH; i++) {
            for(int j = 0; j < GameMap.HEIGHT; j++) {
                switch (gameMap.getTileState(i,j)) {
                    case GameMap.TS_WATER:
                        batch.setColor(0, 0, 0.07f, 1);
                        break;
                    case GameMap.TS_EARTH:
                        batch.setColor(0.1f, 0.1f, 0.8f, 1);
                        break;
                    case GameMap.TS_TAIL:
                        batch.setColor(0.3f, 0.3f, 1f, 1);
                        break;
                }

                batch.draw(texture, i*blockSize + shift.x, j*blockSize + shift.y, blockSize, blockSize);
            }
        }
    }

    private void renderProtagonist(Point shift) {
        batch.setColor(1, 0, 0, 1);
        batch.draw(texture, protagonist.pos.x * blockSize + shift.x, protagonist.pos.y * blockSize + shift.y, blockSize, blockSize);
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
