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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.actors.game.Protagonist;
import net.ivang.axonix.main.events.facts.*;
import net.ivang.axonix.main.preferences.PreferencesWrapper;

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

    @Subscribe
    @SuppressWarnings("unused")
    public void onPointsObtained(ObtainedPointsFact fact) {
        int points = fact.getPoints();
        if (points < ObtainedPointsFact.QUANTITY_1) {
            Sounds.FILLING_SHORT_1.play(sfxVolume);
        } else if (points < ObtainedPointsFact.QUANTITY_2){
            Sounds.FILLING_SHORT_2.play(sfxVolume);
        } else if (points < ObtainedPointsFact.QUANTITY_3) {
            Sounds.FILLING_SHORT_3.play(sfxVolume);
        } else {
            Sounds.FILLING.play(sfxVolume);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onProtagonistStateChange(Protagonist.State state) {
        if (state == Protagonist.State.DYING) {
            Sounds.PROT_DYING.play(sfxVolume);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onButtonClick(ButtonClickFact fact) {
        Sounds.BUTTON_CLICK.play(sfxVolume);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private enum Sounds {
        BUTTON_CLICK("data/audio/sounds/button_click.ogg"),
        PROT_DYING("data/audio/sounds/prot_dying.ogg"),
        TAIL_BLOCK("data/audio/sounds/tail_block.ogg"),
        ENEMY_DIRECTION("data/audio/sounds/enemy_direction.ogg", false, 150, 100),
        FILLING_SHORT_1("data/audio/sounds/filling_short_1.ogg"),
        FILLING_SHORT_2("data/audio/sounds/filling_short_2.ogg"),
        FILLING_SHORT_3("data/audio/sounds/filling_short_3.ogg"),
        FILLING("data/audio/sounds/filling_1.ogg",
                "data/audio/sounds/filling_2.ogg",
                "data/audio/sounds/filling_3.ogg");

        private final SoundWrapper sound;

        private Sounds(String path) {
            this.sound = new CustomSoundWrapper(path, true, 0, 0);
        }

        private Sounds(String path, boolean concurrent, int gapMin, int gapRange) {
            this.sound = new CustomSoundWrapper(path, concurrent, gapMin, gapRange);
        }

        private Sounds(String... paths) {
            this.sound = new SequentialSoundWrapper(paths);
        }

        public long play(float volume) {
            return sound.play(volume);
        }
    }
}
