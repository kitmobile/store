package kit.ce.ash.mobileproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GMapActivity extends AppCompatActivity {

    static LatLng LOCAL;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmap);
        final Intent intent = getIntent();
      //  Double latitude = intent.getExtras().getDouble("latitude");
      //  Double longitude = intent.getExtras().getDouble("longitude");
        LatLng LOCAL = new LatLng(36.146, 128.393);
      //  Toast.makeText(this,latitude)
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        Marker seoul = mMap.addMarker(new MarkerOptions().position(LOCAL));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCAL, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10),2000,null);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                markerOptions.getPosition();
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                finish();
            }
        });
    }
}
