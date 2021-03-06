package com.slfuture.pretty.im.view.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.utility.message.ImageMessage;
import com.slfuture.pretty.im.utility.message.TextMessage;
import com.slfuture.pretty.im.utility.message.core.IMessage;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

/**
 * 对话更多窗口层
 */
@ResourceView(clazz=R.layout.class, field="fragment_chat_emoticon")
public abstract class ChatEmoticonFragment extends FragmentEx {
	/**
	 * 自定义表情位图
	 */
	public final static int[] EMOTICON_IMAGE = {R.drawable.emoticon_1, R.drawable.emoticon_2, R.drawable.emoticon_3, R.drawable.emoticon_4, R.drawable.emoticon_5, R.drawable.emoticon_6, R.drawable.emoticon_7, R.drawable.emoticon_8, R.drawable.emoticon_9, R.drawable.emoticon_10, R.drawable.emoticon_11, R.drawable.emoticon_12, R.drawable.emoticon_13, R.drawable.emoticon_14, R.drawable.emoticon_15, R.drawable.emoticon_16};
	/**
	 * 自定义表情文字
	 */
	public final static String[] EMOTICON_WORD = {"问好","无语","黑脸","鄙视","失落","汗颜","好色","贱笑","开心","痛哭","嘻嘻","亲亲","挖鼻","笑脸","不屑","挥手"};
	/**
	 * 表格
	 */
	@ResourceView(clazz=R.id.class, field="chatemoticon_grid_icon")
	public GridView gridIcon;
	/**
	 * 图标数据
	 */
	private ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

	
	/**
	 * 检测消息是否表情转义
	 * 
	 * @param context 上下文
	 * @param message 待检测消息
	 * @return 转义后的消息对象
	 */
	public static IMessage convert(Context context, IMessage message) {
		if(IMessage.TYPE_TEXT != message.type()) {
			return message;
		}
		String text = ((TextMessage) message).text;
		if(!text.startsWith("[") || !text.endsWith("]")) {
			return message;
		}
		text = text.substring(1, text.length() - 1);
		int id = -1;
		for(int i = 0; i < EMOTICON_WORD.length; i++) {
			String word = EMOTICON_WORD[i];
			if(word.equals(text)) {
				id = i;
				break;
			}
		}
		if(-1 == id) {
			return message;
		}
		ImageMessage result = new ImageMessage();
		result.original = GraphicsHelper.decodeResource(context, EMOTICON_IMAGE[id]);
		result.from = message.from();
		result.id = message.id();
		result.orientation = message.orientation();
		result.time = message.time();
		result.thumbnail = result.original;
		return result;
	}

	/**
	 * 是否附着到根视图
	 * 
	 * @return 是否附着到根视图
	 */
	@Override
	protected boolean attachToRoot() {
		return false;
	}

	/**
	 * 界面创建
	 */
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	items.clear();
    	for(int i : EMOTICON_IMAGE) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
    		// map.put("icon", GraphicsHelper.decodeResource(ChatEmoticonFragment.this.getActivity(), i));
        	map.put("icon", i);
        	items.add(map);
    	}
		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(),
				items,
				R.layout.griditem_emoticon,
				new String[] {"icon"},
				new int[] {R.id.emoticon_image_icon});
		gridIcon.setAdapter(adapter);
		gridIcon.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onChoose(EMOTICON_WORD[position]);
			}
		});
		adapter.notifyDataSetChanged();
	}

	/**
	 * 选择一个表情
	 * 
	 * @param emoticon 表情文字
	 */
	public abstract void onChoose(String emoticon);
}
