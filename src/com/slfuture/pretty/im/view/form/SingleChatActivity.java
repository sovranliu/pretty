package com.slfuture.pretty.im.view.form;

import com.slfuture.pretty.R;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
		FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();
        frgChatMessages = new ChatMessagesFragment();
        transaction.replace(R.id.singlechat_layout_messages, frgChatMessages);
        transaction.commit();
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
					// TODO:环信发送
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
}
