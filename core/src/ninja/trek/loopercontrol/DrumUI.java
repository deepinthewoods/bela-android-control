package ninja.trek.loopercontrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.IntArray;

public class DrumUI {
    public static final int DRUM_TRIGGERS = 8;
    public static final String[][] DRUM_SIGNAL_LABELS = new String[][]{
        {"K", "k", "S", "s", "H", "h"},
        {"C", "c", "R", "r", "", ""},
        {"T0", "T1", "T2", "T3", "", ""}
    };
    public static final int DRUM_SIGNALS = DRUM_SIGNAL_LABELS[0].length;
    private static final int MAX_DRUM_TRIGGGERS = 256;
    private final DrumStateWidget[] drumStateWidgets = new DrumStateWidget[MAX_DRUM_TRIGGGERS];
    private Table drumTable;
    private ScrollPane drumPane;
    public Table table = new Table();
    private Table drumTriggerTable;
    private TextButton drumAddBtn;
    private DrumTriggerState[] drumTriggerStates = new DrumTriggerState[DRUM_TRIGGERS];
    private Table drumSelectTable;

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
            drumTriggerStates[i] = new DrumTriggerState();
            drumTriggerStates[i].triggers.add(1 << i);
        }

        drumTable = new Table();
        initDrumTable(skin, this);
        for (int i = 0; i < drumStateWidgets.length; i++){
            drumStateWidgets[i] = new DrumStateWidget(i, skin, 0, this);
        }
        drumTable.setTransform(true);
        drumPane = new ScrollPane(drumTable, skin);
        drumPane.setScrollbarsVisible(false);
        drumPane.setScrollbarsOnTop(false);
        //drumPane.setTouchable(Touchable.enabled);
        table.add(drumPane);
        //table.setTouchable(Touchable.enabled);
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
                    populateTable(in, ui, skin);
                }
            });
            drumSelectTable.add(triggerSelect).width(Gdx.graphics.getWidth()/DRUM_TRIGGERS);
            drumSelectGroup.add(triggerSelect);
        }

        drumPageTable = new Table();
        TextButton drumPageL = new TextButton("<<", skin);
        TextButton drumPageR = new TextButton(">>", skin);
        drumPageL.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentDrumPage--;
                if (currentDrumPage < 0)
                    currentDrumPage = DRUM_SIGNAL_LABELS.length-1;
                populateTable(currentDrumTriggerIndex, ui, skin);
            }
        });
        drumPageR.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentDrumPage++;
                if (currentDrumPage >= DRUM_SIGNAL_LABELS.length)
                    currentDrumPage = 0;
                populateTable(currentDrumTriggerIndex, ui, skin);
            }
        });
        drumPageTable.add(drumPageL).fillX();
        drumPageTable.add(new Label("         ", skin)).expandX();
        drumPageTable.add(drumPageR);

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

    public void populateTable(final int triggerIndex, final DrumUI ui, Skin skin) {
        currentDrumTriggerIndex = triggerIndex;
        //baseTable.clearChildren();
        //baseTable.add(drumTable);
        drumTable.clearChildren();
        drumTable.add(drumTriggerPresetTable).row();
        drumTable.add(drumTriggerLabel).row();
        drumTable.add(drumSelectTable).row();
        drumTable.add(drumPageTable).row();
        drumTable.add(drumSelectSpacer).row();

        drumTriggerTable.clear();

        drumTable.add(drumTriggerTable);

        for (int p = 0; p < drumTriggerStates[triggerIndex].triggers.size; p++){
            int drumState = drumTriggerStates[triggerIndex].triggers.get(p);
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
        populateTable(triggerIndex, ui, skin);

    }
    private void deleteDrumEntry(int drumSequenceIndex, DrumUI ui, Skin skin) {
        drumTriggerStates[currentDrumTriggerIndex].triggers.removeIndex(drumSequenceIndex);
        populateTable(currentDrumTriggerIndex, ui, skin);
    }

    public class DrumTriggerState{
        public IntArray triggers = new IntArray();//bitmasks
        public int currentIndex;
        public int reset;
        public int link;
    }

    private void changeDrumState(int drumIndex, boolean isChacked, int drumSequenceIndex) {
        int val = drumTriggerStates[currentDrumTriggerIndex].triggers.get(drumSequenceIndex);
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

        public void set(int drumState, int page) {
            Gdx.app.log("drumwidget", "set " + drumState);

            for (int i = 0; i < drumBtns.length; i++){
                int bit = (drumState >> (i + page*DRUM_SIGNALS)) & 1;
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
                int bit = (drumState >> i) & 1;
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
                int bit = (drumState >> i) & 1;
                if (bit == 1)
                    drumBtns[i].setChecked(true);
                else drumBtns[i].setChecked(false);
            }
        }
    }
}
