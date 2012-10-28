package net.ivang.xonix.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class XonixGame extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }

    @Override
    public void render () {
        // TODO: only for testing
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            getScreen().dispose();
            setScreen(new GameScreen(this));
        }
        super.render();
    }

}
