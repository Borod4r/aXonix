package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Protagonist extends Actor {

    private float px, py;
    private float speed;
    private Move move;

    private Level level;

    private TextureRegion region;

    public Protagonist(float x, float y, Level level, Skin skin) {
        this.speed = 8;
        this.move = Move.IDLE;
        this.level = level;
        this.region = skin.getRegion("tile");
        setX(x); setY(y);
        setPx(x); setPy(y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        processKeys();
        updatePosition(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.setColor(1, 0, 0, 1);
        batch.draw(region, getX() - 0.5f, getY() - 0.5f, 1, 1);
    }

    //---------------------------------------------------------------------
    // Helper Methods
    //---------------------------------------------------------------------

    private void processKeys() {
        int dx = Gdx.input.getDeltaX();
        int dy = Gdx.input.getDeltaY();
        float diff = Math.abs(dx) - Math.abs(dy);

        boolean isDraggedDown = (Gdx.input.isTouched() && dy < 0 && diff < 0);
        boolean isDraggedUp = (Gdx.input.isTouched() && dy > 0 && diff <= 0);
        boolean isDraggedLeft = (Gdx.input.isTouched() && dx < 0 && diff > 0);
        boolean isDraggedRight = (Gdx.input.isTouched() && dx > 0 && diff >= 0);

        boolean onEarth = level.getBlockState(getX(), getY()) == Level.BS_EARTH;

        if((onEarth || move != Move.DOWN)
                && (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || isDraggedUp)) {
            move = Move.UP;
        }
        if((onEarth || move != Move.UP)
                && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || isDraggedDown)) {
            move = Move.DOWN;
        }
        if((onEarth || move != Move.RIGHT)
                && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || isDraggedLeft)) {
            move = Move.LEFT;
        }
        if((onEarth || move != Move.LEFT)
                && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || isDraggedRight)) {
            move = Move.RIGHT;
        }
    }

    private void updatePosition(float deltaTime) {
        float deltaPx = deltaTime * speed;
        float x = getX();
        float y = getY();

        switch (move) {
            case UP:
                if (y > 0.5) {
                    y -= deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case DOWN:
                if (y < level.getHeight() - 0.5) {
                    y += deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case LEFT:
                if (x > 0.5) {
                    x -= deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
            case RIGHT:
                if (x < level.getWidth() - 0.5) {
                    x += deltaPx;
                } else {
                    move = Move.IDLE;
                }
                break;
        }

        float step = 0.05f;
        switch (move) {
            case UP:
            case DOWN:
                float nx = x + 0.5f;
                float rx = Math.round(nx);
                if (rx > nx) {
                    x += step;
                } else if (rx < nx) {
                    if (rx - nx < step) {
                        x = rx - 0.5f; // round x for smoother movement
                    } else {
                        x -= step;
                    }
                }
                break;
            case RIGHT:
            case LEFT:
                float ny = y + 0.5f;
                float ry = Math.round(ny);
                if (ry > ny) {
                    y += step;
                } else if (ry < ny) {
                    if (ry - ny < step) {
                        y = ry - 0.5f; // round y for smoother movement
                    } else {
                        y -= step;
                    }
                }
                break;

        }

        // update previous coords
        if (move != Move.IDLE) {
            px = getX();
            py = getY();
        }

        setX(x); setY(y);
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public float getPx() {
        return px;
    }

    public void setPx(float px) {
        this.px = px;
    }

    public float getPy() {
        return py;
    }

    public void setPy(float py) {
        this.py = py;
    }
}
