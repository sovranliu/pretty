package com.slfuture.pretty.im.view.form;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.R;

import android.os.Bundle;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
}
