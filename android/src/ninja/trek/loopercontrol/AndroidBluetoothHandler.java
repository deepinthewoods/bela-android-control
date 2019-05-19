package ninja.trek.loopercontrol;

import android.content.Context;
import android.widget.Toast;

import com.bmxgates.logger.BluetoothSerial;

import java.io.IOException;

class AndroidBluetoothHandler implements IBluetoothHandler {
    private final BluetoothSerial bluetoothSerial;
    private final String deviceNamePrefix = "deviceNamePrefix";

    public AndroidBluetoothHandler(Context context) {
        bluetoothSerial = new BluetoothSerial(context, new BluetoothSerial.MessageHandler() {
            @Override
            public int read(int bufferSize, byte[] buffer) {
                return doRead(bufferSize, buffer);
            }
        }, deviceNamePrefix);
    }

    private int doRead(int bufferSize, byte[] buffer) {
        return 0;
    }

    @Override
    public void onResume() {
        bluetoothSerial.onResume();
    }

    @Override
    public void onDispose() {
        bluetoothSerial.onPause();  // if not done yet
        bluetoothSerial.close();
    }

    @Override
    public void onPause() {
        bluetoothSerial.onPause();
    }

    @Override
    public void update() {
        byte[] message = new byte[4];
        try {
            bluetoothSerial.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}