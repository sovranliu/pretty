package com.slfuture.pretty.view.form;

import com.slfuture.pretty.R;
import com.slfuture.pretty.general.view.form.ImageActivity;
import com.slfuture.pretty.im.view.form.SingleChatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 引导页
 */
@ResourceView(id=R.layout.activity_load)
public class LoadActivity extends ActivityEx {
	@ResourceView(id=R.id.load_button_1)
	public Button btn1;
	@ResourceView(id=R.id.load_button_2)
	public Button btn2;
	@ResourceView(id=R.id.load_button_3)
	public Button btn3;
	@ResourceView(id=R.id.load_button_4)
	public Button btn4;
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoadActivity.this, SingleChatActivity.class);
				LoadActivity.this.startActivity(intent);
			}
		});
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoadActivity.this, TestActivity.class);
				LoadActivity.this.startActivity(intent);
			}
		});
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoadActivity.this, ImageActivity.class);
				intent.putExtra("path", "/storage/emulated/0/pretty/image/bd_logo1.png");
				intent.putExtra("url", "http://www.slfuture.com/home/images/background.jpg");
				LoadActivity.this.startActivity(intent);
			}
		});
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoadActivity.this, TestListViewActivity.class);
				LoadActivity.this.startActivity(intent);
			}
		});
	}
}
