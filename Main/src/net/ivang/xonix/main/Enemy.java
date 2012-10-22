package net.ivang.xonix.main;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class Enemy {

    public Point pos;
    private Move move;
    private float timeStep;

    private GameMap gameMap;

    Enemy(int x, int y, GameMap gameMap) {
        pos = new Point(x, y);
        move = Move.UP_LEFT;
        this.gameMap = gameMap;
    }

    public void update(float deltaTime) {
        timeStep += deltaTime;
        if (timeStep > 0.1) {
            timeStep = 0;

            switch (move) {
                case UP_LEFT:
                    pos.x--;
                    pos.y++;
                    if (gameMap.getTileState(pos.x,pos.y+1) == GameMap.TS_EARTH) {
                        if (gameMap.getTileState(pos.x-1,pos.y) == GameMap.TS_EARTH) {
                            move = Move.DOWN_RIGHT;
                        } else {
                            move = Move.DOWN_LEFT;
                        }
                    } else if (gameMap.getTileState(pos.x-1,pos.y) == GameMap.TS_EARTH) {
                        move = Move.UP_RIGHT;
                    }
                    break;
                case UP_RIGHT:
                    pos.x++;
                    pos.y++;
                    if (gameMap.getTileState(pos.x,pos.y+1) == GameMap.TS_EARTH) {
                        if (gameMap.getTileState(pos.x+1,pos.y) == GameMap.TS_EARTH) {
                            move = Move.DOWN_LEFT;
                        } else {
                            move = Move.DOWN_RIGHT;
                        }
                    } else if (gameMap.getTileState(pos.x+1,pos.y)== GameMap.TS_EARTH) {
                        move = Move.UP_LEFT;
                    }
                    break;
                case DOWN_LEFT:
                    pos.x--;
                    pos.y--;
                    if (gameMap.getTileState(pos.x, pos.y-1) == GameMap.TS_EARTH) {
                        if (gameMap.getTileState(pos.x-1, pos.y) == GameMap.TS_EARTH) {
                            move = Move.UP_RIGHT;
                        } else {
                            move = Move.UP_LEFT;
                        }
                    } else if (gameMap.getTileState(pos.x-1, pos.y) == GameMap.TS_EARTH) {
                        move = Move.DOWN_RIGHT;
                    }
                    break;
                case DOWN_RIGHT:
                    pos.x++;
                    pos.y--;
                    if (gameMap.getTileState(pos.x,pos.y-1) == GameMap.TS_EARTH) {
                        if (gameMap.getTileState(pos.x+1,pos.y) == GameMap.TS_EARTH) {
                            move = Move.UP_LEFT;
                        } else {
                            move = Move.UP_RIGHT;
                        }
                    } else if (gameMap.getTileState(pos.x+1,pos.y) == GameMap.TS_EARTH) {
                        move = Move.DOWN_LEFT;
                    }
                    break;
            }
        }
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------


    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
