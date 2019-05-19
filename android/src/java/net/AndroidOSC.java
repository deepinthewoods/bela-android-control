package java.net;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortOut;

import java.net.InetAddress;

import ninja.trek.loopercontrol.IOSCHandler;

public class AndroidOSC implements IOSCHandler {

    public AndroidOSC(){
        InetAddress addr = new InetAddress();
        OSCPort sender = null;
        try {
            sender = new OSCPortOut(addr);
        } catch (SocketException e) {
            e.printStackTrace();
        }


        Object args[] = new Object[2];
        args[0] = new Integer(3);
        args[1] = "hello";
        OSCMessage msg = new OSCMessage("/sayhello", args);
        try {
            sender.send(msg);
        } catch (Exception e) {
            throw new GdxRuntimeException("Couldn't send");
        }
    }
}
