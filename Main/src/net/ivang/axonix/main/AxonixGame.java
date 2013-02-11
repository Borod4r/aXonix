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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import net.ivang.axonix.main.screen.StartScreen;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class AxonixGame extends Game {

    private Screen startScreen;

    private List<FileHandle> levelsFiles;

    @Override
    public void create() {
        /* init list of levels files */
        FileHandle dirHandle = Gdx.files.internal("data/levels");
        levelsFiles = Arrays.asList(dirHandle.list());
        setStartScreen();
    }

    public void setStartScreen() {
        if (startScreen == null) {
            startScreen = new StartScreen(this);
        }
        setScreen(startScreen);
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public List<FileHandle> getLevelsFiles() {
        return levelsFiles;
    }

    public void setLevelsFiles(List<FileHandle> levelsFiles) {
        this.levelsFiles = levelsFiles;
    }

}
