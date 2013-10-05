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

package net.ivang.axonix.main.actors.levels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.eventbus.EventBus;
import net.ivang.axonix.main.events.facts.ButtonClickFact;
import net.ivang.axonix.main.events.intents.screen.GameScreenIntent;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class LevelButton extends TextButton {

    private final int levelIndex;
    private Image[] starsArray;
    private Cell starsCell;
    private Style style;

    public LevelButton(final int levelIndex, Style style, final EventBus eventBus) {
        super(Integer.toString(levelIndex), style.buttonStyle);
        this.levelIndex = levelIndex;
        this.starsArray = new Image[3];
        this.style = style;

        row();

        Table starTable = new Table();
        starTable.pad(0, 2, 0, 2);

        for (int i = 0; i < 3; i++) {
            starsArray[i] = new Image();
            starsArray[i].setScaling(Scaling.fit);
            starTable.add(starsArray[i]);
        }

        starsCell = add(starTable);

        addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new ButtonClickFact());
                eventBus.post(new GameScreenIntent(levelIndex));
            }
        });
    }

    public void setRating(int stars) {
        for (int i = 0; i < starsArray.length; i++) {
            Drawable star = (i < stars) ? style.starOn : style.starOff;
            starsArray[i].setDrawable(star);
        }
    }

    public void setStyle(Style style) {
        starsCell.height(style.starsHeight);
        super.setStyle(style.buttonStyle);
        this.style = style;
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public int getLevelIndex() {
        return levelIndex;
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    public static class Style {
        public TextButtonStyle buttonStyle;
        public Drawable starOn, starOff;
        public float starsHeight;
    }
}
