package ninja.trek.loopercontrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.LinkedScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pools;

import java.nio.charset.Charset;

import static ninja.trek.loopercontrol.MainLooperControl.GRID_H;
import static ninja.trek.loopercontrol.MainLooperControl.GRID_W;
import static ninja.trek.loopercontrol.MainLooperControl.GRID_W1;
import static ninja.trek.loopercontrol.MainLooperControl.GRID_W4;

public class DrumUI {
    public static final int DRUM_TRIGGERS = 31;
    public static final String[][] DRUM_SIGNAL_LABELS = new String[][]{{
            "K", "k", "S", "s", "H", "h",
            "C", "c", "R", "r", "", "",
            "T0", "T1", "T2", "T3", "", ""
            , "", "", "", "", "", "", ""
            , "", "", "", "", "", "", "", "", ""
    }};
    public static final int DRUM_SIGNALS = 31;//DRUM_SIGNAL_LABELS[0].length;
    private static final int MAX_DRUM_TRIGGGERS = 256;
    private static final String TAG = "Drum ui";
    private final DrumStateWidget[] drumStateWidgets = new DrumStateWidget[MAX_DRUM_TRIGGGERS];
    private final Actor exp;
    private final LinkedScrollPane.ScrollPaneGroup drumStatesGroup;
    private ScrollPane drumPane;
    private Table drumTable;
    //private ScrollPane drumPane;
    public Table table = new Table();
    private Table drumTriggerTable;
    private TextButton drumAddBtn;
    private DrumTriggerState[] drumTriggerStates = new DrumTriggerState[DRUM_TRIGGERS];
    private Table drumSelectTable;
    public static final int DRUMSTATE_COMMAND = 2;
    public static final int DRUMTEST_COMMAND = 3;

