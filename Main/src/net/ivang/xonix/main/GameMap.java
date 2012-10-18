package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;

import java.util.*;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class GameMap {

    public static final int WIDTH = 40;
    public static final int HEIGHT = 20;

    public static final byte TS_WATER = 0;
    public static final byte TS_EARTH = 1;
    public static final byte TS_TAIL = 2;

    private static int blockSize;

    private byte[][] state;

    public GameMap() {
        state = new byte[WIDTH][HEIGHT];

        // init borders
        for(int i = 0; i < WIDTH; i++) {
            state[i][0] = TS_EARTH;
            state[i][HEIGHT -1] = TS_EARTH;
        }

        for(int j = 0; j < HEIGHT; j++) {
            state[0][j] = TS_EARTH;
            state[WIDTH -1][j] = TS_EARTH;
        }
    }

    public void update(float deltaTime, Protagonist protagonist, Enemy enemy) {
        int prevX = protagonist.getPrevX();
        int prevY = protagonist.getPrevY();
        if (getTileState(prevX, prevY) == GameMap.TS_WATER) {
            setTileState(prevX, prevY, GameMap.TS_TAIL);
        }

        // try to fill areas
        int posX = protagonist.getPosX();
        int posY = protagonist.getPosY();

        if (getTileState(prevX, prevY) == GameMap.TS_TAIL && getTileState(posX, posY) == GameMap.TS_EARTH) {

            byte[][] tmpState = new byte[WIDTH][HEIGHT];
            // normalize
//            for(int i = 1; i < GameMap.WIDTH - 1; i++) {
//                for(int j = 1; j < GameMap.HEIGHT - 1; j++) {
//                    if (state[i][j] == TS_WATER) {
//                        tmpState[i][j] = 1;
//                    } else {
//                        tmpState[i][j] = 0;
//                    }
//                }
//            }

            // http://habrahabr.ru/post/119244/
            byte spotNum = 0;
            // TODO: HashMap and ArrayList?
            Map<Byte, List<Position>> spots = new HashMap<Byte, List<Position>>();

            for(int i = 1; i < GameMap.WIDTH - 1; i++) {
                for(int j = 1; j < GameMap.HEIGHT - 1; j++) {
                    byte A = state[i][j];
                    if (A == TS_WATER) {
                        byte B = tmpState[i][j-1];
                        byte C = tmpState[i-1][j];

                        if ( B == 0) {
                            if (C == 0) {
                                // New Spot
                                spotNum++;
                                tmpState[i][j] = spotNum;

                                List<Position> spot = new ArrayList<Position>();
                                spot.add(new Position(i,j));

                                spots.put(spotNum, spot);
                            } else {   // C!=0
                                tmpState[i][j] = C;
                                spots.get(C).add(new Position(i,j));
                            }
                        }

                        if (B != 0) {
                            if(C == 0) {
                                tmpState[i][j] = B;
                                spots.get(B).add(new Position(i,j));
                            } else { // C != 0
                                tmpState[i][j] = B;
                                spots.get(B).add(new Position(i,j));
                                if (B != C) {
                                    for(int m = 1; m < GameMap.WIDTH - 1; m++) {
                                        for(int n = 1; n < GameMap.HEIGHT; n++) {
                                            if (tmpState[m][n] == C) {
                                                tmpState[m][n] = B;
                                            }
                                        }
                                    }
                                    spots.get(B).addAll(spots.get(C));
                                    spots.remove(C);
                                }
                            }
                        }
                    } else if(A == TS_TAIL) {
                        // turn trail to the land
                        setTileState(i,j, TS_EARTH);
                    }
                }
            }

            Iterator iterator = spots.keySet().iterator();
            while (iterator.hasNext()) {
                for(Position pos: spots.get((Byte) iterator.next())) {
                    if (pos.equals(enemy.pos)) {
                        iterator.remove();
                        break;
                    }
                }
            }

            for(List<Position> spot : spots.values()) {
                for(Position pos : spot) {
                    setTileState(pos.x, pos.y, TS_EARTH);
                }
            }

        }
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public void setTileState(int x, int y, byte value) {
        if (x >= 0 && x < WIDTH && y >=0 && y < HEIGHT) {
            state[x][y] = value;
        }
    }

    public byte getTileState(int x, int y) {
        return state[x][y];
    }

    public static int getBlockSize() {
        return blockSize;
    }

    public static void setBlockSize(int blockSize) {
        GameMap.blockSize = blockSize;
    }
}
