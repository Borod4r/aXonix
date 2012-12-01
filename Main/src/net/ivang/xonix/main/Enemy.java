package net.ivang.xonix.main;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Enemy extends Actor {

    private TextureRegion region;
    private Move moveDirection;

    Enemy(float x, float y, Skin skin) {
        setX(x); setY(y);
        this.region = skin.getRegion("bomb");
        this.moveDirection = Move.getRandomDiagonal();
    }

    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);

        float radius = 0.75f;
        float deltaPx = deltaTime * 4f;

        float x = getX();
        float y = getY();

        Level level = (Level) getParent();

        switch (moveDirection) {
            case UP_LEFT:
                x -= deltaPx;
                y += deltaPx;
                if (level.getBlockState(x, y + radius) == Level.BS_EARTH) {
                    if (level.getBlockState(x - radius, y) == Level.BS_EARTH) {
                        moveDirection = Move.DOWN_RIGHT;
                    } else {
                        moveDirection = Move.DOWN_LEFT;
                    }
                } else if (level.getBlockState(x - radius, y) == Level.BS_EARTH) {
                    moveDirection = Move.UP_RIGHT;
                }
                break;
            case UP_RIGHT:
                x += deltaPx;
                y += deltaPx;
                if (level.getBlockState(x, y + radius) == Level.BS_EARTH) {
                    if (level.getBlockState(x + radius, y) == Level.BS_EARTH) {
                        moveDirection = Move.DOWN_LEFT;
                    } else {
                        moveDirection = Move.DOWN_RIGHT;
                    }
                } else if (level.getBlockState(x + radius, y)== Level.BS_EARTH) {
                    moveDirection = Move.UP_LEFT;
                }
                break;
            case DOWN_LEFT:
                x -= deltaPx;
                y -= deltaPx;
                if (level.getBlockState(x, y - radius) == Level.BS_EARTH) {
                    if (level.getBlockState(x - radius, y) == Level.BS_EARTH) {
                        moveDirection = Move.UP_RIGHT;
                    } else {
                        moveDirection = Move.UP_LEFT;
                    }
                } else if (level.getBlockState(x - radius, y) == Level.BS_EARTH) {
                    moveDirection = Move.DOWN_RIGHT;
                }
                break;
            case DOWN_RIGHT:
                x += deltaPx;
                y -= deltaPx;
                if (level.getBlockState(x, y - radius) == Level.BS_EARTH) {
                    if (level.getBlockState(x + radius, y) == Level.BS_EARTH) {
                        moveDirection = Move.UP_LEFT;
                    } else {
                        moveDirection = Move.UP_RIGHT;
                    }
                } else if (level.getBlockState(x + radius, y) == Level.BS_EARTH) {
                    moveDirection = Move.DOWN_LEFT;
                }
                break;
        }

        setX(x);
        setY(y);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.setColor(1, 1, 1, 1);
        batch.draw(region, getX() - 0.75f, getY() - 0.75f, 1.5f, 1.5f);
    }

}
