package com.slfuture.pretty.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pretty.im.core.IReactor;
import com.slfuture.pretty.im.view.form.RingActivity;

/**
 * 即时通信模块
 */
public class Module {
	/**
	 * 未知类型通话
	 */
	public final static int DIAL_TYPE_UNKNOWN = 0;
	/**
	 * 语音通话
	 */
	public final static int DIAL_TYPE_AUDIO = 1;
	/**
	 * 视频通话
	 */
	public final static int DIAL_TYPE_VIDEO = 2;


	/**
	 * 上下文
	 */
	public static Context context = null;
	/**
	 * 回调接口
	 */
	public static IReactor reactor = null;
	/**
	 * 呼叫接收器
	 */
	private static BroadcastReceiver dialReceiver = null;


    /**
     * 初始化
     *
     * @return 是否初始化成功
     */
	public static boolean initialize() {
		EMChat.getInstance().init(context);
		EMChat.getInstance().setDebugMode(true);
		//
		dialReceiver = new BroadcastReceiver() {
           	@Override
           	public void onReceive(Context cxt, Intent intent) {
           		String from = intent.getStringExtra("from");
           		String type = intent.getStringExtra("type");
           		int dialType = Module.DIAL_TYPE_UNKNOWN;
           		if("audio".equals(type)) {
           			dialType = Module.DIAL_TYPE_AUDIO;
           		}
           		else if("video".equals(type)) {
           			dialType = Module.DIAL_TYPE_VIDEO;
           		}
           		Intent ringIntent = new Intent(context, RingActivity.class);
           		ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
           		ringIntent.putExtra("type", dialType);
           		ringIntent.putExtra("from", from);
           		context.startActivity(ringIntent);
           	}
        };
        context.registerReceiver(dialReceiver, new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction()));
        return true;
	}

    /**
     * 终止
     */
	public static void terminate() {
		if(null != dialReceiver) {
			context.unregisterReceiver(dialReceiver);
			dialReceiver = null;
		}
		context = null;
    }

	/**
	 * 登录服务器
	 * 
	 * @param callback 回调
	 */
	public static void login(final IEventable<Boolean> callback) {
		Controller.doJoin(559, new IEventable<Boolean>() {
			@Override
			public void on(Boolean data) {
				if(data) {
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					callback.on(true);
				}
				else {
					Toast.makeText(context, "即时通信登录失败", Toast.LENGTH_LONG).show();
					callback.on(false);
				}
			}
		});
		EMChatManager.getInstance().login(reactor.getAccount(), reactor.getPassword(), new EMCallBack() {
			@Override
			public void onSuccess() {
				Log.d("pretty", "EMChatManager.login() success");
				Controller.doMerge(559, true);
			}
			@Override
			public void onProgress(int progress, String status) { }
			@Override
			public void onError(int code, String message) {
				Log.d("pretty", "EMChatManager.login(" + code + ", '" + message + "') error");
				Controller.doMerge(559, false);
			}
		});
	}
}
