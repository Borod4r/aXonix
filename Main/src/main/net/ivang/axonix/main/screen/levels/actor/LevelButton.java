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

package net.ivang.axonix.main.screen.levels.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.common.eventbus.EventBus;
import net.ivang.axonix.main.screen.game.event.LoadLevelAction;
import net.ivang.axonix.main.screen.game.event.ScreenEvent;

import static net.ivang.axonix.main.screen.game.event.ScreenEvent.Screen;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class LevelButton extends TextButton {

    private final int levelNumber;

    public LevelButton(final int levelNumber, Skin skin, String styleName, final EventBus eventBus) {
        super(Integer.toString(levelNumber), skin, styleName);
        this.levelNumber = levelNumber;
        addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new LoadLevelAction(levelNumber));
                eventBus.post(new ScreenEvent(Screen.GAME));
            }
        });
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public int getLevelNumber() {
        return levelNumber;
    }
}
