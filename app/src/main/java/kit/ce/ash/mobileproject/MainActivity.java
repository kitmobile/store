package kit.ce.ash.mobileproject;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
import android.nfc.NfcAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends Activity {

    public LocationService mService; // bind 타입 서비스
    public boolean mBound = false; // 서비스 연결 상태

    ListViewAdapter adapter;

    // startActivityForResult 에서 다른 액티비티로 넘겨주는 requestCode 값
    final int newData = 1;
    final int editData = 0;

    ActionBar aBar;

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
                Intent intent = new Intent(MainActivity.this, InputDataActivity.class);

                intent.putExtra("edit","edit");
                intent.putExtra("position", String.valueOf(position));
                intent.putExtra("location", data.getLocation());
                intent.putExtra("latitude", String.valueOf(data.getLatitude()));
                intent.putExtra("longitude", String.valueOf(data.getLongitude()));
                intent.putExtra("wlan", String.valueOf(data.getWlan()));
                intent.putExtra("sound", String.valueOf(data.getSound()));
                intent.putExtra("vibrate", String.valueOf(data.getVibrate()));
                intent.putExtra("silent", String.valueOf(data.getSilent()));
                intent.putExtra("noUse", String.valueOf(data.getNouse()));
                intent.putExtra("dataNetwork", String.valueOf(data.getDataNetwork()));
                intent.putExtra("NFC", String.valueOf(data.getNFC()));
                intent.putExtra("bluetooth", String.valueOf(data.getBluetooth()));
                intent.putExtra("working", String.valueOf(data.getWorking()));

                Log.w("position", String.valueOf(position));
                Log.i("getLocation", data.getLocation());
                Log.i("getLatitude", String.valueOf(data.getLatitude()));
                Log.i("getLongitude", String.valueOf(data.getLongitude()));
                Log.i("getWlan", String.valueOf(data.getWlan()));
                Log.i("getSound", String.valueOf(data.getSound()));
                Log.i("getVibrate", String.valueOf(data.getVibrate()));
                Log.i("getSilent", String.valueOf(data.getSilent()));
                Log.i("setNoUse", String.valueOf(data.getNouse()));
                Log.i("getDataNetwork", String.valueOf(data.getDataNetwork()));
                Log.i("getNFC", String.valueOf(data.getNFC()));
                Log.i("getBluetooth", String.valueOf(data.getBluetooth()));
                Log.i("getWorking", String.valueOf(data.getWorking()));

                startActivityForResult(intent, 0);
            }
        });

        // 커스텀 리스트뷰의 항목을 오래동안 클릭하는 경우
        view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                inputData data = adapter.mListData.get(position);

                // 해당 프리셋을 사용하는 경우
                if(data.getWorking()) {
                    // 프리셋 사용안함으로 변경
                    data.setWorking(false);
                }
                // 해당 프리셋을 사용안하는 경우
                else {
                    // 프리셋 사용으로 변경
                    data.setWorking(true);
                }

                adapter.notifyDataSetChanged();

                // 리턴값 false - onitemclicklistener도 같이 작동, true - onitemlongclicklistener만 작동
                return true;
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
        newBtn.setBackgroundResource(R.drawable.more);

        // 버튼의 클릭리스너 생성
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = adapter.getCount();
                Intent intent = new Intent(MainActivity.this, InputDataActivity.class);
                intent.putExtra("position", String.valueOf(size));
                startActivityForResult(intent, newData);
            }
        });

               /*
        앱의 데이터를 휴대폰의 로컬데이터로 저장하는 SharedPreferences 인터페이스 사용
        앱의 데이터는 onDestroy() 호출시에 로컬 데이터로 저장한다
        앱 시작시 onCreate() 호출시에 로컬 데이터로 저장되어 있는 정보들을 파싱하여서 커스텀 리스트뷰의 각 항목에 다시 집어넣는다.
         */
        SharedPreferences sp = this.getSharedPreferences("sp", MODE_PRIVATE);
        int size = 0;
        try {
            // onDestroy()에서 로컬 데이터로 저장한 커스텀 리스트뷰의 항목 개수를 받아와서 int형 변수로 변환
            if (!sp.getString("size", "").equals(""))
                size = Integer.parseInt(sp.getString("size", ""));
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }

        Log.d("size", String.valueOf(size));

        // 반복문을 이용하여서 로컬 데이터를 파싱하여서 커스텀 리스트뷰의 항목으로 추가
        for (int i = 0; i < size; i++) {
            String location;
            double latitude;
            double longitude;
            boolean wlan;
            boolean sound;
            boolean vibrate;
            boolean silent;
            boolean no_use;
            boolean dataNetwork;
            boolean nfc;
            boolean bluetooth;
            boolean working;

            String data = sp.getString(String.valueOf(i), "");
            String[] dataArray = data.split("!@#@!");

            location = dataArray[0];
            latitude = Double.parseDouble(dataArray[1]);
            longitude = Double.parseDouble(dataArray[2]);
            wlan = Boolean.valueOf(dataArray[3]);
            sound = Boolean.valueOf(dataArray[4]);
            vibrate = Boolean.valueOf(dataArray[5]);
            silent = Boolean.valueOf(dataArray[6]);
            no_use = Boolean.valueOf(dataArray[7]);
            dataNetwork = Boolean.valueOf(dataArray[8]);
            nfc = Boolean.valueOf(dataArray[9]);
            bluetooth = Boolean.valueOf(dataArray[10]);
            working = Boolean.valueOf(dataArray[11]);

            adapter.addItem(location, latitude, longitude, wlan, sound, vibrate, silent, no_use, dataNetwork, nfc, bluetooth, working);

            adapter.notifyDataSetChanged();
        }

    }

    // 액티비티가 특정한 값을 받아올 때 자동 호출
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // requestCode를 사용해서 어떠한 요청인지 구분한다. 0:항목수정, 1:새항목
        switch (requestCode) {
            case editData :
                // resultCode를 이용하여서 데이터 입력 화면에서 어떠한 결과를 처리하여서 데이터를 넘겨주는지 확인한다
                if (resultCode == RESULT_OK) {
                    int position = Integer.parseInt(intent.getStringExtra("position"));

                    Log.w("position", String.valueOf(position));

                    inputData data = adapter.mListData.get(position);

                    Double latitude = round(Double.parseDouble(intent.getStringExtra("latitude")));
                    Double longitude = round(Double.parseDouble(intent.getStringExtra("longitude")));

                    data.setLocation(intent.getStringExtra("location"));
                    data.setLatitude(latitude);
                    data.setLongitude(longitude);
                    data.setWlan(Boolean.valueOf(intent.getStringExtra("setWlan")));
                    data.setSound(Boolean.valueOf(intent.getStringExtra("setSound")));
                    data.setVibrate(Boolean.valueOf(intent.getStringExtra("setVibrate")));
                    data.setSilent(Boolean.valueOf(intent.getStringExtra("setSilent")));
                    data.setNouse(Boolean.valueOf(intent.getStringExtra("setNouse")));
                    data.setDataNetwork(Boolean.valueOf(intent.getStringExtra("setDataNetwork")));
                    data.setNFC(Boolean.valueOf(intent.getStringExtra("setNFC")));
                    data.setBluetooth(Boolean.valueOf(intent.getStringExtra("setBluetooth")));
                    data.setWorking(Boolean.valueOf(intent.getStringExtra("setWorking")));
                }
                adapter.notifyDataSetChanged();
                break;

            case newData:
                if (resultCode == RESULT_OK) {
                    Double latitude = round(Double.parseDouble(intent.getStringExtra("latitude")));
                    Double longitude = round(Double.parseDouble(intent.getStringExtra("longitude")));

                    adapter.addItem(
                            intent.getStringExtra("location"),
                            latitude,
                            longitude,
                            Boolean.valueOf(intent.getStringExtra("setWlan")),
                            Boolean.valueOf(intent.getStringExtra("setSound")),
                            Boolean.valueOf(intent.getStringExtra("setVibrate")),
                            Boolean.valueOf(intent.getStringExtra("setSilent")),
                            Boolean.valueOf(intent.getStringExtra("setNoUse")),
                            Boolean.valueOf(intent.getStringExtra("setDataNetwork")),
                            Boolean.valueOf(intent.getStringExtra("setNFC")),
                            Boolean.valueOf(intent.getStringExtra("setBluetooth")),
                            Boolean.valueOf(intent.getStringExtra("setWorking"))
                    );

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

            if(data.getWorking()) {
                holder.location.setBackgroundResource(R.drawable.preseton);
            }
            else{
                holder.location.setBackgroundResource(R.drawable.presetoff);
            }
            adapter.notifyDataSetChanged();

            return convertView;
        }

        // 새 항목 추가
        public void addItem(String location, double latitude, double longitude, boolean wlan, boolean sound, boolean vibrate, boolean silent, boolean no_use, boolean dataNetwork, boolean nfc, boolean bluetooth, boolean working) {
            inputData addInfo;
            addInfo = new inputData(location, latitude, longitude, wlan, sound, vibrate, silent, no_use, dataNetwork, nfc, bluetooth, working);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 앱의 데이터를 로컬 데이터로 저장하기 위한 객체 생성
        SharedPreferences sp = this.getSharedPreferences("sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // 반복문을 이용하여서 커스텀 리스트뷰의 각 항목을 순서대로 저장
        for (int i = 0; i < adapter.getCount(); i++) {
            inputData data = adapter.mListData.get(i);
            String str = data.getLocation() + "!@#@!"
                    + data.getLatitude() + "!@#@!"
                    + data.getLongitude() + "!@#@!"
                    + data.getWlan() + "!@#@!"
                    + data.getSound() + "!@#@!"
                    + data.getVibrate() + "!@#@!"
                    + data.getSilent() + "!@#@!"
                    + data.getNouse() + "!@#@!"
                    + data.getDataNetwork() + "!@#@!"
                    + data.getNFC() + "!@#@!"
                    + data.getBluetooth()+ "!@#@!"
                    + data.getWorking();

            editor.putString(String.valueOf(i), str);
        }
        editor.putString("size", String.valueOf(adapter.getCount())); // 커스텀 리스트뷰의 항목 개수를 저장
        editor.commit(); // 로컬 데이터로 저장
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
            for(int i=0 ; i<adapter.getCount(); i++) {

                inputData data = adapter.mListData.get(i);

                if(data.getWorking()) {
                    if(data.getLatitude() == latitude && data.getLongitude() == longitude){
                        setWifi(data.getWlan());
                        setDataNet(data.getDataNetwork());
                        setBluetooth(data.getBluetooth());
                        if(data.getSound())
                            setSound(0);
                        else if(data.getVibrate()){
                            setSound(1);
                        }
                        else if(data.getSilent()){
                            setSound(2);
                        }
                        setNFC(data.getNFC());
                    }
                }
            }
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
            }
            else {
                wManager.setWifiEnabled(false);
                Toast.makeText(MainActivity.this, "NOW WIFI OFF.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(netName.equals("WIFI")) {
                Toast.makeText(MainActivity.this, "ALREADY WIFI ON", Toast.LENGTH_SHORT).show();
            }
            else {
                wManager.setWifiEnabled(true);
                Toast.makeText(MainActivity.this, "NOW WIFI ON", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setSound(int val) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (val) {
            case 0:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); //소리
                Toast.makeText(MainActivity.this, "NOW RINGTONE", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); //진동
                Toast.makeText(MainActivity.this, "NOW VIBRATE", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT); //무음
                Toast.makeText(MainActivity.this, "NOW SILENT", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                break;
        }
    }

    public void setBluetooth(boolean val)
    {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if(!val){
            if(!adapter.isEnabled())
                Toast.makeText(MainActivity.this, "ALREADY BLUETOOTH OFF.", Toast.LENGTH_SHORT).show();
            else {
                adapter.disable();
                Toast.makeText(MainActivity.this, "NOW BLUETOOTH OFF.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(adapter.isEnabled())
                Toast.makeText(MainActivity.this, "ALREADY BLUETOOTH ON", Toast.LENGTH_SHORT).show();
            else {
                adapter.enable();
                Toast.makeText(MainActivity.this, "NOW BLUETOOTH ON", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setNFC(boolean val) {
        NfcAdapter mNfcAdapter= NfcAdapter.getDefaultAdapter(getApplicationContext());

        if (mNfcAdapter == null) {
            // NFC is not supported
            Toast.makeText(MainActivity.this, "NFC Cannot Used.", Toast.LENGTH_SHORT).show();
            return ;
        }

        if(val)
                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
    }

    public void setDataNet(boolean val) {
        if(val)
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
    }
}

