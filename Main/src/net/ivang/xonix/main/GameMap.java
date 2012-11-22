package net.ivang.xonix.main;

import java.util.*;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class GameMap {

    public static final byte BS_WATER = 0;
    public static final byte BS_EARTH = 1;
    public static final byte BS_TAIL = 2;

    private int width;
    private int height;
    private byte[][] state;

    public int mapScore;
    public byte percentComplete;
    private int earthBlocks;

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.state = new byte[width][height];
    }

    public void update(float deltaTime, Protagonist protagonist, List<Enemy> enemies) {
        switch (getBlockState(protagonist.prev.x, protagonist.prev.y)) {
            case BS_WATER:
                setBlockState(protagonist.prev.x, protagonist.prev.y, GameMap.BS_TAIL);
                break;
            case BS_TAIL:
                if (getBlockState(protagonist.pos.x, protagonist.pos.y) == GameMap.BS_EARTH) {

                    byte[][] tmpState = new byte[width][height];

                    // thanks to http://habrahabr.ru/post/119244/
                    byte spotNum = 0;
                    // TODO: HashMap and ArrayList?
                    Map<Byte, List<Point>> spots = new HashMap<Byte, List<Point>>();

                    for(int i = 1; i < width - 1; i++) {
                        for(int j = 1; j < height - 1; j++) {
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
                                            for(int m = 1; m < width - 1; m++) {
                                                for(int n = 1; n < height; n++) {
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
                                earthBlocks++;

                            }
                        }
                    }

                    Iterator iterator = spots.keySet().iterator();
                    while (iterator.hasNext()) {
                        check_spot_points:
                        for(Point pos: spots.get((Byte) iterator.next())) {
                            for (Enemy enemy : enemies) {
                                if ((pos.x == (int) enemy.pos.x) && (pos.y == (int) enemy.pos.y)) {
                                    iterator.remove();
                                    break check_spot_points;
                                }
                            }
                        }
                    }

                    for(List<Point> spot : spots.values()) {
                        for(Point pos : spot) {
                            setBlockState(pos.x, pos.y, BS_EARTH);
                            mapScore++;
                            earthBlocks++;
                        }
                        float bonus = 1 + (float) spot.size() / 200;
                        mapScore += spot.size() * bonus;

                    }

                    // update percentage
                    percentComplete = (byte) (((float) earthBlocks / ((width - 2) * (height - 2))) * 100) ;

                }
            break;
        }
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public void setBlockState(int x, int y, byte value) {
        if (x >= 0 && x < width && y >=0 && y < height) {
            state[x][y] = value;
        }
    }

    public void setBlockState(float x, float y, byte value) {
        if (x >= 0 && x < width && y >=0 && y < height) {
            state[(int) x][(int) y] = value;
        }
    }

    public byte getBlockState(int x, int y) {
        return state[x][y];
    }

    public byte getBlockState(float x, float y) {
        return state[(int) x][ (int) y];
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[][] getState() {
        return state;
    }

    public void setState(byte[][] state) {
        this.state = state;
    }

}
