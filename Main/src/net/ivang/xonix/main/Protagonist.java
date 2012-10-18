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

    private int posX;
    private int posY;

    private int prevX;
    private int prevY;

    private int accel;

    private Moving moving;
    private float timeStep;

    Protagonist(int posX, int posY) {
//        this.gameMap = gameMap;
        this.posX = posX;
        this.posY = posY;
        this.accel = 1;

        moving = Moving.IDLE;
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
        int tmpX = posX;
        int tmpY = posY;

        switch (moving) {
            case UP:
                if (posY > 0) {
                    posY--;
                }
                break;
            case DOWN:
                if (posY < GameMap.HEIGHT - 1) {
                    posY++;
                }
                break;
            case LEFT:
                if (posX > 0) {
                    posX--;
                }
                break;
            case RIGHT:
                if (posX < GameMap.WIDTH - 1) {
                    posX++;
                }
                break;
        }

        // update previous coords
        if (tmpX != posX || tmpY != posY) {
            prevX = tmpX;
            prevY = tmpY;
        }
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPrevX() {
        return prevX;
    }

    public void setPrevX(int prevX) {
        this.prevX = prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public void setPrevY(int prevY) {
        this.prevY = prevY;
    }
}
