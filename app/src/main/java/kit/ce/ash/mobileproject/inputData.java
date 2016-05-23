package kit.ce.ash.mobileproject;


public class inputData {
    private String location;
    private boolean wlan;
    private int sound;
    private int brighness;
    private boolean dataNetwork;

    inputData(String location){
        this.location = location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getLocation(){
        return location;
    }

}
