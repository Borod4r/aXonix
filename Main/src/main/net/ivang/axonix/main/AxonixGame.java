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

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import net.ivang.axonix.main.screen.StartScreen;
import net.ivang.axonix.main.screen.game.GameScreen;
import net.ivang.axonix.main.screen.levels.LevelsScreen;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class AxonixGame extends Game {

    public static final String PREFS_NAME = "aXonix";
    public static final String PREF_KEY_LIVES = "lives_";
    public static final String PREF_KEY_LVL_SCORE = "level_score_";
    public static final String PREF_KEY_TTL_SCORE = "total_score";

    private StartScreen startScreen;
    private LevelsScreen levelsScreen;
    private GameScreen gameScreen;
    private Skin skin;
    private List<FileHandle> levelsFiles;
    private Preferences preferences;

    @Override
    public void create() {
        initSkin();
        initLevels();
        initPreferences();
        setStartScreen();
    }

    public void setStartScreen() {
        if (startScreen == null) {
            startScreen = new StartScreen(this);
        }
        setScreen(startScreen);
    }

    public void setLevelsScreen() {
        if (levelsScreen == null) {
            levelsScreen = new LevelsScreen(this);
        }
        setScreen(levelsScreen);
    }

    public void setGameScreen(int levelNumber) {
        if (gameScreen == null) {
            gameScreen = new GameScreen(this);
        }
        gameScreen.loadLevel(levelNumber);
        setScreen(gameScreen);
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

    private void initPreferences() {
        preferences = Gdx.app.getPreferences(AxonixGame.PREFS_NAME);
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

    public Preferences getPreferences() {
        return preferences;
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
