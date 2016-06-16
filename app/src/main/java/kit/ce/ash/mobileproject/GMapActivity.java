package kit.ce.ash.mobileproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat;
    double lon;

    Intent getIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmap);

        getIntent = getIntent();

        Log.w("edit", getIntent.getStringExtra("edit"));
        if(getIntent.getStringExtra("edit").equals("edit")){
            lat = Double.parseDouble(getIntent.getStringExtra("latitude"));
            lon = Double.parseDouble(getIntent.getStringExtra("longitude"));
        }
        else{
            lat = 36.146;
            lon = 128.392;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //LatLng seoul = new LatLng(37.567, 126.978);


        LatLng kumoh = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(kumoh).title("현재 선택한 지점"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kumoh, 14));

        Log.i("lat2",String.valueOf(lat));
        Log.i("lon2",String.valueOf(lon));

        // 지도의 한 지점을 선택할 경우 호출되는 맵 클릭 리스너 생성
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);

                // 선택한 지점의 위도, 경도값을 저장
                lat = latLng.latitude;
                lon = latLng.longitude;
                markerOptions.getPosition();

                Log.d("latitude", String.valueOf(lat));
                Log.d("longitude", String.valueOf(lon));
            }
        });

        // 선택 버튼 객체 생성하고 리스너 등록
        Button mapBtn = (Button) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();

                // intent에 위도값과 경도값을 넣어줌
                intent.putExtra("latitude", String.valueOf(lat));
                intent.putExtra("longitude", String.valueOf(lon));

                // 결과값을 inputDataActivity로 전달
                setResult(RESULT_OK, intent);

                // 지도 종료
                finish();
            }
        });

        // 현재 위치 찾기 버튼 추가
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }
}
