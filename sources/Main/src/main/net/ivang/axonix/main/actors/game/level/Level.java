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

package net.ivang.axonix.main.actors.game.level;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.events.facts.ObtainedPointsFact;
import net.ivang.axonix.main.events.facts.TailBlockFact;
import net.ivang.axonix.main.events.facts.level.LevelProgressFact;
import net.ivang.axonix.main.events.facts.level.LevelScoreFact;
import net.ivang.axonix.main.events.intents.game.LevelScoreIntent;
import net.ivang.axonix.main.events.intents.game.NotificationIntent;
import net.ivang.axonix.main.screens.GameScreen;

import java.util.*;

import static net.ivang.axonix.main.actors.game.level.Block.Type;

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
    private List<Enemy> enemies;
    private List<Block> tailBlocks;

    private boolean containsRedBlocks;
    private float redBlocksDelta;

    private Skin skin;

    @Inject
    public Level(int levelIndex, Pixmap pixmap, Skin skin, EventBus eventBus) {
        // register with the event bus
        this.eventBus = eventBus;
        eventBus.register(this);

        this.skin = skin;
        this.width = pixmap.getWidth();
        this.height = pixmap.getHeight();
        this.levelMap = new Block[width][height];
        this.enemies = new ArrayList<Enemy>();
        this.tailBlocks = new ArrayList<Block>();

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

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pix = (pixmap.getPixel(x, height-y-1) >>> 8) & 0xffffff;
                if(pix == EARTH) {
                    levelMap[x][y] = new Block(x, y, Type.BLUE, skin);
                }else if (pix == ENEMY) {
                    levelMap[x][y] = new Block(x, y, Type.EMPTY, skin);
                    Enemy enemy = new Enemy(x + 0.5f, y + 0.5f, skin, eventBus);
                    enemies.add(enemy);
                } else if (pix == PROTAGONIST) {
                    levelMap[x][y] = new Block(x, y, Type.BLUE, skin);
                    protagonist = new Protagonist(eventBus, x + 0.5f, y + 0.5f, this, skin);
                } else {
                    levelMap[x][y] = new Block(x, y, Type.EMPTY, skin);
                }
                addActor(levelMap[x][y]);
            }
        }

        addActor(protagonist);

        for (Enemy enemy : enemies) {
            addActor(enemy);
        }
    }

    @Override
    public void act(float delta) {
        if (hasState(State.PLAYING)) {
            super.act(delta);
            checkTail(delta);
            checkEnemies();
            checkProtagonist();
            checkPercentComplete();
        }
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
                clearTail(Type.EMPTY);
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

    private void checkTail(float delta) {
        if (containsRedBlocks) {
            redBlocksDelta += delta;
            float interval = 1 / (protagonist.getSpeed() * 3);
            if (redBlocksDelta > interval) {
                redBlocksDelta = 0;
                int tailSize = tailBlocks.size() - 1;
                for (int i = 1; i < tailSize; i++) {
                    if (tailBlocks.get(i).hasType(Type.RED)) {
                        tailBlocks.get(i-1).setType(Type.RED);
                        while (tailBlocks.get(++i).hasType(Type.RED)) {
                            if (i == tailSize) {
                                protagonist.setState(Protagonist.State.DYING);
                                return;
                            }
                        }
                        tailBlocks.get(i).setType(Type.RED);
                    }
                }
            }
        }
    }

    private void checkEnemies() {
        for (Enemy enemy : enemies) {
            // check collision with protagonist
            Circle enemyCircle = enemy.getCollisionCircle();
            Circle protagonistCircle = protagonist.getCollisionCircle();
            if (Intersector.overlapCircles(enemyCircle, protagonistCircle)) {
                protagonist.setState(Protagonist.State.DYING);
                return;
            }
            // check collision with tail
            int ex = (int) enemy.getX();
            int ey = (int) enemy.getY();
            for (int i = ex - 1; i <= ex + 1; i++) {
                for (int j = ey - 1; j <= ey + 1; j++) {
                    Block block = getBlock(i, j);
                    if (block.hasType(Type.TAIL)) {
                        Rectangle blockRectangle = new Rectangle(i,j, 1, 1);
                        if (Intersector.overlapCircleRectangle(enemyCircle, blockRectangle)) {
                            block.setType(Type.RED);
                            containsRedBlocks = true;
                            return;
                        }
                    }
                }
            }
        }
    }

    private void checkProtagonist() {
        if(protagonist.hasState(Protagonist.State.ALIVE) && protagonist.isOnNewBlock()) {
            // previous block
            Block prevBlock = getBlock(protagonist.getPrevX(), protagonist.getPrevY());
            if (prevBlock.hasType(Type.EMPTY)) {
                prevBlock.setType(Type.TAIL);
                tailBlocks.add(prevBlock);
                eventBus.post(new TailBlockFact());
            }
            // current block
            switch (getBlock(protagonist.getX(), protagonist.getY()).getType()) {
                case TAIL:
                    protagonist.setState(Protagonist.State.DYING);
                    break;
                case BLUE:
                    if (prevBlock.hasType(Type.TAIL)) {
                        // convert tail
                        int newBlocks = tailBlocks.size();
                        clearTail(Type.BLUE);
                        // fill areas
                        newBlocks += fillAreas();
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

    private void checkPercentComplete() {
        if (percentComplete > 80) {
            setState(State.LEVEL_COMPLETED);
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
                if (A.hasType(Type.EMPTY)) {
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
                levelMap[(int) pos.x][(int) pos.y].setType(Type.GREEN);
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

    private void showNotification(String text, float showDelay, float hideDelay) {
        eventBus.post(new NotificationIntent(text, showDelay, hideDelay));
    }

    private void clearTail(Type newType) {
        for (Block block : tailBlocks) {
            block.setType(newType);
        }
        tailBlocks.clear();
        containsRedBlocks = false;
    }

    private boolean hasState(State state) {
        return this.state == state;
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

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

}
