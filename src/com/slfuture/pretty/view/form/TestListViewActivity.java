package com.slfuture.pretty.view.form;

import com.slfuture.carrie.base.type.List;
import com.slfuture.carrie.base.type.Table;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@ResourceView(clazz=R.layout.class, field="activity_testlistview")
public class TestListViewActivity extends ActivityEx {
	public class ViewHolder {
		public TextView label;
	}
	
	public class TestAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        
        public TestAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return dataList.size();
        }
        @Override
        public Object getItem(int arg0) {
            return null;
        }
        @Override
        public long getItemId(int arg0) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.listitem_testitem, null);
                //
                holder.label = (TextView)convertView.findViewById(R.id.testitem_label_title);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }
            Table<String, Object> map = dataList.get(position);
            holder.label.setText((String) map.get("title"));
            holder.label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	Toast.makeText(TestListViewActivity.this, "元素被点击", Toast.LENGTH_LONG).show();
                }
            });
            return convertView;
        }
    }
	
	public class TestOnScrollListener implements OnScrollListener {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) { }

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if(0 == totalItemCount) {
				return;
			}
			if(0 == firstVisibleItem) {  
                // 上拉
				scrollStatus = -1;
				return;
			}
            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {  
                 // 下拉
            	scrollStatus = 1;
				return;
            }
            scrollStatus = 0;
		}
	}

	public class TestOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchY =  event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				if(-1 == scrollStatus && event.getRawY() > touchY + 20) {
					if(View.GONE == loading.getVisibility()) {
						loading.setVisibility(View.VISIBLE);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if(-1 == scrollStatus && event.getRawY() > touchY + 20) {
					//
					for(int i = 0; i <2; i++) {
						Table<String, Object> table = new Table<String, Object>();
						table.put("title", "标题-" + i);
						dataList.insert(0, table);
					}
					adapter.notifyDataSetChanged();
					listTest.setSelection(3);
				}
				break;
			}
			return false;
		}
	}
	
	
	/**
	 * 列表控件
	 */
	@ResourceView(clazz=R.id.class, field="test_listview_test")
	public ListView listTest;
	/**
	 * 数据列表
	 */
	public List<Table<String, Object>> dataList = new List<Table<String, Object>>();
	/**
	 * 加载图
	 */
	public ImageView loading = null;
	/**
	 * 适配器
	 */
	public TestAdapter adapter = null;
	/**
	 * 滚动状态
	 * -1：顶部，1：底部，0：其他
	 */
	private int scrollStatus = 0;
	/**
	 * 触摸状态
	 * 当前高度
	 */
	private float touchY = 0.0f;

	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		RelativeLayout layout = new RelativeLayout(this);
		layout.setGravity(RelativeLayout.CENTER_IN_PARENT);
		loading = new ImageView(this);
		loading.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		loading.setScaleType(ScaleType.FIT_XY);
		loading.setImageResource(R.drawable.loading);
		layout.addView(loading);
		listTest.addHeaderView(layout);
		listTest.setOnScrollListener(new TestOnScrollListener());
		listTest.setOnTouchListener(new TestOnTouchListener());
		loading.setVisibility(View.GONE);
		//
		dataList.clear();
		for(int i = 0; i <10; i++) {
			Table<String, Object> table = new Table<String, Object>();
			table.put("title", "标题-" + i);
			dataList.add(table);
		}
		adapter = new TestAdapter(TestListViewActivity.this);
		listTest.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listTest.setSelection(1);
	}
}
