package com.slfuture.pretty.im.utility.message.core;

import com.slfuture.carrie.base.time.DateTime;

/**
 * 消息接口
 */
public interface IMessage {
	/**
	 * 未知类型
	 */
	public final static int TYPE_UNKNOWN = 0;
	/**
	 * 文本类型
	 */
	public final static int TYPE_TEXT = 1;
	/**
	 * 图片类型
	 */
	public final static int TYPE_IMAGE = 2;
	/**
	 * 短语音类型
	 */
	public final static int TYPE_VOICE = 3;
	/**
	 * 语音通话类型
	 */
	public final static int TYPE_AUDIO = 4;
	/**
	 * 视频通话类型
	 */
	public final static int TYPE_VIDEO = 5;

	/**
	 * 发送出的消息
	 */
	public final static int ORIENTATION_SEND = 1;
	/**
	 * 接收到的消息
	 */
	public final static int ORIENTATION_RECEIVE = 2;

	/**
	 * 发送失败
	 */
	public final static int SENDSTATUS_FAIL = -1;
	/**
	 * 发送成功，对方未读
	 */
	public final static int SENDSTATUS_UNREAD = 0;
	/**
	 * 发送成功，对方已读
	 */
	public final static int SENDSTATUS_HASREAD = 1;


	/**
	 * 获取消息ID
	 * 
	 * @return 消息ID
	 */
	public String id();

	/**
	 * 获取消息类型
	 * 
	 * @return 消息类型
	 */
	public int type();

	/**
	 * 获取消息方向
	 * 
	 * @return 消息方向
	 */
	public int orientation();
	
	/**
	 * 获取消息发送者
	 * 
	 * @return 消息发送者
	 */
	public String from();

	/**
	 * 获取消息发送时间
	 * 
	 * @return 消息发送时间
	 */
	public DateTime time();

	/**
	 * 获取投递状态
	 * 
	 * @return 投递状态
	 */
	public int sendStatus();
}
