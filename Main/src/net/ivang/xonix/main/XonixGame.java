package net.ivang.xonix.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class XonixGame extends Game {

    private GameScreen gameScreen;

    private List<Level> levels;

    @Override
    public void create() {
        // init levels list from files
        levels = new ArrayList<Level>();
        FileHandle dirHandle = Gdx.files.internal("data/levels");
        for (FileHandle entry: dirHandle.list()) {
            Pixmap pixmap = new Pixmap(entry);
            levels.add(new Level(pixmap));
        }

        gameScreen = new GameScreen(this);
        gameScreen.setLevel(0);

        setScreen(gameScreen);
    }

    @Override
    public void render () {
        //TODO: only for testing
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            getScreen().dispose();
            setScreen(new GameScreen(this));
        }
        super.render();
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }
}
