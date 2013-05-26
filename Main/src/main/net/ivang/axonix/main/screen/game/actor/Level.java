/*
 * Copyright 2012-2013 Ivan Gadzhega
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.ivang.axonix.main.screen.game.actor;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.screen.game.GameScreen;
import net.ivang.axonix.main.screen.game.event.*;

import java.util.*;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Level extends Group {

    public enum State {
        PLAYING, PAUSED, LEVEL_COMPLETED
    }

    // TODO: enum is better
    public static final byte BS_EMPTY = 0;
    public static final byte BS_BLUE = 1;
    public static final byte BS_GREEN = 2;
    public static final byte BS_TAIL = 3;

    private State state;
    private EventBus eventBus;

    private int width;
    private int height;
    private byte[][] levelMap;

    private int score;
    private byte percentComplete;
    private int filledBlocks;

    private Protagonist protagonist;
    private Vector2 protStartPos;
    private List<Enemy> enemies;

    private Skin skin;

    @Inject
    public Level(int levelIndex, Pixmap pixmap, Skin skin, EventBus eventBus) {
        // register with the event bus
        this.eventBus = eventBus;
        eventBus.register(this);

        this.width = pixmap.getWidth();
        this.height = pixmap.getHeight();
        this.levelMap = new byte[width][height];
        this.skin = skin;

        initFromPixmap(pixmap);

        setScore(0);

        String level = Integer.toString(levelIndex);
        showNotification("Level " + level + ". Go-go-go!", 0.25f, 1.5f);
    }

    private void initFromPixmap(Pixmap pixmap) {
        final int EARTH = 0x000000;
        final int ENEMY = 0xFF0000;
        final int PROTAGONIST = 0x00FF00;

        enemies = new ArrayList<Enemy>();

        byte[][] levelMap = new byte[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pix = (pixmap.getPixel(x, height-y-1) >>> 8) & 0xffffff;
                if(pix == EARTH) {
                    levelMap[x][y] = Level.BS_BLUE;
                }else if (pix == ENEMY) {
                    Enemy enemy = new Enemy(x + 0.5f, y + 0.5f, skin);
                    enemies.add(enemy);
                    addActor(enemy);
                    levelMap[x][y] = Level.BS_EMPTY;
                } else if (pix == PROTAGONIST) {
                    protStartPos = new Vector2(x + 0.5f, y + 0.5f);
                    levelMap[x][y] = Level.BS_BLUE;
                } else {
                    levelMap[x][y] = Level.BS_EMPTY;
                }
            }
        }

        protagonist = new Protagonist(protStartPos.x, protStartPos.y, this, skin);
        addActor(protagonist);

        this.setLevelMap(levelMap);
    }

    @Override
    public void act(float delta) {
        if (isInState(State.PLAYING)) {
            check();
            super.act(delta);
            // change blocks states
            if(protagonist.isOnNewBlock()) {
                switch (getBlockState(protagonist.getX(), protagonist.getY())) {
                    case BS_EMPTY:
                        setBlockState(protagonist.getX(), protagonist.getY(), Level.BS_TAIL);
                        break;
                    case BS_TAIL:
                        killProtagonist();
                        break;
                    case BS_BLUE:
                        if (getBlockState(protagonist.getPrevX(), protagonist.getPrevY()) == Level.BS_TAIL) {
                            // fill areas
                            int newBlocks = fillAreas();
                            // update level score
                            float bonus = 1 + newBlocks / 200f;
                            int obtainedPoints = (int) (newBlocks * bonus);
                            eventBus.post(new LevelScoreDeltaEvent(obtainedPoints));
                            // update percentage
                            filledBlocks += newBlocks;
                            percentComplete = (byte) (((float) filledBlocks / ((width - 2) * (height - 2))) * 100) ;
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                switch (getBlockState(i, j)) {
                    case Level.BS_BLUE:
                        batch.setColor(1, 1, 1, 1);
                        break;
                    case Level.BS_GREEN:
                        batch.setColor(0, 1, 0.3f, 1);
                        break;
                    case Level.BS_TAIL:
                        batch.setColor(0.3f, 0.3f, 1f, 1);
                        break;
                    default:
                        continue;
                }
                batch.draw(skin.getRegion("block"), getX() + i * getScaleX(), getY() + j * getScaleY(), getScaleX(), getScaleY());
            }
        }

        super.draw(batch, parentAlpha);
    }

    public void killProtagonist() {
        protagonist.setState(Protagonist.State.DEAD);

        for(int i = 1; i < getWidth() - 1; i++) {
            for(int j = 1; j < getHeight() - 1; j++) {
                if (getBlockState(i, j) == Level.BS_TAIL) {
                    setBlockState(i, j, Level.BS_EMPTY);
                }
            }
        }

        eventBus.post(new LivesDeltaEvent(-1));
        showNotification("LIFE LEFT!", 0, 1);
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameScreenStateChange(GameScreen.State gameScreenState) {
        switch (gameScreenState) {
            case PLAYING:
                setState(State.PLAYING);
                break;
            case PAUSED:
                setState(State.PAUSED);
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScoreChange(LevelScoreDeltaEvent event) {
        int scoreDelta = event.getScoreDelta();
        setScore(score + scoreDelta);
        showObtainedPoints(scoreDelta);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void check() {
        // check percent
        if (percentComplete > 80) {
            setState(State.LEVEL_COMPLETED);
        }
        // check collisions
        for (Enemy enemy : enemies) {
            if (getBlockState(enemy.getX(), enemy.getY()) == Level.BS_TAIL) {
                killProtagonist();
                break;
            }
        }
    }

    /**
     * Fills fenced areas if they do not contain enemies.
     *
     * @return the number of filled blocks
     */

    private int fillAreas() {
        // thanks to http://habrahabr.ru/post/119244/
        byte[][] tmpState = new byte[width][height];
        byte spotNum = 0;
        int blocks = 0;
        Map<Byte, List<Vector2>> spots = new HashMap<Byte, List<Vector2>>();
        for(int i = 1; i < width - 1; i++) {
            for(int j = 1; j < height - 1; j++) {
                byte A = levelMap[i][j];
                if (A == BS_EMPTY) {
                    byte B = tmpState[i][j-1];
                    byte C = tmpState[i-1][j];

                    if ( B == 0) {
                        if (C == 0) {
                            // New Spot
                            spotNum++;
                            tmpState[i][j] = spotNum;

                            List<Vector2> spot = new ArrayList<Vector2>();
                            spot.add(new Vector2(i,j));

                            spots.put(spotNum, spot);
                        } else {   // C!=0
                            tmpState[i][j] = C;
                            spots.get(C).add(new Vector2(i,j));
                        }
                    }

                    if (B != 0) {
                        if(C == 0) {
                            tmpState[i][j] = B;
                            spots.get(B).add(new Vector2(i,j));
                        } else { // C != 0
                            tmpState[i][j] = B;
                            spots.get(B).add(new Vector2(i,j));
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
                    // turn tail to blue blocks
                    setBlockState(i, j, BS_BLUE);
                    blocks++;

                }
            }
        }

        Iterator<Byte> iterator = spots.keySet().iterator();
        while (iterator.hasNext()) {
            check_spot_points:
            for(Vector2 pos: spots.get(iterator.next())) {
                for (Enemy enemy : enemies) {
                    if ((pos.x == (int) enemy.getX()) && (pos.y == (int) enemy.getY())) {
                        iterator.remove();
                        break check_spot_points;
                    }
                }
            }
        }

        for(List<Vector2> spot : spots.values()) {
            for(Vector2 pos : spot) {
                setBlockState(pos.x, pos.y, BS_GREEN);
                blocks++;
            }

        }

        return blocks;
    }

    private void showObtainedPoints(int points) {
        // calculate new position for label
        float protX = protagonist.getX();
        float protY = protagonist.getY();
        float labelX = (protX) * getScaleX() + this.getX();
        float labelY = (protY) * getScaleY() + this.getY();
        // movement distance and direction
        float moveY = ((protY > getHeight()/2) ? -3 : 3) * getScaleY();
        // correct position if is on the right side
        boolean subtractBounds = protX > getWidth()/2;
        // post event
        eventBus.post(new ObtainedPointsEvent(points, labelX, labelY, moveY, subtractBounds));
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void showNotification(String text, float showDelay, float hideDelay) {
        eventBus.post(new NotificationEvent(text, showDelay, hideDelay));
    }

    private boolean isInState(State state) {
        return this.state == state;
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public void setBlockState(int x, int y, byte value) {
        if (x >= 0 && x < width && y >=0 && y < height) {
            levelMap[x][y] = value;
        }
    }

    public void setBlockState(float x, float y, byte value) {
        setBlockState((int) x, (int) y, value);
    }

    public byte getBlockState(int x, int y) {
        return levelMap[x][y];
    }

    public byte getBlockState(float x, float y) {
        return getBlockState((int) x, (int) y);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[][] getLevelMap() {
        return levelMap;
    }

    public void setLevelMap(byte[][] levelMap) {
        this.levelMap = levelMap;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        eventBus.post(new LevelScoreEvent(score));
    }

    public byte getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(byte percentComplete) {
        this.percentComplete = percentComplete;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        eventBus.post(state);
    }
}
