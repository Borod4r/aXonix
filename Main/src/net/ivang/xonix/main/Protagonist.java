package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class Protagonist {

    private GameMap gameMap;

    private int lives;

    public Vector2 pos;
    public Vector2 prev;

    private float accel;

    private Move move;
    private float timeStep;

    public Protagonist(Vector2 pos, GameMap gameMap) {
        this.pos = pos;
        this.prev = new Vector2();
        this.accel = 4;
        this.move = Move.IDLE;
        this.gameMap = gameMap;
    }

    public Protagonist(float y, float x, GameMap gameMap) {
        this(new Vector2(x, y), gameMap);
    }

    public void update(float deltaTime) {
        timeStep += deltaTime;
        if (timeStep > 0.1 / accel) {
            timeStep = 0;

            processKeys();
        }
        updatePosition(deltaTime);
    }

    //---------------------------------------------------------------------
    // Helper Methods
    //---------------------------------------------------------------------

    private void processKeys() {
        Point delta = new Point(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
        int diff = Math.abs(delta.x) - Math.abs(delta.y);

        boolean isDraggedDown = (Gdx.input.isTouched() && delta.y < 0 && diff < 0);
        boolean isDraggedUp = (Gdx.input.isTouched() && Gdx.input.getDeltaY() > 0 && diff <= 0);
        boolean isDraggedLeft = (Gdx.input.isTouched() && Gdx.input.getDeltaX() < 0 && diff > 0);
        boolean isDraggedRight = (Gdx.input.isTouched() && Gdx.input.getDeltaX() > 0 && diff >= 0);

        boolean onEarth = gameMap.getBlockStateByPx(pos.x, pos.y) == GameMap.BS_EARTH;

        if((onEarth || move != Move.DOWN) && (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || isDraggedUp)) {
            move = Move.UP;
        }
        if((onEarth || move != Move.UP) && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || isDraggedDown)) {
            move = Move.DOWN;
        }
        if((onEarth || move != Move.RIGHT) && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || isDraggedLeft)) {
            move = Move.LEFT;
        }
        if((onEarth || move != Move.LEFT) && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || isDraggedRight)) {
            move = Move.RIGHT;
        }
//        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
//            accel = 2;
//        }
    }

    private void updatePosition(float deltaTime) {
        float deltaPx = GameScreen.blockSize * deltaTime * 4f;

        Vector2 tmp = new Vector2(pos.x, pos.y);

        switch (move) {
            case UP:
                if (pos.y > GameScreen.blockSize * 0.5) {
                    pos.y -= deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case DOWN:
                if (pos.y < (GameMap.HEIGHT - 0.5) * GameScreen.blockSize) {
                    pos.y += deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case LEFT:
                if (pos.x > GameScreen.blockSize * 0.5) {
                    pos.x -= deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case RIGHT:
                if (pos.x < (GameMap.WIDTH - 0.5) * GameScreen.blockSize) {
                    pos.x += deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
        }

        switch (move) {

            case UP:
            case DOWN:
                float nx = (pos.x + GameScreen.blockSize * 0.5f) / GameScreen.blockSize ;
                float rx = Math.round(nx);
                if (rx > nx) {
                    pos.x++;
                } else if (rx < nx) {
                    pos.x--;
                    pos.x = Math.round(pos.x); // round x for smoother movement
                }
                break;
            case RIGHT:
            case LEFT:
                float ny = (pos.y + GameScreen.blockSize * 0.5f) / GameScreen.blockSize ;
                float ry = Math.round(ny);
                if (ry > ny) {
                    pos.y++;
                } else if (ry < ny) {
                    pos.y--;
                    pos.y = Math.round(pos.y); // round y for smoother movement
                }
                break;

        }

        Gdx.app.log("pos: ", pos.toString());

        // update previous coords
        if (move != Move.IDLE) {
            prev.x = tmp.x;
            prev.y = tmp.y;
        }
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------


    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

}
