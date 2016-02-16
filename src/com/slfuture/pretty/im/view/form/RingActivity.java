package com.slfuture.pretty.im.view.form;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMCallStateChangeListener.CallState;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EMNetworkUnconnectedException;
import com.easemob.exceptions.EMNoActiveCallException;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.Module;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 响铃界面
 */
@ResourceView(clazz=R.layout.class, field="activity_ring")
public class RingActivity extends ActivityEx {
	@ResourceView(clazz=R.id.class, field="ring_image_photo")
	public ImageView imgPhoto;
	@ResourceView(clazz=R.id.class, field="ring_label_name")
	public TextView labName;
	@ResourceView(clazz=R.id.class, field="ring_label_description")
	public TextView labDescription;
	@ResourceView(clazz=R.id.class, field="ring_image_handup")
	public ImageView imgHandup;
	@ResourceView(clazz=R.id.class, field="ring_image_answer")
	public ImageView imgAnswer;


	/**
	 * 通话类型
	 */
	private int dialType = Module.DIAL_TYPE_UNKNOWN;
	/**
	 * 环信ID
	 */
	private String from = null;
	/**
	 * 响铃是否已经挂断
	 */
	private boolean hasHandup = false;
	/**
	 * 语音
	 */
	private SoundPool soundPool = null;


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
		if(null != soundPool) {
			soundPool.release();
			soundPool = null;
		}
		if(!hasHandup) {
			try {
				EMChatManager.getInstance().rejectCall();
			}
			catch (EMNoActiveCallException e) {
				Log.e("pretty", "rejectCall execute failed", e);
			}
		}
    }

	/**
	 * 界面预处理
	 */
	public void prepare() {
		prepareData();
		prepareCaller();
		prepareHandUp();
		prepareAnswer();
	}

	/**
	 * 处理数据
	 */
	@SuppressWarnings("deprecation")
	public void prepareData() {
		dialType = this.getIntent().getIntExtra("type", 0);
		from = this.getIntent().getStringExtra("from");
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		Controller.doDelay(new Runnable() {
			@Override
			public void run() {
				int soundId = soundPool.load(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).getPath(), 1);
				soundPool.play(soundId, 1, 1, 0, -1, 1);
			}
		}, 1000);
	}

	/**
	 * 处理呼入者名称
	 */
	public void prepareCaller() {
		Controller.doJoin(555, new IEventable<com.easemob.chat.EMCallStateChangeListener.CallState>() {
			@Override
			public void on(CallState data) {
				switch (data) {
		        case DISCONNNECTED:
		        	hasHandup = true;
		        	RingActivity.this.finish();
		            break;
		        default:
		            break;
		        }
			}
		});
		Bitmap bitmap = Module.reactor.getPhoto(from);
		if(null != bitmap) {
			imgPhoto.setImageBitmap(GraphicsHelper.makeCornerImage(Module.reactor.getPhoto(from), 10));
		}
		labName.setText(Module.reactor.getName(from));
		if(Module.DIAL_TYPE_AUDIO == dialType) {
			labDescription.setText("正在向您发起语音通话邀请");
		}
		else {
			labDescription.setText("正在向您发起视频通话邀请");
		}
		EMChatManager.getInstance().addVoiceCallStateChangeListener(new EMCallStateChangeListener() {
		    @Override
		    public void onCallStateChanged(CallState callState, CallError error) {
		    	Controller.doMerge(555, callState);
		    }
		});
	}

	/**
	 * 处理挂断按钮
	 */
	public void prepareHandUp() {
		imgHandup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RingActivity.this.finish();
			}
		});
	}

	/**
	 * 处理接听按钮
	 */
	public void prepareAnswer() {
		imgAnswer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EMChatManager.getInstance().answerCall();
				}
				catch (EMNoActiveCallException e) {
					Log.e("pretty", "prepareAnswer execute failed", e);
				}
				catch (EMNetworkUnconnectedException e) {
					Log.e("pretty", "prepareAnswer execute failed", e);
				}
				hasHandup = true;
				if(Module.DIAL_TYPE_AUDIO == dialType) {
					Intent voiceIntent = new Intent(RingActivity.this, AudioActivity.class);
					voiceIntent.putExtra("from", from);
					voiceIntent.putExtra("mode", false);
	           		RingActivity.this.startActivity(voiceIntent);
				}
				else {
					Intent videoIntent = new Intent(RingActivity.this, VideoActivity.class);
					videoIntent.putExtra("from", from);
					videoIntent.putExtra("mode", false);
	           		RingActivity.this.startActivity(videoIntent);
				}
				RingActivity.this.finish();
			}
		});
	}
}
