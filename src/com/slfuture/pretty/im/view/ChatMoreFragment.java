package com.slfuture.pretty.im.view;

import java.lang.reflect.Field;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * 对话更多窗口层
 */
@ResourceView(id = R.layout.fragment_chat_more)
public class ChatMoreFragment extends FragmentEx {
	@ResourceView(id = R.id.chatmore_button_picture)
	public ImageButton btnPicture;
	@ResourceView(id = R.id.chatmore_button_dial)
	public ImageButton btnDial;
	@ResourceView(id = R.id.chatmore_button_location)
	public ImageButton btnLocation;
	@ResourceView(id = R.id.chatmore_button_money)
	public ImageButton btnMoney;


	/**
	 * 界面创建
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		com.slfuture.pluto.view.annotation.ResourceView activityView = this.getClass().getAnnotation(com.slfuture.pluto.view.annotation.ResourceView.class);
		if(null == activityView) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		else {
			// Activity
			View result = inflater.inflate(activityView.id(), container, false);
			// Control
			for(Field field : this.getClass().getFields()) {
				com.slfuture.pluto.view.annotation.ResourceView controlView = field.getAnnotation(com.slfuture.pluto.view.annotation.ResourceView.class);
				if(null == controlView) {
					continue;
				}
				try {
					field.set(this, result.findViewById(controlView.id()));
				}
				catch (IllegalAccessException e) {
					Log.e("pluto", "FragmentEx.onCreate() failed", e);
				}
				catch (IllegalArgumentException e) {
					Log.e("pluto", "FragmentEx.onCreate() failed", e);
				}
			}
			return result;
		}
	}
	
	/**
	 * 界面创建
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
}
