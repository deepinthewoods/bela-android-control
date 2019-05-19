package ninja.trek.loopercontrol.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ninja.trek.loopercontrol.IBluetoothHandler;
import ninja.trek.loopercontrol.MainLooperControl;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 450;
		config.height = 700;

		IBluetoothHandler desktopBluetooth = new IBluetoothHandler() {
			@Override
			public void onResume() {

			}

			@Override
			public void onDispose() {

			}

			@Override
			public void onPause() {

			}

			@Override
			public void update() {

			}
		};
		new LwjglApplication(new MainLooperControl(desktopBluetooth), config);
	}
}
