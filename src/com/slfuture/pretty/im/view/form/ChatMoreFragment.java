package com.slfuture.pretty.im.view.form;

import java.io.File;

import com.slfuture.pluto.storage.Storage;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.Module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * 对话更多窗口层
 */
@ResourceView(id = R.layout.fragment_chat_more)
public abstract class ChatMoreFragment extends FragmentEx {
	@ResourceView(id = R.id.chatmore_button_picture)
	public ImageButton btnPicture;
	@ResourceView(id = R.id.chatmore_button_camera)
	public ImageButton btnCamera;
	@ResourceView(id = R.id.chatmore_button_audio)
	public ImageButton btnAudio;
	@ResourceView(id = R.id.chatmore_button_video)
	public ImageButton btnVideo;


	/**
	 * 是否附着到根视图
	 * 
	 * @return 是否附着到根视图
	 */
	@Override
	protected boolean attachToRoot() {
		return false;
	}

	/**
	 * 界面创建
	 */
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	//
    	btnPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, 1);
			}
		});
    	btnAudio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDial(Module.DIAL_TYPE_AUDIO);
			}
		});
    	btnVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDial(Module.DIAL_TYPE_VIDEO);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(1 == requestCode) {
			Uri uri = (intent == null || resultCode != -1 ? null : intent.getData());
			if(null == uri) {
				return;
			}
			onChooseImage(new File(Storage.getPathFromURI(this.getActivity(), uri)));
		}
	}

	/**
	 * 选择图片
	 * 
	 * @param file 图片文件
	 */
	public abstract void onChooseImage(File file);

	/**
	 * 拨号回调
	 * 
	 * @param type 拨号类型
	 * @see Module
	 */
	public abstract void onDial(int type);
}
