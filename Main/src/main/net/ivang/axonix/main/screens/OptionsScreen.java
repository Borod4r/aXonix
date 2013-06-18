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

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.actors.options.VolumeSlider;
import net.ivang.axonix.main.events.facts.MusicVolumeFact;
import net.ivang.axonix.main.events.intents.BackIntent;
import net.ivang.axonix.main.events.intents.screen.StartScreenIntent;
import net.ivang.axonix.main.preferences.PreferencesWrapper;

import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class OptionsScreen extends BaseScreen {

    private Style style;

    private Label musicVolumeLabel;
    private Label musicVolumeValue;
    private VolumeSlider musicVolumeSlider;

    @Inject
    PreferencesWrapper preferences;

    @Inject
    private OptionsScreen(final AxonixGame game, InputMultiplexer inputMultiplexer, final EventBus eventBus) {
        super(game, inputMultiplexer, eventBus);

        setStyle(getStyleByHeight().toString());

        // root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        // music volume text
        musicVolumeLabel = new Label("Music Volume:", style.labelStyle);
        musicVolumeValue = new Label(null, style.valueStyle);
        // music volume slider
        musicVolumeSlider = new VolumeSlider(0, 1, 0.01f, style.sliderStyle);
        musicVolumeSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float value = ((VolumeSlider) actor).getValue();
                eventBus.post(new MusicVolumeFact(value));
            }
        });

        rootTable.add(musicVolumeLabel).left().padLeft(20);
        rootTable.add(musicVolumeValue).right().padRight(20).spaceLeft(1); // spaceLeft(1) - workaround to avoid flickering
        rootTable.row();
        rootTable.add(musicVolumeSlider).colspan(2);

        stage.addActor(rootTable);
    }

    @Override
    public void show() {
        super.show();
        float musicVolume = preferences.getMusicVolume();
        musicVolumeValue.setText(Math.round(musicVolume * 100) + "%");
        musicVolumeSlider.setValue(musicVolume);
    }

    @Override
    public void hide() {
        super.hide();
        // save all options automatically on screen hide
        preferences.flush();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
        setStyle(getStyleByHeight(height).toString());
    }

    public void setStyle(String styleName) {
        setStyle(skin.get(styleName, Style.class));
    }

    public void setStyle(Style style) {
        if (style != null) {
            this.style = style;
            if (musicVolumeLabel != null) musicVolumeLabel.setStyle(style.labelStyle);
            if (musicVolumeValue != null) musicVolumeValue.setStyle(style.valueStyle);
            if (musicVolumeSlider != null) musicVolumeSlider.setStyle(style.sliderStyle);
        } else {
            throw new IllegalArgumentException("style cannot be null.");
        }
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onMusicVolumeChange(MusicVolumeFact fact) {
        float musicVolume = fact.getVolume();
        musicVolumeValue.setText(Math.round(musicVolume * 100) + "%");

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void doBacktAction(BackIntent intent) {
        eventBus.post(new StartScreenIntent());
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    static public class Style {
        public LabelStyle labelStyle;
        public LabelStyle valueStyle;
        public VolumeSlider.Style sliderStyle;
    }

}
