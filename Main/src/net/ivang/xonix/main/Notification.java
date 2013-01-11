package net.ivang.xonix.main;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class Notification extends Label {

    private GameScreen gameScreen;
    private Skin skin;

    public Notification(CharSequence text, GameScreen gameScreen, Skin skin, String fontName) {
        super(text, skin, fontName, "white");
        setVisible(false);
        setFillParent(true);
        setAlignment(Align.center);
        this.gameScreen = gameScreen;
        this.skin = skin;
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

    public void setFont(String fontName) {
        setFont(skin.getFont(fontName));
    }

    public void setFont(BitmapFont font) {
        setStyle(new Label.LabelStyle(font, skin.getColor("white")));
    }

}
