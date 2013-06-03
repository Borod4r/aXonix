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

package net.ivang.axonix.main.input;

import com.badlogic.gdx.InputAdapter;
import com.google.common.eventbus.EventBus;
import net.ivang.axonix.main.events.intents.BackIntent;
import net.ivang.axonix.main.events.intents.DefaultIntent;

import static com.badlogic.gdx.Input.Keys;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class AxonixGameInputProcessor extends InputAdapter {

    private EventBus eventBus;

    public AxonixGameInputProcessor(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.SPACE:
                eventBus.post(new DefaultIntent());
                return true;
            case Keys.BACK:
            case Keys.ESCAPE:
                eventBus.post(new BackIntent());
                return true;
        }
        return false;
    }
}