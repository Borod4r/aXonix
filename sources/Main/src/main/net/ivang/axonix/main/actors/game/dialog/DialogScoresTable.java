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

package net.ivang.axonix.main.actors.game.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @author Ivan Gadzhega
 * @since 0.4
 */
public class DialogScoresTable extends Table {

    private Label levelScoreLabel, levelScoreValue;
    private Label totalScoreLabel, totalScoreValue;

    public DialogScoresTable(Style style) {
        levelScoreLabel = new Label("Level Score: ", style.label);
        totalScoreLabel = new Label("Total Score: ", style.label);
        levelScoreValue = new Label(null, style.value);
        totalScoreValue = new Label(null, style.value);

        add(levelScoreLabel).right();
        add(levelScoreValue).left();
        row();
        add(totalScoreLabel).right();
        add(totalScoreValue).left();
    }

    public void setStyle(Style style) {
        levelScoreLabel.setStyle(style.label);
        levelScoreValue.setStyle(style.value);
        totalScoreLabel.setStyle(style.label);
        totalScoreValue.setStyle(style.value);
    }

    public void setLevelScore(int score) {
        levelScoreValue.setText(Integer.toString(score));
    }

    public void setTotalScore(int score) {
        totalScoreValue.setText(Integer.toString(score));
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public static class Style {
        public Label.LabelStyle label;
        public Label.LabelStyle value;
    }
}