    private int currentDrumTriggerIndex;
    private ButtonGroup drumSelectGroup;
    private DrumResetWidget drumResetWidget;
    private Label drumResetLabel;
    private DrumLinkWidget drumLinkWidget;
    private Label drumLinkLabel;
    private Label drumTriggerLabel;
    private Table drumTriggerPresetTable;
    private TextButton presetLoad;
    private TextButton presetSave;
    private TextButton presetSaveAs;
    private Label presetLabel;
    private Label drumSelectSpacer;
    private int currentDrumPage;
    private Table drumPageTable;
    private LinkedScrollPane drumSelectPane;
    private LinkedScrollPane.ScrollPaneGroup scrollPaneGroupA = new LinkedScrollPane.ScrollPaneGroup();
    private Label testLabel;
    private boolean hasScaledFonts = false;
    private DrumSetupUI setupUI;
    private DrumHandler drumHandler = new DrumHandler();
    public DrumUI(Skin skin, DrumSetupUI drumSetupUI) {
        setupUI = drumSetupUI;
        for (int i = 0; i < DRUM_TRIGGERS; i++) {
            drumTriggerStates[i] = new DrumTriggerState(i);
            drumTriggerStates[i].triggers.add(1 << i);
        }

        drumTable = new Table();
        drumStatesGroup = new LinkedScrollPane.ScrollPaneGroup();
        initDrumTable(skin, this);
        for (int i = 0; i < drumStateWidgets.length; i++) {
            drumStateWidgets[i] = new DrumStateWidget(i, skin, 0, this);
            drumStatesGroup.add(drumStateWidgets[i].getPane());
        }
        //drumTable.setTransform(true);
//        drumPane = new ScrollPane(drumTable, skin);
//        drumPane.setupFadeScrollBars(0f, 0f);
//        drumPane.setScrollBarPositions(true, false);
//        drumPane.setOverscroll(false, false);
//        drumPane.setFlingTime(0f);
//        table.add(drumPane);
        //drumPane = new ScrollPane(drumTable, skin);
        //drumTable.setTransform(true);
//        table.setWidth(MainLooperControl.GRID_W1);
//        table.add(drumPane);
        table.add(drumTable);
//        drumPane.setWidth(MainLooperControl.GRID_W1);

        exp = new Actor();

        for (int i = 0; i < 155; i++) {
            char ch = (char) i;
            Gdx.app.log(TAG, "char " + ch + " int " + (int) ch);
        }
    }
    private void initDrumTable(final Skin skin, final DrumUI ui) {

        drumTriggerTable = new Table();
        drumAddBtn = new TextButton("  +  ", skin);

        drumAddBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ui.addDrumEntry( ui, skin);
            }
        });

        drumSelectTable = new Table();
        drumSelectGroup = new ButtonGroup();

        for (int i = 0; i < DRUM_TRIGGERS; i++){
            final int in = i;
            TextButton triggerSelect = new TextButton(""+i, skin, "toggle");
            triggerSelect.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    populateTable(in);
                }
            });
            drumSelectTable.add(triggerSelect).size(GRID_W, GRID_H);//.width(Gdx.graphics.getWidth()/DRUM_TRIGGERS);
            drumSelectGroup.add(triggerSelect);
        }


        drumResetWidget = new DrumResetWidget(skin, 0, ui);

        drumResetLabel = new Label("Resets:", skin, "label");

        drumResetLabel.setTouchable(Touchable.disabled);
        drumResetLabel.setSize(GRID_W4, GRID_H);

        drumLinkWidget = new DrumLinkWidget(skin, 0, ui);
        drumLinkLabel = new Label("Link To:", skin, "label");
        drumLinkLabel.setTouchable(Touchable.disabled);
        drumLinkLabel.setSize(GRID_W4, GRID_H);

        drumTriggerLabel = new Label("Trigger:", skin, "label");
        drumTriggerLabel.setTouchable(Touchable.disabled);
        drumTriggerLabel.setSize(GRID_W4, GRID_H);
        drumTriggerPresetTable = new Table();

        presetLoad = new TextButton("Load", skin);
        presetSave = new TextButton("Save", skin);
        presetSaveAs = new TextButton("Save As", skin);
        presetLabel = new Label("Preset", skin, "label");

        presetLoad.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });
        presetSave.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });
        presetSaveAs.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });

        drumTriggerPresetTable.add(presetLoad).size(GRID_W4, GRID_H);//;
        drumTriggerPresetTable.add(presetLabel).size(GRID_W4, GRID_H);//;
        drumTriggerPresetTable.add(presetSave).size(GRID_W4, GRID_H);//;
        drumTriggerPresetTable.add(presetSaveAs).size(GRID_W4, GRID_H);//;

        drumSelectSpacer = new Label("", skin);

        drumSelectPane = new LinkedScrollPane(drumSelectTable, skin);
        scrollPaneGroupA.add(drumSelectPane);
        DelayedRemovalArray<EventListener> ls = drumResetWidget.getListeners();
        //ls.removeIndex(ls.size-1);


        testLabel = new Label("W", skin);



    }
    Table drumTableB = new Table();
    public void populateTable(final int triggerIndex) {

        if (!hasScaledFonts){
            hasScaledFonts = true;
            drumTable.add(testLabel);
            float scale = 20f;
            testLabel.getStyle().font.getData().setScale(scale);
            drumTable.invalidate();
            drumTable.layout();
            while (testLabel.getHeight() > GRID_H*1.0f){
                scale *= 0.9f;
                testLabel.getStyle().font.getData().setScale(scale);
                drumTable.invalidate();
                drumTable.layout();
                Gdx.app.log(TAG, "reducing" + scale);
            }
            testLabel.remove();
        }



        currentDrumTriggerIndex = triggerIndex;
        //baseTable.clearChildren();
        //baseTable.add(drumTable);

        //Actor spacer = new Table();
        //drumTable.add(spacer).expand().fill();

        drumTable.clearChildren();
        drumTableB.clearChildren();
        //drumTable.add(exp).expand().fill();
        drumTableB.add(drumTriggerPresetTable).row();
        //drumTableB.add(drumTriggerLabel).row();

        drumTableB.add(drumSelectPane).width(GRID_W1).row();
       //drumTableB.add(drumPageTable).row();
        drumTableB.add(drumSelectSpacer).row();
        drumTable.add(drumTableB).top().row();

        //drumTriggerTable.clear();
        drumTriggerTable.clearChildren();

        for (int p = 0; p < drumTriggerStates[triggerIndex].triggers.size; p++){
            long drumState = drumTriggerStates[triggerIndex].triggers.get(p);
            DrumStateWidget drumStateWidget = drumStateWidgets[p];
            drumStateWidget.set(drumState, currentDrumPage);
            drumTriggerTable.add(drumStateWidget);//;
            drumTriggerTable.row();
        }

        drumResetWidget.set(drumTriggerStates[triggerIndex].reset);
        drumLinkWidget.set(drumTriggerStates[triggerIndex].link);
        drumTriggerTable.add(drumAddBtn).size(GRID_W, GRID_H).row();

        //ScrollPane drumTriggerPane = new ScrollPane(drumTriggerTable);
        //drumTable.add(drumTriggerPane).row();
        drumTable.add(drumTriggerTable).row();
        drumTable.add(drumResetWidget).size(GRID_W1, GRID_H).row();
        drumTable.add(drumLinkWidget).size(GRID_W1, GRID_H).row();
//        drumTable.add(drumTriggerTable);
        //drumPane.layout();
        //drumPane.layout();


    }



    private void addDrumEntry(DrumUI ui, Skin skin) {
        addDrumEntry(drumTriggerStates[currentDrumTriggerIndex].triggers.size, ui, skin);
    }
    private void addDrumEntry(int drumSequenceIndex, DrumUI ui, Skin skin) {
        int triggerIndex = currentDrumTriggerIndex;
        //drumTriggerStates[triggerIndex].triggers.add(0);
        drumTriggerStates[triggerIndex].triggers.insert(drumSequenceIndex, 0);
        populateTable(triggerIndex);

    }
    private void deleteDrumEntry(int drumSequenceIndex, DrumUI ui, Skin skin) {
        drumTriggerStates[currentDrumTriggerIndex].triggers.removeIndex(drumSequenceIndex);
        populateTable(currentDrumTriggerIndex);
    }
    Vector2 v = new Vector2();
    public void addActors(Stage stage) {
        //if (drumTriggerLabel.hasParent()) throw new GdxRuntimeException("non null parent");
        //stage.addActor(drumTriggerLabel);
        //stage.addActor(drumResetLabel);
        //stage.addActor(drumLinkLabel);
        drumSelectTable.invalidate();
        stage.act();
        v.set(0, -drumSelectTable.getHeight()/4 * 3);
        drumSelectTable.localToStageCoordinates(v);
        drumTriggerLabel.setPosition(0, v.y);

//        v.set(0, -drumResetWidget.getHeight()/4 * 3);
//        drumResetWidget.localToStageCoordinates(v);
//        drumResetLabel.setPosition(0, v.y);

        v.set(0, -drumLinkWidget.getHeight()/4 * 3);
        drumLinkWidget.localToStageCoordinates(v);
        drumLinkLabel.setPosition(0, v.y);



    }

    public void clear() {
       // drumTriggerLabel.remove();
       // drumResetLabel.remove();
       // drumLinkLabel.remove();
    }

    public class DrumTriggerState{
        private final int index;
        public LongArray triggers = new LongArray();//bitmasks

        public int reset;
        public int link;

        public DrumTriggerState(int i) {
            index = i;
        }

        @Override
        public String toString() {
            String s = "";
            s += (char)DRUMTEST_COMMAND;
            int len = 0;

            for (int i = 120; i < 130; i++){
                s += (char)((byte)i);
                len++;
            }
            String ns = ""+(char)(len & 255);
            ns += (char)((len>>8) & 255);
            s = ns + s;
            return s;
        }
        //@Override
        public String toString2() {
            String s = "";
            s += (char)DRUMSTATE_COMMAND;
            int len = 0;
            s += (char)((reset)&255);
            s += (char)((reset>>>8)&255);
            s += (char)((reset>>>16)&255);
            s += (char)((reset>>>24)&255);
            len += 4;
            //s += " ";
            s += (char)((link)&255);
            s += (char)((link>>>8)&255);
            s += (char)((link>>>16)&255);
            s += (char)((link>>>24)&255);
            len += 4;

            s += (char)((index)&255);
            s += (char)((index>>>8)&255);
            s += (char)((index>>>16)&255);
            s += (char)((index>>>24)&255);
            len += 4;
            //s += " ";
            s += (char)((triggers.size)&255);
            s += (char)((triggers.size>>>8)&255);
            s += (char)((triggers.size>>>16)&255);
            s += (char)((triggers.size>>>24)&255);
            len += 4;


            //s += " ";

            for (int i = 0; i < triggers.size; i++){
                long v = triggers.get(i);
                s += (char)((v)&255);
                s += (char)((v>>>8)&255);
                s += (char)((v>>>16)&255);
                s += (char)((v>>>24)&255);
                len+= 4;
                //s += " ";
            }
            String ns = ""+(char)(len & 255);
            ns += (char)((len>>8) & 255);
            s = ns + s;
            String str = new String(s.getBytes(), Charset.forName("UTF-8"));
            return str;

        }

        public ByteArray toByteArray() {
            ByteArray s = Pools.obtain(ByteArray.class);
            s.add((byte)'m');
            s .add( (byte)DRUMSTATE_COMMAND);
            int len = 0;
            s .add( (byte)((reset)&255));
            s .add( (byte)((reset>>>8)&255));
            s .add( (byte)((reset>>>16)&255));
            s .add( (byte)((reset>>>24)&255));

            len += 4;
            //s += " ";
            s .add( (byte)((link)&255));
            s .add( (byte)((link>>>8)&255));
            s .add( (byte)((link>>>16)&255));
            s .add( (byte)((link>>>24)&255));
            len += 4;

            s .add( (byte)((index)&255));
            s .add( (byte)((index>>>8)&255));
            s .add( (byte)((index>>>16)&255));
            s .add( (byte)((index>>>24)&255));
            len += 4;
            //s += " ";
            s .add ((byte) ((triggers.size)&255));
            s .add ((byte) ((triggers.size>>>8)&255));
            s .add ((byte) ((triggers.size>>>16)&255));
            s .add ((byte) ((triggers.size>>>24)&255));
            len += 4;


            //s += " ";

            for (int i = 0; i < triggers.size; i++){
                long v = triggers.get(i);
                s .add( (byte)((v)&255));
                s .add( (byte)((v>>>8)&255));
                s .add( (byte)((v>>>16)&255));
                s .add( (byte)((v>>>24)&255));
                len+= 4;
                //s += " ";
            }
            s.insert(2, (byte)(len & 255));
            s.insert(3, (byte)((len>>8) & 255));

            return s;
        }
    }

    private void changeDrumState(int drumIndex, boolean isChacked, int drumSequenceIndex) {
        long val = drumTriggerStates[currentDrumTriggerIndex].triggers.get(drumSequenceIndex);
        int mask = 1 << (drumIndex + currentDrumPage*DRUM_SIGNALS);
        mask = ~mask ;
        val = val & mask;
        if (isChacked)
            val |= (1 << drumIndex + currentDrumPage*DRUM_SIGNALS);
        drumTriggerStates[currentDrumTriggerIndex].triggers.set(drumSequenceIndex, val);
    }

    private void changeDrumReset(int finalI, boolean isChacked) {
        int val = drumTriggerStates[currentDrumTriggerIndex].reset;
        int mask = 1 << (finalI + currentDrumPage*DRUM_SIGNALS);
        mask = ~mask ;
        val = val & mask;
        if (isChacked)
            val |= (1 << (finalI + currentDrumPage*DRUM_SIGNALS));
        drumTriggerStates[currentDrumTriggerIndex].reset = val;
    }

    private void changeDrumLink(int finalI, boolean isChacked) {
        int val = drumTriggerStates[currentDrumTriggerIndex].link;
        int mask = 1 << (finalI + currentDrumPage*DRUM_SIGNALS);
        mask = ~mask ;
        val = val & mask;
        if (isChacked)
            val |= (1 << (finalI) + currentDrumPage*DRUM_SIGNALS);
        drumTriggerStates[currentDrumTriggerIndex].link = val;
    }

    public DrumTriggerState[] getTriggerStates(){
        return drumTriggerStates;
    }

    private class DrumStateWidget extends Stack{

        private final int drumSequenceIndex;
        private final TextButton[] drumBtns;
        private final LinkedScrollPane pane;
        private final Label label;
        private Table mtable;
        public DrumStateWidget(int p, final Skin skin, int state, final DrumUI ui) {
            super();
            mtable = new Table();

            TextButton addBtn = new TextButton("+", skin);
            addBtn.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    ui.addDrumEntry(drumSequenceIndex, ui, skin);
                }
            });
            mtable.add(addBtn).size(GRID_W, GRID_H);//.width((Gdx.graphics.getWidth()/(DRUM_SIGNALS+1))/2);

            drumSequenceIndex = p;
            drumBtns = new TextButton[DRUM_SIGNALS];
            for (int i = 0; i < DRUM_SIGNALS; i++){
                drumBtns[i] = new TextButton("", skin, "toggle");
                final int finalI = i;
                drumBtns[i].addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        TextButton b = (TextButton) event.getListenerActor();
                        boolean isChacked = b.isChecked();
                        ui.changeDrumState(finalI, isChacked, drumSequenceIndex);
                    }
                });
                final int index = i;
                drumBtns[i].addListener(new ActorGestureListener(){
                    @Override
                    public boolean longPress(Actor actor, float x, float y) {
                        getStage().addActor(setupUI.settingWindow(index));
                        drumBtns[index].toggle();
                        return true;//super.longPress(actor, x, y);
                    }
                });

                mtable.add(drumBtns[i]).size(GRID_W, GRID_H);//.width(Gdx.graphics.getWidth()/(DRUM_SIGNALS+1));
            }
            Actor expander = new Label(" ", skin);
            //add(expander).expandX().fillX();
            TextButton subBtn = new TextButton("-", skin);
            subBtn.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    ui.deleteDrumEntry(drumSequenceIndex, ui, skin);
                }
            });
            mtable.add(subBtn).size(GRID_W, GRID_H);//.width((Gdx.graphics.getWidth()/(DRUM_SIGNALS+1))/2);

            pane = new LinkedScrollPane(mtable, skin);
            //pane.setWidth(GRID_W1);
            label = new Label("Drums", skin, "label"){
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    //setPosition(getX(), getY()-getHeight()/4);
                    super.draw(batch, parentAlpha);
                    //setPosition(getX(), getY()+getHeight()/4);
                }
            };
            label.setTouchable(Touchable.disabled);
            label.setAlignment(Align.bottomLeft);

            pane.setTouchable(Touchable.enabled);
            add(pane);
            addActor(label);


        }

        public void set(long drumState, int page) {
            //Gdx.app.log("drumwidget", "set " + drumState + " page" + page);

            for (int i = 0; i < drumBtns.length; i++){
                long bit = (drumState >>> (i + page*DRUM_SIGNALS)) & 1;
                if (bit == 1)
                    drumBtns[i].setChecked(true);
                else drumBtns[i].setChecked(false);
            }
            int i;
            for (i = 0; i < DRUM_SIGNALS; i++){
                drumBtns[i].setText(DRUM_SIGNAL_LABELS[page][i]);
            }

        }

        public LinkedScrollPane getPane() {
            return pane;
        }
    }

    private class DrumResetWidget extends Stack {

        private final TextButton[] drumBtns;
        private final Label label;
        private final LinkedScrollPane pane;
        private Table mtable;

        public DrumResetWidget(Skin skin, int state, final DrumUI ui) {
            super();
            mtable = new Table();
            drumBtns = new TextButton[DRUM_TRIGGERS];
            for (int i = 0; i < DRUM_TRIGGERS; i++){
                drumBtns[i] = new TextButton(""+i, skin, "toggle");
                final int finalI = i;
                drumBtns[i].addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        TextButton b = (TextButton) event.getListenerActor();
                        boolean isChacked = b.isChecked();
                        ui.changeDrumReset(finalI, isChacked);
                    }
                });
                mtable.add(drumBtns[i]).size(GRID_W, GRID_H);//.width(Gdx.graphics.getWidth()/DRUM_TRIGGERS);
            }
            //Actor expander = new TextButton("   ", skin);
            //add(expander).expandX();
            pane = new LinkedScrollPane(mtable, skin);
            scrollPaneGroupA.add(pane);
            //pane.setWidth(GRID_W1);
            label = new Label("Reset", skin, "label");
            label.setTouchable(Touchable.disabled);
            pane.setTouchable(Touchable.enabled);
            add(pane);
            add(label);
            //label.setFillParent(true);

        }

        public void set(int drumState) {
            Gdx.app.log("drumwidget", "set " + drumState);

            for (int i = 0; i < drumBtns.length; i++){
                int bit = (drumState >>> i) & 1;
                if (bit == 1)
                    drumBtns[i].setChecked(true);
                else drumBtns[i].setChecked(false);
            }
        }
    }

    private class DrumLinkWidget extends Stack{

        private final TextButton[] drumBtns;
        private final LinkedScrollPane pane;
        private final Label label;
        private Table table;
        public DrumLinkWidget(Skin skin, int state, final DrumUI ui) {
            super();
            table = new Table();
            drumBtns = new TextButton[DRUM_TRIGGERS];
            for (int i = 0; i < DRUM_TRIGGERS; i++){
                drumBtns[i] = new TextButton(""+i, skin, "toggle");
                final int finalI = i;
                drumBtns[i].addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        TextButton b = (TextButton) event.getListenerActor();
                        boolean isChacked = b.isChecked();
                        ui.changeDrumLink(finalI, isChacked);
                    }
                });
                table.add(drumBtns[i]).size(GRID_W, GRID_H);//.width(Gdx.graphics.getWidth()/DRUM_TRIGGERS);
            }
            Actor expander = new Actor();
            table.add(expander).expandX();

            pane = new LinkedScrollPane(table, skin);
            scrollPaneGroupA.add(pane);
            pane.setWidth(GRID_W1);
            label = new Label("Link", skin, "label");
            label.setTouchable(Touchable.disabled);
            pane.setTouchable(Touchable.enabled);
            add(pane);
            add(label);
        }

        public void set(int drumState) {
            Gdx.app.log("drumwidget", "set " + drumState);

            for (int i = 0; i < drumBtns.length; i++){
                int bit = (drumState >>> i) & 1;
                if (bit == 1)
                    drumBtns[i].setChecked(true);
                else drumBtns[i].setChecked(false);
            }
        }
    }
}
