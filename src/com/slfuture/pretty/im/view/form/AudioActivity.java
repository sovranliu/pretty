package com.slfuture.pretty.im.view.form;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMCallStateChangeListener.CallState;
import com.easemob.exceptions.EMServiceNotReadyException;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.Module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 语音通话界面
 */
@ResourceView(id = R.layout.activity_audio)
public class AudioActivity extends ActivityEx {
	@ResourceView(id = R.id.audio_image_photo)
	public ImageView imgPhoto;
	@ResourceView(id = R.id.audio_label_name)
	public TextView labName;
	@ResourceView(id = R.id.audio_label_description)
	public TextView labDescription;
	@ResourceView(id = R.id.audio_image_mute)
	public ImageView imgMute;
	@ResourceView(id = R.id.audio_image_handup)
	public ImageView imgHandup;
	@ResourceView(id = R.id.audio_image_speaker)
	public ImageView imgSpeaker;

	/**
	 * 环信用户名
	 */
	private String from = null;
	/**
	 * 拨号状态，true：主动拨号，false：被动接听
	 */
	private boolean isCaller = false;
	/**
	 * 呼叫状态，true：正在通话中，false：未通话
	 */
	private boolean isDialing = false;
	/**
	 * 静音状态
	 */
	private boolean muteStatus = false;
	/**
	 * 免提状态
	 */
	private boolean speakerStatus = false;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 界面处理
		prepare();
	}
	
	@Override
    protected void onDestroy() {
		super.onDestroy();
		//
		if(isDialing) {
			try {
				isDialing = false;
				EMChatManager.getInstance().endCall();
			}
			catch (Exception e) { }
		}
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		prepareData();
		prepareMute();
		prepareSpeaker();
		prepareHandUp();
		prepareName();
	}
	
	/**
	 * 处理呼入者名称
	 */
	public void prepareName() {
		Bitmap bitmap = Module.reactor.getPhoto(from);
		if(null != bitmap) {
			imgPhoto.setImageBitmap(GraphicsHelper.makeCornerImage(Module.reactor.getPhoto(from), 10));
		}
		labName.setText(Module.reactor.getName(from));
	}
	
	/**
	 * 处理数据
	 */
	public void prepareData() {
		from = this.getIntent().getStringExtra("from");
		isCaller = this.getIntent().getBooleanExtra("isCaller", false);
		//
		Controller.doJoin(556, new IEventable<com.easemob.chat.EMCallStateChangeListener.CallState>() {
			@Override
			public void on(CallState data) {
				switch ((CallState) data) {
		        case CONNECTING:
		        	isDialing = true;
		        	labName.setText("语音通话连接中");
		            break;
		        case CONNECTED:
		        	isDialing = true;
		        	labName.setText("语音通话响铃中");
		            break;
		        case ACCEPTED:
		        	isDialing = true;
		        	labName.setText("语音通话进行中");
		            break;
		        case DISCONNNECTED:
		        	isDialing = false;
		        	labName.setText("语音通话已断开");
		        	AudioActivity.this.finish();
		            break;
		        default:
		            break;
		        }
			}
		});
		EMChatManager.getInstance().addVoiceCallStateChangeListener(new EMCallStateChangeListener() {
		    @Override
		    public void onCallStateChanged(CallState callState, CallError error) {
		    	Controller.doMerge(556, callState);
		    }
		});
		if(isCaller) {
			try {
				EMChatManager.getInstance().makeVoiceCall(from);
			}
			catch (EMServiceNotReadyException e) {
				Log.e("pretty", "prepareData failed", e);
			}
		}
	}

	/**
	 * 处理挂断按钮
	 */
	public void prepareHandUp() {
		imgHandup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgHandup.setVisibility(View.INVISIBLE);
				try {
					isDialing = false;
					EMChatManager.getInstance().endCall();
				}
				catch (Exception e) { }
				AudioActivity.this.finish();
			}
		});
	}

	/**
	 * 处理静音按钮
	 */
	public void prepareMute() {
		imgMute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioManager audioManager = (AudioManager) AudioActivity.this.getSystemService(Context.AUDIO_SERVICE);
				if (muteStatus) {
					// 关闭静音
					imgMute.setImageResource(R.drawable.icon_mute_normal);
					audioManager.setMicrophoneMute(false);
					muteStatus = false;
				}
				else {
					// 打开静音
					imgMute.setImageResource(R.drawable.icon_mute_on);
					audioManager.setMicrophoneMute(true);
					muteStatus = true;
				}
			}
		});
	}

	/**
	 * 处理免提按钮
	 */
	@SuppressLint("InlinedApi")
	public void prepareSpeaker() {
		imgSpeaker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioManager audioManager = (AudioManager) AudioActivity.this.getSystemService(Context.AUDIO_SERVICE);
				if (speakerStatus) {
					// 关闭免提
					imgSpeaker.setImageResource(R.drawable.icon_speaker_normal);
					speakerStatus = false;
					//
					if(audioManager.isSpeakerphoneOn()) {
	                    audioManager.setSpeakerphoneOn(false);
					}
	                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
				}
				else {
					imgSpeaker.setImageResource(R.drawable.icon_speaker_on);
					speakerStatus = true;
					//
					if(!audioManager.isSpeakerphoneOn()) {
		                audioManager.setSpeakerphoneOn(true);
					}
		            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
				}
			}
		});
	}
}
