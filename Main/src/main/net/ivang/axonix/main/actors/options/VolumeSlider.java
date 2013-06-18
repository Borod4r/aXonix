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

package net.ivang.axonix.main.actors.options;

import com.badlogic.gdx.scenes.scene2d.ui.Slider;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class VolumeSlider extends Slider {

    private Style style;

    public VolumeSlider(float min, float max, float stepSize, Style style) {
        super(min, max, stepSize, false, style);
    }

    @Override
    public float getPrefWidth () {
            return style.prefWidth;
    }

    public void setStyle(Style style) {
        this.style = style;
        if (style.prefHeight > 0) {
            style.background.setMinHeight(style.prefHeight);
            if (style.knobBefore != null) {
                style.knobBefore.setMinHeight(style.prefHeight);
            }
            if (style.knobAfter != null) {
                style.knobAfter.setMinHeight(style.prefHeight);
            }
        }
        super.setStyle(style);
    }

    @Override
    public void setStyle(SliderStyle style) {
        if (style instanceof Style) {
            setStyle((Style) style);
        } else {
            throw new IllegalArgumentException("It's confusing but argument should have type 'VolumeSlider.Style', not 'SliderStyle'");
        }
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    static public class Style extends SliderStyle {
        public float prefWidth;
        public float prefHeight;
    }
}
