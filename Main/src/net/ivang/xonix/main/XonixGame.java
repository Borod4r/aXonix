package net.ivang.xonix.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class XonixGame extends Game {

    private List<FileHandle> levelsFiles;

    @Override
    public void create() {
        // init list of levels files
        FileHandle dirHandle = Gdx.files.internal("data/levels");
        levelsFiles = Arrays.asList(dirHandle.list());

        GameScreen gameScreen = new GameScreen(this);
        gameScreen.setLevel(0);

        setScreen(gameScreen);
    }

    //---------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------


    public List<FileHandle> getLevelsFiles() {
        return levelsFiles;
    }

    public void setLevelsFiles(List<FileHandle> levelsFiles) {
        this.levelsFiles = levelsFiles;
    }

}
