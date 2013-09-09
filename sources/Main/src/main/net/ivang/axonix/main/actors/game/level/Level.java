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
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.actors.game.level.blocks.Block;
import net.ivang.axonix.main.actors.game.level.blocks.BlocksParticlesHolder;
import net.ivang.axonix.main.actors.game.level.bonuses.Bonus;
import net.ivang.axonix.main.actors.game.level.bonuses.LifeBonus;
import net.ivang.axonix.main.actors.game.level.bonuses.SlowBonus;
import net.ivang.axonix.main.actors.game.level.bonuses.SpeedBonus;
import net.ivang.axonix.main.actors.game.level.enemies.BlueEnemy;
import net.ivang.axonix.main.actors.game.level.enemies.Enemy;
import net.ivang.axonix.main.actors.game.level.enemies.PurpleEnemy;
import net.ivang.axonix.main.actors.game.level.enemies.RedEnemy;
import net.ivang.axonix.main.events.facts.EnemyBounceFact;
import net.ivang.axonix.main.events.facts.ObtainedPointsFact;
import net.ivang.axonix.main.events.facts.TailBlockFact;
import net.ivang.axonix.main.events.facts.level.LevelProgressFact;
import net.ivang.axonix.main.events.facts.level.LevelScoreFact;
import net.ivang.axonix.main.events.intents.game.DestroyBlockIntent;
import net.ivang.axonix.main.events.intents.game.LevelScoreIntent;
import net.ivang.axonix.main.events.intents.game.NotificationIntent;
import net.ivang.axonix.main.screens.GameScreen;

import java.util.*;

