package net.ivang.xonix.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Random;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Background extends Group {

    private final byte ACTORS_NUM = 50;
    private final int MAX_HEIGHT = 1080;

    private Random random;

    public Background(Skin skin) {
        this.random = new Random();
        // init flares
        TextureRegion texture = skin.getRegion("circular_flare");
        Color color = new Color(1, 1, 1, 0.2f);
        int x = -texture.getRegionWidth() - 1;
        int y = -texture.getRegionHeight() - 1;
        for (int i = 0; i < ACTORS_NUM; i++) {
            addActor(new BackgroundFlare(x, y, random.nextFloat(), 0, color, texture));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update(false);
    }

    public void update(boolean force) {
        float width = getStage().getWidth();
        float height = getStage().getHeight();
        float scaleCorrection = height / MAX_HEIGHT;
        for (Actor flare : getChildren()) {
            if (force || (flare.getX() > width) || (flare.getX() < -flare.getWidth())
                    || (flare.getY() < -flare.getHeight()) || (flare.getY() > height) ) {
                float x = width * random.nextFloat();
                float y = height * random.nextFloat();
                float angle = random.nextInt(360);
                Color color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.2f);
                if (force) {
                    float scale = random.nextFloat() * scaleCorrection;
                    ((BackgroundFlare) flare).update(x, y, angle, scale, color);
                } else {
                    ((BackgroundFlare) flare).update(x, y, angle, color);
                }

            }
        }
    }

}
