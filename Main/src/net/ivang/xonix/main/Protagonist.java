package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class Protagonist {

    private enum Moving {
        UP, DOWN, LEFT, RIGHT, IDLE
    }

    private GameMap gameMap;

    private int lives;

    public Point pos;
    public Point prev;

    private int accel;

    private Moving moving;
    private float timeStep;

    public Protagonist(Point pos, GameMap gameMap) {
        this.pos = pos;
        this.prev = new Point();
        this.accel = 1;
        this.moving = Moving.IDLE;
        this.gameMap = gameMap;
    }

    public Protagonist(int posX, int posY, GameMap gameMap) {
        this(new Point(posX, posY), gameMap);
    }

    public void update(float deltaTime) {
        timeStep += deltaTime;
        if (timeStep > 0.1 / accel) {
            timeStep = 0;

            processKeys();
            updatePosition();
        }
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

        boolean onEarth = gameMap.getTileState(pos.x, pos.y) == GameMap.TS_EARTH;

        if((onEarth || moving != Moving.DOWN) && (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || isDraggedUp)) {
            moving = Moving.UP;
        }
        if((onEarth || moving != Moving.UP) && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || isDraggedDown)) {
            moving = Moving.DOWN;
        }
        if((onEarth || moving != Moving.RIGHT) && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || isDraggedLeft)) {
            moving = Moving.LEFT;
        }
        if((onEarth || moving != Moving.LEFT) && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || isDraggedRight)) {
            moving = Moving.RIGHT;
        }
//        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
//            accel = 2;
//        }
    }

    private void updatePosition() {
        Point tmp = new Point(pos.x, pos.y);
        switch (moving) {
            case UP:
                if (pos.y > 0) {
                    pos.y--;
                } else {
                    moving = Moving.IDLE;
                }
                break;
            case DOWN:
                if (pos.y < GameMap.HEIGHT - 1) {
                    pos.y++;
                } else {
                    moving = Moving.IDLE;
                }
                break;
            case LEFT:
                if (pos.x > 0) {
                    pos.x--;
                } else {
                    moving = Moving.IDLE;
                }
                break;
            case RIGHT:
                if (pos.x < GameMap.WIDTH - 1) {
                    pos.x++;
                } else {
                    moving = Moving.IDLE;
                }
                break;
        }
        // update previous coords
        if (moving != Moving.IDLE) {
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
