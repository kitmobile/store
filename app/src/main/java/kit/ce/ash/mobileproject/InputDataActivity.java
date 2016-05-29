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
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;


public class InputDataActivity extends Activity{

    EditText location;
    EditText latitude;
    EditText longitude;
    RadioGroup rg;
    ListView settingListView;

    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        location = (EditText) findViewById(R.id.location);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);

        latitude.setText("36.146");
        longitude.setText("128.393");

        settingListView = (ListView) findViewById(R.id.checkListView);
        settingListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new ListViewAdapter(this); // 어댑터 객채 생성
        settingListView.setAdapter(adapter); // 커스텀 리스트뷰에 어댑터 연결


        adapter.addItem("와이파이");
        adapter.addItem("데이터네트워크");

        Button changeData = (Button) findViewById(R.id.changeData);
        changeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("location", location.getText().toString());
                intent.putExtra("latitude", latitude.getText().toString());
                intent.putExtra("longitude", longitude.getText().toString());
                setResult(RESULT_OK, intent);

                rg = (RadioGroup) findViewById(R.id.SoundGroup);
                RadioButton rd = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                setPreset(rd.getText().toString(), 0, true);    //position 넣고

                finish();
           }
        });

    }

    private class ViewHolder {
        public TextView setting;
        public CheckBox checkBox;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            // view가 없다면 새로운 뷰를 생성
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.datalistview, null);

                // 뷰홀더를 통해 커스텀 리스트뷰 내부의 객체 생성
                holder.setting = (TextView) convertView.findViewById(R.id.location);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            inputData data = mListData.get(position);

            holder.setting.setText(data.getSetting());

            holder.checkBox.setChecked(((ListView)parent).isItemChecked(position));

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
                adapter.mListData.get(position).setWlan(value);
                Toast.makeText(InputDataActivity.this, "getWlan : " + adapter.mListData.get(position).getWlan(), Toast.LENGTH_SHORT).show();
                break;
            case "소리" :
                adapter.mListData.get(position).setSound(value);
                Toast.makeText(InputDataActivity.this, "getSound : " + adapter.mListData.get(position).getSound(), Toast.LENGTH_SHORT).show();
                break;
            case "진동" :
                adapter.mListData.get(position).setVibrate(value);
                Toast.makeText(InputDataActivity.this, "getVibrate : " + adapter.mListData.get(position).getVibrate(), Toast.LENGTH_SHORT).show();
                break;
            case "무음" :
                adapter.mListData.get(position).setSilent(value);
                Toast.makeText(InputDataActivity.this, "getSilent : " + adapter.mListData.get(position).getSilent(), Toast.LENGTH_SHORT).show();
                break;
            case "사용 안함" :
                adapter.mListData.get(position).setNouse(value);
                Toast.makeText(InputDataActivity.this, "No use : " + adapter.mListData.get(position).getNouse(), Toast.LENGTH_SHORT).show();
                break;
            case "데이터네트워크" :
                adapter.mListData.get(position).setDataNetwork(value);
                Toast.makeText(InputDataActivity.this, "getDataNetwork : " + adapter.mListData.get(position).getDataNetwork(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
