package kit.ce.ash.mobileproject;


public class inputData {
    private String location;
    private String setting;
    private boolean working;
    private double latitude;
    private double longitude;
    private boolean wlan;
    private boolean sound;
    private boolean vibrate;
    private boolean silent;
    private boolean no_use;
    private boolean dataNetwork;
    private boolean nfc;
    private boolean bluetooth;


    inputData(String setting){
        this.setting = setting;
    }

    public String getSetting(){
        return setting;
    }

    public void setSetting(String setting){
        this.setting = setting;
    }

    inputData(String location, double latitude, double longitude, boolean wlan, boolean sound, boolean vibrate, boolean silent, boolean no_use, boolean dataNetwork, boolean nfc, boolean bluetooth){
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wlan = wlan;
        this.sound = sound;
        this.vibrate = vibrate;
        this.silent = silent;
        this.no_use = no_use;
        this.dataNetwork = dataNetwork;
        this.nfc = nfc;
        this.bluetooth = bluetooth;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getLocation(){
        return location;
    }

    public void setWorking(boolean working){
        this.working = working;
    }

    public boolean getWorking(){
        return working;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setWlan(boolean val){
        this.wlan = val;
    }

    public boolean getWlan(){
        return wlan;
    }

    public void setVibrate(boolean val){
        this.vibrate = val;
    }

    public boolean getVibrate(){
        return vibrate;
    }

    public void setSilent(boolean val){
        this.silent = val;
    }

    public boolean getSilent(){
        return silent;
    }

    public void setNouse(boolean val){
        this.no_use = val;
    }

    public boolean getNouse(){
        return no_use;
    }

    public void setSound(boolean val){
        this.sound = val;
    }

    public boolean getSound(){
        return sound;
    }

    public void setDataNetwork(boolean val){
        this.dataNetwork = val;
    }

    public boolean getDataNetwork(){
        return dataNetwork;
    }

    public void setNFC(boolean val){
        this.nfc = val;
    }

    public boolean getNFC(){
        return nfc;
    }

    public void setBluetooth(boolean val){
        this.bluetooth = val;
    }

    public boolean getBluetooth(){
        return bluetooth;
    }
}
