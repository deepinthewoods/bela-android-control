package ninja.trek.loopercontrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongArray;

import java.nio.charset.Charset;

public class DrumUI {
    public static final int DRUM_TRIGGERS = 32;
    public static final String[][] DRUM_SIGNAL_LABELS = new String[][]{
        {"K", "k", "S", "s", "H", "h"},
        {"C", "c", "R", "r", "", ""},
        {"T0", "T1", "T2", "T3", "", ""}
    };
    public static final int DRUM_SIGNALS = DRUM_SIGNAL_LABELS[0].length;
    private static final int MAX_DRUM_TRIGGGERS = 256;
    private static final String TAG = "Drum ui";
    private final DrumStateWidget[] drumStateWidgets = new DrumStateWidget[MAX_DRUM_TRIGGGERS];
    private final Actor exp;
    private Table drumTable;
    private ScrollPane drumPane;
    public Table table = new Table();
    private Table drumTriggerTable;
    private TextButton drumAddBtn;
    private DrumTriggerState[] drumTriggerStates = new DrumTriggerState[DRUM_TRIGGERS];
    private Table drumSelectTable;
    public static final int DRUMSTATE_COMMAND = 2;
    public static final int DRUMTEST_COMMAND = 3;

    private int currentDrumTriggerIndex;
    private ButtonGroup drumSelectGroup;
    private DrumResetWidget drumResetButton;
    private Label drumResetLabel;
    private DrumLinkWidget drumLinkButton;
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

