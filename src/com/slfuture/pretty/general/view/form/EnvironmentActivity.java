package com.slfuture.pretty.general.view.form;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.BitmapFactory;

import com.slfuture.pluto.communication.Environment;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pretty.R;

/**
 * 环境界面
 */
@ResourceView(clazz=R.layout.class, field="activity_radio")
public class EnvironmentActivity extends RadioActivity {
	/**
	 * 加载数据
	 */
	@Override
	public void load() {
		title = "环境选择";
		ArrayList<String> array = new ArrayList<String>();
		int i = 0;
		for(Environment environment : Networking.environments) {
			array.add(environment.name);
			if(environment == Networking.currentEnvironment) {
				current = i;
			}
			i++;
		}
		String[] items = array.toArray(new String[0]);
		i = 0;
		for(String item : items) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("caption", item);
			if(current == i) {
				map.put("status", BitmapFactory.decodeResource(EnvironmentActivity.this.getResources(), R.drawable.yes));
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
	@Override
	public void onSelect(int index) {
		if(-1 == index) {
			return;
		}
		Networking.selectEnvironment(EnvironmentActivity.this, Networking.environments.get(index).name);
		super.onSelect(index);
	}
}
