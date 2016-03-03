package com.slfuture.pretty.general.view.control;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.slfuture.pluto.js.BridgeHandler;
import com.slfuture.pluto.js.CallBackFunction;

/**
 * 浏览器
 */
public class Browser extends com.slfuture.pluto.js.BridgeWebView {
	/**
	 * 当前窗口
	 */
	public Activity activity = null;


	public Browser(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public Browser(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public Browser(Context context) {
		super(context);
	}

	/**
	 * 注册回调函数
	 */
	public void register() {
		registerHandler("closeWindow", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				if(null != activity) {
					activity.finish();
					activity = null;
				}
            }
		});
	}
}
