package net.ivang.xonix.main;

import java.util.*;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class GameMap {

    public static final int WIDTH = 40;
    public static final int HEIGHT = 20;

    public static final byte BS_WATER = 0;
    public static final byte BS_EARTH = 1;
    public static final byte BS_TAIL = 2;

    private byte[][] state;

    public int mapScore;
    public byte percentComplete;
    private int eartBlocks;

    public GameMap() {
        state = new byte[WIDTH][HEIGHT];

        // init borders
        for(int i = 0; i < WIDTH; i++) {
            state[i][0] = BS_EARTH;
            state[i][HEIGHT -1] = BS_EARTH;
        }

        for(int j = 0; j < HEIGHT; j++) {
            state[0][j] = BS_EARTH;
            state[WIDTH -1][j] = BS_EARTH;
        }
    }

    public void update(float deltaTime, Protagonist protagonist, Enemy enemy) {
        switch (getBlockStateByPx(protagonist.prev.x, protagonist.prev.y)) {
            case BS_WATER:
                setBlockStateByPx(protagonist.prev.x, protagonist.prev.y, GameMap.BS_TAIL);
                break;
            case BS_TAIL:
                if (getBlockStateByPx(protagonist.pos.x, protagonist.pos.y) == GameMap.BS_EARTH) {

                    byte[][] tmpState = new byte[WIDTH][HEIGHT];

                    // thanks to http://habrahabr.ru/post/119244/
                    byte spotNum = 0;
                    // TODO: HashMap and ArrayList?
                    Map<Byte, List<Point>> spots = new HashMap<Byte, List<Point>>();

                    for(int i = 1; i < GameMap.WIDTH - 1; i++) {
                        for(int j = 1; j < GameMap.HEIGHT - 1; j++) {
                            byte A = state[i][j];
                            if (A == BS_WATER) {
                                byte B = tmpState[i][j-1];
                                byte C = tmpState[i-1][j];

                                if ( B == 0) {
                                    if (C == 0) {
                                        // New Spot
                                        spotNum++;
                                        tmpState[i][j] = spotNum;

                                        List<Point> spot = new ArrayList<Point>();
                                        spot.add(new Point(i,j));

                                        spots.put(spotNum, spot);
                                    } else {   // C!=0
                                        tmpState[i][j] = C;
                                        spots.get(C).add(new Point(i,j));
                                    }
                                }

                                if (B != 0) {
                                    if(C == 0) {
                                        tmpState[i][j] = B;
                                        spots.get(B).add(new Point(i,j));
                                    } else { // C != 0
                                        tmpState[i][j] = B;
                                        spots.get(B).add(new Point(i,j));
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
                            } else if(A == BS_TAIL) {
                                // turn trail to the land
                                setBlockState(i, j, BS_EARTH);
                                mapScore++;
                                eartBlocks++;

                            }
                        }
                    }

                    Iterator iterator = spots.keySet().iterator();
                    while (iterator.hasNext()) {
                        for(Point pos: spots.get((Byte) iterator.next())) {
                            if ((pos.x == (int) (enemy.pos.x / GameScreen.blockSize)) && (pos.y == (int)(enemy.pos.y / GameScreen.blockSize))) {
                                iterator.remove();
                                break;
                            }
                        }
                    }

                    for(List<Point> spot : spots.values()) {
                        for(Point pos : spot) {
                            setBlockState(pos.x, pos.y, BS_EARTH);
                            mapScore++;
                            eartBlocks++;
                        }
                        float bonus = 1 + (float) spot.size() / 200;
                        mapScore += spot.size() * bonus;

                    }

                    // update percentage
                    percentComplete = (byte) (((float) eartBlocks / ((WIDTH - 2) * (HEIGHT - 2))) * 100) ;

                }
            break;
        }
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public void setBlockState(int x, int y, byte value) {
        if (x >= 0 && x < WIDTH && y >=0 && y < HEIGHT) {
            state[x][y] = value;
        }
    }

    public byte getBlockState(int x, int y) {
        return state[x][y];
    }

    public void setBlockStateByPx(float px, float py, byte value) {
        int x = pixelsToBlocks(px);
        int y = pixelsToBlocks(py);
        if (x >= 0 && x < WIDTH && y >=0 && y < HEIGHT) {
            state[x][y] = value;
        }
    }

    public byte getBlockStateByPx(float x, float y) {
        return state[(int) x / GameScreen.blockSize][(int) y / GameScreen.blockSize];
    }

    //---------------------------------------------------------------------
    // Helper Methods
    //---------------------------------------------------------------------

    private int pixelsToBlocks(float px) {
        return (int) (px / GameScreen.blockSize);
    }
}
