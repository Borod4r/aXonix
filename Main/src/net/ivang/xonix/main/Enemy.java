package net.ivang.xonix.main;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class Enemy {

    private enum Moving {
        UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT, IDLE
    }

    public Point pos;
    private Moving moving;
    private float timeStep;

    private GameMap gameMap;

    Enemy(int x, int y, GameMap gameMap) {
        pos = new Point(x, y);
        moving = Moving.UP_LEFT;
        this.gameMap = gameMap;
    }

    public void update(float deltaTime) {
        timeStep += deltaTime;
        if (timeStep > 0.1) {
            timeStep = 0;

            switch (moving) {
                case UP_LEFT:
                    pos.x--;
                    pos.y++;
                    if (gameMap.getTileState(pos.x,pos.y+1) == GameMap.TS_EARTH) {
                        if (gameMap.getTileState(pos.x-1,pos.y) == GameMap.TS_EARTH) {
                            moving = Moving.DOWN_RIGHT;
                        } else {
                            moving = Moving.DOWN_LEFT;
                        }
                    } else if (gameMap.getTileState(pos.x-1,pos.y) == GameMap.TS_EARTH) {
                        moving = Moving.UP_RIGHT;
                    }
                    break;
                case UP_RIGHT:
                    pos.x++;
                    pos.y++;
                    if (gameMap.getTileState(pos.x,pos.y+1) == GameMap.TS_EARTH) {
                        if (gameMap.getTileState(pos.x+1,pos.y) == GameMap.TS_EARTH) {
                            moving = Moving.DOWN_LEFT;
                        } else {
                            moving = Moving.DOWN_RIGHT;
                        }
                    } else if (gameMap.getTileState(pos.x+1,pos.y)== GameMap.TS_EARTH) {
                        moving = Moving.UP_LEFT;
                    }
                    break;
                case DOWN_LEFT:
                    pos.x--;
                    pos.y--;
                    if (gameMap.getTileState(pos.x, pos.y-1) == GameMap.TS_EARTH) {
                        if (gameMap.getTileState(pos.x-1, pos.y) == GameMap.TS_EARTH) {
                            moving = Moving.UP_RIGHT;
                        } else {
                            moving = Moving.UP_LEFT;
                        }
                    } else if (gameMap.getTileState(pos.x-1, pos.y) == GameMap.TS_EARTH) {
                        moving = Moving.DOWN_RIGHT;
                    }
                    break;
                case DOWN_RIGHT:
                    pos.x++;
                    pos.y--;
                    if (gameMap.getTileState(pos.x,pos.y-1) == GameMap.TS_EARTH) {
                        if (gameMap.getTileState(pos.x+1,pos.y) == GameMap.TS_EARTH) {
                            moving = Moving.UP_LEFT;
                        } else {
                            moving = Moving.UP_RIGHT;
                        }
                    } else if (gameMap.getTileState(pos.x+1,pos.y) == GameMap.TS_EARTH) {
                        moving = Moving.DOWN_LEFT;
                    }
                    break;
            }
        }
    }
}
