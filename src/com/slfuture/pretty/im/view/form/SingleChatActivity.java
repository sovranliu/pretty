package com.slfuture.pretty.im.view.form;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;

import com.slfuture.carrie.base.time.DateTime;
import com.slfuture.carrie.base.type.List;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.utility.message.Message;
import com.slfuture.pretty.im.utility.message.SoundMessage;
import com.slfuture.pretty.im.utility.message.core.IMessage;
import com.slfuture.pretty.im.view.form.ChatMessagesFragment.IChatMessageAdapter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.File;

import com.slfuture.pluto.config.Configuration;
import com.slfuture.pluto.config.core.IConfig;
import com.slfuture.pluto.sensor.SoundRecorder;
import com.slfuture.pluto.storage.SDCard;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 单聊窗口
 */
@ResourceView(id=R.layout.activity_singlechat)
public class SingleChatActivity extends ActivityEx {
	/**
	 * 消息接收器
	 */
	public class MessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
	        EMMessage emMessage = EMChatManager.getInstance().getMessage(intent.getStringExtra("msgid"));
	        String from = intent.getStringExtra("from");
	        if(from.equals(selfId)) {
	        	return;
	        }
	        if((!from.equals(groupId)) && (!from.equals(remoteId))) {
	        	// 不是本会话窗口内的消息
	        	return;
	        }
	        frgChatMessages.addMessage(Message.build(emMessage, IMessage.ORIENTATION_RECEIVE));
	        abortBroadcast();
		}
	}


	@ResourceView(id=R.id.singlechat_layout_tail)
	public View viewTail;
	@ResourceView(id=R.id.singlechat_image_mode)
	public ImageButton btnMode;
	@ResourceView(id=R.id.singlechat_image_emoticon)
	public ImageButton btnEmoticon;
	@ResourceView(id=R.id.singlechat_image_more)
	public ImageButton btnMore;
	@ResourceView(id=R.id.singlechat_text_text)
	public EditText txtText;
	@ResourceView(id=R.id.singlechat_button_sound)
	public Button btnSound;
	public ChatMoreFragment frgChatMore = null;
	public ChatMessagesFragment frgChatMessages = null;
	
	public SoundRecorder recorder = null;
	public EMConversation conversation = null;
	public MessageBroadcastReceiver receiver = null;
	/**
	 * 自己的用户ID
	 */
	public String selfId;
	/**
	 * 对方的用户ID
	 */
	public String remoteId;
	/**
	 * 群组内聊天的群组ID
	 */
	public String groupId;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IConfig conf = Configuration.root().visit("/program/component/im/sound");
		if(null == conf) {
			throw new RuntimeException("not find config '/program/component/im/sound'");
		}
		recorder = new SoundRecorder(SDCard.root() + conf.get("folder"));
		prepareParameter();
		//
		btnMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(View.GONE == btnSound.getVisibility()) {
					txtText.setVisibility(View.GONE);
					btnSound.setVisibility(View.VISIBLE);
				}
				else {
					txtText.setVisibility(View.VISIBLE);
					btnSound.setVisibility(View.GONE);
				}
			}
		});
		//
		txtText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event != null && (KeyEvent.KEYCODE_ENTER == event.getKeyCode())) {
					
				}
				return false;
			}
		});
		//
		FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();
        frgChatMessages = new ChatMessagesFragment();
        transaction.replace(R.id.singlechat_layout_messages, frgChatMessages);
        transaction.commit();
        frgChatMessages.setAdapter(new IChatMessageAdapter() {
			@Override
			public List<IMessage> onLoad(String messageId) {
				return loadHistory(messageId);
			}

			@Override
			public void onClick(int button) {
				// TODO:点击头像进入个人主页
			}
        });
        //
		btnSound.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction() & MotionEvent.ACTION_MASK;
				if(MotionEvent.ACTION_DOWN == action) {
					recorder.start(SingleChatActivity.this);
				}
				else if(MotionEvent.ACTION_UP == action) {
					File file = recorder.stop();
					if(null == file) {
						return false;
					}
					if(recorder.duration() < 500) {
						Toast.makeText(SingleChatActivity.this, "录音时间太短", Toast.LENGTH_LONG).show();
						return false;
					}
					// 发送短语音
					SoundMessage message = new SoundMessage();
					message.from = selfId;
					message.orientation = IMessage.ORIENTATION_SEND;
					message.time = DateTime.now();
					message.file = file;
					send(message);
				}
				else if(MotionEvent.ACTION_OUTSIDE == action) {
					recorder.discard();
				}
				return false;
			}
		});
		btnEmoticon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("tag", "msg");
			}
		});
		btnMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == frgChatMore) {
					FragmentManager fm = getFragmentManager();  
			        FragmentTransaction transaction = fm.beginTransaction();
			        frgChatMore = new ChatMoreFragment();
			        transaction.replace(R.id.singlechat_layout_panel, frgChatMore);
			        transaction.commit();
			        //
			        final RotateAnimation animMore =new RotateAnimation(0f, 135f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
			        animMore.setDuration(500);
			        btnMore.startAnimation(animMore);
			        animMore.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) { }
						@Override
						public void onAnimationRepeat(Animation animation) { }
						@Override
						public void onAnimationEnd(Animation animation) {
							btnMore.setImageResource(R.drawable.more_changed);
							btnMore.clearAnimation();
						}
			        });
				}
				else {
					FragmentManager fm = getFragmentManager();  
			        FragmentTransaction transaction = fm.beginTransaction();
			        transaction.detach(frgChatMore);
			        transaction.commit();
			        frgChatMore = null;
			        //
			        final RotateAnimation animation =new RotateAnimation(0f, -135f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
			        animation.setDuration(500);
			        btnMore.startAnimation(animation);
			        animation.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) { }
						@Override
						public void onAnimationRepeat(Animation animation) { }
						@Override
						public void onAnimationEnd(Animation animation) {
							btnMore.setImageResource(R.drawable.more);
							btnMore.clearAnimation();
						}
			        });
				}
			}
		});
	}

    /**
     * 准备数据
     */
    private void prepareParameter() {
    	selfId = getIntent().getStringExtra("selfId");
    	groupId = getIntent().getStringExtra("groupId");
    	remoteId = getIntent().getStringExtra("remoteId");
    	//
    	receiver = new MessageBroadcastReceiver();
    	IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
    	intentFilter.setPriority(4);
    	registerReceiver(receiver, intentFilter);
    	if(null != groupId) {
        	conversation = EMChatManager.getInstance().getConversation(groupId);
    	}
    	else {
        	conversation = EMChatManager.getInstance().getConversation(remoteId);
    	}
    }

    /**
     * 发送消息
     * 
     * @param message 消息对象
     */
	public void send(Message message) {
		EMMessage emMessage = message.toEMMessage();
		if(null == groupId) {
			emMessage.setReceipt(remoteId);
		}
		else {
			emMessage.setChatType(ChatType.GroupChat);
			emMessage.setReceipt(groupId);
		}
        conversation.addMessage(emMessage);
        try {
			EMChatManager.getInstance().sendMessage(emMessage);
		}
        catch (EaseMobException e) {
        	Log.e("Tower", "call send(?) failed", e);
        }
        frgChatMessages.addMessage(message);
	}

	/**
	 * 加载指定消息ID之前的历史记录
	 * 
	 * @param messageId 指定消息ID
	 */
	public List<IMessage> loadHistory(String messageId) {
		java.util.List<EMMessage> emMessages = null;
		if(null == messageId) {
			emMessages = conversation.getAllMessages();
		}
		else {
			emMessages = conversation.loadMoreMsgFromDB(messageId, 20);
		}
		List<IMessage> result = new List<IMessage>();
		for(EMMessage emMessage : emMessages) {
			if(selfId.equals(emMessage.getFrom())) {
				result.add(Message.build(emMessage, IMessage.ORIENTATION_SEND));
			}
			else {
				result.add(Message.build(emMessage, IMessage.ORIENTATION_RECEIVE));
			}
		}
		return result;
	}
}
