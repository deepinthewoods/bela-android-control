package ninja.trek.loopercontrol;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.GdxRuntimeException;

class RecordStateButton extends TextButton {
    public final int index;
    public String enabledText = "null", disabledText = "null";
    public RecordStateButton(String txt, Skin skin, int index) {
        super(txt, skin);
        this.index = index;
        enabledText = "(" + txt + ")";
        disabledText = " " + txt + " ";

        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RecordStateButton btn = (RecordStateButton) event.getListenerActor();
                if (btn == null) throw new GdxRuntimeException("null btn");
                if (!btn.isChecked())btn.setText(disabledText);
                else btn.setText(enabledText);
            }
        });
    }
}
