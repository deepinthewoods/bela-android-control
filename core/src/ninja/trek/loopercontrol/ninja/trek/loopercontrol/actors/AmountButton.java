package ninja.trek.loopercontrol.ninja.trek.loopercontrol.actors;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

public class AmountButton extends Slider {

    public AmountButton(float max,  Skin skin) {

        super(0, max, 1, false, skin);
    }

    public void set(int i) {
        setValue(i);
    }
}
