package com.slfuture.pretty.im.view;

import com.slfuture.pretty.R;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 单聊窗口
 */
@ResourceView(id=R.layout.activity_singlechat)
public class SingleChatActivity extends ActivityEx {
	@ResourceView(id=R.id.singlechat_layout_tail)
	public View viewTail;
	@ResourceView(id=R.id.singlechat_image_emoticon)
	public ImageButton btnEmoticon;
	@ResourceView(id=R.id.singlechat_image_more)
	public ImageButton btnMore;
	
	public ChatMoreFragment frgChatMore = null;

	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
