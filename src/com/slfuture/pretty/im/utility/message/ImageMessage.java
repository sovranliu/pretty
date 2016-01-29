package com.slfuture.pretty.im.utility.message;

import java.io.File;

import android.graphics.Bitmap;

import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.slfuture.pretty.im.utility.message.core.IMessage;

/**
 * 聊天消息
 */
public class ImageMessage extends Message {
	/**
	 * 缩略位图
	 */
	public Bitmap thumbnail;
	/**
	 * 缩略图本地路径
	 */
	public String thumbnailPath;
	/**
	 * 缩略图URL
	 */
	public String thumbnailUrl;
	/**
	 * 原始位图
	 */
	public Bitmap original;
	/**
	 * 原始图本地路径
	 */
	public String originalPath;
	/**
	 * 原始图URL
	 */
	public String originalUrl;


	/**
	 * 获取消息类型
	 * 
	 * @return 消息类型
	 */
	@Override
	public int type() {
		return IMessage.TYPE_IMAGE;
	}

	/**
	 * 转为环信消息对象
	 * 
	 * @return 环信消息对象
	 */
	@Override
	public EMMessage toEMMessage() {
		EMMessage emMessage = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		if(null != thumbnailPath) {
			emMessage.addBody(new ImageMessageBody(new File(thumbnailPath)));
		}
		else if(null != originalPath) {
			emMessage.addBody(new ImageMessageBody(new File(originalPath)));
		}
		return emMessage;
	}
}
