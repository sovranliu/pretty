package com.slfuture.pretty.general.view.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

/**
 * 单选对话框
 */
@ResourceView(clazz=R.layout.class, field="activity_radio")
public class RadioActivity extends ActivityEx {
	/**
	 * 放弃修改
	 */
	public final static int RESULT_CANCEL = 0;
	/**
	 * 已更新
	 */
	public final static int RESULT_UPDATED = 1;

	/**
	 * 头部
	 */
	@ResourceView(clazz=R.id.class, field="radio_image_close")
	public ImageView imgClose;
	@ResourceView(clazz=R.id.class, field="radio_label_title")
	public TextView labTitle;
	@ResourceView(clazz=R.id.class, field="radio_list")
	public ListView list;

	/**
	 * 标题
	 */
	protected String title = "";
	/**
	 * 数据列表
	 */
	protected ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 当前选择索引
	 */
	protected int current = -1;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		load();
		prepare();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		dealReturn();
		dealList();
	}

	/**
	 * 处理返回按钮
	 */
	public void dealReturn() {
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("index", current);
				RadioActivity.this.setResult(RESULT_CANCEL, intent);
				RadioActivity.this.finish();
			}
		});
		labTitle.setText(title);
	}

	/**
	 * 处理列表
	 */
	private void dealList() {
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataList, R.layout.listitem_radio, new String[]{"caption", "status"}, new int[]{R.id.radiolist_label_caption, R.id.radiolist_image_status});
		simpleAdapter.setViewBinder(new ViewBinder() {
			@SuppressWarnings("deprecation")
			public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView)view;
                    Bitmap bitmap = (Bitmap) data;
                    imageView.setImageDrawable(new BitmapDrawable(bitmap));
                    return true;
                }
                return false;
            }
        });
		list.setAdapter(simpleAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				onSelect(index);
            }
		});
	}

	/**
	 * 加载数据
	 */
	public void load() {
		title = this.getIntent().getStringExtra("title");
		if(null == title) {
			title = "";
		}
		String[] items = this.getIntent().getStringArrayExtra("items");
		if(null == items) {
			return;
		}
		current = this.getIntent().getIntExtra("index", current);
		int i = 0;
		for(String item : items) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("caption", item);
			if(current == i) {
				map.put("status", BitmapFactory.decodeResource(RadioActivity.this.getResources(), R.drawable.yes));
			}
			i++;
			dataList.add(map);
		}
	}

	/**
	 * 选择回调
	 * 
	 * @param index 选中的索引
	 */
	public void onSelect(int index) {
		current = index;
		Intent intent = new Intent();
		intent.putExtra("index", current);
		RadioActivity.this.setResult(RESULT_UPDATED, intent);
		RadioActivity.this.finish();
	}
}
