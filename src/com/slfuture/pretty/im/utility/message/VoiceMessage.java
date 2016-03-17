package com.slfuture.pretty.im.utility.message;

import java.io.File;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.slfuture.pretty.im.utility.message.core.IMessage;

/**
 * 短语音消息
 */
public class VoiceMessage extends Message {
	/**
	 * 音频长度（秒）
	 */
	public int length = 0;
	/**
	 * 下载地址
	 */
	public String url = null;
	/**
	 * 录音文件
	 */
	public File file = null;
	/**
	 * 消息是否已收听
	 */
	public boolean hasListened = true;
	/**
	 * 环信消息对象
	 */
	public EMMessage emMessage = null;


	/**
	 * 获取消息类型
	 * 
	 * @return 消息类型
	 */
	@Override
	public int type() {
		return IMessage.TYPE_VOICE;
	}

	/**
	 * 获取消息是否已收听
	 * 
	 * @return 消息是否已收听
	 */
	public boolean hasListened() {
		return hasListened;
	}

	/**
	 * 获取消息是否已收听
	 * 
	 * @param hasListened 消息是否已收听
	 */
	public void setHasListened(boolean hasListened) {
		this.hasListened = hasListened;
		if(null != emMessage) {
			emMessage.setListened(hasListened);
			EMChatManager.getInstance().setMessageListened(emMessage);
		}
	}

	/**
	 * 转为环信消息对象
	 * 
	 * @return 环信消息对象
	 */
	@Override
	public EMMessage toEMMessage() {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
		VoiceMessageBody body = new VoiceMessageBody(file, length);
		message.addBody(body);
		return message;
	}
}
