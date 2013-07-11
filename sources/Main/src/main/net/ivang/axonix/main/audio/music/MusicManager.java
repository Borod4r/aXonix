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

package net.ivang.axonix.main.audio.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.events.intents.MusicVolumeIntent;
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

    private Loops currentLoop;
    private float musicVolume;

    @Inject
    public MusicManager(PreferencesWrapper preferences, EventBus eventBus) {
        this.musicVolume = preferences.getMusicVolume();
        eventBus.register(this);
        Loops.initAll();
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onMusicVolumeChange(MusicVolumeIntent intent) {
        musicVolume = intent.getVolume();
        if (musicVolume > 0) {
            currentLoop.play(musicVolume);
        } else {
            currentLoop.pause();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(StartScreenFact fact) {
        setCurrentLoop(Loops.START);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(LevelsScreenFact fact) {
        setCurrentLoop(Loops.START);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(GameScreenFact fact) {
        setCurrentLoop(Loops.GAME);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameScreenStateChange(GameScreen.State state) {
        switch (state) {
            case PLAYING:
                currentLoop.play(musicVolume);
                break;
            case PAUSED:
                currentLoop.pause();
                break;
            case LEVEL_COMPLETED:
            case GAME_OVER:
            case WIN:
                currentLoop.stop();
                break;
        }
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void setCurrentLoop(Loops loop) {
        if (currentLoop != loop) {
            // stop the previous loop
            if (currentLoop != null) {
                currentLoop.stop();
            }
            // play new loop
            currentLoop = loop;
            currentLoop.play(musicVolume);
        }
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private enum Loops {
        START("data/audio/music/loop_start.ogg"),
        GAME("data/audio/music/loop_game.ogg");

        private final String path;
        private Music music;

        private Loops(String path) {
            this.path = path;
        }

        /**
         * Initializes music from internal file by path.
         *
         * Should be called outside, because on android static classes may keep on living even though
         * the game has been closed and then music will not be reinitialized properly after reopening.
         */
        public void init() {
            this.music = Gdx.audio.newMusic(Gdx.files.internal(path));
            music.setLooping(true);
        }

        /**
         * Initializes all music loops for this enum.
         *
         * Should be called outside, because on android static classes may keep on living even though
         * the game has been closed and then music will not be reinitialized properly after reopening.
         */

        public static void initAll() {
            for (Loops loop : values()) {
                loop.init();
            }
        }

        public void play(float volume) {
            music.setVolume(volume);
            if (volume > 0 && !music.isPlaying()) {
                music.play();
            }
        }

        public void pause() {
            music.pause();
        }

        public void stop() {
            music.stop();
        }

    }

}
