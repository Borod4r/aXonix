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

package net.ivang.axonix.main.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.events.facts.EnemyDirectionFact;
import net.ivang.axonix.main.events.facts.SfxVolumeFact;
import net.ivang.axonix.main.events.facts.TailBlockFact;
import net.ivang.axonix.main.preferences.PreferencesWrapper;

import java.util.Random;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class SoundManager {

    private PreferencesWrapper preferences;

    private float sfxVolume;

    @Inject
    public SoundManager(PreferencesWrapper preferences, EventBus eventBus) {
        this.preferences = preferences;
        this.sfxVolume = preferences.getSfxVolume();
        eventBus.register(this);
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onSfxVolumeChange(SfxVolumeFact fact) {
        sfxVolume = fact.getVolume();
        // save to preferences
        preferences.setSfxVolume(sfxVolume);
        // play sample sound
        Sounds.ENEMY_DIRECTION.play(sfxVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEnemyCollision(EnemyDirectionFact fact) {
        Sounds.ENEMY_DIRECTION.play(sfxVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNewTailBlock(TailBlockFact fact) {
        Sounds.TAIL_BLOCK.play(sfxVolume);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private enum Sounds {
        ENEMY_DIRECTION("data/audio/sounds/enemy_direction.ogg", false, 150, 100),
        TAIL_BLOCK("data/audio/sounds/tail_block.ogg", true, 0, 0);

        private final Sound sound;
        private long soundId;
        private boolean concurrent;
        private int gapMin, gapRange;
        private long lastPlayed;
        private int gap;
        private Random random;

        private Sounds(String path, boolean concurrent, int gapMin, int gapRange) {
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

        private int getRandomGap() {
            return random.nextInt(gapRange) + gapMin;
        }

    }
}
