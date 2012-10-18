package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
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

    private OrthographicCamera camera = new OrthographicCamera();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private GameMap gameMap;
    private Protagonist protagonist;
    private Enemy enemy;

    private int blockSize;
    private Position shift;

    public GameScreen() {
        camera.setToOrtho(false);

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
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Rectangle);

            renderMap(shift);
            renderProtagonist(shift);
            renderEnemy(enemy, shift);

        shapeRenderer.end();
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

                shapeRenderer.rect(i*blockSize + shift.x, j*blockSize + shift.y, blockSize, blockSize);
            }
        }
    }

    private void renderProtagonist(Position shift) {
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(protagonist.getPosX() * blockSize + shift.x, protagonist.getPosY() * blockSize + shift.y, blockSize, blockSize);
    }

    private void renderEnemy(Enemy enemy, Position shift) {
        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.rect(enemy.pos.x * blockSize + shift.x, enemy.pos.y * blockSize + shift.y, blockSize, blockSize);
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
