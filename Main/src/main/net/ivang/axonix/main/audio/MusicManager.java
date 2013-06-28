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
import com.badlogic.gdx.audio.Music;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.events.facts.MusicVolumeFact;
import net.ivang.axonix.main.events.facts.screen.GameScreenFact;
import net.ivang.axonix.main.events.facts.screen.LevelsScreenFact;
import net.ivang.axonix.main.events.facts.screen.StartScreenFact;
import net.ivang.axonix.main.preferences.PreferencesWrapper;
import net.ivang.axonix.main.screens.GameScreen;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class MusicManager {

    private PreferencesWrapper preferences;

    private Loop currentLoop;
    private float musicVolume;

    @Inject
    public MusicManager(PreferencesWrapper preferences, EventBus eventBus) {
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
        START("data/audio/music/loop_start.ogg"),
        GAME("data/audio/music/loop_game.ogg");

        private final Music music;

        private Loop(String path) {
            this.music = Gdx.audio.newMusic(Gdx.files.internal(path));
            music.setLooping(true);
        }

        public Music getMusic() {
            return music;
        }
    }

}
