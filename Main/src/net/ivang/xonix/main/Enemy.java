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

    Enemy(int x, int y, GameMap gameMap) {
        pos = new Vector2(x, y);
        moveDirection = Move.UP_LEFT;
        this.gameMap = gameMap;
    }

    public void update(float deltaTime) {

        int radius = GameScreen.blockSize/2;
        float deltaPx = GameScreen.blockSize * deltaTime * 4f;

        switch (moveDirection) {
            case UP_LEFT:
                pos.x -= deltaPx;
                pos.y += deltaPx;
                if (gameMap.getTileStateByPos(pos.x, (pos.y + radius)) == GameMap.TS_EARTH) {
                    if (gameMap.getTileStateByPos((pos.x - radius), pos.y) == GameMap.TS_EARTH) {
                        moveDirection = Move.DOWN_RIGHT;
                    } else {
                        moveDirection = Move.DOWN_LEFT;
                    }
                } else if (gameMap.getTileStateByPos((pos.x - radius), pos.y) == GameMap.TS_EARTH) {
                    moveDirection = Move.UP_RIGHT;
                }
                break;
            case UP_RIGHT:
                pos.x += deltaPx;
                pos.y += deltaPx;
                if (gameMap.getTileStateByPos(pos.x, (pos.y + radius)) == GameMap.TS_EARTH) {
                    if (gameMap.getTileStateByPos((pos.x + radius), pos.y) == GameMap.TS_EARTH) {
                        moveDirection = Move.DOWN_LEFT;
                    } else {
                        moveDirection = Move.DOWN_RIGHT;
                    }
                } else if (gameMap.getTileStateByPos((pos.x + radius), pos.y)== GameMap.TS_EARTH) {
                    moveDirection = Move.UP_LEFT;
                }
                break;
            case DOWN_LEFT:
                pos.x -= deltaPx;
                pos.y -= deltaPx;
                if (gameMap.getTileStateByPos(pos.x, (pos.y - radius)) == GameMap.TS_EARTH) {
                    if (gameMap.getTileStateByPos((pos.x - radius), pos.y) == GameMap.TS_EARTH) {
                        moveDirection = Move.UP_RIGHT;
                    } else {
                        moveDirection = Move.UP_LEFT;
                    }
                } else if (gameMap.getTileStateByPos((pos.x - radius), pos.y) == GameMap.TS_EARTH) {
                    moveDirection = Move.DOWN_RIGHT;
                }
                break;
            case DOWN_RIGHT:
                pos.x += deltaPx;
                pos.y -= deltaPx;
                if (gameMap.getTileStateByPos(pos.x, (pos.y - radius)) == GameMap.TS_EARTH) {
                    if (gameMap.getTileStateByPos((pos.x + radius), pos.y) == GameMap.TS_EARTH) {
                        moveDirection = Move.UP_LEFT;
                    } else {
                        moveDirection = Move.UP_RIGHT;
                    }
                } else if (gameMap.getTileStateByPos((pos.x + radius), pos.y) == GameMap.TS_EARTH) {
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
