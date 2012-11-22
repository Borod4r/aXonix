package net.ivang.xonix.main;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class Enemy {

    public Vector2 pos;
    private Move moveDirection;
    private GameMap gameMap;

    Enemy(float x, float y, GameMap gameMap) {
        pos = new Vector2(x, y);
        moveDirection = Move.UP_LEFT;
        this.gameMap = gameMap;
    }

    public void update(float deltaTime) {

        float radius = 0.75f;
        float deltaPx = deltaTime * 4f;

        switch (moveDirection) {
            case UP_LEFT:
                pos.x -= deltaPx;
                pos.y += deltaPx;
                if (gameMap.getBlockState(pos.x, pos.y + radius) == GameMap.BS_EARTH) {
                    if (gameMap.getBlockState(pos.x - radius, pos.y) == GameMap.BS_EARTH) {
                        moveDirection = Move.DOWN_RIGHT;
                    } else {
                        moveDirection = Move.DOWN_LEFT;
                    }
                } else if (gameMap.getBlockState(pos.x - radius, pos.y) == GameMap.BS_EARTH) {
                    moveDirection = Move.UP_RIGHT;
                }
                break;
            case UP_RIGHT:
                pos.x += deltaPx;
                pos.y += deltaPx;
                if (gameMap.getBlockState(pos.x, pos.y + radius) == GameMap.BS_EARTH) {
                    if (gameMap.getBlockState(pos.x + radius, pos.y) == GameMap.BS_EARTH) {
                        moveDirection = Move.DOWN_LEFT;
                    } else {
                        moveDirection = Move.DOWN_RIGHT;
                    }
                } else if (gameMap.getBlockState(pos.x + radius, pos.y)== GameMap.BS_EARTH) {
                    moveDirection = Move.UP_LEFT;
                }
                break;
            case DOWN_LEFT:
                pos.x -= deltaPx;
                pos.y -= deltaPx;
                if (gameMap.getBlockState(pos.x, pos.y - radius) == GameMap.BS_EARTH) {
                    if (gameMap.getBlockState(pos.x - radius, pos.y) == GameMap.BS_EARTH) {
                        moveDirection = Move.UP_RIGHT;
                    } else {
                        moveDirection = Move.UP_LEFT;
                    }
                } else if (gameMap.getBlockState(pos.x - radius, pos.y) == GameMap.BS_EARTH) {
                    moveDirection = Move.DOWN_RIGHT;
                }
                break;
            case DOWN_RIGHT:
                pos.x += deltaPx;
                pos.y -= deltaPx;
                if (gameMap.getBlockState(pos.x, pos.y - radius) == GameMap.BS_EARTH) {
                    if (gameMap.getBlockState(pos.x + radius, pos.y) == GameMap.BS_EARTH) {
                        moveDirection = Move.UP_LEFT;
                    } else {
                        moveDirection = Move.UP_RIGHT;
                    }
                } else if (gameMap.getBlockState(pos.x + radius, pos.y) == GameMap.BS_EARTH) {
                    moveDirection = Move.DOWN_LEFT;
                }
                break;
        }
    }


    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------


    public Move getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(Move moveDirection) {
        this.moveDirection = moveDirection;
    }

}
