package ninja.trek.loopercontrol;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MainLooperControl extends ApplicationAdapter {
	private final IBluetoothHandler bluetooth;
	SpriteBatch batch;
	Texture img;
	ShapeRenderer shape;
    private LooperTracksRenderer looperTracksRenderer;

    public Stage stage;
    private UI ui;
	private Skin skin;
	private InputMultiplexer mux;

	public MainLooperControl(IBluetoothHandler bluetooth) {
		this.bluetooth = bluetooth;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		shape = new ShapeRenderer();
        looperTracksRenderer = new LooperTracksRenderer();
        //skin = new Skin(Gdx.files.internal("holo/skin/dark-mdpi/Holo-dark-mdpi.json"));
        skin = new Skin(Gdx.files.internal("flat/skin/skin.json"));
        ui = new UI(skin);
        stage = new Stage();
        ui.addTo(stage);
        mux = new InputMultiplexer();
        mux.addProcessor(stage);
        Gdx.input.setInputProcessor(mux);
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		//batch.draw(img, 0, 0);
		batch.end();
		bluetooth.update();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
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
