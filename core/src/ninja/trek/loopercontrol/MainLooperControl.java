package ninja.trek.loopercontrol;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cz.tchalupnik.libgdx.Toast;

public class MainLooperControl extends ApplicationAdapter {
	private static Array<String> longToastQueue = new Array<String>();

	private final IBluetoothHandler bluetooth;
	SpriteBatch batch;
	ShapeBatch shape;
	public static int GRID_X = 9, GRID_Y = 16, GRID_W, GRID_H;

    public Stage stage;
    private UI ui;
	private Skin skin;
	private InputMultiplexer mux;
	private static final List<Toast> toasts = new LinkedList<Toast>();
	private static Toast.ToastFactory toastFactory;
	public static int GRID_W4, GRID_W1, GRID_W2, GRID_W3, GRID_W34, GRID_H34;

	public MainLooperControl(IBluetoothHandler bluetooth) {
		this.bluetooth = bluetooth;
	}

	@Override
	public void create () {
		GRID_W = Gdx.graphics.getWidth()/GRID_X;
		GRID_W4 = Gdx.graphics.getWidth()/4;
		GRID_W1 = Gdx.graphics.getWidth();
		GRID_W2 = Gdx.graphics.getWidth()/2;
		GRID_W3 = Gdx.graphics.getWidth()/3;
		GRID_W34 = Gdx.graphics.getWidth()/4 * 3;

		GRID_H = Gdx.graphics.getHeight()/GRID_Y;
		GRID_H34 = ((Gdx.graphics.getHeight()*3)/4)/GRID_Y;

		batch = new SpriteBatch();
		shape = new ShapeBatch();
        //skin = new Skin(Gdx.files.internal("holo/skin/dark-mdpi/Holo-dark-mdpi.json"));
        skin = new Skin(Gdx.files.internal("flat/skin/skin.json"));

        toastFactory = new Toast.ToastFactory.Builder().font(skin.get(BitmapFont.class)).build();
        stage = new Stage();
        ui = new UI(skin, shape, bluetooth, stage);
        ui.addTo(stage);
        mux = new InputMultiplexer();
        mux.addProcessor(stage);
        Gdx.input.setInputProcessor(mux);
        bluetooth.onCreate();

    }

	@Override
	public void render () {
		//stage.setDebugAll(true);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		bluetooth.update(Gdx.graphics.getDeltaTime(), ui.drum);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		batch.begin();
		shape.draw();
		batch.end();

		Iterator<String> qIter = longToastQueue.iterator();
		while (qIter.hasNext()){
			String text = qIter.next();
			toasts.add(toastFactory.create(text, Toast.Length.LONG));
		}
		longToastQueue.clear();
		Iterator<Toast> it = toasts.iterator();
		while(it.hasNext()) {
			Toast t = it.next();
			if (!t.render(Gdx.graphics.getDeltaTime())) {
				it.remove(); // toast finished -> remove
			} else {
				break; // first toast still active, break the loop
			}
		}

	}
	/**
	 * Displays long toast
	 */
	public static void toastLong(String text) {
		longToastQueue.add(text);

	}

	/**
	 * Displays short toast
	 */
	public static void toastShort(String text) {
		toasts.add(toastFactory.create(text, Toast.Length.SHORT));
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		bluetooth.onDispose();
		skin.dispose();
		stage.dispose();
		bluetooth.onDispose();
	}

	@Override
	public void resume() {
		super.resume();
		bluetooth.onResume();
	}

	@Override
	public void pause() {
		super.pause();
		bluetooth.onPause();
	}
}
