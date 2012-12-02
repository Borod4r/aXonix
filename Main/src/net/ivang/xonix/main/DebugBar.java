package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class DebugBar extends Table {

    private Label fpsLabel;
    private Label sizeLabel;

    public DebugBar(Skin skin) {
        this.setFillParent(true);
        this.right().top();

        fpsLabel = new Label(null, skin);
        sizeLabel = new Label(null, skin);

        add(fpsLabel).padRight(5);
        add(sizeLabel).padRight(5);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        String fps = Integer.toString(Gdx.graphics.getFramesPerSecond());
        String width = Integer.toString(Gdx.graphics.getWidth());
        String height = Integer.toString(Gdx.graphics.getHeight());
        fpsLabel.setText(fps + "fps");
        sizeLabel.setText(width + "x" + height);
    }

}
