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

import java.util.Random;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class CustomSoundWrapper implements SoundWrapper {

    private final Sound sound;
    private long soundId;
    private boolean concurrent;
    private int gapMin, gapRange;
    private long lastPlayed;
    private int gap;
    private Random random;

    public CustomSoundWrapper(String path, boolean concurrent, int gapMin, int gapRange) {
        this.sound = Gdx.audio.newSound(Gdx.files.internal(path));
        this.concurrent = concurrent;
        this.gapMin = gapMin;
        this.gapRange = gapRange;

        if (gapRange != 0) {
            this.random = new Random();
            gap = getRandomGap();
        } else {
            gap = gapMin;
        }
    }

    public long play(float volume) {
        long newId = -1;
        if (volume > 0) {
            long now = System.currentTimeMillis();
            if (gap <= 0 || now - lastPlayed > gap) {
                lastPlayed = now;
                if (gapRange != 0) gap = getRandomGap();
                newId = sound.play(volume);
                // workaround to avoid clapping on android
                if (!concurrent) {
                    sound.stop(soundId);
                    soundId = newId;
                }
            }
        }
        return newId;
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private int getRandomGap() {
        return random.nextInt(gapRange) + gapMin;
    }

}
