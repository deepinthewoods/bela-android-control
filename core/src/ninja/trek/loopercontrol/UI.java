package ninja.trek.loopercontrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

class UI {

    public static final int INPUT_TRACKS = 6;
    public static final int TOP_BUTTONS = 5;


    private final Table baseTable;
    private final Skin skin;
    public final DrumUI drum;
    public final LoopsUI loops;
    private final IBluetoothHandler bluetooth;
    private final DrumSetupUI drumSetup;
    private TextButton mixerBtn;
    private TextButton drumBtn;
    private TextButton looperBtn;
    private Table bottomTable;
    private Table topTable;
    private Table topButtonsTable;
    private Table bottomButtonsTable;
    private TextButton sendBtn;
    private TextButton drumSetupBtn;

    public UI(Skin skin, ShapeBatch shape, IBluetoothHandler bluetooth, Stage stage){
        baseTable = new Table();
        baseTable.setFillParent(true);
        this.skin = skin;
        populateTable(skin);
        drum = new DrumUI(skin);
        loops = new LoopsUI(skin, shape);
        drumSetup = new DrumSetupUI(skin, stage, shape, bluetooth);
        this.bluetooth = bluetooth;
    }

    private void populateTable(final Skin skin) {
       // skin.getFont("default").getData().setScale(2.5f);
        Table expanderTable = new Table();
        mixerBtn = new TextButton("Mix", skin);

        final UI ui = this;
        drumBtn = new TextButton("Drum", skin);
        drumBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                topTable.clearChildren();
                drum.populateTable(0);
                topTable.add(drum.table).colspan(10);
            }
        });

        drumSetupBtn = new TextButton("D sett", skin);
        drumSetupBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                topTable.clearChildren();
                drumSetup.populateTable();
                topTable.add(drumSetup.table).colspan(10);
            }
        });
        looperBtn = new TextButton("Loop", skin);
        looperBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                topTable.clearChildren();
                loops.populateTable(0);
                topTable.add(loops.table).colspan(10);
            }
        });

        sendBtn = new TextButton("Send", skin);
        sendBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                bluetooth.queueSendAll();
            }
        });
        topButtonsTable = new Table();
        topButtonsTable.add(looperBtn).width(Gdx.graphics.getWidth()/TOP_BUTTONS);
        topButtonsTable.add(mixerBtn).width(Gdx.graphics.getWidth()/TOP_BUTTONS);
        topButtonsTable.add(drumBtn).width(Gdx.graphics.getWidth()/TOP_BUTTONS);
        topButtonsTable.add(drumSetupBtn).width(Gdx.graphics.getWidth()/TOP_BUTTONS);
        topButtonsTable.add(sendBtn).width(Gdx.graphics.getWidth()/TOP_BUTTONS);

        baseTable.add(topButtonsTable).colspan(10);

        topTable = new Table();
        bottomTable = new Table();
        baseTable.row();
        baseTable.add(topTable).top();
        baseTable.row();
        baseTable.add(expanderTable).expand();
        baseTable.row();
        baseTable.add(bottomTable);
        baseTable.row();
        bottomButtonsTable = new Table();

        for (int i = 0; i < INPUT_TRACKS; i++){

            TextButton button = new RecordStateButton(""+i, skin, i);

            button.setTouchable(Touchable.enabled);
            bottomButtonsTable.add(button).width(Gdx.graphics.getWidth()/INPUT_TRACKS);
        }
        baseTable.add(bottomButtonsTable).colspan(10);
        baseTable.row();

    }



    public void addTo(Stage stage) {
        stage.addActor(baseTable);
    }






}
