package com.slfuture.pretty.im.utility.message;

import com.slfuture.pretty.im.utility.message.core.IMessage;

/**
 * 视频通话消息
 */
public class VideoMessage extends Message {
	/**
	 * 持续时长
	 */
	public long duration = 0;
	
	
	/**
	 * 获取消息类型
	 * 
	 * @return 消息类型
	 */
	@Override
	public int type() {
		return IMessage.TYPE_VIDEO;
	}

	/**
	 * 获取描述信息
	 * 
	 * @return 描述信息
	 */
	public String description() {
		return "[视频通话]";
	}
}
