package com.slfuture.pretty.im.view.form;

import java.io.File;

import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.pluto.storage.Storage;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.Module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * 对话更多窗口层
 */
@ResourceView(clazz=R.layout.class, field="fragment_chat_more")
public abstract class ChatMoreFragment extends FragmentEx {
	@ResourceView(clazz=R.id.class, field="chatmore_button_picture")
	public ImageButton btnPicture;
	@ResourceView(clazz=R.id.class, field="chatmore_button_camera")
	public ImageButton btnCamera;
	@ResourceView(clazz=R.id.class, field="chatmore_button_audio")
	public ImageButton btnAudio;
	@ResourceView(clazz=R.id.class, field="chatmore_button_video")
	public ImageButton btnVideo;

	private String cameraPath = null;

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
    	btnCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String state = Environment.getExternalStorageState();  
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					cameraPath = Storage.cameraDirectory() + Serial.makeSerialString() + ".jpg";
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraPath)));
					startActivityForResult(intent, 2);
				}
				else {
					Toast.makeText(ChatMoreFragment.this.getActivity(), "未发现SD卡", Toast.LENGTH_LONG).show();  
				}
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
		super.onActivityResult(requestCode, resultCode, intent);
		if(1 == requestCode) {
			Uri uri = (intent == null || resultCode != -1 ? null : intent.getData());
			if(null == uri) {
				return;
			}
			onChooseImage(new File(Storage.getPathFromURI(this.getActivity(), uri)));
		}
		else if(2 == requestCode) {
			File file = new File(cameraPath);
			if(file.exists()) {
				onChooseImage(file);
			}
//			File file = new File(Storage.cameraDirectory() + Serial.makeSerialString() + ".jpg");
//			Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//            	Bitmap photo = (Bitmap) bundle.get("data");
//            	try {
//					GraphicsHelper.saveFile(photo, file, 0, 0);
//				}
//            	catch (IOException e) {
//					return;
//				}
//            	onChooseImage(file);
//            }
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
