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

package net.ivang.axonix.main.preferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class PreferencesWrapper {

    private static final String PREFS_NAME = "aXonix";
    private static final String PREF_KEY_LIVES = "lives_";
    private static final String PREF_KEY_LVL_SCORE = "level_score_";
    private static final String PREF_KEY_TTL_SCORE = "total_score";

    private static final String PREF_KEY_MUSIC_VOLUME = "music_volume";
    private static final float MUSIC_VOLUME_DEF_VALUE = 0.7f;

    private static final String PREF_KEY_SFX_VOLUME = "sfx_volume";
    private static final float SFX_VOLUME_DEF_VALUE = 0.7f;

    private Preferences preferences;
    private boolean isChanged;

    public PreferencesWrapper() {
        preferences = Gdx.app.getPreferences(PREFS_NAME);
    }

    /* Lives */

    public int getLives(int levelIndex) {
        return preferences.getInteger(PREF_KEY_LIVES + levelIndex);
    }

    public void setLives(int levelIndex, int livesNumber) {
        preferences.putInteger(PREF_KEY_LIVES + levelIndex, livesNumber);
        isChanged = true;
    }

    public boolean containsLives(int levelIndex) {
        return preferences.contains(PREF_KEY_LIVES + levelIndex);
    }

    /* Level Score */

    public int getLevelScore(int levelIndex) {
        return preferences.getInteger(PREF_KEY_LVL_SCORE + levelIndex);
    }

    public void setLevelScore(int levelIndex, int levelScore) {
        preferences.putInteger(PREF_KEY_LVL_SCORE + levelIndex, levelScore);
        isChanged = true;
    }

    /* Total Score */

    public int getTotalScore() {
        return preferences.getInteger(PREF_KEY_TTL_SCORE);
    }

    public void setTotalScore(int totalScore) {
        preferences.putInteger(PREF_KEY_TTL_SCORE, totalScore);
        isChanged = true;
    }

    /* Options */

    public float getMusicVolume() {
        return preferences.getFloat(PREF_KEY_MUSIC_VOLUME, MUSIC_VOLUME_DEF_VALUE);
    }

    public void setMusicVolume(float musicVolume) {
        preferences.putFloat(PREF_KEY_MUSIC_VOLUME, musicVolume);
        isChanged = true;
    }

    public float getSfxVolume() {
        return preferences.getFloat(PREF_KEY_SFX_VOLUME, SFX_VOLUME_DEF_VALUE);
    }

    public void setSfxVolume(float sfxVolume) {
        preferences.putFloat(PREF_KEY_SFX_VOLUME, sfxVolume);
        isChanged = true;
    }

    /* Utility */

    public void flush() {
        if (isChanged) {
            preferences.flush();
            isChanged = false;
        }
    }

    public void clear() {
        preferences.clear();
        isChanged = true;
    }


}
