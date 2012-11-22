package net.ivang.xonix.main;

import com.badlogic.gdx.graphics.Pixmap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class Level {

    private GameMap gameMap;
    private Protagonist protagonist;
    private List<Enemy> enemies;

    public Level(Pixmap pixmap) {
        final int EARTH = 0x000000;
        final int ENEMY = 0xFF0000;
        final int PROTAGONIST = 0x00FF00;

        int width = pixmap.getWidth();
        int height = pixmap.getHeight();

        gameMap = new GameMap(width, height);
        enemies = new ArrayList<Enemy>();

        byte[][] state = new byte[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pix = (pixmap.getPixel(x, height-y-1) >>> 8) & 0xffffff;
                if(pix == EARTH) {
                    state[x][y] = GameMap.BS_EARTH;
                }else if (pix == ENEMY) {
                    Enemy enemy = new Enemy(x + 0.5f, y + 0.5f, gameMap);
                    enemies.add(enemy);
                    state[x][y] = GameMap.BS_WATER;
                } else if (pix == PROTAGONIST) {
                    protagonist = new Protagonist(x + 0.5f, y + 0.5f, gameMap);
                    protagonist.setLives(2);
                    state[x][y] = GameMap.BS_EARTH;
                } else {
                    state[x][y] = GameMap.BS_WATER;
                }
            }
        }

        gameMap.setState(state);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Protagonist getProtagonist() {
        return protagonist;
    }

    public void setProtagonist(Protagonist protagonist) {
        this.protagonist = protagonist;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }
}
