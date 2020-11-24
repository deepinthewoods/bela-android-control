package ninja.trek.loopercontrol;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.rtp.AudioStream;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pools;
import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;

class AndroidBluetoothHandler implements IBluetoothHandler {

    public static final float TIME_BETWEEN_DEVICE_SCANS = 10;
    private static final String TAG = "android bluetooth handler";
    //public static final Array<String> deviceNamesToConnect = new Array<String>();
    private final Context context;
    private BluetoothManager bluetoothManager;
    private float time;
    private float scanDevicesTimer;
    private boolean sendAllQueued = false;
    private boolean sendingAll;
    private int sendProgress;
    private Array<SimpleBluetoothDeviceInterface> connectedDevices = new Array<>();
    private Array<BluetoothSerialDevice> connectedSerialDevices = new Array<>();
    private Array<IntArray> noteQ = new Array<IntArray>();
    private Array<File> waveQ = new Array<File>();
    private SoundFile activeSoundFile;
    private int sendAllIndex;
    private DrumUI drums;

    public AndroidBluetoothHandler(Context context) {
        this.context = context;
        scanDevicesTimer = 0;
        //deviceNamesToConnect.add("trek.ninja controller");
       // Looper.prepare();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDispose() {
        bluetoothManager.close();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void update(float deltaTime, DrumUI drums) {
        time += deltaTime;
        this.drums = drums;
        scanDevicesTimer -= deltaTime;
        if (scanDevicesTimer < 0){
            scanDevicesTimer = TIME_BETWEEN_DEVICE_SCANS;
            scanForDevices();
        }
        if (sendAllQueued){
            sendAllQueued = false;
            sendingAll = true;
            sendProgress = 0;
        }
        if (sendingAll){
            try {
                sleep(1250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DrumUI.DrumTriggerState[] dr = drums.getTriggerStates();
            if (sendProgress == 0){

            }

            for (BluetoothSerialDevice device : connectedSerialDevices){

                String message = "m";
                if (sendAllIndex < dr.length)
                    message += dr[sendAllIndex++];
                else {
                    message += (char) 1;
                    message += (char) 0;
                    message += (char) 0;
                    message += (char) 2;
                    sendAllIndex++;
                }
                //message += "\n";
                device.toSimpleDeviceInterface().sendMessage(message);


                Gdx.app.log(TAG, "sent to device " + device);
               // Toast.makeText(context, "sending all to " + device.toString(), 1);
                MainLooperControl.toastLong("send to " + device.getMac());
            }
            sendProgress++;
            sendingAll = false;
        }

        while (noteQ.size > 0){
            IntArray info = noteQ.pop();
            String message = "noteSet ";
            message += info.get(0) + " ";
            message += info.get(1) + " ";
            message += info.get(2) + " ";
            //message += info.get(3) + "\n";
            for (BluetoothSerialDevice device : connectedSerialDevices){
                //device.toSimpleDeviceInterface().sendMessage(message);

                Gdx.app.log(TAG, "sent note to device " + device);
                // Toast.makeText(context, "sending all to " + device.toString(), 1);
                MainLooperControl.toastLong("send note to " + device.getMac());
            }

            Pools.free(info);
        }


    }

    private void scanForDevices() {
        List<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevicesList();
        for (BluetoothDevice device : pairedDevices) {
            Log.d("My Bluetooth App", "Device name: " + device.getName());
            Log.d("My Bluetooth App", "Device MAC Address: " + device.getAddress());

            if (device.getName().contains("trekninja") && !listContains(device.getAddress())){
                bluetoothManager.openSerialDevice(device.getAddress())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onConnected, this::onError);
            }
        }
    }

    private boolean listContains(String mac) {
        Iterator<BluetoothSerialDevice> iter = connectedSerialDevices.iterator();
        while (iter.hasNext()){

            BluetoothSerialDevice dev = iter.next();
            if (dev.getMac().equals(mac))

                return true;

        }
        return false;
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        SimpleBluetoothDeviceInterface deviceInterface = connectedDevice.toSimpleDeviceInterface();

        // Listen to bluetooth events
        deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

        // Let's send a message:
        //deviceInterface.sendMessage("connected");
        addConnectedDevice(deviceInterface, connectedDevice);
    }

    private void addConnectedDevice(SimpleBluetoothDeviceInterface deviceInterface, BluetoothSerialDevice connectedDevice) {
        connectedDevices.add(deviceInterface);
        connectedSerialDevices.add(connectedDevice);
        MainLooperControl.toastLong("connected to " + connectedDevice.getMac());
    }

    private void onMessageSent(String message) {
        // We sent a message! Handle it here.
        Toast.makeText(context, "Sent a message Message was: " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
        //if (sendAllIndex < drums.getTriggerStates().length) sendingAll = true;
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        Toast.makeText(context, "Received a message! Message was: " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
    }

    private void onError(Throwable error) {
        MainLooperControl.toastLong("error " + error.getMessage() + " \n" + error.getClass());

    }

    @Override
    public void onCreate() {
        bluetoothManager = BluetoothManager.getInstance();
        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(context, "Bluetooth not available.", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            //finish();
        }

    }

    @Override
    public void queueSendAll() {
        sendAllQueued = true;
        sendAllIndex = 0;
    }

    @Override
    public void queueSendNote(DrumSetupUI.DrumSettingsData data, int index) {
        IntArray noteInfo = Pools.obtain(IntArray.class);
        noteInfo.add(data.sample[index]);
        noteInfo.add(data.velocity[index]);
        noteInfo.add( data.channel[index]);
        noteInfo.add(index);
        noteQ.add(noteInfo);
    }

    public void queueSendWav(int index, File wavFile){
        waveQ.add(wavFile);
    }
}
