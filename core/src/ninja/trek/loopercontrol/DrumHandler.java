package ninja.trek.loopercontrol;

class DrumHandler {
    public byte[] queue = new byte[100];
    public int queueStart, queueEnd;
    public int queueCurrent;
    public int playState;
    public long currentTime;


    public void signalIn(int index){

    }
    public boolean update(long millis){

        return true;
    }

    public DrumHandler(){

    }
}
