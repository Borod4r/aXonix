package net.ivang.xonix.main;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class StatusBar extends Table {

    private GameScreen gameScreen;

    private Label livesValue;
    private Label scoreValue;
    private Label levelValue;

    public StatusBar(GameScreen gameScreen, Skin skin) {
        this.gameScreen = gameScreen;

        Label livesLabel = new Label("Lives: ", skin);
        Label scoreLabel = new Label("Score: ", skin);
        Label levelLabel = new Label("Level: ", skin);

        livesValue = new Label("n/a", skin, "yellow");
        scoreValue = new Label("n/a", skin, "yellow");
        levelValue = new Label("n/a", skin, "yellow");

        // lives
        add(livesLabel).padLeft(5);
        add(livesValue).padRight(5);
        // score
        add(scoreLabel).padLeft(5);
        add(scoreValue).padRight(5);
        // level
        add(levelLabel).padLeft(5);
        add(levelValue).padRight(5);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // lives
        String lives = Integer.toString(gameScreen.getLives());
        livesValue.setText(lives);
        // score
        String score = Integer.toString(gameScreen.getLevel().getScore());
        scoreValue.setText(score);
        // level
        String level = Integer.toString(gameScreen.getLevelIndex() + 1);
        String percent = Byte.toString(gameScreen.getLevel().getPercentComplete());
        levelValue.setText(level + " (" + percent + "/80%)");
    }
}
