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

package net.ivang.axonix.main.screen.game.input;

import com.badlogic.gdx.InputAdapter;
import com.google.common.eventbus.EventBus;
import net.ivang.axonix.main.screen.game.event.DefaultAction;
import net.ivang.axonix.main.screen.game.event.ScreenEvent;

import static com.badlogic.gdx.Input.Keys;
import static net.ivang.axonix.main.screen.game.event.ScreenEvent.Screen;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class GameScreenInputProcessor extends InputAdapter {

    private EventBus eventBus;

    public  GameScreenInputProcessor(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.SPACE:
                eventBus.post(new DefaultAction());
                return true;
            case Keys.ESCAPE:
                //TODO: only for testing purposes
                eventBus.post(new ScreenEvent(Screen.START));
                return true;
        }
        return false;
    }
}