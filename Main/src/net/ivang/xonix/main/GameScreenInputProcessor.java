package net.ivang.xonix.main;

import com.badlogic.gdx.InputAdapter;
import net.ivang.xonix.main.GameScreen.State;

import static com.badlogic.gdx.Input.Keys;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class GameScreenInputProcessor extends InputAdapter {

    private GameScreen gameScreen;

    public  GameScreenInputProcessor(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.SPACE:
                switch (gameScreen.getState()) {
                    case PLAYING:
                        gameScreen.setState(State.PAUSED);
                        break;
                    case PAUSED:
                        gameScreen.setState(State.PLAYING);
                        break;
                    case GAME_OVER:
                        break;
                }
            return true;
        }
        return false;
    }
}