package com.slfuture.pretty.general.utility;

import com.slfuture.pretty.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 常见帮助类
 */
public class GeneralHelper {
	/**
	 * 请求手机磁盘
	 */
	public static final int INTENT_REQUEST_PHONE = 111;
	/**
	 * 请求手机
	 */
	public static final int INTENT_REQUEST_CAMERA = 112;


	/**
	 * 隐藏构造函数
	 */
	private GeneralHelper() { }

	/**
	 * 选择图片
	 * 
	 * @param activity 上下文
	 */
	public static void selectImage(final Activity activity) {
		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setGravity(Gravity.BOTTOM);
		window.setAttributes(layoutParams);
		window.setContentView(R.layout.dialog_imageselect);
		TextView txtCancel = (TextView) window.findViewById(R.id.imageselect_cancel);
		txtCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.cancel();
			}
		});
		TextView txtPhone = (TextView) window.findViewById(R.id.imageselect_phone);
		txtPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				activity.startActivityForResult(intent, INTENT_REQUEST_PHONE);
				alertDialog.hide();
			}
		});
		TextView txtCamera = (TextView) window.findViewById(R.id.imageselect_camera);
		txtCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String status = Environment.getExternalStorageState();
				if (!status.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(activity, "SD卡不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				activity.startActivityForResult(intent, INTENT_REQUEST_CAMERA);
				alertDialog.hide();
			}
		});
	}
}
