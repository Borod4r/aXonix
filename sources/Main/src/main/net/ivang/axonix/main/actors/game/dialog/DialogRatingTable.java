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

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;

import java.util.List;

/**
 * @author Ivan Gadzhega
 * @since 0.4
 */
public class DialogRatingTable extends Table {

    private Image[][] stars;

    public DialogRatingTable(Style style) {
        stars = new Image[3][2];
        for (int i = 0; i < 3; i++) {
            stars[i][0] = new Image(style.starOff);
            stars[i][1] = new Image(style.starOn);
            // set scaling
            stars[i][0].setScaling(Scaling.fit);
            stars[i][1].setScaling(Scaling.fit);
            // add to stack
            Stack stack = new Stack();
            stack.add(stars[i][0]);
            stack.add(stars[i][1]);
            add(stack);
        }
    }

    public void setRating(int stars) {
        for (int i = 0; i < this.stars.length; i++) {
            this.stars[i][0].getColor().a = 1;
            this.stars[i][1].getColor().a = 0;
            if (i < stars) {
                float delay = (i + 1) * 0.35f;
                this.stars[i][0].addAction(Actions.sequence(Actions.delay(delay), Actions.fadeOut(0.5f)));
                this.stars[i][1].addAction(Actions.sequence(Actions.delay(delay), Actions.fadeIn(0.5f)));
            }
        }
    }

    public void setStyle(Style style) {
        List<Cell> cells = getCells();
        for (int i = 0; i < cells.size(); i++) {
            Cell cell = cells.get(i);
            cell.pad(0, style.padding, 0, style.padding);
            float cellSize = (i % 2 == 0) ? style.sizeSmall : style.sizeBig;
            cell.width(cellSize).height(cellSize);
        }
        invalidate();
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public static class Style {
        public Drawable starOn, starOff;
        public float sizeSmall;
        public float sizeBig;
        public float padding;
    }
}
