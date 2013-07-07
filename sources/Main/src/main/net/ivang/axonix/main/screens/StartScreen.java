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

package net.ivang.axonix.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.events.facts.ButtonClickFact;
import net.ivang.axonix.main.events.intents.BackIntent;
import net.ivang.axonix.main.events.intents.DefaultIntent;
import net.ivang.axonix.main.events.intents.screen.LevelsScreenIntent;
import net.ivang.axonix.main.events.intents.screen.OptionsScreenIntent;

import static com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class StartScreen extends BaseScreen {

    private Style style;
    private Button startButton;
    private Button optionsButton;
    private Cell logoCell;
    private Cell startButtonCell;
    private Cell optionsButtonCell;

    @Inject
    private StartScreen(AxonixGame game, InputMultiplexer inputMultiplexer, final EventBus eventBus) {
        super(game, inputMultiplexer, eventBus);

        // root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        // logo
        Image logo = new Image(skin, "logo");
        logoCell = rootTable.add(logo);
        rootTable.row();
        // start button
        startButton = new TextButton("Start", style.button);
        startButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new ButtonClickFact());
                eventBus.post(new LevelsScreenIntent());
            }
        });
        startButtonCell = rootTable.add(startButton);
        rootTable.row();
        // options button
        optionsButton = new TextButton("Options", style.button);
        optionsButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new ButtonClickFact());
                eventBus.post(new OptionsScreenIntent());
            }
        });
        optionsButtonCell = rootTable.add(optionsButton);
        // stage
        stage.addActor(rootTable);
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void doDefaultAction(DefaultIntent intent) {
        eventBus.post(new LevelsScreenIntent());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void doBacktAction(BackIntent intent) {
        Gdx.app.exit();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    @Override
    protected void setStyleByName(String styleName) {
        style = skin.get(styleName, Style.class);
    }

    @Override
    protected void applyStyle() {
        // logo
        logoCell.width(style.logoWidth).height(style.logoHeight).pad(style.logoPad);
        // start button
        startButton.setStyle(style.button);
        startButtonCell.width(style.buttonWidth).height(style.buttonHeight).pad(style.buttonPad);
        // options button
        optionsButton.setStyle(style.button);
        optionsButtonCell.width(style.buttonWidth).height(style.buttonHeight).pad(style.buttonPad);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    static public class Style {
        public TextButtonStyle button;
        public float buttonWidth;
        public float buttonHeight;
        public float buttonPad;
        public float logoWidth;
        public float logoHeight;
        public float logoPad;
    }

}
