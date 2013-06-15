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

package net.ivang.axonix.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.events.facts.screen.GameScreenFact;
import net.ivang.axonix.main.events.facts.screen.LevelsScreenFact;
import net.ivang.axonix.main.events.facts.screen.StartScreenFact;
import net.ivang.axonix.main.events.intents.screen.GameScreenIntent;
import net.ivang.axonix.main.events.intents.screen.LevelsScreenIntent;
import net.ivang.axonix.main.events.intents.screen.StartScreenIntent;
import net.ivang.axonix.main.input.AxonixGameInputProcessor;
import net.ivang.axonix.main.screens.GameScreen;
import net.ivang.axonix.main.screens.LevelsScreen;
import net.ivang.axonix.main.screens.StartScreen;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class AxonixGame extends Game {

    @Inject private StartScreen startScreen;
    @Inject private LevelsScreen levelsScreen;
    @Inject private GameScreen gameScreen;

    private Skin skin;
    private List<FileHandle> levelsFiles;

    private EventBus eventBus;

    @Inject
    private AxonixGame(InputMultiplexer inputMultiplexer, EventBus eventBus) {
        initSkin();
        initLevels();
        // Input event handling
        inputMultiplexer.addProcessor(new AxonixGameInputProcessor(eventBus));
        Gdx.input.setInputProcessor(inputMultiplexer);
        // register with the event bus
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void create() {
        eventBus.post(new StartScreenIntent());
    }

    @Override
    public void setScreen(Screen screen) {
        throw new UnsupportedOperationException("Use intents instead of setting screens directly");
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void setStartScreen(StartScreenIntent intent) {
        super.setScreen(startScreen);
        eventBus.post(new StartScreenFact());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void setLevelsScreen(LevelsScreenIntent intent) {
        super.setScreen(levelsScreen);
        eventBus.post(new LevelsScreenFact());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void setGameScreen(GameScreenIntent intent) {
        super.setScreen(gameScreen);
        int levelIndex = intent.getLevelIndex();
        if ( levelIndex != 0) {
            gameScreen.loadLevel(levelIndex);
        }
        eventBus.post(new GameScreenFact());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void catchDeadEvent(DeadEvent event) {
        Logger logger = new Logger("aXonix");
        logger.error("Dead event - " + event.getEvent().getClass().toString());
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void initSkin() {
        TextureAtlas atlas = new TextureAtlas("data/atlas/axonix_atlas.atlas");
        skin = new Skin(Gdx.files.internal("data/skin/axonix_skin.json"), atlas);
    }

    private void initLevels() {
        FileHandle dirHandle = Gdx.files.internal("data/levels");
        levelsFiles = Arrays.asList(dirHandle.list());
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public List<FileHandle> getLevelsFiles() {
        return levelsFiles;
    }

    public Skin getSkin() {
        return skin;
    }

    public StartScreen getStartScreen() {
        return startScreen;
    }

    public LevelsScreen getLevelsScreen() {
        return levelsScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
}
