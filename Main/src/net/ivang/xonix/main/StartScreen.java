package net.ivang.xonix.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class StartScreen implements Screen {

    private XonixGame game;
    private Stage stage;

    public StartScreen(final XonixGame game) {
        stage = new Stage();
        /* atlas and skin */
        TextureAtlas atlas = new TextureAtlas("data/atlas/start_screen.atlas");
        Skin skin = new Skin(Gdx.files.internal("data/skin/start_screen.json"), atlas);
        /* logo */
        Image logo = new Image(skin, "logo");
        /* start button */
        Button startButton = new TextButton("Start", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen gameScreen = new GameScreen(game);
                gameScreen.setLevel(0);
                game.setScreen(gameScreen);
            }
        });
        /* options button */
        Button optionsButton = new TextButton("Options", skin);
        /* root table */
        Table rootTable = new Table();
//        rootTable.debug();
        rootTable.setFillParent(true);
        rootTable.add(logo);
        rootTable.row();
        rootTable.add(startButton);
        rootTable.row();
        rootTable.add(optionsButton).pad(10);
        stage.addActor(rootTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
