package kit.ce.ash.mobileproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;


public class InputDataActivity extends Activity{

    EditText location;
    EditText latitude;
    EditText longitude;

    double getLatitudeMap;
    double getLongitudeMap;

    ListView settingListView;

    ListViewAdapter adapter;

    Intent getIntent;

    int position;

    boolean setWlan;
    boolean setSound;
    boolean setVibrate;
    boolean setSilent;
    boolean setNoUse;
    boolean setDataNetwork;

    boolean getWlan;
    boolean getSound;
    boolean getVibrate;
    boolean getSilent;
    boolean getNoUse;
    boolean getDataNetwork;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        getIntent = getIntent();



        location = (EditText)findViewById(R.id.location);
        latitude = (EditText)findViewById(R.id.latitude);
        longitude = (EditText)findViewById(R.id.longitude);

        if(getIntent.getStringExtra("edit") != null){
            setData(getIntent);
        }

        settingListView = (ListView)findViewById(R.id.checkListView);
        settingListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        adapter = new ListViewAdapter(this); // 어댑터 객채 생성
        settingListView.setAdapter(adapter); // 커스텀 리스트뷰에 어댑터 연결

        adapter.addItem("와이파이");
        adapter.addItem("데이터네트워크");



        Button changeData = (Button)findViewById(R.id.changeData);
        changeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(getIntent.getStringExtra("edit") != null)
                    intent.putExtra("position", getIntent.getStringExtra("position"));

