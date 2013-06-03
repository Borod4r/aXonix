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

package net.ivang.axonix.main.actors.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.screens.GameScreen;
import net.ivang.axonix.main.events.facts.LevelProgressFact;
import net.ivang.axonix.main.events.facts.LevelScoreFact;
import net.ivang.axonix.main.events.facts.ObtainedPointsFact;
import net.ivang.axonix.main.events.intents.game.LevelScoreIntent;
import net.ivang.axonix.main.events.intents.game.NotificationIntent;

import java.util.*;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Level extends Group {

    private State state;
    private EventBus eventBus;

    private int width;
    private int height;
    private Block[][] levelMap;

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
        this.levelMap = new Block[width][height];
        this.skin = skin;

        initFromPixmap(pixmap);

        setScore(0);
        setPercentComplete((byte) 0);

        String level = Integer.toString(levelIndex);
        showNotification("Level " + level + ". Go-go-go!", 0.25f, 1.5f);
    }

    private void initFromPixmap(Pixmap pixmap) {
        final int EARTH = 0x000000;
        final int ENEMY = 0xFF0000;
        final int PROTAGONIST = 0x00FF00;

        enemies = new ArrayList<Enemy>();

        Block[][] levelMap = new Block[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pix = (pixmap.getPixel(x, height-y-1) >>> 8) & 0xffffff;
                if(pix == EARTH) {
                    levelMap[x][y] = Block.BLUE;
                }else if (pix == ENEMY) {
                    Enemy enemy = new Enemy(x + 0.5f, y + 0.5f, skin);
                    enemies.add(enemy);
                    addActor(enemy);
                    levelMap[x][y] = Block.EMPTY;
                } else if (pix == PROTAGONIST) {
                    protStartPos = new Vector2(x + 0.5f, y + 0.5f);
                    levelMap[x][y] = Block.BLUE;
                } else {
                    levelMap[x][y] = Block.EMPTY;
                }
            }
        }

        protagonist = new Protagonist(eventBus, protStartPos.x, protStartPos.y, this, skin);
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
                switch (getBlock(protagonist.getX(), protagonist.getY())) {
                    case EMPTY:
                        setBlock(protagonist.getX(), protagonist.getY(), Block.TAIL);
                        break;
                    case TAIL:
                            protagonist.setState(Protagonist.State.DYING);
                        break;
                    case BLUE:
                        if (getBlock(protagonist.getPrevX(), protagonist.getPrevY()) == Block.TAIL) {
                            // fill areas
                            int newBlocks = fillAreas();
                            // update level score
                            float bonus = 1 + newBlocks / 200f;
                            int obtainedPoints = (int) (newBlocks * bonus);
                            eventBus.post(new LevelScoreIntent(obtainedPoints));
                            // update percentage
                            filledBlocks += newBlocks;
                            byte percentComplete = (byte) (((float) filledBlocks / ((width - 2) * (height - 2))) * 100) ;
                            setPercentComplete(percentComplete);
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
                switch (getBlock(i, j)) {
                    case BLUE:
                        batch.setColor(1, 1, 1, 1);
                        break;
                    case GREEN:
                        batch.setColor(0, 1, 0.3f, 1);
                        break;
                    case TAIL:
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

    public void unregister() {
        eventBus.unregister(this);
        eventBus.unregister(protagonist);
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
            case GAME_OVER:
                setState(State.PAUSED);
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onProtagonistStateChange(Protagonist.State protagonistState) {
        switch (protagonistState) {
            case DYING:
                for(int i = 1; i < getWidth() - 1; i++) {
                    for(int j = 1; j < getHeight() - 1; j++) {
                        if (getBlock(i, j) == Block.TAIL) {
                            setBlock(i, j, Block.EMPTY);
                        }
                    }
                }
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScoreChange(LevelScoreIntent event) {
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
            if (getBlock(enemy.getX(), enemy.getY()) == Block.TAIL) {
                protagonist.setState(Protagonist.State.DYING);
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
                Block A = levelMap[i][j];
                if (A == Block.EMPTY) {
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
                } else if(A == Block.TAIL) {
                    // turn tail to blue blocks
                    setBlock(i, j, Block.BLUE);
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
                setBlock(pos.x, pos.y, Block.GREEN);
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
        eventBus.post(new ObtainedPointsFact(points, labelX, labelY, moveY, subtractBounds));
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void showNotification(String text, float showDelay, float hideDelay) {
        eventBus.post(new NotificationIntent(text, showDelay, hideDelay));
    }

    private boolean isInState(State state) {
        return this.state == state;
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public void setBlock(int x, int y, Block value) {
        if (x >= 0 && x < width && y >=0 && y < height) {
            levelMap[x][y] = value;
        }
    }

    public void setBlock(float x, float y, Block block) {
        setBlock((int) x, (int) y, block);
    }

    public Block getBlock(int x, int y) {
        return levelMap[x][y];
    }

    public Block getBlock(float x, float y) {
        return getBlock((int) x, (int) y);
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

    public Block[][] getLevelMap() {
        return levelMap;
    }

    public void setLevelMap(Block[][] levelMap) {
        this.levelMap = levelMap;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        eventBus.post(new LevelScoreFact(score));
    }

    public byte getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(byte percentComplete) {
        this.percentComplete = percentComplete;
        eventBus.post(new LevelProgressFact(percentComplete));
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        eventBus.post(state);
    }

    public Protagonist getProtagonist() {
        return protagonist;
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public enum State {
        PLAYING, PAUSED, LEVEL_COMPLETED
    }

    public enum Block {
        EMPTY, BLUE, GREEN, TAIL
    }
}
