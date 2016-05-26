package kit.ce.ash.mobileproject;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class inputData {
    private String location;
    private WifiManager mWifiManager;
    Context context;

    inputData(String location) {
        this.location = location;
        this.mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        Toast.makeText(context,"2",Toast.LENGTH_SHORT).show();
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getLocation(){
        return location;
    }

    public void setWifi(boolean wifistate) {
        Toast.makeText(this.context,"success",Toast.LENGTH_SHORT).show();
        if(wifistate)
            mWifiManager.setWifiEnabled(true);
        else
            mWifiManager.setWifiEnabled(false);
    }
}
