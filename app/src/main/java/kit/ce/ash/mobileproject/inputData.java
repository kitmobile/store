package kit.ce.ash.mobileproject;


public class inputData {
    private String location;
    private String setting;
    private boolean working;
    private double latitude;
    private double longitude;
    private boolean wlan;
    private boolean sound;
    private boolean dataNetwork;


    inputData(String setting){
        this.setting = setting;
    }

    public String getSetting(){
        return setting;
    }

    public void setSetting(String setting){
        this.setting = setting;
    }

    inputData(String location, double latitude, double longitude){
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
