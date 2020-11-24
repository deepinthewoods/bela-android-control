package ninja.trek.loopercontrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

class LoopsUI {
    private static final int LOOPS_PER_PAGE = 10;
    private final ShapeBatch shape;
    private final Table loopTable;
    public Table table;
    public Array<LoopElement> loops = new Array<LoopElement>();

    public LoopsUI(Skin skin, ShapeBatch shape) {
        table = new Table();
        loopTable = new Table();

        this.shape = shape;
        ScrollPane loopPane = new ScrollPane(loopTable);
        table.add(loopPane);

        for (int i = 0; i < 3; i++){
            loops.add(new LoopElement(skin, shape));
        }
        loopTable.setTransform(true);
    }



    public void populateTable(int page) {
        loopTable.clear();
        for (int i = 0; i < LOOPS_PER_PAGE && i < loops.size; i++){
            loopTable.add(loops.get(i)).expand().fill();
            if (i%2 == 1)
                loopTable.row();
        }
    }

    public static class LoopElement extends TextButton {
        private final ShapeBatch shape;

        public LoopElement(Skin skin, ShapeBatch shape) {
            super("OO", skin);
            this.shape = shape;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {

            shape.queueDraw(this);
            //super.draw(batch, parentAlpha);
        }
    }
}
