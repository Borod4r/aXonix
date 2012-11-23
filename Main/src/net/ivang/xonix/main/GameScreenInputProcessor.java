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

    private XonixGame game;
    private GameScreen gameScreen;

    public  GameScreenInputProcessor(XonixGame game, GameScreen gameScreen) {
        this.game = game;
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
//                    case GAME_OVER:
//                        break;
                    case LEVEL_COMPLETED:
                        int nextIndex = gameScreen.getLevelIndex() + 1;
                        if (nextIndex < game.getLevelsFiles().size()) {
                            gameScreen.setLevel(nextIndex);
                        } else {
                            // WIN!!!
                        }

                        break;
                }
            return true;
        }
        return false;
    }
}