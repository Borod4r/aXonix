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
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.actors.options.VolumeSlider;
import net.ivang.axonix.main.events.intents.SfxVolumeIntent;
import net.ivang.axonix.main.events.intents.MusicVolumeIntent;
import net.ivang.axonix.main.events.intents.BackIntent;
import net.ivang.axonix.main.events.intents.DefaultIntent;
import net.ivang.axonix.main.events.intents.screen.StartScreenIntent;
import net.ivang.axonix.main.preferences.PreferencesWrapper;

import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class OptionsScreen extends BaseScreen {

    private Style style;
    // music volume
    private Label musicVolumeLabel;
    private Label musicVolumeValue;
    private VolumeSlider musicVolumeSlider;
    private Cell musicVolumeLabelCell;
    private Cell musicVolumeValueCell;
    private Cell musicVolumeSliderCell;
    // sfx volume
    private Label sfxVolumeLabel;
    private Label sfxVolumeValue;
    private VolumeSlider sfxVolumeSlider;
    private Cell sfxVolumeLabelCell;
    private Cell sfxVolumeValueCell;
    private Cell sfxVolumeSliderCell;

    PreferencesWrapper preferences;

    @Inject
    private OptionsScreen(final AxonixGame game, InputMultiplexer inputMultiplexer, PreferencesWrapper preferences,
                          final EventBus eventBus) {
        super(game, inputMultiplexer, eventBus);
        this.preferences = preferences;

        // root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        // music volume text
        float musicVolume = preferences.getMusicVolume();
        musicVolumeLabel = new Label("Music Volume:", style.labelStyle);
        musicVolumeValue = new Label(Math.round(musicVolume * 100) + "%", style.valueStyle);
        // music volume slider
        musicVolumeSlider = new VolumeSlider(0, 1, 0.01f, style.sliderStyle);
        musicVolumeSlider.setValue(musicVolume);
        musicVolumeSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float value = ((VolumeSlider) actor).getValue();
                eventBus.post(new MusicVolumeIntent(value));
            }
        });

        // sfx volume text
        float sfxVolume = preferences.getSfxVolume();
        sfxVolumeLabel = new Label("SFX Volume:", style.labelStyle);
        sfxVolumeValue = new Label(Math.round(sfxVolume * 100) + "%", style.valueStyle);
        // sfx volume slider
        sfxVolumeSlider = new VolumeSlider(0, 1, 0.01f, style.sliderStyle);
        sfxVolumeSlider.setValue(sfxVolume);
        sfxVolumeSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float value = ((VolumeSlider) actor).getValue();
                eventBus.post(new SfxVolumeIntent(value));
            }
        });

        // music section
        musicVolumeLabelCell = rootTable.add(musicVolumeLabel);
        musicVolumeLabelCell.left().padLeft(style.labelPad);
        musicVolumeValueCell = rootTable.add(musicVolumeValue);
        musicVolumeValueCell.right().padRight(style.labelPad).spaceLeft(1); // spaceLeft(1) - workaround to avoid flickering
        rootTable.row();
        musicVolumeSliderCell = rootTable.add(musicVolumeSlider);
        musicVolumeSliderCell.colspan(2).padBottom(style.sectionPad);
        // sfx section
        rootTable.row();
        sfxVolumeLabelCell = rootTable.add(sfxVolumeLabel);
        sfxVolumeLabelCell.left().padLeft(style.labelPad);
        sfxVolumeValueCell = rootTable.add(sfxVolumeValue);
        sfxVolumeValueCell.right().padRight(style.labelPad).spaceLeft(1); // spaceLeft(1) - workaround to avoid flickering
        rootTable.row();
        sfxVolumeSliderCell = rootTable.add(sfxVolumeSlider);
        sfxVolumeSliderCell.colspan(2);

        stage.addActor(rootTable);
    }

    @Override
    public void hide() {
        super.hide();
        // save all options automatically on screen hide
        preferences.flush();
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onMusicVolumeChange(MusicVolumeIntent intent) {
        float musicVolume = intent.getVolume();
        preferences.setMusicVolume(musicVolume);
        musicVolumeValue.setText(Math.round(musicVolume * 100) + "%");

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onSfxVolumeChange(SfxVolumeIntent intent) {
        float sfxVolume = intent.getVolume();
        preferences.setSfxVolume(sfxVolume);
        sfxVolumeValue.setText(Math.round(sfxVolume * 100) + "%");
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void doBacktAction(BackIntent intent) {
        eventBus.post(new StartScreenIntent());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void doDefaultAction(DefaultIntent intent) {
        eventBus.post(new BackIntent());
    }

    //---------------------------------------------------------------------
    // Helper Methods
    //---------------------------------------------------------------------

    @Override
    protected void setStyleByName(String styleName) {
        style = skin.get(styleName, Style.class);
    }

    @Override
    protected void applyStyle() {
        // music
        musicVolumeLabel.setStyle(style.labelStyle);
        musicVolumeValue.setStyle(style.valueStyle);
        musicVolumeSlider.setStyle(style.sliderStyle);
        musicVolumeLabelCell.padLeft(style.labelPad);
        musicVolumeValueCell.padRight(style.labelPad);
        musicVolumeSliderCell.padBottom(style.sectionPad);
        // sfx
        sfxVolumeLabel.setStyle(style.labelStyle);
        sfxVolumeValue.setStyle(style.valueStyle);
        sfxVolumeSlider.setStyle(style.sliderStyle);
        sfxVolumeLabelCell.padLeft(style.labelPad);
        sfxVolumeValueCell.padRight(style.labelPad);
        sfxVolumeSliderCell.padBottom(style.sectionPad);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    static public class Style {
        public LabelStyle labelStyle;
        public LabelStyle valueStyle;
        public VolumeSlider.Style sliderStyle;
        public float labelPad;
        public float sectionPad;
    }

}
