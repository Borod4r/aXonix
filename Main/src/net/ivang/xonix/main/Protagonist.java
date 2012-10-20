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

//    private GameMap gameMap;

    private int lives;

    public Position pos;
    public Position prev;

    private int accel;

    private Moving moving;
    private float timeStep;

    public Protagonist(Position pos) {
        this.pos = pos;
        this.prev = new Position();
        this.accel = 1;
        this.moving = Moving.IDLE;
    }

    public Protagonist(int posX, int posY) {
        this(new Position(posX, posY));
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
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            moving = Moving.UP;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            moving = Moving.DOWN;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            moving = Moving.LEFT;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moving = Moving.RIGHT;
        }
//        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
//            accel = 2;
//        }
    }

    private void updatePosition() {
        Position tmp = new Position(pos.x, pos.y);
        switch (moving) {
            case UP:
                if (pos.y > 0) {
                    pos.y--;
                }
                break;
            case DOWN:
                if (pos.y < GameMap.HEIGHT - 1) {
                    pos.y++;
                }
                break;
            case LEFT:
                if (pos.x > 0) {
                    pos.x--;
                }
                break;
            case RIGHT:
                if (pos.x < GameMap.WIDTH - 1) {
                    pos.x++;
                }
                break;
        }
        // update previous coords
        if (tmp.x != pos.x || tmp.y != pos.y) {
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
