package net.ivang.xonix.main;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class Level {

    private GameMap gameMap;
    private Protagonist protagonist;
    private Enemy enemy;

    public Level() {
        gameMap = new GameMap();
        protagonist = new Protagonist(0.5f * GameScreen.blockSize, (GameMap.HEIGHT - 0.5f) * GameScreen.blockSize, gameMap);
        protagonist.setLives(2);
        enemy = new Enemy(100,100, gameMap);
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

    public Enemy getEnemy() {
        return enemy;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }
}
