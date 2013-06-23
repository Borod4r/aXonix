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

package net.ivang.axonix.main.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.events.facts.EnemyDirectionFact;
import net.ivang.axonix.main.events.facts.MusicVolumeFact;
import net.ivang.axonix.main.events.facts.screen.GameScreenFact;
import net.ivang.axonix.main.events.facts.screen.LevelsScreenFact;
import net.ivang.axonix.main.events.facts.screen.StartScreenFact;
import net.ivang.axonix.main.preferences.PreferencesWrapper;
import net.ivang.axonix.main.screens.GameScreen;

import java.util.Random;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class SoundManager {

    private PreferencesWrapper preferences;

    private Loop currentLoop;
    private float musicVolume;

    @Inject
    public SoundManager(PreferencesWrapper preferences, EventBus eventBus) {
        this.preferences = preferences;
        this.musicVolume = preferences.getMusicVolume();
        eventBus.register(this);
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onMusicVolumeChange(MusicVolumeFact fact) {
        musicVolume = fact.getVolume();
        if (musicVolume > 0) {
            Music loopMusic = currentLoop.getMusic();
            loopMusic.setVolume(musicVolume);
            if (!loopMusic.isPlaying()) {
                loopMusic.play();
            }
        } else {
            currentLoop.getMusic().pause();
        }
        // save to preferences
        preferences.setMusicVolume(musicVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(StartScreenFact fact) {
        setCurrentLoop(Loop.START);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(LevelsScreenFact fact) {
        setCurrentLoop(Loop.START);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(GameScreenFact fact) {
        setCurrentLoop(Loop.GAME);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameScreenStateChange(GameScreen.State state) {
        switch (state) {
            case PLAYING:
                currentLoop.getMusic().play();
                break;
            case PAUSED:
                currentLoop.getMusic().pause();
                break;
            case LEVEL_COMPLETED:
            case GAME_OVER:
            case WIN:
                currentLoop.getMusic().stop();
                break;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEnemyCollision(EnemyDirectionFact fact) {
        Sounds.ENEMY_COLLISION.play(1);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void setCurrentLoop(Loop currentLoop) {
        this.currentLoop = currentLoop;

        for (Loop loop : Loop.values()) {
            Music loopMusic = loop.getMusic();
            if (loop == currentLoop) {
                if (musicVolume > 0 && !loopMusic.isPlaying()) {
                    loopMusic.setVolume(musicVolume);
                    loopMusic.play();
                }
            } else {
                loopMusic.stop();
            }
        }
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private enum Loop {
        START(Gdx.audio.newMusic(Gdx.files.internal("data/music/loop_start.ogg"))),
        GAME(Gdx.audio.newMusic(Gdx.files.internal("data/music/loop_game.ogg")));

        private final Music music;

        private Loop(Music music) {
            this.music = music;
            music.setLooping(true);
        }

        public Music getMusic() {
            return music;
        }
    }

    private enum Sounds {
        ENEMY_COLLISION(Gdx.audio.newSound(Gdx.files.internal("data/music/enemy_collision.ogg")), false, 150, 100);

        private final Sound sound;
        private boolean concurrent;
        private int gapMin, gapRange;
        private long lastPlayed;
        private int gap;
        private Random random;

        private Sounds(Sound sound, boolean concurrent, int gapMin, int gapRange) {
            this.sound = sound;
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
            long soundId = -1;
            long now = System.currentTimeMillis();
            if (gap <= 0 || now - lastPlayed > gap) {
                lastPlayed = now;
                if (gapRange != 0) gap = getRandomGap();
                if (!concurrent) sound.stop();
                soundId = sound.play(volume);
            }
            return soundId;
        }

        private int getRandomGap() {
            return random.nextInt(gapRange) + gapMin;
        }

    }
}
