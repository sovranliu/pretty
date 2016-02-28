package com.slfuture.pretty.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map.Entry;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnNotificationClickListener;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.type.Table;
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
	 * 通话接收器
	 */
	private static BroadcastReceiver messageReceiver = null;
	/**
	 * 通话接收器
	 */
	private static BroadcastReceiver dialReceiver = null;
	/**
	 * 命令接收器
	 */
	private static BroadcastReceiver commandReceiver = null;
	/**
	 * 连接监听器
	 */
	private static EMConnectionListener connectionListener = null;


    /**
     * 初始化
     *
     * @return 是否初始化成功
     */
	public static boolean initialize() {
		EMChat.getInstance().init(context);
		EMChat.getInstance().setDebugMode(true);
		EMChat.getInstance().setAutoLogin(false);
		//
		messageReceiver = new BroadcastReceiver() {
           	@Override
           	public void onReceive(Context cxt, Intent intent) {
				if(null == reactor) {
					return;
				}
           		String from = intent.getStringExtra("from");
    	        reactor.onCommand(from, "message", new Table<String, Object>());
           	}
        };
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
    	intentFilter.setPriority(3);
        context.registerReceiver(messageReceiver, intentFilter);
        dialReceiver = new BroadcastReceiver() {
           	@Override
           	public void onReceive(Context cxt, Intent intent) {
				if(null == reactor) {
					return;
				}
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
        commandReceiver = new BroadcastReceiver() {
    		@Override
    		public void onReceive(Context context, Intent intent) {
				if(null == reactor) {
					return;
				}
    			EMMessage message = intent.getParcelableExtra("message");
    			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
    			Table<String, Object> data = new Table<String, Object>();
    			for(Entry<String, String> entry : cmdMsgBody.params.entrySet()) {
    				data.put(entry.getKey(), entry.getValue());
    			}
				reactor.onCommand(message.getFrom(), cmdMsgBody.action, data);
    		}
    	};
    	context.registerReceiver(commandReceiver, new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction()));
        connectionListener = new EMConnectionListener() {
    	    @Override
    		public void onConnected() {
    	    	Log.d("pretty", "EMConnectionListener.onConnected()");
    		}
    		@Override
    		public void onDisconnected(final int error) {
    	    	Log.d("pretty", "EMConnectionListener.onDisconnected(" + error + ")");
    			if (EMError.CONNECTION_CONFLICT == error) {
    				if(null == reactor) {
    					return;
    				}
    				reactor.onConflict();
				}
    		}
    	};
    	EMChatManager.getInstance().addConnectionListener(connectionListener);
    	//
        EMChatOptions option = new EMChatOptions();
        option.setNoticeBySound(true);
        option.setNotificationEnable(true);
        option.setOnNotificationClickListener(new OnNotificationClickListener() {
            @Override
            public Intent onNotificationClick(EMMessage msg) {
                return null;
            }
        });
        EMChatManager.getInstance().setChatOptions(option);
        //
        Controller.<EMMessage>doJoin(551, new IEventable<EMMessage>() {
			@Override
			public void on(EMMessage message) {
				if(null == reactor) {
					return;
				}
    			Table<String, Object> data = new Table<String, Object>();
				Field field = null;
				try {
					field = EMMessage.class.getDeclaredField("attributes");
				}
				catch (NoSuchFieldException e) { }
				if(null != field) {
					field.setAccessible(true);
					Hashtable<String, Object> fields = null;
					try {
						fields = (Hashtable<String, Object>) field.get(message);
					}
					catch (Exception e) { }
					if(null != fields) {
						for(Entry<String, Object> entry : fields.entrySet()) {
							data.put(entry.getKey(), entry.getValue());
						}
					}
				}
    			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
    			for(Entry<String, String> entry : cmdMsgBody.params.entrySet()) {
    				data.put(entry.getKey(), entry.getValue());
    			}
				reactor.onCommand(message.getFrom(), cmdMsgBody.action, data);
        		message = null;
			}
        });
        EMChatManager.getInstance().registerEventListener(new EMEventListener() {
	        	@SuppressWarnings("unchecked")
				@Override
	        	public void onEvent(EMNotifierEvent event) {
					if(null == reactor) {
						return;
					}
	        		Controller.<EMMessage>doFork(551,  (EMMessage) event.getData());
	        	}
        	}, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewCMDMessage});
        return true;
	}

    /**
     * 终止
     */
	public static void terminate() {
		if(null != commandReceiver) {
			context.unregisterReceiver(commandReceiver);
			commandReceiver = null;
		}
		if(null != dialReceiver) {
			context.unregisterReceiver(dialReceiver);
			dialReceiver = null;
		}
		if(null != messageReceiver) {
			context.unregisterReceiver(messageReceiver);
			messageReceiver = null;
		}
		if(null != connectionListener) {
			EMChatManager.getInstance().removeConnectionListener(connectionListener);
			connectionListener = null;
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
					if(null != commandReceiver) {
						context.unregisterReceiver(commandReceiver);
						commandReceiver = null;
					}
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
		EMChatManager.getInstance().login(reactor.getUserId(), reactor.getPassword(), new EMCallBack() {
			@Override
			public void onSuccess() {
				Log.d("pretty", "EMChatManager.login() success");
				Controller.<Boolean>doMerge(559, true);
			}
			@Override
			public void onProgress(int progress, String status) { }
			@Override
			public void onError(int code, String message) {
				Log.d("pretty", "EMChatManager.login(" + code + ", '" + message + "') error");
				Controller.<Boolean>doMerge(559, false);
			}
		});
	}

	/**
	 * 获取未读消息个数
	 * 
	 * @param userId 用户ID
	 * @return 未读消息个数
	 */
	public static int getUnreadMessageCount(String userId) {
		if(null == userId) {
			return EMChatManager.getInstance().getUnreadMsgsCount();
		}
		EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
		if(null == conversation) {
			return 0;
		}
        return conversation.getUnreadMsgCount();
	}
}
