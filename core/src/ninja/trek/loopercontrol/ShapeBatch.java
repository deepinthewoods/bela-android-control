package ninja.trek.loopercontrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

class ShapeBatch extends ShapeRenderer{
    private static final String TAG = "shape batch";
    Array<LoopsUI.LoopElement> q = new Array<LoopsUI.LoopElement>();
    Array<PianoKeyData> pianoKeyQ = new Array<PianoKeyData>();
    Vector2 min = new Vector2(), max = new Vector2();
    Color[] colors = {Color.PURPLE, Color.CYAN};
    public void draw(){
        begin(ShapeType.Filled);
        for (int i = 0; i < q.size; i+=1){
            int colorIndex = i + i/2;
            colorIndex %= 2;
            setColor(colors[colorIndex]);
            LoopsUI.LoopElement element = q.get(i);
            min.set(0, 0);
            max.set(element.getWidth(),element. getHeight());
            element.localToStageCoordinates(min);
            element.localToStageCoordinates(max);
            float w = max.x - min.x;
            float h = max.y - min.y;
            rect(min.x, min.y, w, h);
        }

        setColor(Color.GREEN);
        while (pianoKeyQ.size > 0){
            PianoKeyData data = pianoKeyQ.pop();
            for (int i = 0; i < data.total; i++){
                rect(data.x, data.y + data.h * i, data.w, data.h);
                Gdx.app.log(TAG, "draw piano " + data.total);
            }
            Pools.free(data);
        }
        end();
        q.clear();
    }

    public void queueDraw(LoopsUI.LoopElement e) {
        q.add(e);
    }

    public void queueDrawPianoKeys(float x, float y, int total, float height, float width) {
        PianoKeyData data = Pools.obtain(PianoKeyData.class);
        data.x = x;
        data.y = y;
        data.h = height;
        data.w = width;
        data.total = total;
        pianoKeyQ.add(data);
    }

    private static class PianoKeyData{
        public float x, y, h, w;
        public int total;
    }
}
