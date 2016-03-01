package com.slfuture.pretty.general.utility;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pretty.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		showSelector(activity, new IEventable<Integer>() {
			@Override
			public void on(Integer position) {
				switch(position) {
				case 0:
					String status = Environment.getExternalStorageState();
					if (!status.equals(Environment.MEDIA_MOUNTED)) {
						Toast.makeText(activity, "SD卡不可用", Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");
					activity.startActivityForResult(intentCamera, INTENT_REQUEST_CAMERA);
					break;
				case 1:
					Intent intentPhone = new Intent();
					intentPhone.setType("image/*");
					intentPhone.setAction(Intent.ACTION_GET_CONTENT);
					activity.startActivityForResult(intentPhone, INTENT_REQUEST_PHONE);
					break;
				case 2:
					break;
				}
			}
		}, "拍  照" , "手机相册", "取  消");
	}

	/**
	 * 打开单选框
	 * 
	 * @param context 上下文
	 * @param parameters 参数列表
	 */
	public static void showSelector(Context context, final IEventable<Integer> callback, String... parameters) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setGravity(Gravity.BOTTOM);
		window.setAttributes(layoutParams);
		LinearLayout contentLayout = new LinearLayout(context);
		LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		contentLayout.setGravity(Gravity.BOTTOM);
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		contentLayout.setBackgroundColor(context.getResources().getColor(R.color.grey_bg));
		boolean sentry = false;
		int position = -1;
		for(int i = 0; i < parameters.length; i++) {
			String parameter = parameters[i];
			if(Text.isBlank(parameter)) {
				sentry = true;
				continue;
			}
			position++;
			TextView textView = new TextView(context);
			textView.setText(parameter);
			textView.setTag(position);
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(context.getResources().getColor(R.color.grey_text));
			textView.setTextSize(18);
			textView.setBackgroundColor(Color.WHITE);
			textView.setHeight(GraphicsHelper.dip2px(context, 40));
			LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			if(sentry) {
				textLayoutParams.topMargin = GraphicsHelper.dip2px(context, 2);
			}
			else {
				textLayoutParams.topMargin = GraphicsHelper.dip2px(context, 1);
			}
			contentLayout.addView(textView, textLayoutParams);
			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.on((Integer) v.getTag());
					alertDialog.cancel();
				}
			});
		}
		window.setContentView(contentLayout, contentLayoutParams);
	}
	
	/**
	 * 打开单选框
	 * 
	 * @param context 上下文
	 */
	public static AlertDialog showWaiting(Context context) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setGravity(Gravity.CENTER);
		window.setAttributes(layoutParams);
		window.setContentView(R.layout.dialog_waiting);
		ViewGroup background = (ViewGroup) window.findViewById(R.id.waiting_layout_background);
		background.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// alertDialog.cancel();
			}
		});
		ImageView icon = (ImageView) window.findViewById(R.id.waiting_image_icon);
		final RotateAnimation animation = new RotateAnimation(0f, 720f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        icon.startAnimation(animation);
        return alertDialog;
	}
}
