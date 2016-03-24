package com.slfuture.pretty.im.view.form;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMVideoCallHelper;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMCallStateChangeListener.CallState;
import com.easemob.chat.EMVideoCallHelper.EMVideoOrientation;
import com.easemob.exceptions.EMServiceNotReadyException;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.time.Duration;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.Module;
import com.slfuture.pretty.im.utility.CameraHelper;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.etc.ParameterRunnable;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 视频通话界面
 */
@ResourceView(clazz=R.layout.class, field="activity_video")
public class VideoActivity extends ActivityEx {
    /**
     * 本地SurfaceHolder回调
     */
    class LocalCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) { }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            cameraHelper.startCapture();
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) { }
    }

    /**
     * 对方SurfaceHolder回调
     */
    class OppositeCallback implements SurfaceHolder.Callback {
        @SuppressWarnings("deprecation")
		@Override
        public void surfaceCreated(SurfaceHolder holder) {
            holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        	EMVideoCallHelper.getInstance().onWindowResize(width, height, format);
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) { }
    }


	@ResourceView(clazz=R.id.class, field="video_image_photo")
	public ImageView imgPhoto;
	@ResourceView(clazz=R.id.class, field="video_label_name")
	public TextView labName;
	@ResourceView(clazz=R.id.class, field="video_label_description")
	public TextView labDescription;
	@ResourceView(clazz=R.id.class, field="video_image_mute")
	public ImageView imgMute;
	@ResourceView(clazz=R.id.class, field="video_image_handup")
	public ImageView imgHandup;
	@ResourceView(clazz=R.id.class, field="video_image_speaker")
	public ImageView imgSpeaker;
	@ResourceView(clazz=R.id.class, field="video_surface_local")
	public SurfaceView surfaceLocal;
	@ResourceView(clazz=R.id.class, field="video_surface_opposite")
	public SurfaceView surfaceOpposite;


    /**
     * 摄像头帮助对象
     */
    private CameraHelper cameraHelper = null;
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
        EMVideoCallHelper.getInstance().setSurfaceView(null);
    	if(null != cameraHelper) {
			cameraHelper.stopCapture();
    	}
		cameraHelper = null;
    }

	/**
	 * 界面预处理
	 */
	public void prepare() {
		prepareData();
		prepareMute();
		prepareSpeaker();
		prepareHandUp();
		prepareVideo();
		prepareName();
		if(isCaller) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            message.setReceipt(from);
            TextMessageBody body = new TextMessageBody("[视频通话]");
            message.addBody(body);
            EMChatManager.getInstance().saveMessage(message, false);
		}
		else {
			EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.setFrom(from);
            TextMessageBody body = new TextMessageBody("[视频通话]");
            message.addBody(body);
            // EMChatManager.getInstance().saveMessage(message, false);
		}
		if(isCaller) {
			call();
		}
	}

	/**
	 * 处理数据
	 */
	public void prepareData() {
		from = this.getIntent().getStringExtra("from");
		isCaller = this.getIntent().getBooleanExtra("isCaller", false);
		//
		Controller.doJoin(557, new IEventable<com.easemob.chat.EMCallStateChangeListener.CallState>() {
			@Override
			public void on(CallState data) {
				switch ((CallState) data) {
		        case CONNECTING:
		        	isDialing = true;
		        	labDescription.setText("视频通话连接中");
		            break;
		        case CONNECTED:
		        	isDialing = true;
		        	labDescription.setText("视频通话响铃中");
		            break;
		        case ACCEPTED:
		        	isDialing = true;
		        	labDescription.setText("视频通话中");
		        	surfaceOpposite.setVisibility(View.VISIBLE);
		            surfaceLocal.setZOrderOnTop(true);
		        	Controller.doDelay(new ParameterRunnable(0) {
						@Override
						public void run() {
							if(!isDialing) {
								return;
							}
							parameter = (Integer) parameter + 1;
							labDescription.setText("视频通话中 " + Duration.createSeconds((Integer) parameter).toString());
							Controller.doDelay(this, 1000);
						}
		        	}, 1000);
		            break;
		        case DISCONNNECTED:
		        	isDialing = false;
		        	labDescription.setText("视频通话已断开");
		        	VideoActivity.this.finish();
		            break;
		        default:
		            break;
		        }
			}
		});
		EMChatManager.getInstance().addVoiceCallStateChangeListener(new EMCallStateChangeListener() {
		    @Override
		    public void onCallStateChanged(CallState callState, CallError error) {
		    	Controller.doFork(557, callState);
		    }
		});
	}
	
	/**
	 * 处理呼入者名称
	 */
	public void prepareName() {
		Bitmap bitmap = Module.reactor.getPhoto(from);
		if(null != bitmap) {
			imgPhoto.setImageBitmap(GraphicsHelper.makeCycleImage(Module.reactor.getPhoto(from), 200, 200));
		}
		labName.setText(Module.reactor.getName(from));
	}

	/**
	 * 开始呼叫
	 */
	public void call() {
		try {
			EMChatManager.getInstance().makeVideoCall(from);
		}
		catch (EMServiceNotReadyException e) {
			Log.e("pretty", "call failed", e);
		}
	}

	public void prepareVideo() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        		| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        		| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        // 显示本地图像的surfaceview
        surfaceLocal.setZOrderMediaOverlay(true);
        surfaceLocal.setZOrderOnTop(true);
        surfaceLocal.getHolder().addCallback(new LocalCallback());
        // SurfaceHolder localSurfaceHolder = localSurface.getHolder();
        // 显示对方图像的surfaceview
        SurfaceHolder oppositeSurfaceHolder = surfaceOpposite.getHolder();
        // 设置显示对方图像的surfaceview
        EMVideoCallHelper.getInstance().setSurfaceView(surfaceOpposite);
        oppositeSurfaceHolder.addCallback(new OppositeCallback());
        EMVideoCallHelper.getInstance().setVideoOrientation(EMVideoOrientation.EMPortrait);
        cameraHelper = new CameraHelper(EMVideoCallHelper.getInstance(), surfaceLocal.getHolder());
        cameraHelper.setStartFlag(true);
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
				VideoActivity.this.finish();
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
				AudioManager audioManager = (AudioManager) VideoActivity.this.getSystemService(Context.AUDIO_SERVICE);
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
				AudioManager audioManager = (AudioManager) VideoActivity.this.getSystemService(Context.AUDIO_SERVICE);
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
