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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public abstract class AlertDialog extends Table {

    private Style style;

    private Table window;
    private Label title;
    private Label levelScoreLabel, levelScoreValue;
    private Label totalScoreLabel, totalScoreValue;
    private DialogActionsGroup actionsGroup;

    public AlertDialog(String titleText, Style style) {
        this.style = style;

        setFillParent(true);
        setVisible(false);

        window = new Table();
        window.setBackground(style.background);
        window.pad(style.vPad, style.hPad, style.vPad, style.hPad);

        title = new Label(titleText, style.title);
        levelScoreLabel = new Label("Level Score: ", style.label);
        totalScoreLabel = new Label("Total Score: ", style.label);
        levelScoreValue = new Label(null, style.value);
        totalScoreValue = new Label(null, style.value);

        window.add(title).colspan(2);
        window.row();
        window.add(levelScoreLabel).right();
        window.add(levelScoreValue).left();
        window.row();
        window.add(totalScoreLabel).right();
        window.add(totalScoreValue).left();

        add(window);
        row();

        actionsGroup = new DialogActionsGroup(style.actionsGroup);
        add(actionsGroup).center();
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setLevelScore(int score) {
        levelScoreValue.setText(Integer.toString(score));
    }

    public void setTotalScore(int score) {
        totalScoreValue.setText(Integer.toString(score));
    }

    public void setStyle(Style style) {
        this.style = style;

        window.setBackground(style.background);
        window.pad(style.vPad, style.hPad, style.vPad, style.hPad);

        title.setStyle(style.title);
        levelScoreLabel.setStyle(style.label);
        levelScoreValue.setStyle(style.value);
        totalScoreLabel.setStyle(style.label);
        totalScoreValue.setStyle(style.value);

        actionsGroup.setStyle(style.actionsGroup);
    }

    public boolean addButtonListener(int whichButton , EventListener listener) {
        return actionsGroup.addButtonListener(whichButton, listener);
    }

    @Override
    protected void drawBackground (SpriteBatch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        Stage stage = getStage();
        Vector2 position = stageToLocalCoordinates(Vector2.tmp.set(0, 0));
        Vector2 size = stageToLocalCoordinates(Vector2.tmp2.set(stage.getWidth(), stage.getHeight()));
        style.stageBackground.draw(batch, getX() + position.x, getY() + position.y, getX() + size.x, getY() + size.y);

        super.drawBackground(batch, parentAlpha);
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------


    public Label getTitle() {
        return title;
    }

    public DialogActionsGroup getActionsGroup() {
        return actionsGroup;
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public static class Style {
        public Drawable background;
        public Drawable stageBackground;
        public float hPad, vPad;
        public LabelStyle title;
        public LabelStyle label;
        public LabelStyle value;
        public DialogActionsGroup.Style actionsGroup;
    }

}
