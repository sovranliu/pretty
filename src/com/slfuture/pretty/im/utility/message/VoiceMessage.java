package com.slfuture.pretty.im.utility.message;

import java.io.File;

import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.slfuture.pretty.im.utility.message.core.IMessage;

/**
 * 短语音消息
 */
public class VoiceMessage extends Message {
	/**
	 * 音频长度（毫秒）
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
	 * 获取消息类型
	 * 
	 * @return 消息类型
	 */
	@Override
	public int type() {
		return IMessage.TYPE_VOICE;
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
