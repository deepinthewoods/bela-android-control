package ninja.trek.loopercontrol;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;


public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		AndroidBluetoothHandler bluetooth = new AndroidBluetoothHandler(this.getContext());
		initialize(new MainLooperControl(bluetooth), config);
	}
}
