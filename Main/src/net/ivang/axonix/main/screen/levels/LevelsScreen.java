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

package net.ivang.axonix.main.screen.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.esotericsoftware.tablelayout.Cell;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.screen.BaseScreen;
import net.ivang.axonix.main.screen.levels.actor.LevelButton;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class LevelsScreen extends BaseScreen {

    private final static int LEVELS_TABLE_COLS = 6;

    private Skin skin;
    private Table levelsTable;

    public LevelsScreen(final AxonixGame game) {
        super(game);

        TextureAtlas atlas = new TextureAtlas("data/atlas/start_screen.atlas");
        skin = new Skin(Gdx.files.internal("data/skin/start_screen.json"), atlas);
        Style style = getStyleByHeight(Gdx.graphics.getHeight());

        levelsTable = new Table();
        Preferences prefs = Gdx.app.getPreferences(AxonixGame.PREFS_NAME);

        for (int levelNumber = 1; levelNumber <= game.getLevelsFiles().size(); levelNumber++) {
            LevelButton button = new LevelButton(Integer.toString(levelNumber), skin, style.toString(), game, levelNumber);
            // disable button if current level isn't first and there is no prefs for previous levels
            if (levelNumber > 1 && !prefs.contains(AxonixGame.PREF_KEY_LEVEL + (levelNumber - 1))) {
                button.setColor(1f, 1f, 1f, 0.35f);
                button.setDisabled(true);
            }
            levelsTable.add(button);
            if (levelNumber % LEVELS_TABLE_COLS == 0) {
                levelsTable.row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(levelsTable);
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
        Style style = getStyleByHeight(height);
        float scale = getScaleByStyle(style);
        float buttonSide = 85 * scale;
        float padding = 20 * scale;

        TextButton.TextButtonStyle buttonStyle = skin.get(style.toString(), TextButton.TextButtonStyle.class);

        for(Cell cell : levelsTable.getCells()) {
            TextButton button = (TextButton) cell.getWidget();
            if (button != null) {
                button.setStyle(buttonStyle);
            }
            cell.width(buttonSide).height(buttonSide).pad(padding);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

}
