package com.slfuture.pretty.view.form;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.type.core.ITable;
import com.slfuture.pretty.Program;
import com.slfuture.pretty.R;
import com.slfuture.pretty.general.view.form.ImageActivity;
import com.slfuture.pretty.im.Module;
import com.slfuture.pretty.im.core.IReactor;
import com.slfuture.pretty.im.view.form.SingleChatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 引导页
 */
@ResourceView(clazz=R.layout.class, field="activity_load")
public class LoadActivity extends ActivityEx {
	@ResourceView(clazz=R.id.class, field="load_button_1")
	public Button btn1;
	@ResourceView(clazz=R.id.class, field="load_button_2")
	public Button btn2;
	@ResourceView(clazz=R.id.class, field="load_button_3")
	public Button btn3;
	@ResourceView(clazz=R.id.class, field="load_button_4")
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
				Module.context = Program.application;
				Module.reactor = new IReactor() {
					@Override
					public Bitmap getPhoto(String userId) {
						return GraphicsHelper.decodeResource(LoadActivity.this, R.drawable.demo_photo);
					}
					@Override
					public String getName(String userId) {
						return "柳君";
					}
					@Override
					public String getUserId() {
						return "appuser_2879";
					}
					@Override
					public String getPassword() {
						return "15021819287";
					}
					@Override
					public void onConflict() { }
					@Override
					public void onCommand(String from, String action, ITable<String, Object> data) { }
				};
				Module.initialize();
				Module.login(new IEventable<Boolean>() {
					@Override
					public void on(Boolean data) {
						if(!data) {
							return;
						}
						Intent intent = new Intent(LoadActivity.this, SingleChatActivity.class);
						intent.putExtra("selfId", "appuser_2879");
						intent.putExtra("remoteId", "appuser_1679");
						LoadActivity.this.startActivity(intent);
					}
				});
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
