package kit.ce.ash.mobileproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LocationService mService; // bind 타입 서비스
    public boolean mBound = false; // 서비스 연결 상태

    ListViewAdapter adapter;

    Button testBtn;
    Button test2Btn;

    // startActivityForResult 에서 다른 액티비티로 넘겨주는 requestCode 값
    int newData = 1;

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

        testBtn = (Button)findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    double latitude = mService.getLatitude();
                    double longtitude = mService.getLongtitude();
                    Toast.makeText(MainActivity.this, "위도 = " + latitude + "\n경도 = " + longtitude, Toast.LENGTH_LONG).show();
                }
                else{
                    bind();
                    Toast.makeText(MainActivity.this, "서비스 바인딩", Toast.LENGTH_SHORT).show();
                }
            }
        });

        test2Btn = (Button)findViewById(R.id.test2Btn);
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
        // requestCode를 사용해서 어떠한 요청인지 구분한다.
        switch (requestCode) {
            case 0:
                // resultCode를 이용하여서 데이터 입력 화면에서 어떠한 결과를 처리하여서 데이터를 넘겨주는지 확인한다
                if (resultCode == RESULT_OK) {

                }
                adapter.notifyDataSetChanged();
                break;
            case 1:
                if (resultCode == RESULT_OK) {

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

            adapter.notifyDataSetChanged();

            return convertView;
        }

        // 새 항목 추가
        public void addItem(String location) {
            inputData addInfo;
            addInfo = new inputData(location);

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

    // ServiceConnection 객체 생성으로 bind타입 서비스와 액티비티간 연결
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

}
