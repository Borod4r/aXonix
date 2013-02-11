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

package net.ivang.axonix.main.screen.game;

import com.badlogic.gdx.InputAdapter;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.screen.game.GameScreen.State;

import static com.badlogic.gdx.Input.Keys;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class GameScreenInputProcessor extends InputAdapter {

    private AxonixGame game;
    private GameScreen gameScreen;

    public  GameScreenInputProcessor(AxonixGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.SPACE:
            case Keys.MENU:  // TODO: Change it to some gesture
                switch (gameScreen.getState()) {
                    case PLAYING:
                        gameScreen.setState(State.PAUSED);
                        break;
                    case PAUSED:
                        gameScreen.setState(State.PLAYING);
                        break;
                    case GAME_OVER:
                        game.setStartScreen();
                        break;
                    case LEVEL_COMPLETED:
                        int nextIndex = gameScreen.getLevelIndex() + 1;
                        if (nextIndex < game.getLevelsFiles().size()) {
                            gameScreen.setLevel(nextIndex);
                        } else {
                            gameScreen.setState(State.WIN);
                        }
                        break;
                    case WIN:
                        game.setStartScreen();
                        break;
                }
                return true;
            case Keys.ESCAPE:
                //TODO: only for testing
                game.setStartScreen();
                return true;
        }
        return false;
    }
}