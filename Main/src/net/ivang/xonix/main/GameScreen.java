package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static java.lang.Math.floor;
import static java.lang.Math.min;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class GameScreen implements Screen {

    OrthographicCamera camera = new OrthographicCamera();
    ShapeRenderer shapeRenderer = new ShapeRenderer();

    GameMap gameMap;
    Protagonist protagonist;
    Enemy enemy, enemy2;

    public GameScreen() {
        camera.setToOrtho(false);

        gameMap = new GameMap();
        protagonist = new Protagonist(0, 0);

        enemy = new Enemy(15,10, gameMap);
//        enemy2 = new Enemy(20,7, gameMap);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        camera.update();

        protagonist.update(delta);
        enemy.update(delta);
//        enemy2.update(delta);
        gameMap.update(delta, protagonist, enemy);

        // TODO: move to constructor and on resize?
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        GameMap.setBlockSize((int) min(floor(width / GameMap.WIDTH), floor(height / GameMap.HEIGHT)));

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Rectangle);

        renderMap();
        renderProtagonist();
        renderEnemy(enemy);
//        renderEnemy(enemy2);

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
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

    private void renderMap() {
        int blockSize = GameMap.getBlockSize();

        for(int i = 0; i < GameMap.WIDTH; i++) {
            for(int j = 0; j < GameMap.HEIGHT; j++) {
                switch (gameMap.getTileState(i,j)) {
                    case GameMap.TS_WATER:
                        continue;
                    case GameMap.TS_EARTH:
                        shapeRenderer.setColor(1, 1, 1, 1);
                        break;
                    case GameMap.TS_TAIL:
                        shapeRenderer.setColor(0, 1, 0, 1);
                        break;
                    default:
                        shapeRenderer.setColor(1, 1, 1, 1);
                        break;
                }

                shapeRenderer.rect(i*blockSize, j*blockSize, blockSize, blockSize);
            }
        }
    }

    private void renderProtagonist() {
        int blockSize = GameMap.getBlockSize();

        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(protagonist.getPosX() * blockSize, protagonist.getPosY() * blockSize, blockSize, blockSize);
    }

    private void renderEnemy(Enemy enemy) {
        int blockSize = GameMap.getBlockSize();

        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.rect(enemy.pos.x * blockSize, enemy.pos.y * blockSize, blockSize, blockSize);
    }

}
