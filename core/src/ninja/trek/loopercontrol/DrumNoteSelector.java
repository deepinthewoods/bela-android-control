package ninja.trek.loopercontrol;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

class DrumNoteSelector extends Table {
    public static final String[] note_names;
    private final TextButton[] noteBtn;
    private static final int TOTAL_NOTES = 88;
    public static final String[] labelText = new String[TOTAL_NOTES];
    public final ButtonGroup noteGroup;
    private int channel;

    public DrumNoteSelector(Skin skin, ShapeBatch shape, Table table) {
        super();

        noteBtn = new TextButton[TOTAL_NOTES];
        noteGroup = new ButtonGroup();
        noteGroup.setMaxCheckCount(1);

        for (int i = 0; i < TOTAL_NOTES; i++){
            noteBtn[i] = new TextButton(note_names[i], skin, "toggle");
            Integer in = new Integer(i);
            noteBtn[i].setUserObject(in);
            add(noteBtn[i]);
            noteGroup.add(noteBtn[i]);
            Label noteLabel = new Label(labelText[i], skin);
            add(noteLabel).row();

        }
    }




    public static final int NOTE_OFFSET = 21;
    private static final String[] NOTE_LETTERS = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    static {
        note_names = new String[TOTAL_NOTES];

        for (int i = 0; i < 109 - 21; i++){//21
            note_names[i] = NOTE_LETTERS[i%12] + i/12;
        }

        labelText[35-21] = "acoustic bass drum";
        labelText[36-21] = "bass drum";
        labelText[38-21] = "acoustic snare";
        labelText[40-21] = "electric snare";
        labelText[41-21] = "low floor tom";
        labelText[43-21] = "high floor tom";
        labelText[45-21] = "low tom";
        labelText[47-21] = "low-mid tom";
        labelText[48-21] = "hi-mid tom";
        labelText[50-21] = "high tom";
        labelText[59-21] = "ride 2";
        labelText[57-21] = "crash 2";
        labelText[55-21] = "splash cymbal";
        labelText[53-21] = "ride bell";
        labelText[52-21] = "chinese cymbal";
        labelText[42-21] = "closed hh";
        labelText[44-21] = "pedal hh";
        labelText[46-21] = "open hh";
        labelText[49-21] = "crash 1";
        labelText[51-21] = "ride 1";
        labelText[54-21] = "tamb";
        labelText[56-21] = "cowbell";
        labelText[58-21] = "vibraslap";
        labelText[39-21] = "hand clap";
        labelText[37-21] = "side stick";

    }


    public int getNote() {

        int note = (Integer)noteGroup.getChecked().getUserObject();
        return note;
    }


    public void setSelected(int i) {
        noteBtn[i].setChecked(true);
    }
}
