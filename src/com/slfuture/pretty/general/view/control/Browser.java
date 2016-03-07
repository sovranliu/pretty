package com.slfuture.pretty.general.view.control;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.js.BridgeHandler;
import com.slfuture.pluto.js.CallBackFunction;
import com.slfuture.pretty.general.core.IBrowserHandler;

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

	/**
	 * 注册回调函数
	 * 
	 * @param functionName 函数名称
	 * @param handler 回调句柄
	 */
	public void register(final IBrowserHandler handler) {
		registerHandler(handler.name(), new BridgeHandler() {
			@Override
			public void handler(String data, final CallBackFunction function) {
				handler.on(data, new IEventable<String>() {
					@Override
					public void on(String event) {
						function.onCallBack(event);
					}
				});
            }
		});
	}
}
