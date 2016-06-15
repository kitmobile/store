package kit.ce.ash.mobileproject;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by 성현 on 2016-05-23.
 */
public class LocationService extends Service{

    double latitude;
    double longitude;

    private LocationManager locationManager;
    private String locationProvider;
    // 컴포넌트로 반환되는 IBinder
    private final IBinder mBinder = new LocalBinder();
    public LocationListener mLocationListener;

    // IBinder 상속하여 이너클래스 생성, 이 클래스로 컴포넌트와 서비스를 bind한다
    public class LocalBinder extends Binder {
        LocationService getService(){
            return LocationService.this;
        }
    }

    public static Criteria getCriteria(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongtitude(){
        return longitude;
    }

    public void removeRequest(){
        try {
            locationManager.removeUpdates(mLocationListener);
            Log.e("removeUpdates","Success");
        }
        catch (SecurityException e){
            Log.e("removeUpdates","Failed");
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new myLocationListener();

        locationProvider = locationManager.getBestProvider(getCriteria(), true);

        getLastLocation();

        try {
            locationManager.requestLocationUpdates(locationProvider, 10000, 0, mLocationListener);
            Log.e("requestLocationUpdate", "Permission Allowed");
        }
        catch (SecurityException e) {
            Log.e("requestLocationUpdate", "Permission Denied");
        }
    }

    // 마지막 위치를 받아와 출력하는 메소드
    public void getLastLocation(){
        Location myLocation = getLastKnownLocation();

        latitude = myLocation.getLatitude();
        longitude = myLocation.getLongitude();

        Log.e("getData", "lat = " + latitude + "\nlng = " + longitude);

    }

    // 마지막 위치를 확인하여 그 위치를 반환하는 메소드, 최초실행시 locationManager.getLastKnownLocation(provider) 사용하면 null 반환으로 인한 회피법
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        Location l = null;
        for (String provider : providers) {
            try {
                l = locationManager.getLastKnownLocation(provider);
                Log.e("getLastKnownLocation", "bestProvider : " + locationProvider);
            }
            catch(SecurityException e){
                Log.e("getLastKnownLocation", "Permission Denied");
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    //서비스에서 액티비티로 데이터를 전달하기 위한 콜백 인터페이스 선언
    public interface ICallback {
        public void recvData(double latitude, double longitude); //액티비티에서 선언한 콜백 함수.
    }

    private ICallback mCallback;

    //액티비티에서 콜백 함수를 등록하기 위함.
    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class myLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

            // 위치정보 획득
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            // 획득한 위치정보를 액티비티로 전달한다.
            mCallback.recvData(latitude, longitude);

            Log.e("onLocationChanged", "bestProvider : " + locationProvider);
            Log.e("getData", "lat = " + latitude + " lng = " + longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    //Toast.makeText(LocationService.this, provider +" Available", Toast.LENGTH_LONG).show();
                    Log.e("LocationProvider : ", provider + " Available");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    //Toast.makeText(LocationService.this, provider +" Out of Service", Toast.LENGTH_LONG).show();
                    Log.e("LocationProvider : ", provider + " Out of Service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    //Toast.makeText(LocationService.this, provider +" Service Stop", Toast.LENGTH_LONG).show();
                    Log.e("LocationProvider : ", provider + " Service Stop");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("Provider","enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("Provider","disabled");
            Toast.makeText(getApplicationContext(), "현재 위치 서비스를 사용할 수 없습니다.\nGPS가 활성화 되었는지 확인하세요.", Toast.LENGTH_LONG).show();
        }
    }
}
