package net.ivang.xonix.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class BackgroundFlare extends Actor {

    private final static int VELOCITY_MULTIPLIER = 50;

    private TextureRegion texture;

    private float velocityX;
    private float velocityY;
    private Color color;
    private float scale;

    public BackgroundFlare(float x, float y, float scale, float angle, Color color, TextureRegion texture) {
        this.texture = texture;
        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth()/2, getHeight()/2);
        this.scale = (scale < 0.1f) ? 0.1f : scale;
        update(x, y, angle, color);
    }

    public void update(float x, float y, float angle, Color color) {
        setX(x);
        setY(y);
        setScale(0);
        velocityX = MathUtils.cosDeg(angle) * scale * VELOCITY_MULTIPLIER;
        velocityY = MathUtils.sinDeg(angle) * scale * VELOCITY_MULTIPLIER;
        this.color = color;
    }

    public void update(float x, float y, float angle, float scale, Color color) {
        this.scale = (scale < 0.1f) ? 0.1f : scale;
        update(x, y, angle, color);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setX(getX() + velocityX * delta);
        setY(getY() + velocityY * delta);
        if (getScaleX() < scale) {
            setScale(getScaleX() + 0.01f);
        }

    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.setColor(color);
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

}
