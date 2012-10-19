package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static java.lang.Math.floor;
import static java.lang.Math.min;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class GameScreen implements Screen {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture texture;

    private GameMap gameMap;
    private Protagonist protagonist;
    private Enemy enemy;

    private int blockSize;
    private Position shift;

    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("data/tile.png"));

        gameMap = new GameMap();
        protagonist = new Protagonist(0, 0);
        enemy = new Enemy(15,10, gameMap);

        blockSize = calculateBlockSize();
        shift = calculateShift();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        camera.update();

        protagonist.update(delta);
        enemy.update(delta);
        gameMap.update(delta, protagonist, enemy);


        // rendering part
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

            renderMap(shift);
            renderProtagonist(shift);
            renderEnemy(enemy, shift);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        blockSize = calculateBlockSize();
        shift = calculateShift();
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

    private void renderMap(Position shift) {
        batch.disableBlending();
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

    private void renderProtagonist(Position shift) {
        batch.setColor(1, 0, 0, 1);
        batch.draw(texture, protagonist.getPosX() * blockSize + shift.x, protagonist.getPosY() * blockSize + shift.y, blockSize, blockSize);
    }

    private void renderEnemy(Enemy enemy, Position shift) {
        batch.setColor(1, 1, 0, 1);
        batch.draw(texture, enemy.pos.x * blockSize + shift.x, enemy.pos.y * blockSize + shift.y, blockSize, blockSize);
    }

    private int calculateBlockSize() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        return (int) min(floor(width / GameMap.WIDTH), floor(height / GameMap.HEIGHT));
    }

    private Position calculateShift() {
        int sx = (Gdx.graphics.getWidth() - (GameMap.WIDTH * blockSize)) / 2;
        int sy = (Gdx.graphics.getHeight() - (GameMap.HEIGHT * blockSize)) / 2;

        return new Position(sx, sy);
    }

}
