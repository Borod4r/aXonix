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
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.events.facts.screen.GameScreenFact;
import net.ivang.axonix.main.events.facts.screen.LevelsScreenFact;
import net.ivang.axonix.main.events.facts.screen.StartScreenFact;
import net.ivang.axonix.main.screens.GameScreen;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class SoundManager {

    private Music gameScreenMusic;
    private Music startScreenMusic;

    @Inject
    public SoundManager(EventBus eventBus) {
        eventBus.register(this);
        // start screen
        startScreenMusic = Gdx.audio.newMusic(Gdx.files.internal("data/music/loop_start.ogg"));
        startScreenMusic.setLooping(true);
        // game screen
        gameScreenMusic = Gdx.audio.newMusic(Gdx.files.internal("data/music/loop_game.ogg"));
        gameScreenMusic.setLooping(true);
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(StartScreenFact fact) {
        if (!startScreenMusic.isPlaying()) {
            startScreenMusic.play();
        }
        gameScreenMusic.stop();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(LevelsScreenFact fact) {
        if (!startScreenMusic.isPlaying()) {
            startScreenMusic.play();
        }
        gameScreenMusic.stop();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onScreenChangeTo(GameScreenFact fact) {
        startScreenMusic.stop();
        gameScreenMusic.play();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameScreenStateChange(GameScreen.State state) {
        switch (state) {
            case PLAYING:
                gameScreenMusic.play();
                break;
            case PAUSED:
                gameScreenMusic.pause();
                break;
            case LEVEL_COMPLETED:
            case GAME_OVER:
            case WIN:
                gameScreenMusic.stop();
                break;
        }
    }
}