import static net.ivang.axonix.main.actors.game.KinematicActor.Direction;
import static net.ivang.axonix.main.actors.game.level.blocks.Block.Type;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Level extends Group {

    private State state;
    private EventBus eventBus;

    private int mapWidth;
    private int mapHeight;
    private Block[][] levelMap;

    private int levelIndex;
    private int score;
    private byte percentComplete;
    private int filledBlocks;

    private Protagonist protagonist;
    private List<Enemy> enemies;
    private List<Block> tailBlocks;
    private Group bonuses;
    private BlocksParticlesHolder blocksParticles;

    private boolean containsRedBlocks;
    private float redBlocksDelta;

    private Skin skin;

    @Inject
    public Level(int levelIndex, Pixmap pixmap, Skin skin, EventBus eventBus) {
        // register with the event bus
        this.eventBus = eventBus;
        eventBus.register(this);

        this.skin = skin;
        this.mapWidth = pixmap.getWidth();
        this.mapHeight = pixmap.getHeight();
        this.levelMap = new Block[mapWidth][mapHeight];
        this.tailBlocks = new ArrayList<Block>();
        this.enemies = new ArrayList<Enemy>();
        this.bonuses = new Group();
        this.blocksParticles = new BlocksParticlesHolder(skin, eventBus);

        initFromPixmap(pixmap);

        addActor(blocksParticles);
        addActor(bonuses);

        setScore(0);
        updateLevelProgress(0);

        this.levelIndex = levelIndex;
        String level = Integer.toString(levelIndex);
        showNotification("Level " + level + ". Go-go-go!", 0.25f, 1.5f);
    }

    private void initFromPixmap(Pixmap pixmap) {
        final int BLOCK_BLUE_HARD = 0x000055;
        final int ENEMY_RED = 0xFF0000;
        final int ENEMY_PURPLE = 0xFF00FF;
        final int ENEMY_BLUE_U = 0x0000FC;
        final int ENEMY_BLUE_R = 0x0000FD;
        final int ENEMY_BLUE_D = 0x0000FE;
        final int ENEMY_BLUE_L = 0x0000FF;
        final int PROTAGONIST = 0x00FF00;

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int pix = (pixmap.getPixel(x, mapHeight-y-1) >>> 8) & 0xffffff;

                switch (pix) {
                    case BLOCK_BLUE_HARD:
                    case PROTAGONIST:
                        levelMap[x][y] = new Block(x, y, Type.BLUE_HARD, skin);
                        break;
                    default:
                        levelMap[x][y] = new Block(x, y, Type.EMPTY, skin);
                        break;
                }

                addActor(levelMap[x][y]);

                switch (pix) {
                    case PROTAGONIST:
                        protagonist = new Protagonist(x + 0.5f, y + 0.5f, this, skin, eventBus);
                        break;
                    case ENEMY_RED:
                        Enemy redEnemy = new RedEnemy(x + 0.5f, y + 0.5f, skin, eventBus);
                        enemies.add(redEnemy);
                        break;
                    case ENEMY_PURPLE:
                        Enemy purpleEnemy = new PurpleEnemy(x + 0.5f, y + 0.5f, skin, eventBus);
                        enemies.add(purpleEnemy);
                        break;
                    case ENEMY_BLUE_U:
                        Enemy blueEnemyU = new BlueEnemy(x + 0.5f, y + 0.8f, skin, Direction.UP, eventBus);
                        enemies.add(blueEnemyU);
                        break;
                    case ENEMY_BLUE_R:
                        Enemy blueEnemyR = new BlueEnemy(x + 0.8f, y + 0.5f, skin, Direction.RIGHT, eventBus);
                        enemies.add(blueEnemyR);
                        break;
                    case ENEMY_BLUE_D:
                        Enemy blueEnemyD = new BlueEnemy(x + 0.5f, y + 0.2f, skin, Direction.DOWN, eventBus);
                        enemies.add(blueEnemyD);
                        break;
                    case ENEMY_BLUE_L:
                        Enemy blueEnemyL = new BlueEnemy(x + 0.2f, y + 0.5f, skin, Direction.LEFT, eventBus);
                        enemies.add(blueEnemyL);
                        break;
                }
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
            checkEnemies(delta);
            checkProtagonist();
            checkPercentComplete();
        }
    }

    public void unregister() {
        eventBus.unregister(this);
        eventBus.unregister(blocksParticles);
        eventBus.unregister(protagonist);
        for (Enemy enemy : enemies) {
            eventBus.unregister(enemy);
        }
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
        if (scoreDelta > 0) {
            showObtainedPoints(scoreDelta);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void destroyBlock(DestroyBlockIntent intent) {
        Block block = intent.getBlock();
        block.setType(Type.EMPTY);
        // update the adjacent blocks
        int bx = (int) block.getX();
        int by = (int) block.getY();
        for (int i = bx - 1; i <= bx + 1; i++) {
            for (int j = by - 1; j <= by + 1; j++) {
                Block adjacentBlock = getBlock(i, j);
                if (adjacentBlock.hasType(Type.GREEN)) {
                    adjacentBlock.setType(Type.BLUE);
                }
            }
        }
        // update score and progress
        eventBus.post(new LevelScoreIntent(-1));
        updateLevelProgress(-1);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    @SuppressWarnings("StatementWithEmptyBody")
    private void checkTail(float delta) {
        if (containsRedBlocks) {
            redBlocksDelta += delta;
            float interval = 1 / (protagonist.getSpeed() * 3);
            if (redBlocksDelta > interval) {
                redBlocksDelta = 0;
                int tailSize = tailBlocks.size() - 1;
                for (int i = 0; i <= tailSize; i++) {
                    if (tailBlocks.get(i).hasType(Type.RED)) {
                        // burn previous block
                        if (i > 0) tailBlocks.get(i-1).setType(Type.RED);
                        // skip consequent red blocks
                        while (i < tailSize && tailBlocks.get(++i).hasType(Type.RED));
                        // check if we caught up the protagonist
                        if (i == tailSize && tailBlocks.get(i).hasType(Type.RED)) {
                            protagonist.setState(Protagonist.State.DYING);
                        } else {
                            tailBlocks.get(i).setType(Type.RED);
                        }
                    }
                }
            }
        }
    }

    private void checkEnemies(float delta) {
        for (Enemy enemy : enemies) {
            checkEnemyCollisionWithProtagonist(enemy);
            checkEnemyCollisionsWithBonuses(enemy);
            checkEnemyCollisionsWithBlocks(enemy, delta);
        }
    }

    private void checkEnemyCollisionWithProtagonist(Enemy enemy) {
        Circle enemyCircle = enemy.getCollisionCircle();
        Circle protagonistCircle = protagonist.getCollisionCircle();
        if (Intersector.overlapCircles(enemyCircle, protagonistCircle)) {
            protagonist.setState(Protagonist.State.DYING);
        }
    }

    private void checkEnemyCollisionsWithBonuses(Enemy enemy) {
        for (Actor actor : bonuses.getChildren()) {
            Bonus bonus = (Bonus) actor;
            if (Intersector.overlapCircles(enemy.getCollisionCircle(), bonus.getCollisionCircle())) {
                bonus.removeSmoothly();
            }
        }
    }

    private void checkEnemyCollisionsWithBlocks(Enemy enemy, float delta) {
        if (enemy.isBouncingOffBlocks()) {
            checkBouncingEnemyCollisionsWithBlocks(enemy);
        } else {
            checkCrawlingEnemyCollisionsWithBlocks(enemy, delta);
        }
    }

    private void checkBouncingEnemyCollisionsWithBlocks(Enemy enemy) {
        Vector2 direction = enemy.getDirection();
        Vector2 signum = new Vector2(Math.signum(direction.x), Math.signum(direction.y));

        Block b1 = getBlock(enemy.getX() + signum.x, enemy.getY());
        Block b2 = getBlock(enemy.getX(), enemy.getY() + signum.y);
        Block b3 = getBlock(enemy.getX() + signum.x, enemy.getY() + signum.y);

        List<Block> collisions = new ArrayList<Block>();

        if (!b1.isEmpty()) {
            Rectangle r1 = b1.getCollisionRectangle();
            if (Intersector.overlapCircleRectangle(enemy.getCollisionCircle(), r1)) {
                collisions.add(b1);
                direction.x = - direction.x;
            }
        }
        if (!b2.isEmpty()) {
            Rectangle r2 = b2.getCollisionRectangle();
            if (Intersector.overlapCircleRectangle(enemy.getCollisionCircle(), r2)) {
                collisions.add(b2);
                direction.y = - direction.y;
            }
        }

        if (collisions.isEmpty() && !b3.isEmpty()) {
            Rectangle r3 = b3.getCollisionRectangle();
            if (Intersector.overlapCircleRectangle(enemy.getCollisionCircle(), r3)) {
                collisions.add(b3);
                direction.x = - direction.x;
                direction.y = - direction.y;
            }
        }

        if (!collisions.isEmpty()) {
            // direction has changed
            eventBus.post(new EnemyBounceFact(direction));
            // burn tail
            for (Block block : collisions) {
                switch (block.getType()) {
                    case TAIL:
                        block.setType(Type.RED);
                        containsRedBlocks = true;
                        break;
                    case BLUE:
                    case GREEN:
                        if (enemy.isDestroyingBlocks()) {
                            eventBus.post(new DestroyBlockIntent(block));
                        }
                        break;
                }
            }
        }
    }

    private void checkCrawlingEnemyCollisionsWithBlocks(Enemy enemy, float delta) {
        float dx = enemy.getDirection().x;
        float dy = enemy.getDirection().y;
        float speed = enemy.getSpeed();
        // next block
        float nx = enemy.getX() + delta * speed * dx;
        float ny = enemy.getY() + delta * speed * dy;
        Block nextBlock = getBlock(nx, ny);
        // check whether enemy should turn left
        if (!nextBlock.isEmpty()) {
            enemy.getDirection().set(-dy, dx);
        } else {
            // right block (-90 degrees)
            float rx = enemy.getX() + dy;
            float ry = enemy.getY() - dx;
            Block rightBlock = getBlock(rx, ry);
            // right rear block (-135 degrees)
            float rrx = enemy.getX() - 0.7f * dx + 0.7f * dy;
            float rry = enemy.getY() - 0.7f * dx - 0.7f * dy;
            Block rightRearBlock = getBlock(rrx , rry);
            // check whether enemy should turn right
            if (rightBlock.isEmpty() && !rightRearBlock.isEmpty()) {
                enemy.getDirection().set(dy, -dx);
            }
        }
    }

    private void checkProtagonist() {
        if(protagonist.hasState(Protagonist.State.ALIVE) && protagonist.isOnNewBlock()) {
            // check bonuses
            for (Actor actor : bonuses.getChildren()) {
                Bonus bonus = (Bonus) actor;
                if (bonus.isActive()) {
                    Circle protagonistCircle = protagonist.getCollisionCircle();
                    Circle bonusCircle = bonus.getCollisionCircle();
                    if (Intersector.overlapCircles(protagonistCircle, bonusCircle)) {
                        eventBus.post(bonus);
                        bonus.removeSmoothly();
                    }
                }
            }
            // check blocks
            Block currentBlock = getBlock(protagonist.getX(), protagonist.getY());
            switch (currentBlock.getType()) {
                case EMPTY:
                    currentBlock.setType(Type.TAIL);
                    float duration = 0.5f / protagonist.getSpeed();
                    currentBlock.addAction(Actions.sequence(Actions.delay(duration), Actions.fadeIn(duration)));
                    tailBlocks.add(currentBlock);
                    eventBus.post(new TailBlockFact());
                    break;
                case TAIL:
                    protagonist.setState(Protagonist.State.DYING);
                    break;
                case GREEN:
                case BLUE:
                case BLUE_HARD:
                    Block prevBlock = getBlock(protagonist.getPrevX(), protagonist.getPrevY());
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
                        updateLevelProgress(newBlocks);
                        // add bonus with some probability
                        addBonus();
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
        byte[][] tmpState = new byte[mapWidth][mapHeight];
        byte spotNum = 0;
        int blocks = 0;
        Map<Byte, List<Vector2>> spots = new HashMap<Byte, List<Vector2>>();
        for(int i = 1; i < mapWidth - 1; i++) {
            for(int j = 1; j < mapHeight - 1; j++) {
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
                                for(int m = 1; m < mapWidth - 1; m++) {
                                    for(int n = 1; n < mapHeight; n++) {
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
        float moveY = ((protY > mapHeight/2) ? -3 : 3) * getScaleY();
        // correct position if is on the right side
        boolean subtractBounds = protX > mapWidth/2;
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

    private void addBonus() {
        float probability = 0.1f + (levelIndex * 0.01f);
        if (probability > MathUtils.random()) {
            int x = MathUtils.random(1, mapWidth - 2);
            int y = MathUtils.random(1, mapHeight - 2);
            switch (MathUtils.random(2)) {
                case 0:
                    bonuses.addActor(new SpeedBonus(x + 0.5f, y + 0.5f, skin));
                    break;
                case 1:
                    bonuses.addActor(new SlowBonus(x + 0.5f, y + 0.5f, skin));
                    break;
                case 2:
                    bonuses.addActor(new LifeBonus(x + 0.5f, y + 0.5f, skin));
                    break;
            }
        }
    }

    private void updateLevelProgress(int blocksDelta) {
        filledBlocks += blocksDelta;
        percentComplete = (byte) (((float) filledBlocks / ((mapWidth - 2) * (mapHeight - 2))) * 100) ;
        eventBus.post(new LevelProgressFact(percentComplete));
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

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        eventBus.post(new LevelScoreFact(score));
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
