package com.slfuture.pretty.view.form;

import com.slfuture.pretty.R;
import com.slfuture.pretty.im.view.SingleChatActivity;

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
	}
}
