package com.slfuture.pretty.im.utility.message;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import com.slfuture.pretty.im.utility.message.core.IMessage;

/**
 * 文本消息
 */
public class TextMessage extends Message {
	/**
	 * 消息文本
	 */
	public String text;


	/**
	 * 获取消息类型
	 * 
	 * @return 消息类型
	 */
	@Override
	public int type() {
		return IMessage.TYPE_TEXT;
	}

	/**
	 * 转为环信消息对象
	 * 
	 * @return 环信消息对象
	 */
	@Override
	public EMMessage toEMMessage() {
		EMMessage emMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
		emMessage.addBody(new TextMessageBody(text));
		return emMessage;
	}
}
