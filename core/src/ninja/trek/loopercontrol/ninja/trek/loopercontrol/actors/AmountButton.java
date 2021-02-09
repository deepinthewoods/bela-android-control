package ninja.trek.loopercontrol.ninja.trek.loopercontrol.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import static ninja.trek.loopercontrol.MainLooperControl.GRID_H;
import static ninja.trek.loopercontrol.MainLooperControl.GRID_H34;

public class AmountButton extends Stack {

    public final Slider slider;
    private final Label label;

    public AmountButton(float max, Skin skin, String text) {
        super();
        slider = new Slider(0, max, 1, false, skin);
        slider.getStyle().knob.setMinHeight(GRID_H34);
        slider.getStyle().knobDown.setMinHeight(GRID_H34);
        slider.setColor(Color.WHITE);
        label = new Label(text, skin, "label");
        add(slider);


        add(label);

        label.setTouchable(Touchable.disabled);
    }

    @Override
    public void layout() {
        super.layout();
        updatePosition();
        label.layout();
        slider.layout();
        //label.setPosition(slider.getX(),  slider.getY());
    }

    public void set(int i) {
        slider.setValue(i);
    }
    static Vector2 v = new Vector2();
    public void updatePosition() {
        v.set(0, -slider.getHeight()/4 * 3);
        slider.localToStageCoordinates(v);

        //label.setPosition(0, v.y);
    }
}
