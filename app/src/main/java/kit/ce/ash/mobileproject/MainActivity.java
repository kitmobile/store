package kit.ce.ash.mobileproject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends Activity {

    public LocationService mService; // bind 타입 서비스
    public boolean mBound = false; // 서비스 연결 상태

    ListViewAdapter adapter;


    // startActivityForResult 에서 다른 액티비티로 넘겨주는 requestCode 값
    int newData = 1;
    int editData = 0;

    String netName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView view = (ListView)findViewById(R.id.listView);
        adapter = new ListViewAdapter(this); // 어댑터 객채 생성
        view.setAdapter(adapter); // 커스텀 리스트뷰에 어댑터 연결



        /*
        리스트뷰를 스와이프 하여서 커스텀 리스트뷰의 항목을 삭제하는 코드
        https://github.com/romannurik/Android-SwipeToDismiss 소스 사용
         */
        // 커스텀 리스트뷰의 터치리스너 생성
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(view, new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            // 항목을 스와이프 하였을 때
            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    adapter.remove(position); // 선택한 항목을 삭제
                }
                adapter.notifyDataSetChanged(); // 어댑터의 내용이 변경된걸 알려줌줌
            }
        });

        // 생성한 터치리스너를 커스텀 리스트뷰에 등록
        view.setOnTouchListener(touchListener);
        view.setOnScrollListener(touchListener.makeScrollListener());

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inputData data = adapter.mListData.get(position);
                Toast.makeText(MainActivity.this, "위도 = " + data.getLatitude() + "\n경도 = " + data.getLongitude(), Toast.LENGTH_LONG).show();
            }
        });

        /* LocationService 테스트용 버튼 2개
           처음 버튼은 서비스가 없을 시 bindService 호출하여서 서비스와 바인딩, 서비스와 연결되어 있다면 서비스에서 위경도 값을 받아와서 Toast로 출력
           두번째 버튼은 unbindService 호출하여서 서비스 바인딩 해제
         */

        Button testBtn = (Button)findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    double latitude = mService.getLatitude();
                    double longitude = mService.getLongtitude();
                    Toast.makeText(MainActivity.this, "위도 = " + change(latitude) + "\n경도 = " + change(longitude), Toast.LENGTH_LONG).show();
                    Log.d("위도", latitude + "   " + change(latitude));
                    Log.d("경도", longitude + "   " + change(longitude));
                }
                else{
                    bind();
                    Toast.makeText(MainActivity.this, "서비스 바인딩", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button test2Btn = (Button)findViewById(R.id.test2Btn);
        test2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    if (mBound) {
                        mBound = false;
                        unbind(mConn);
                    }
                }
            }
        });

        // 새 항목 추가하는 버튼 객체 생성
        Button newBtn = (Button)findViewById(R.id.newBtn);

        // 버튼의 클릭리스너 생성
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputDataActivity.class);
                startActivityForResult(intent, newData);
            }
        });
    }

    // 액티비티가 특정한 값을 받아올 때 자동 호출
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // requestCode를 사용해서 어떠한 요청인지 구분한다. 0:항목수정, 1:새항목
        switch (requestCode) {
            case 0:
                // resultCode를 이용하여서 데이터 입력 화면에서 어떠한 결과를 처리하여서 데이터를 넘겨주는지 확인한다
                if (resultCode == RESULT_OK) {

                }
                adapter.notifyDataSetChanged();
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Double latitude = round(Double.parseDouble(intent.getStringExtra("latitude")));
                    Double longitude = round(Double.parseDouble(intent.getStringExtra("longitude")));
                    adapter.addItem(intent.getStringExtra("location"), latitude, longitude);
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }


    private class ViewHolder {
        public TextView location;
    }

    // 커스텀 리스트 뷰 어댑터
    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<inputData> mListData = new ArrayList<>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            // view가 없다면 새로운 뷰를 생성
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview, null);

                // 뷰홀더를 통해 커스텀 리스트뷰 내부의 객체 생성
                holder.location = (TextView) convertView.findViewById(R.id.location);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            inputData data = mListData.get(position);

            holder.location.setText(data.getLocation());

            Log.d("getLocation",data.getLocation());

            adapter.notifyDataSetChanged();

            return convertView;
        }

        // 새 항목 추가
        public void addItem(String location, double latitude, double longitude) {
            inputData addInfo;
            addInfo = new inputData(location, latitude, longitude);

            mListData.add(addInfo);
        }

        // 항목 삭제
        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        // 데이터 변경을 알려줌
        public void dataChange() {
            adapter.notifyDataSetChanged();
        }
    }

    // bind 할때 사용
    public void bind(){
        bindService(new Intent(this, LocationService.class), mConn, Context.BIND_AUTO_CREATE);
    }

    // 서비스 bind 해제
    public void unbind(ServiceConnection connection){
        unbindService(connection);
        // 위치정보 갱신 정지 요청, 미사용시 서비스는 무한정으로 위치정보를 받아옴
        mService.removeRequest();
    }

    // onLocationChanged 호출시 획득한 위치정보를 액티비티에서 인자값으로 전달받음
    private LocationService.ICallback mCallback = new LocationService.ICallback() {
        public void recvData(double latitude, double longitude) {
            Toast.makeText(MainActivity.this, "recvData \n" + latitude + "\n" + longitude, Toast.LENGTH_SHORT).show();

            /* 현재 접속중인 네트워크 확인
             WIFI 접속시 WIFI, 데이터 사용시 MOBILE 이라고 netName 값에 저장함
             */
            ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();


            netName = ni.getTypeName();
            if (netName.equals("MOBILE")) {
                Log.i("network", "Network - > " + netName);
            }
            else{
                Log.i("network", "Network - > " + netName);
            }


            setWifi(false);
        }
    };


    // ServiceConnection 객체 생성으로 bind타입 서비스와 액티비티간 연결
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
            mService.registerCallback(mCallback); //콜백 등록
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    // double로 된 위,경도 값을 받아와서 도/분/초 String 값으로 변경
    public String change(double val){
        String lat = "";
        int doe;
        double bun;
        double cho;

        // val = 36.1455559, = 36도 8분 44.00초

        doe = (int)val; // 36

        lat = String.valueOf(doe) + "도 "; // 36도

        bun = (val-doe) * 60; // 0.1455559 * 60 = 8.733354

        lat = lat + String.valueOf((int)bun) + "분 "; // 36도 8분

        cho = (bun-(int)bun) * 60; // 0.733354 * 60 = 44.00124

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);//소수점 아래 최소 자리수
        nf.setMaximumFractionDigits(2);//소수점 아래 최대 자리수

        lat = lat + nf.format(cho) + "초"; // 36도 8분 44.00초

        return lat;
    }

    public double round(double val){
        return Math.round(val*1000)/1000.0;
    }

    public void setWifi(boolean val){
        WifiManager wManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        if(!val){
            if(!netName.equals("WIFI")) {
                Toast.makeText(MainActivity.this, "ALREADY WIFI OFF.", Toast.LENGTH_SHORT).show();
                Log.d("netName",netName + " 0");
            }
            else {
                wManager.setWifiEnabled(false);
                Toast.makeText(MainActivity.this, "NOW ㅗWIFI OFF.", Toast.LENGTH_SHORT).show();
                Log.d("netName", netName + " 1");
            }
        }
        else{
            if(netName.equals("WIFI")) {
                Toast.makeText(MainActivity.this, "ALREADY WIFI ON", Toast.LENGTH_SHORT).show();
                Log.d("netName", netName + " 2");
            }
            else {
                wManager.setWifiEnabled(true);
                Toast.makeText(MainActivity.this, "NOW WIFI ON", Toast.LENGTH_SHORT).show();
                Log.d("netName",netName + " 3");
            }
        }
    }
}
