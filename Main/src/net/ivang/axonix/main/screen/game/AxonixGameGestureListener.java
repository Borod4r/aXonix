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

import com.badlogic.gdx.input.GestureDetector;
import net.ivang.axonix.main.AxonixGame;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class AxonixGameGestureListener extends GestureDetector.GestureAdapter {

    private AxonixGame game;
    private GameScreen gameScreen;

    public  AxonixGameGestureListener(AxonixGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        switch (gameScreen.getState()) {
            case LEVEL_COMPLETED:
                int nextIndex = gameScreen.getLevelIndex() + 1;
                if (nextIndex < game.getLevelsFiles().size()) {
                    gameScreen.setLevel(nextIndex);
                } else {
                    gameScreen.setState(GameScreen.State.WIN);
                }
                return true;
            case GAME_OVER:
                game.setStartScreen();
                return true;
            case WIN:
                game.setStartScreen();
                return true;
        }
        return false;
    }

}
