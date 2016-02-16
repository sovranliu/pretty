package com.slfuture.pretty.view.form;

import com.slfuture.pretty.R;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 引导页
 */
@ResourceView(clazz=R.layout.class, field="activity_test")
public class TestActivity extends ActivityEx {
	@ResourceView(clazz=R.id.class, field="test_image_1")
	public ImageView img1;
	@ResourceView(clazz=R.id.class, field="test_image_control1")
	public ImageView ctl1;
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		ctl1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AnimationSet set = new AnimationSet(true);
				TranslateAnimation animation = new TranslateAnimation(0, 0, -100, 100);
				animation.setDuration(1000);
				set.addAnimation(animation);
				img1.startAnimation(set);
			}
		});
	}
}
