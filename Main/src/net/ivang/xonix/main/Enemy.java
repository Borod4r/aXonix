package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Enemy extends Actor {

    private Move moveDirection;
    private TextureRegion region;
    private ParticleEffect particleEffect;

    Enemy(float x, float y, Skin skin) {
        setX(x); setY(y);
        setWidth(1f);
        setHeight(1f);
        setOriginX(0.5f);
        setOriginY(0.5f);
        this.moveDirection = Move.getRandomDiagonal();
        this.region = skin.getRegion("circular_flare");
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("data/particles/enemy.p"), skin.getAtlas());
        particleEffect.setPosition(x, y);

    }

    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);

        float radius = getWidth() / 2;
        float deltaPx = deltaTime * 4f;

        float x = getX();
        float y = getY();

        Level level = (Level) getParent();

        switch (moveDirection) {
            case UP_LEFT:
                x -= deltaPx;
                y += deltaPx;
                if (level.getBlockState(x, y + radius) == Level.BS_BLUE) {
                    if (level.getBlockState(x - radius, y) == Level.BS_BLUE) {
                        moveDirection = Move.DOWN_RIGHT;
                    } else {
                        moveDirection = Move.DOWN_LEFT;
                    }
                } else if (level.getBlockState(x - radius, y) == Level.BS_BLUE) {
                    moveDirection = Move.UP_RIGHT;
                }
                break;
            case UP_RIGHT:
                x += deltaPx;
                y += deltaPx;
                if (level.getBlockState(x, y + radius) == Level.BS_BLUE) {
                    if (level.getBlockState(x + radius, y) == Level.BS_BLUE) {
                        moveDirection = Move.DOWN_LEFT;
                    } else {
                        moveDirection = Move.DOWN_RIGHT;
                    }
                } else if (level.getBlockState(x + radius, y)== Level.BS_BLUE) {
                    moveDirection = Move.UP_LEFT;
                }
                break;
            case DOWN_LEFT:
                x -= deltaPx;
                y -= deltaPx;
                if (level.getBlockState(x, y - radius) == Level.BS_BLUE) {
                    if (level.getBlockState(x - radius, y) == Level.BS_BLUE) {
                        moveDirection = Move.UP_RIGHT;
                    } else {
                        moveDirection = Move.UP_LEFT;
                    }
                } else if (level.getBlockState(x - radius, y) == Level.BS_BLUE) {
                    moveDirection = Move.DOWN_RIGHT;
                }
                break;
            case DOWN_RIGHT:
                x += deltaPx;
                y -= deltaPx;
                if (level.getBlockState(x, y - radius) == Level.BS_BLUE) {
                    if (level.getBlockState(x + radius, y) == Level.BS_BLUE) {
                        moveDirection = Move.UP_LEFT;
                    } else {
                        moveDirection = Move.UP_RIGHT;
                    }
                } else if (level.getBlockState(x + radius, y) == Level.BS_BLUE) {
                    moveDirection = Move.DOWN_LEFT;
                }
                break;
        }

        setX(x);
        setY(y);

        particleEffect.update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        //draw particles
        particleEffect.setPosition(getX(), getY());
        particleEffect.draw(batch);
        // draw texture
        batch.setColor(1, 0.2f, 0.1f, 1);
        batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getWidth(), getHeight());
    }

}
