package kit.ce.ash.mobileproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListViewAdapter adapter;
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

            // 항목을 스퐈이프 하였을 때
            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    adapter.remove(position); // 선택한 항목을 삭제
                }
                adapter.notifyDataSetChanged();
            }
        });

        // 생성한 터치리스너를 커스텀 리스트뷰에 등록
        view.setOnTouchListener(touchListener);
        view.setOnScrollListener(touchListener.makeScrollListener());

        Button newBtn = (Button)findViewById(R.id.newBtn);

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputDataActivity.class);
                startActivityForResult(intent, newData);
            }
        });
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
}
