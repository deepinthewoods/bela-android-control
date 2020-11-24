package ninja.trek.loopercontrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

class PianoRoll extends Table {
    private final ShapeBatch shape;
    public float scale = 1f;
    public float start;
    public int DEFAULT_NOTES = 12;
    public PianoRoll(Skin skin, final ShapeBatch shape){

        this.shape = shape;
        addListener(new ClickListener(){
            public static final String TAG = "Piano Roll";

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "clicked: " );
                super.clicked(event, x, y);
            }
        });

    }

    Vector2 v = new Vector2();
    @Override
    public void draw(Batch batch, float parentAlpha) {
        int total = (int)(DEFAULT_NOTES * scale);
        v.set(0, 0);
        localToStageCoordinates(v);
        float height = getHeight(), x = v.x, y = v.y;
        for (int i = 0; i < DEFAULT_NOTES; i++){
        }
        shape.queueDrawPianoKeys(x, y, total, height/total, getWidth());
        super.draw(batch, parentAlpha);
    }
}
