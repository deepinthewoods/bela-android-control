package ninja.trek.loopercontrol;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.rtp.AudioStream;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pools;


import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;
import static ninja.trek.loopercontrol.BluetoothSerial.BLUETOOTH_CONNECTED;
import static ninja.trek.loopercontrol.BluetoothSerial.BLUETOOTH_DISCONNECTED;
import static ninja.trek.loopercontrol.BluetoothSerial.BLUETOOTH_FAILED;

class AndroidBluetoothHandler implements IBluetoothHandler {

    public static final float TIME_BETWEEN_DEVICE_SCANS = 10;
    private static final String TAG = "android bluetooth handler";
    //public static final Array<String> deviceNamesToConnect = new Array<String>();
    private final BluetoothSerial bluetoothSerial;
    private float time;
    private float scanDevicesTimer;
    private boolean sendAllQueued = false;
    private boolean sendingAll;
    private int sendProgress;
    //private Array<BluetoothSerialDevice> connectedSerialDevices = new Array<>();
    private Array<IntArray> noteQ = new Array<IntArray>();
    private Array<File> waveQ = new Array<File>();
    private SoundFile activeSoundFile;
    private DrumUI drums;
    String deviceNamePrefix = "trek";
    private Array<ByteArray> queuedMessages = new Array<ByteArray>(true, 16);

    public AndroidBluetoothHandler(Context context) {
        scanDevicesTimer = 0;
        //deviceNamesToConnect.add("trek.ninja controller");
       // Looper.prepare();
        //MessageHandler is call when bytes are read from the serial input
        bluetoothSerial = new BluetoothSerial(context, new BluetoothSerial.MessageHandler() {
            @Override
            public int read(int bufferSize, byte[] buffer) {
                return doRead(bufferSize, buffer);
            }


        }, deviceNamePrefix);

        //Fired when connection is established and also fired when onResume is called if a connection is already established.
        BroadcastReceiver bluetoothConnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice eventDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BLUETOOTH_CONNECTED.equals(intent.getAction())) {
                    bluetoothSerial.bluetoothDevice = eventDevice;
                    MainLooperControl.toastLong("connected to " + bluetoothSerial.bluetoothDevice.getName());

                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(bluetoothConnectReceiver, new IntentFilter(BLUETOOTH_CONNECTED));
        //Fired when the connection is lost
        BroadcastReceiver bluetoothDisconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                bluetoothSerial.connect();

            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BLUETOOTH_DISCONNECTED));
        //Fired when connection can not be established, after 30 attempts.
        LocalBroadcastManager.getInstance(context).registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BLUETOOTH_FAILED));

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
    public void update(float deltaTime, DrumUI drums) {
        time += deltaTime;
        this.drums = drums;
        scanDevicesTimer -= deltaTime;
//        if (scanDevicesTimer < 0){
//            scanDevicesTimer = TIME_BETWEEN_DEVICE_SCANS;
//            scanForDevices();
//        }
        if (sendAllQueued){
            sendAllQueued = false;
            sendingAll = true;
            sendProgress = 0;
        }
        if ( sendingAll){
//            try {
//                sleep(1250);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            DrumUI.DrumTriggerState[] dr = drums.getTriggerStates();
            if (sendProgress == 0){

            }
            for (int i = 0; i < dr.length; i++){
                queuedMessages.add(dr[i].toByteArray());
            }

            //String message = "m";



//                message += (char) 1;
//                message += (char) 0;
//                message += (char) 0;
//                message += (char) 2;
//                sendAllIndex++;

            //message += "\n";



            sendProgress++;
            sendingAll = false;
        }

        while (noteQ.size > 0){
            IntArray info = noteQ.pop();
            String message = "noteSet ";
            message += info.get(0) + " ";
            message += info.get(1) + " ";
            message += info.get(2) + " ";

            //MainLooperControl.toastLong("send note to " + bluetoothSerial.bluetoothDevice.getName());


            Pools.free(info);
        }

        ByteArray queuedMessage = null;
        //queuedMessage.add
        if (queuedMessages.size > 0)try {
            if (bluetoothSerial == null) return;
            if (bluetoothSerial.serialOutputStream == null) return;
            queuedMessage = queuedMessages.removeIndex(0);
            if (queuedMessage.items == null) throw new GdxRuntimeException("null message");
            bluetoothSerial.serialOutputStream.write(queuedMessage.items, 0, queuedMessage.size);
            bluetoothSerial.serialOutputStream.flush();
            MainLooperControl.toastLong(( "sent to device " + bluetoothSerial.bluetoothDevice.getName() + "  : " + queuedMessage));
            Pools.free(queuedMessage);
        } catch (IOException e) {
            e.printStackTrace();
            if (queuedMessage!= null){
                MainLooperControl.toastLong(( "FAILED, device " + bluetoothSerial.bluetoothDevice.getName() + "  : " + queuedMessage));
                Pools.free(queuedMessage);
            }

        }

    }




    @Override
    public void onCreate() {


    }

    @Override
    public void queueSendAll() {
        sendAllQueued = true;

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
