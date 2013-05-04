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

package net.ivang.axonix.main.screen.game.actor.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class AlertDialog extends Table {

    private Skin skin;
    private Style style;

    private Table window;
    private Label title;
    private Label levelScoreLabel, levelScoreValue;
    private Label totalScoreLabel, totalScoreValue;
    private DialogActionsGroup actionsGroup;

    public AlertDialog(String titleText, Skin skin, String styleName) {
        super();
        this.skin = skin;
        setFillParent(true);
        setVisible(false);
        setStyle(styleName);

        window = new Table();
        window.setBackground(style.background);
        window.pad(style.vPad, style.hPad, style.vPad, style.hPad);

        LabelStyle titleStyle= new LabelStyle(style.titleFont, style.titleColor);
        LabelStyle labelStyle= new LabelStyle(style.labelsValuesFont, style.labelsColor);
        LabelStyle valueStyle= new LabelStyle(style.labelsValuesFont, style.valuesColor);

        title = new Label(titleText, titleStyle);
        levelScoreLabel = new Label("Level Score: ", labelStyle);
        totalScoreLabel = new Label("Total Score: ", labelStyle);
        levelScoreValue = new Label(null, valueStyle);
        totalScoreValue = new Label(null, valueStyle);

        window.add(title).colspan(2);
        window.row();
        window.add(levelScoreLabel).right();
        window.add(levelScoreValue).left();
        window.row();
        window.add(totalScoreLabel).right();
        window.add(totalScoreValue).left();

        add(window);
        row();

        actionsGroup = new DialogActionsGroup(style.actionsGroupStyle);
        add(actionsGroup).center();
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setLevelScore(int score) {
        levelScoreValue.setText(Integer.toString(score));
    }

    public void setTotalScore(long score) {
        totalScoreValue.setText(Long.toString(score));
    }

    public void setScores(int levelScore, long totalScore) {
        setLevelScore(levelScore);
        setTotalScore(totalScore);
    }

    public void setStyle(String styleName) {
        setStyle(skin.get(styleName, Style.class));
    }

    public void setStyle(Style style) {
        if (style == null) throw new IllegalArgumentException("style cannot be null.");
        this.style = style;

        if (window != null) {
            window.setBackground(style.background);
            window.pad(style.vPad, style.hPad, style.vPad, style.hPad);
        }

        LabelStyle titleStyle= new LabelStyle(style.titleFont, style.titleColor);
        LabelStyle labelStyle= new LabelStyle(style.labelsValuesFont, style.labelsColor);
        LabelStyle valueStyle= new LabelStyle(style.labelsValuesFont, style.valuesColor);

        if (title != null) title.setStyle(titleStyle);
        if (levelScoreLabel != null) levelScoreLabel.setStyle(labelStyle);
        if (levelScoreValue != null) levelScoreValue.setStyle(valueStyle);
        if (totalScoreLabel != null) totalScoreLabel.setStyle(labelStyle);
        if (totalScoreValue != null) totalScoreValue.setStyle(valueStyle);

        if (actionsGroup != null) actionsGroup.setStyle(style.actionsGroupStyle);
    }

    public boolean addButtonListener(int whichButton , EventListener listener) {
        return actionsGroup.addButtonListener(whichButton, listener);
    }

    @Override
    protected void drawBackground (SpriteBatch batch, float parentAlpha) {
        if (style.stageBackground != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            Stage stage = getStage();
            Vector2 position = stageToLocalCoordinates(Vector2.tmp.set(0, 0));
            Vector2 size = stageToLocalCoordinates(Vector2.tmp2.set(stage.getWidth(), stage.getHeight()));
            style.stageBackground.draw(batch, getX() + position.x, getY() + position.y, getX() + size.x, getY() + size.y);
        }

        super.drawBackground(batch, parentAlpha);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    static public class Style {
        public Drawable background;
        public Drawable stageBackground;
        public float hPad, vPad;
        public BitmapFont titleFont;
        public BitmapFont labelsValuesFont;
        public Color titleColor = new Color(1, 1, 1, 1);
        public Color labelsColor = new Color(1, 1, 1, 1);
        public Color valuesColor = new Color(1, 1, 1, 1);
        public DialogActionsGroup.Style actionsGroupStyle;
    }

}
