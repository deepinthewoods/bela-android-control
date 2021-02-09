package ninja.trek.loopercontrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ninja.trek.loopercontrol.ninja.trek.loopercontrol.actors.AmountButton;

import static ninja.trek.loopercontrol.MainLooperControl.GRID_H;
import static ninja.trek.loopercontrol.MainLooperControl.GRID_W34;

public class DrumSetupUI {
    private static final int TRIGGER_SETTINGS_COUNT = 4;
    private static final String TAG = "drum setup ui";
    private final Table paneTable;
    private final ScrollPane pane;
    private final int DRUMS_COUNT = 31;//DrumUI.DRUM_SIGNAL_LABELS.length * DrumUI.DRUM_SIGNAL_LABELS[0].length;
    private final AmountButton[] triggerHumaniseAmountBtn;
    private final Window humaniseSettingsWindow;
    private final AmountButton humaniseVelMinus;
    private final AmountButton humaniseVelPlus;
    private final Window noteInputWindow;
    private final DrumNoteSelector pianoRoll;
    private final ShapeBatch shape;
    private final TextField channelSelector;
    private final IBluetoothHandler bluetooth;
    private Label[] triggerBtn;
    private final TextButton[] triggerSampleBtn;
    private final TextButton[] triggerHumaniseBtn;
    private final AmountButton[] triggerVelBtn;
    public Table table;
    private DrumSettingsData data;
    private int humaniseSettingsIndex;
    private int noteInputIndex;

    public DrumSetupUI(Skin skin, final Stage stage, ShapeBatch shape, IBluetoothHandler bluetooth) {
        this.bluetooth = bluetooth;
        this.shape = shape;
        data = new DrumSettingsData();
        table = new Table();
        table.setFillParent(true);


        paneTable = new Table();
        pane = new ScrollPane(paneTable, skin);
        pane.setupFadeScrollBars(0f, 0f);
        pane.setScrollBarPositions(true, false);
        pane.setOverscroll(false, false);
        pane.setFlingTime(0f);
        Gdx.app.log(TAG, "density" + Gdx.graphics.getDensity());
        pane.setFlickScrollTapSquareSize(Gdx.graphics.getDensity() * 160 * .5f);


        int screenWidth = Gdx.graphics.getWidth();

        String[] labels = {"id", "humanise", "",  "note", "vel"};
        float labelSize = 0;

        triggerBtn = new Label[DRUMS_COUNT];
        triggerSampleBtn = new TextButton[DRUMS_COUNT];
        triggerHumaniseBtn = new TextButton[DRUMS_COUNT];
        triggerVelBtn = new AmountButton[DRUMS_COUNT];;
        triggerHumaniseAmountBtn = new AmountButton[DRUMS_COUNT];
        for (int i = 0; i < DRUMS_COUNT; i++) {
            int page = i / DrumUI.DRUM_SIGNALS;
            int index = i % DrumUI.DRUM_SIGNALS;
            String text = DrumUI.DRUM_SIGNAL_LABELS[page][index];
            triggerBtn[i] = new Label(text==null?"":text, skin);
            triggerBtn[i].layout();
            labelSize = Math.max(labelSize, triggerBtn[i].getWidth());
        }

        for (int i = 0; i < labels.length; i++){
            Label lab = new Label(labels[i], skin);
            lab.layout();
            if (i == 0) {
                labelSize = Math.max(labelSize, lab.getWidth());
                table.add(lab).width(labelSize);
            } else {
                table.add(lab).width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT );
            }
        }
        table.row();
        table.add(pane).colspan(8).width(Gdx.graphics.getWidth());

