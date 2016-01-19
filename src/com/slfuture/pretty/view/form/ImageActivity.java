package com.slfuture.pretty.view.form;

import java.io.File;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.etc.MulitPointTouchListener;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.R;

/**
 * 图片查看界面
 */
@ResourceView(id=R.layout.activity_image)
public class ImageActivity extends ActivityEx {
	@ResourceView(id=R.id.image_image_image)
	public ImageView image;

	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		String path = this.getIntent().getStringExtra("path");
		String url = this.getIntent().getStringExtra("url");
		if(null != path) {
			Bitmap bitmap = GraphicsHelper.decodeFile(new File(path));
			if(null != bitmap) {
				image.setImageBitmap(bitmap);
			}
		}
		if(null != url) {
			Host.doImage("", new ImageResponse(url) {
				@Override
				public void onFinished(Bitmap content) {
					image.setImageBitmap(content);
				}
			}, url);
		}
		//
		Controller.doDelay(new Runnable() {
			@Override
			public void run() {
				image.setOnTouchListener(new MulitPointTouchListener ());
				image.setScaleType(ScaleType.MATRIX);
			}
		}, 200);
	}
}
