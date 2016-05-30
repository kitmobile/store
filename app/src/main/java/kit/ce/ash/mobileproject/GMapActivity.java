package kit.ce.ash.mobileproject;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button mapBtn;
    double lat;
    double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmap);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //LatLng seoul = new LatLng(37.567, 126.978);
        LatLng kumoh = new LatLng(36.146, 128.392);
        mMap.addMarker(new MarkerOptions().position(kumoh).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kumoh, 13));

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

                Toast.makeText(GMapActivity.this, "latitude = " + lat + "\nlongitude = " + lon, Toast.LENGTH_SHORT).show();

                Log.d("latitude", String.valueOf(lat));
                Log.d("longitude", String.valueOf(lon));
            }
        });

        // 선택 버튼 객체 생성하고 리스너 등록
        mapBtn = (Button)findViewById(R.id.mapBtn);
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
    }
}
