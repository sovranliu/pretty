package com.slfuture.pretty.im.view.form;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;

import com.slfuture.carrie.base.time.DateTime;
import com.slfuture.carrie.base.type.List;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.Module;
import com.slfuture.pretty.im.utility.message.ImageMessage;
import com.slfuture.pretty.im.utility.message.Message;
import com.slfuture.pretty.im.utility.message.VoiceMessage;
import com.slfuture.pretty.im.utility.message.TextMessage;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.File;

import com.slfuture.pluto.sensor.SoundRecorder;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 单聊窗口
 */
@ResourceView(clazz=R.layout.class, field="activity_singlechat")
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


	
	@ResourceView(clazz=R.id.class, field="singlechat_layout_close")
	public View viewClose;
	@ResourceView(clazz=R.id.class, field="singlechat_layout_tail")
	public View viewTail;
	@ResourceView(clazz=R.id.class, field="singlechat_image_mode")
	public ImageButton btnMode;
	@ResourceView(clazz=R.id.class, field="singlechat_image_emoticon")
	public ImageButton btnEmoticon;
	@ResourceView(clazz=R.id.class, field="singlechat_image_more")
	public ImageButton btnMore;
	@ResourceView(clazz=R.id.class, field="singlechat_text_text")
	public EditText txtText;
	@ResourceView(clazz=R.id.class, field="singlechat_button_voice")
	public Button btnVoice;
	public ChatMoreFragment frgChatMore = null;
	public ChatEmoticonFragment frgChatEmoticon = null;
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
		if(!EMChatManager.getInstance().isConnected()) {
			Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
		recorder = new SoundRecorder(SingleChatActivity.this.getExternalFilesDir(android.os.Environment.DIRECTORY_RINGTONES) + "/");
		prepareParameter();
		viewClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SingleChatActivity.this.finish();
			}
		});
		//
		btnMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(View.GONE == btnVoice.getVisibility()) {
					txtText.setVisibility(View.GONE);
					btnVoice.setVisibility(View.VISIBLE);
				}
				else {
					txtText.setVisibility(View.VISIBLE);
					btnVoice.setVisibility(View.GONE);
				}
			}
		});
		//
		txtText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				String text = v.getText().toString();
				if(text.startsWith("\n")) {
					text = text.substring(1);
				}
				if(0 == text.length()) {
					return false;
				}
				if (EditorInfo.IME_ACTION_SEND == actionId) {
					// 发送文字
					TextMessage message = new TextMessage();
					message.from = selfId;
					message.orientation = IMessage.ORIENTATION_SEND;
					message.time = DateTime.now();
					message.text = v.getText().toString();
					send(message);
			        //
			        v.setText("");
			        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			        inputMethodManager.hideSoftInputFromWindow(txtText.getWindowToken(), 0);
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
		btnVoice.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction() & MotionEvent.ACTION_MASK;
				if(MotionEvent.ACTION_DOWN == action) {
					recorder.start(SingleChatActivity.this);
					btnVoice.setText("松开发送");
				}
				else if(MotionEvent.ACTION_UP == action) {
					btnVoice.setText("按住说话");
					File file = recorder.stop();
					if(null == file) {
						return false;
					}
					if(recorder.duration() < 500) {
						Toast.makeText(SingleChatActivity.this, "录音时间太短", Toast.LENGTH_LONG).show();
						return false;
					}
					// 发送短语音
					VoiceMessage message = new VoiceMessage();
					message.from = selfId;
					message.orientation = IMessage.ORIENTATION_SEND;
					message.time = DateTime.now();
					message.file = file;
					message.length = (int) recorder.duration();
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
				if(null == frgChatEmoticon) {
					FragmentManager fm = getFragmentManager();  
			        FragmentTransaction transaction = fm.beginTransaction();
			        frgChatEmoticon = new ChatEmoticonFragment() {
						@Override
						public void onChoose(int index) {
							TextMessage message = new TextMessage();
							message.from = selfId;
							message.orientation = IMessage.ORIENTATION_SEND;
							message.time = DateTime.now();
							message.text = "/" + index;
							send(message);
							//
							if(null != frgChatEmoticon) {
								FragmentManager fm = getFragmentManager();  
						        FragmentTransaction transaction = fm.beginTransaction();
						        transaction.detach(frgChatEmoticon);
						        transaction.commit();
						        frgChatEmoticon = null;
							}
						}
			        };
			        transaction.replace(R.id.singlechat_layout_panel, frgChatEmoticon);
			        transaction.commit();
				}
				else {
					FragmentManager fm = getFragmentManager();  
			        FragmentTransaction transaction = fm.beginTransaction();
			        transaction.detach(frgChatEmoticon);
			        transaction.commit();
			        frgChatEmoticon = null;
			        //
			        if(null != frgChatMore) {
				        transaction = fm.beginTransaction();
				        transaction.replace(R.id.singlechat_layout_panel, frgChatMore);
				        transaction.commit();
					}
				}
			}
		});
		btnMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != frgChatMore) {
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
							btnMore.clearAnimation();
							btnMore.setImageResource(R.drawable.more);
						}
			        });
			        if(null != frgChatEmoticon) {
				        transaction = fm.beginTransaction();
				        transaction.detach(frgChatEmoticon);
				        transaction.commit();
				        frgChatEmoticon = null;
					}
				}
				else {
					FragmentManager fm = getFragmentManager();  
			        FragmentTransaction transaction = fm.beginTransaction();
			        frgChatMore = new ChatMoreFragment() {
			        	/**
			        	 * 选择图片
			        	 * 
			        	 * @param file 图片文件
			        	 */
			        	public void onChooseImage(File file) {
			        		ImageMessage message = new ImageMessage();
							message.from = selfId;
							message.orientation = IMessage.ORIENTATION_SEND;
							message.time = DateTime.now();
							message.originalFile = file;
							send(message);
			        	}

			        	/**
			        	 * 拨号回调
			        	 * 
			        	 * @param type 拨号类型
			        	 * @see Module
			        	 */
			        	public void onDial(int type) {
			        		Intent intent = null;
			        		if(Module.DIAL_TYPE_AUDIO == type) {
			        			intent = new Intent(SingleChatActivity.this, AudioActivity.class);
			        			intent.putExtra("from", remoteId);
			        			intent.putExtra("isCaller", true);
			        			SingleChatActivity.this.startActivity(intent);
			        		}
			        		else if(Module.DIAL_TYPE_VIDEO == type) {
			        			intent = new Intent(SingleChatActivity.this, VideoActivity.class);
			        			intent.putExtra("from", remoteId);
			        			intent.putExtra("isCaller", true);
			        			SingleChatActivity.this.startActivity(intent);
			        		}
			        	}
			        };
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
							btnMore.clearAnimation();
							btnMore.setImageResource(R.drawable.more_changed);
						}
			        });
				}
			}
		});
	}

	@Override
    protected void onDestroy() {
		super.onDestroy();
		//
    	unregisterReceiver(receiver);
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
