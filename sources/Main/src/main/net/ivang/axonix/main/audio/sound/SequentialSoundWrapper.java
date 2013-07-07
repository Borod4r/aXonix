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
package net.ivang.axonix.main.audio.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class SequentialSoundWrapper implements SoundWrapper {

    private final Sound[] sounds;
    private int index;

    public SequentialSoundWrapper(String... paths) {
        this.sounds = new Sound[paths.length];
        for (int i = 0; i < paths.length; i++) {
            sounds[i] = Gdx.audio.newSound(Gdx.files.internal(paths[i]));
        }
    }

    public long play(float volume) {
        long newId = -1;
        if (volume > 0) {
            newId = sounds[index].play(volume);
            index = (index + 1) % sounds.length;
        }
        return newId;
    }
}
