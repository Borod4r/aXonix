package net.ivang.xonix.main;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Notification extends Label {

    private GameScreen gameScreen;

    public Notification(CharSequence text, GameScreen gameScreen, Skin skin) {
        super(text, skin);
        setVisible(false);
        setFillParent(true);
        setAlignment(Align.center);
        this.gameScreen = gameScreen;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        switch (gameScreen.getState()) {
            case PAUSED:
                setText("PAUSE");
                setVisible(true);
                break;
            case LEVEL_COMPLETED:
                setText("LEVEL COMPLETED");
                setVisible(true);
                break;
            case GAME_OVER:
                setText("GAME OVER");
                setVisible(true);
                break;
            case WIN:
                setText("WIN!!!");
                setVisible(true);
                break;
            default:
                if (getActions().size == 0) {
                    setVisible(false);
                }
                break;
        }
    }

}
