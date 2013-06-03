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

package net.ivang.axonix.main.actors.game.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.events.facts.LevelScoreFact;
import net.ivang.axonix.main.events.facts.TotalScoreFact;
import net.ivang.axonix.main.events.intents.DefaultIntent;
import net.ivang.axonix.main.events.intents.screen.LevelsScreenIntent;
import net.ivang.axonix.main.events.intents.game.ReplayLevelIntent;
import net.ivang.axonix.main.screens.GameScreen;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class ScreenStateDialog extends AlertDialog {

    public ScreenStateDialog(String titleText, Skin skin, String styleName, final EventBus eventBus) {
        super(titleText, skin, styleName);
        eventBus.register(this);
        // levels button listener
        addButtonListener(1, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new LevelsScreenIntent());
            }
        });
        // replay button listener
        addButtonListener(2, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new ReplayLevelIntent());
            }
        });
        // forward button listener
        addButtonListener(3, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new DefaultIntent());
            }
        });
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameScreenStateChanged(GameScreen.State state) {
        switch (state) {
            case PLAYING:
                // hide dialog
                if (getActions().size == 0) {
                    setVisible(false);
                }
            break;
            case PAUSED:
                setTitle("PAUSE");
                setVisible(true);
                break;
            case LEVEL_COMPLETED:
                setTitle("LEVEL COMPLETED");
                setVisible(true);
                break;
            case GAME_OVER:
                setTitle("GAME OVER");
                setVisible(true);
                break;
            case WIN:
                setTitle("YOU WIN!");
                setVisible(true);
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLevelScoreChange(LevelScoreFact event) {
        setLevelScore(event.getScore());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onTotalScoreChange(TotalScoreFact event) {
        setTotalScore(event.getScore());
    }

}