                intent.putExtra("location", location.getText().toString());
                intent.putExtra("latitude", latitude.getText().toString());
                intent.putExtra("longitude", longitude.getText().toString());
                intent.putExtra("setWlan", String.valueOf(setWlan));
                intent.putExtra("setSound", String.valueOf(setSound));
                intent.putExtra("setVibrate", String.valueOf(setVibrate));
                intent.putExtra("setSilent", String.valueOf(setSilent));
                intent.putExtra("setNoUse", String.valueOf(setNoUse));
                intent.putExtra("setDataNetwork", String.valueOf(setDataNetwork));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button openMap = (Button)findViewById(R.id.openMap);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputDataActivity.this, GMapActivity.class);

                if(latitude.getText().toString().equals("") || longitude.getText().toString().equals("")){
                    intent.putExtra("edit", "new");
                }
                else{
                    intent.putExtra("latitude", String.valueOf(latitude.getText()));
                    intent.putExtra("longitude", String.valueOf(longitude.getText()));
                    intent.putExtra("edit", "edit");
                }
                startActivityForResult(intent, 2);
            }
        });
    }

    // 액티비티가 특정한 값을 받아올 때 자동 호출
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // requestCode를 사용해서 어떠한 요청인지 구분한다. 0:항목수정, 1:새항목, 2 : 지도에서 받아옴
        switch (requestCode) {
            case 2:
                Log.i("requestCode", "2");
                // resultCode를 이용하여서 데이터 입력 화면에서 어떠한 결과를 처리하여서 데이터를 넘겨주는지 확인한다
                if (resultCode == RESULT_OK) {
                    try {
                        getLatitudeMap = round(Double.parseDouble(intent.getStringExtra("latitude")));
                        getLongitudeMap = round(Double.parseDouble(intent.getStringExtra("longitude")));

                        latitude.setText(String.valueOf(getLatitudeMap));
                        longitude.setText(String.valueOf(getLongitudeMap));


                        Log.i("getLatitude", String.valueOf(getLatitudeMap));
                        Log.i("getLongitude", String.valueOf(getLongitudeMap));
                    }
                    catch (NullPointerException e){
                        Log.e("nullPoint","");
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case 1:
                Log.d("requestCode", "1");



                adapter.notifyDataSetChanged();
                break;
            case 0:
                Log.i("requestCode", "0");

                adapter.notifyDataSetChanged();
                break;
        }
    }

    private class ViewHolder {
        public TextView setting;
        public CheckBox checkBox;
    }

    // 커스텀 리스트 뷰 어댑터
    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = getApplicationContext();
        private ArrayList<inputData> mListData = new ArrayList<>();
        RadioGroup soundGroup;
        RadioButton mbutton[] = null;

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            // view가 없다면 새로운 뷰를 생성
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.datalistview, null);

                // 뷰홀더를 통해 커스텀 리스트뷰 내부의 객체 생성
                holder.setting = (TextView) convertView.findViewById(R.id.setting);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                soundGroup = (RadioGroup)findViewById(R.id.SoundGroup);

                mbutton = new RadioButton[4];

                for(int i=0; i<4; i++){
                    mbutton[i] = new RadioButton(mContext);

                    mbutton[i].setId(i);
                }


                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            inputData data = mListData.get(position);

            holder.setting.setText(data.getSetting());

            holder.checkBox.setChecked(((ListView)parent).isItemChecked(position));

            // 체크박스가 체크되었는지 아닌지 확인하는 리스너
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        Log.d("isChecked", holder.setting.getText().toString());
                        setPreset(holder.setting.getText().toString(), position, true);
                    }
                    else{
                        Log.d("isChecked","nothing");
                        setPreset(holder.setting.getText().toString(), position, false);
                    }
                }
            });

            // 라디오그룹 체크되었는지 확인하는 리스너
            soundGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    for(int i=0; i<4; i++) {
                        RadioButton btn = (RadioButton) group.getChildAt(i);

                        if(btn.getId() == checkedId){
                            setPreset(btn.getText().toString(), position, true);
                            Log.i(btn.getText().toString(), "checked");
                        }
                    }
                }
            });


            if(getIntent.getStringExtra("edit") != null){
                if(holder.setting.getText().toString().equals("와이파이")){
                    holder.checkBox.setChecked(getWlan);
                    Log.i("와이파이","");
                }

                if(holder.setting.getText().toString().equals("데이터네트워크")){
                    holder.checkBox.setChecked(getDataNetwork);
                    Log.i("데이터네트워크","");
                }

                if(getSound){
                    RadioButton btn = (RadioButton)findViewById(R.id.radio0);
                    btn.setChecked(getSound);
                    Log.i("소리","");
                }
                else if(getVibrate){
                    RadioButton btn = (RadioButton)findViewById(R.id.radio1);
                    btn.setChecked(getVibrate);
                    Log.i("진동","");
                }
                else if(getSilent){
                    RadioButton btn = (RadioButton)findViewById(R.id.radio2);
                    btn.setChecked(getSilent);
                    Log.i("무음","");
                }
                else if(getNoUse){
                    RadioButton btn = (RadioButton)findViewById(R.id.radio3);
                    btn.setChecked(getNoUse);
                    Log.i("사용안함","");
                }
            }


            adapter.notifyDataSetChanged();

            return convertView;
        }

        // 새 항목 추가
        public void addItem(String setting) {
            inputData addInfo;
            addInfo = new inputData(setting);

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

    private void setPreset(String preset, int position, boolean value){
        switch(preset){
            case "와이파이" :
                setWlan = value;
                break;
            case "소리" :
                setSound = value;
                setVibrate = !value;
                setSilent = !value;
                setNoUse = !value;
                break;
            case "진동" :
                setSound = !value;
                setVibrate = value;
                setSilent = !value;
                setNoUse = !value;
                break;
            case "무음" :
                setSound = !value;
                setVibrate = !value;
                setSilent = value;
                setNoUse = !value;
                break;
            case "사용 안함" :
                setSound = !value;
                setVibrate = !value;
                setSilent = !value;
                setNoUse = value;
                break;
            case "데이터네트워크" :
                setDataNetwork = value;

                break;
        }
        Log.i("getWlan", String.valueOf(setWlan));
        Log.i("getDataNetwork", String.valueOf(setDataNetwork));
        Log.i("getSound", String.valueOf(setSound));
        Log.i("getVibrate", String.valueOf(setVibrate));
        Log.i("getSilent", String.valueOf(setSilent));
        Log.i("getNouse", String.valueOf(setNoUse));
    }

    public void setData(Intent getIntent){

        position = Integer.parseInt(getIntent.getStringExtra("position"));


        Log.i("location", getIntent.getStringExtra("location"));
        Log.i("location", getIntent.getStringExtra("latitude"));
        Log.i("location", getIntent.getStringExtra("longitude"));
        Log.i("wlan", getIntent.getStringExtra("wlan"));
        Log.i("sound", getIntent.getStringExtra("sound"));
        Log.i("vibrate", getIntent.getStringExtra("vibrate"));
        Log.i("silent", getIntent.getStringExtra("silent"));
        Log.i("noUse", getIntent.getStringExtra("noUse"));
        Log.i("dataNetwork", getIntent.getStringExtra("dataNetwork"));

        location.setText(getIntent.getStringExtra("location"));
        latitude.setText(getIntent.getStringExtra("latitude"));
        longitude.setText(getIntent.getStringExtra("longitude"));

        getWlan = Boolean.valueOf(getIntent.getStringExtra("wlan"));
        getDataNetwork = Boolean.valueOf(getIntent.getStringExtra("dataNetwork"));
        getSound = Boolean.valueOf(getIntent.getStringExtra("sound"));
        getVibrate = Boolean.valueOf(getIntent.getStringExtra("vibrate"));
        getSilent = Boolean.valueOf(getIntent.getStringExtra("silent"));
        getNoUse = Boolean.valueOf(getIntent.getStringExtra("noUse"));

    }


    public double round(double val){
        return Math.round(val*1000)/1000.0;
    }
}