    public DrumUI(Skin skin){
        for (int i = 0; i < DRUM_TRIGGERS; i++){
            drumTriggerStates[i] = new DrumTriggerState(i);
            drumTriggerStates[i].triggers.add(1 << i);
        }

        drumTable = new Table();
        initDrumTable(skin, this);
        for (int i = 0; i < drumStateWidgets.length; i++){
            drumStateWidgets[i] = new DrumStateWidget(i, skin, 0, this);
        }
        drumTable.setTransform(true);
        drumPane = new ScrollPane(drumTable, skin);
        drumPane.setupFadeScrollBars(0f, 0f);
        drumPane.setScrollBarPositions(true, false);
        drumPane.setOverscroll(false, false);
        drumPane.setFlingTime(0f);
        table.add(drumPane);
        exp = new Actor();

        for (int i = 0; i < 155; i++){
            char ch = (char)i;
            Gdx.app.log(TAG, "char " + ch + " int " + (int)ch);
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
            drumSelectTable.add(triggerSelect).width(Gdx.graphics.getWidth()/DRUM_TRIGGERS);
            drumSelectGroup.add(triggerSelect);
        }

       /* drumPageTable = new Table();
        TextButton drumPageL = new TextButton("<<", skin);
        TextButton drumPageR = new TextButton(">>", skin);
        drumPageL.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentDrumPage--;
                if (currentDrumPage < 0)
                    currentDrumPage = DRUM_SIGNAL_LABELS.length-1;
                populateTable(currentDrumTriggerIndex);
            }
        });
        drumPageR.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentDrumPage++;
                if (currentDrumPage >= DRUM_SIGNAL_LABELS.length)
                    currentDrumPage = 0;
                populateTable(currentDrumTriggerIndex);
            }
        });
        drumPageTable.add(drumPageL).fillX();
        drumPageTable.add(new Label("         ", skin)).expandX();
        drumPageTable.add(drumPageR);
*/
        drumResetButton = new DrumResetWidget(skin, 0, ui);
        drumResetLabel = new Label("Resets:", skin);
        drumLinkButton = new DrumLinkWidget(skin, 0, ui);
        drumLinkLabel = new Label("Link To:", skin);
        drumTriggerLabel = new Label("Trigger:", skin);
        drumTriggerPresetTable = new Table();

        presetLoad = new TextButton("Load", skin);
        presetSave = new TextButton("Save", skin);
        presetSaveAs = new TextButton("Save As", skin);
        presetLabel = new Label("Preset", skin);
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

        drumTriggerPresetTable.add(presetLoad);
        drumTriggerPresetTable.add(presetLabel);
        drumTriggerPresetTable.add(presetSave);
        drumTriggerPresetTable.add(presetSaveAs);

        drumSelectSpacer = new Label("", skin);
    }
    Table drumTableB = new Table();
    public void populateTable(final int triggerIndex) {
        currentDrumTriggerIndex = triggerIndex;
        //baseTable.clearChildren();
        //baseTable.add(drumTable);

        //Actor spacer = new Table();
        //drumTable.add(spacer).expand().fill();

        drumTable.clearChildren();
        drumTableB.clearChildren();
        //drumTable.add(exp).expand().fill();
        drumTableB.add(drumTriggerPresetTable).row();
        drumTableB.add(drumTriggerLabel).row();
        drumTableB.add(drumSelectTable).row();
        drumTableB.add(drumPageTable).row();
        drumTableB.add(drumSelectSpacer).row();
        drumTable.add(drumTableB).top().row();

        drumTriggerTable.clear();

        for (int p = 0; p < drumTriggerStates[triggerIndex].triggers.size; p++){
            long drumState = drumTriggerStates[triggerIndex].triggers.get(p);
            DrumStateWidget drumStateWidget = drumStateWidgets[p];
            drumStateWidget.set(drumState, currentDrumPage);
            drumTriggerTable.add(drumStateWidget);
            drumTriggerTable.row();
        }

        drumResetButton.set(drumTriggerStates[triggerIndex].reset);
        drumLinkButton.set(drumTriggerStates[triggerIndex].link);
        drumTriggerTable.add(drumAddBtn).row();
        drumTriggerTable.add(drumResetLabel).row();
        drumTriggerTable.add(drumResetButton).row();
        drumTriggerTable.add(drumLinkLabel).row();
        drumTriggerTable.add(drumLinkButton).row();
        drumTable.add(drumTriggerTable);
        drumPane.layout();
        drumPane.layout();
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

    private class DrumStateWidget extends Table{

        private final int drumSequenceIndex;
        private final TextButton[] drumBtns;

        public DrumStateWidget(int p, final Skin skin, int state, final DrumUI ui) {
            super();

            TextButton addBtn = new TextButton("+", skin);
            addBtn.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    ui.addDrumEntry(drumSequenceIndex, ui, skin);
                }
            });
            add(addBtn).width((Gdx.graphics.getWidth()/(DRUM_SIGNALS+1))/2);

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
                add(drumBtns[i]).width(Gdx.graphics.getWidth()/(DRUM_SIGNALS+1));
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
            add(subBtn).width((Gdx.graphics.getWidth()/(DRUM_SIGNALS+1))/2);
        }

        public void set(long drumState, int page) {
            //Gdx.app.log("drumwidget", "set " + drumState + " page" + page);

            for (int i = 0; i < drumBtns.length; i++){
                long bit = (drumState >>> (i + page*DRUM_SIGNALS)) & 1;
                if (bit == 1)
                    drumBtns[i].setChecked(true);
                else drumBtns[i].setChecked(false);
            }
            for (int i = 0; i < DRUM_SIGNAL_LABELS[page].length; i++){
                drumBtns[i].setText(DRUM_SIGNAL_LABELS[page][i]);
            }
        }
    }

    private class DrumResetWidget extends Table{

        private final TextButton[] drumBtns;

        public DrumResetWidget(Skin skin, int state, final DrumUI ui) {
            super();

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
                add(drumBtns[i]).width(Gdx.graphics.getWidth()/DRUM_TRIGGERS);
            }
            //Actor expander = new TextButton("   ", skin);
            //add(expander).expandX();


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

    private class DrumLinkWidget extends Table{

        private final TextButton[] drumBtns;

        public DrumLinkWidget(Skin skin, int state, final DrumUI ui) {
            super();

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
                add(drumBtns[i]).width(Gdx.graphics.getWidth()/DRUM_TRIGGERS);
            }
            Actor expander = new Actor();
            add(expander).expandX();


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
