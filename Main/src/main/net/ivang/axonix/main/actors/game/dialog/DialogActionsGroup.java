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

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class DialogActionsGroup extends Table {

    private ImageButton button1;
    private ImageButton button2;
    private ImageButton button3;
    private Style style;

    public DialogActionsGroup(Style style) {
        this.style = style;

        button1 = new ImageButton(style.button1);
        button2 = new ImageButton(style.button2);
        button3 = new ImageButton(style.button3);

        add(button1);
        add(button2);
        add(button3);

        updateButtonCells();
    }

    public void setStyle(Style style) {
        if (style != null) {
            this.style = style;

            button1.setStyle(style.button1);
            button2.setStyle(style.button2);
            button3.setStyle(style.button3);

            updateButtonCells();
        } else {
            throw new IllegalArgumentException("style cannot be null.");
        }
    }

    public boolean addButtonListener(int whichButton , EventListener listener) {
        switch (whichButton) {
            case 1:
                return button1.addListener(listener);
            case 2:
                return button2.addListener(listener);
            case 3:
                return button3.addListener(listener);
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void updateButtonCells() {
        for (Cell cell : getCells()) {
            cell.padLeft(style.padding).padRight(style.padding).padTop(style.padding);
            cell.width(style.buttonSize).height(style.buttonSize);
        }
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public ImageButton getButton1() {
        return button1;
    }

    public ImageButton getButton2() {
        return button2;
    }

    public ImageButton getButton3() {
        return button3;
    }


    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    static public class Style {
        public ImageButton.ImageButtonStyle button1;
        public ImageButton.ImageButtonStyle button2;
        public ImageButton.ImageButtonStyle button3;
        public float buttonSize;
        public float padding;
    }
}
