package com.slfuture.pretty.im.utility.message;

import java.io.File;

import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.slfuture.pretty.im.utility.message.core.IMessage;

/**
 * 短语音消息
 */
public class SoundMessage extends Message {
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
		return IMessage.TYPE_SOUND;
	}

	/**
	 * 转为环信消息对象
	 * 
	 * @return 环信消息对象
	 */
	@Override
	public EMMessage toEMMessage() {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
		VoiceMessageBody body = new VoiceMessageBody(file, 0);
		message.addBody(body);
		return message;
	}
}