        for (int i = 0; i < DRUMS_COUNT; i++){
            paneTable.add(triggerBtn[i]).left().width(labelSize).fillY().expandY();
            final int finalI = i;
            triggerHumaniseBtn[i] = new TextButton("set", skin);
            paneTable.add(triggerHumaniseBtn[i]).width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT ).fillY().expandY();
            triggerHumaniseBtn[i].addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    openHumaniseSettings(finalI, stage);
                }
            });

            triggerHumaniseAmountBtn[i] = new AmountButton(127, skin, "humanize amt");
            paneTable.add(triggerHumaniseAmountBtn[i]).width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT ).fillY().expandY();

            triggerSampleBtn[i] = new TextButton("", skin);
            paneTable.add(triggerSampleBtn[i]).width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT ).fillY().expandY();
            triggerSampleBtn[i].addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    openNoteInput(finalI, stage);
                    super.clicked(event, x, y);
                }
            });

            triggerVelBtn[i] = new AmountButton(127, skin, "");
            paneTable.add(triggerVelBtn[i]).width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT ).fillY().expandY();

            paneTable.row();
        }


        humaniseSettingsWindow = new Window("Humanise settings", skin);
        humaniseSettingsWindow.setModal(true);
        Actor spacer = new Actor();
        spacer.setSize(0, 100);
        humaniseVelMinus = new AmountButton(127, skin, "vel minus");
        humaniseVelPlus = new AmountButton(127, skin, "vel plus");
        humaniseSettingsWindow.getTitleTable().row();
        humaniseSettingsWindow.getTitleTable().add(spacer).row();

        humaniseSettingsWindow.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > humaniseSettingsWindow.getWidth() || y < 0 || y > humaniseSettingsWindow.getHeight()){
                    humaniseSettingsWindow.remove();
                    return true;
                }
                return false;
            }
        });

        noteInputWindow = new Window("select note", skin);
        noteInputWindow.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > noteInputWindow.getWidth() || y < 0 || y > noteInputWindow.getHeight()){
                    noteInputWindow.remove();
                    return true;
                }
                return false;
            }
        });
        noteInputWindow.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (event.getKeyCode() == Input.Keys.BACK || event.getKeyCode() == Input.Keys.ESCAPE)
                    noteInputWindow.remove();
                return super.keyDown(event, keycode);
            }
        });

        channelSelector = new TextField("0", skin);
        pianoRoll = new DrumNoteSelector(skin, shape, new Table());
        pianoRoll.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setDrumNote();
                super.clicked(event, x, y);
            }
        });

        //noteInputWindow.add(new Label("Channel:", skin));
        noteInputWindow.add(channelSelector).row();
        noteInputWindow.add(new ScrollPane(pianoRoll, skin)).height(Gdx.graphics.getHeight() * .8f).width(Gdx.graphics.getWidth() * .6f);
        noteInputWindow.setModal(true);

        settingsWindow = new Window("SETTINGS", skin);
        settingsWindow.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > settingsWindow.getWidth() || y < 0 || y > settingsWindow.getHeight()){
                    settingsWindow.remove();
                    return true;
                }
                return false;
            }
        });
        settingsWindow.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (event.getKeyCode() == Input.Keys.BACK || event.getKeyCode() == Input.Keys.ESCAPE)
                    settingsWindow.remove();
                return super.keyDown(event, keycode);
            }
        });
        settingsWindow.setModal(true);
    }
    Window settingsWindow;
    public Window settingWindow(int i){
        humaniseSettingsIndex = i;

        populateTable();
        int screenWidth = Gdx.graphics.getWidth();

        settingsWindow.clearChildren();
        settingsWindow.add(triggerBtn[i]).left().row();//.width(labelSize).fillY().expandY();
        //final int finalI = i;
        //triggerHumaniseBtn[i] = new TextButton("set", skin);
        //settingsWindow.add(triggerHumaniseBtn[i]).row();//.width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT ).fillY().expandY();



        settingsWindow.add(triggerVelBtn[i]).row();//.width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT ).fillY().expandY();
        settingsWindow.add(triggerSampleBtn[i]).row();//.width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT ).fillY().expandY();




        settingsWindow.add(triggerHumaniseAmountBtn[i]).row();//.width((screenWidth- labelSize) / TRIGGER_SETTINGS_COUNT ).fillY().expandY();
        settingsWindow.add(humaniseVelMinus).row();
        settingsWindow.add(humaniseVelPlus).row();

        settingsWindow.row();
        settingsWindow.pack();
        settingsWindow.setWidth(Gdx.graphics.getWidth() * 0.8f);
        settingsWindow.setPosition(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() - settingsWindow.getHeight() - 50, Align.bottomLeft);
        return settingsWindow;
    }

    private void setDrumNote() {
        data.sample[noteInputIndex] = pianoRoll.getNote();
        int channel = 0;
        try {
            channel = Integer.parseInt(channelSelector.getText());
        } catch (Exception ex){
            channelSelector.setText("0");
        }
        data.channel[noteInputIndex] = channel;
        triggerSampleBtn[noteInputIndex].setText("" + data.sample[noteInputIndex] + "(" + data.channel[noteInputIndex] + ")");
        //Gdx.app.log(TAG, "note set " + data.sample[noteInputIndex]  + "index " + noteInputIndex);
        bluetooth.queueSendNote(data, noteInputIndex);
    }

    private void openNoteInput(int index, Stage stage) {
        //Gdx.app.log(TAG, "open note input");
        noteInputIndex = index;
        noteInputWindow.setHeight(Gdx.graphics.getHeight());
        noteInputWindow.pack();
        stage.addActor(noteInputWindow);
        channelSelector.setText(""+data.channel[noteInputIndex]);
        pianoRoll.setSelected(data.sample[noteInputIndex]);
        noteInputWindow.setPosition(Gdx.graphics.getWidth()/2f - noteInputWindow.getWidth()/2, Gdx.graphics.getHeight()/2f - noteInputWindow.getHeight()/2f);
    }

    private void openHumaniseSettings(int index, Stage stage) {
        humaniseSettingsIndex = index;
        humaniseSettingsWindow.pack();
        stage.addActor(humaniseSettingsWindow);



        humaniseSettingsWindow.setPosition(Gdx.graphics.getWidth()/2f - humaniseSettingsWindow.getWidth()/2, Gdx.graphics.getHeight()/2f - humaniseSettingsWindow.getHeight()/2f);
        //humaniseVelPlus.updatePosition();
        //humaniseVelMinus.updatePosition();
    }

    public void populateTable(DrumSettingsData data) {
        this.data = data;
        for (int i = 0; i < DRUMS_COUNT; i++){
            //triggerBtn[i].;
            triggerSampleBtn[i].setText("" + data.sample[i] + "(" + data.channel[i] + ")");

            //triggerHumaniseBtn[i].setText("" + data.humanise[i]);

            triggerVelBtn[i].set(data.velocity[i]);


        }
    }
    public void populateTable(){
        populateTable(data);
    }

    public void addActors(Stage stage) {


    }

    public void clear() {
        table.clear();
    }

    public class DrumSettingsData {

        public int[] sample = new int[DRUMS_COUNT];
        public int[] channel = new int[DRUMS_COUNT];
        public int[] velocity = new int[DRUMS_COUNT];
        public DrumSettingsData(){
            for (int i = 0; i < sample.length; i++){
                sample[i] = i;
                velocity[i] = 127;
                channel[i] = 10;
            }
        }

    }
}
