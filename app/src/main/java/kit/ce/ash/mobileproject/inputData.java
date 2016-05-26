package kit.ce.ash.mobileproject;


public class inputData {
    private String location;
    private boolean working;
    private double latitude;
    private double longitude;
    private boolean wlan;
    /*
    private int sound;
    private int brighness;
    private boolean dataNetwork;
    */

    inputData(String location){
        this.location = location;
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
}
