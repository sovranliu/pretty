package com.slfuture.pretty.im.view.form;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMVideoCallHelper;
import com.easemob.chat.EMCallStateChangeListener.CallState;
import com.easemob.chat.EMVideoCallHelper.EMVideoOrientation;
import com.easemob.exceptions.EMServiceNotReadyException;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.Module;
import com.slfuture.pretty.im.utility.CameraHelper;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.etc.GraphicsHelper;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 视频通话界面
 */
@ResourceView(id = R.layout.activity_video)
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


	@ResourceView(id = R.id.video_image_photo)
	public ImageView imgPhoto;
	@ResourceView(id = R.id.video_label_name)
	public TextView labName;
	@ResourceView(id = R.id.video_label_description)
	public TextView labDescription;
	@ResourceView(id = R.id.video_image_mute)
	public ImageView imgMute;
	@ResourceView(id = R.id.video_image_handup)
	public ImageView imgHandup;
	@ResourceView(id = R.id.video_image_speaker)
	public ImageView imgSpeaker;

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
		Log.i("TOWER", "VideoActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video);
		// 界面处理
		prepare();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
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
		        	labDescription.setText("连接中");
		            break;
		        case CONNECTED:
		        	labDescription.setText("响铃中");
		            break;
		        case ACCEPTED:
		        	labDescription.setText("通话中");
		            break;
		        case DISCONNNECTED:
		        	labDescription.setText("已断开");
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
		    	Controller.doMerge(557, callState);
		    }
		});
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
		SurfaceView localSurface = (SurfaceView) findViewById(R.id.video_surface_local);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);
        localSurface.getHolder().addCallback(new LocalCallback());
        // SurfaceHolder localSurfaceHolder = localSurface.getHolder();
        // 显示对方图像的surfaceview
        SurfaceView oppositeSurface = (SurfaceView) findViewById(R.id.video_surface_opposite);
        SurfaceHolder oppositeSurfaceHolder = oppositeSurface.getHolder();
        // 设置显示对方图像的surfaceview
        EMVideoCallHelper.getInstance().setSurfaceView(oppositeSurface);
        oppositeSurfaceHolder.addCallback(new OppositeCallback());
        EMVideoCallHelper.getInstance().setVideoOrientation(EMVideoOrientation.EMPortrait);
        cameraHelper = new CameraHelper(EMVideoCallHelper.getInstance(), localSurface.getHolder());
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
					imgMute.getBackground().setAlpha(0);
					audioManager.setMicrophoneMute(false);
					muteStatus = false;
				}
				else {
					// 打开静音
					imgMute.setImageResource(R.drawable.icon_mute_on);
					imgMute.getBackground().setAlpha(0);
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
