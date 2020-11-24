package ninja.trek.loopercontrol;

import cz.tchalupnik.libgdx.Toast;

public interface IBluetoothHandler {
    void onResume();

    void onDispose();

    void onPause();

    void update(float delta, DrumUI drums);

    void onCreate();

    void queueSendAll();

    void queueSendNote(DrumSetupUI.DrumSettingsData data, int noteInputIndex);
}
