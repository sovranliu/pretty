package com.slfuture.pretty.im.utility.message;

import java.io.File;

import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.slfuture.carrie.base.time.DateTime;
import com.slfuture.pretty.im.utility.message.core.IMessage;

/**
 * 抽象消息
 */
public abstract class Message implements IMessage {
	/**
	 * 消息ID
	 */
	public String id;
	/**
	 * 消息方向
	 */
	public int orientation;
	/**
	 * 消息来源
	 */
	public String from;
	/**
	 * 消息发送时间
	 */
	public DateTime time;


	/**
	 * 获取消息ID
	 * 
	 * @return 消息ID
	 */
	@Override
	public String id() {
		return id;
	}

	/**
	 * 获取消息方向
	 * 
	 * @return 消息方向
	 */
	@Override
	public int orientation() {
		return orientation;
	}

	/**
	 * 获取消息发送者
	 * 
	 * @return 消息发送者
	 */
	@Override
	public String from() {
		return from;
	}

	/**
	 * 获取消息发送时间
	 * 
	 * @return 消息发送时间
	 */
	@Override
	public DateTime time() {
		return time;
	}

	/**
	 * 构建消息对象
	 * 
	 * @param message 环信消息对象
	 * @param orientation 方向
	 * @return 消息对象
	 */
	public static Message build(EMMessage message, int orientation) {
		switch(message.getType()) {
        case TXT:
        	TextMessage textMessage = new TextMessage();
        	textMessage.id = message.getMsgId();
        	textMessage.from = message.getFrom();
        	textMessage.orientation = orientation;
        	textMessage.time = DateTime.parse(message.getMsgTime());
        	textMessage.text = ((TextMessageBody) message.getBody()).getMessage();
        	return textMessage;
        case IMAGE:
        	ImageMessage imageMessage = new ImageMessage();
        	imageMessage.id = message.getMsgId();
        	imageMessage.from = message.getFrom();
        	imageMessage.orientation = orientation;
        	imageMessage.time = DateTime.parse(message.getMsgTime());
        	if(null != ((ImageMessageBody) message.getBody()).getLocalUrl()) {
        		imageMessage.originalFile = new File(((ImageMessageBody) message.getBody()).getLocalUrl());
        	}
        	imageMessage.thumbnailUrl = ((ImageMessageBody) message.getBody()).getThumbnailUrl();
        	if("null".equals(imageMessage.thumbnailUrl)) {
        		imageMessage.thumbnailUrl = null;
        	}
        	imageMessage.originalUrl = ((ImageMessageBody) message.getBody()).getRemoteUrl();
        	if("null".equals(imageMessage.originalUrl)) {
        		imageMessage.originalUrl = null;
        	}
        	return imageMessage;
        case VOICE:
        	VoiceMessage voiceMessage = new VoiceMessage();
        	voiceMessage.id = message.getMsgId();
        	voiceMessage.from = message.getFrom();
        	voiceMessage.orientation = orientation;
        	voiceMessage.time = DateTime.parse(message.getMsgTime());
        	VoiceMessageBody body = (VoiceMessageBody) message.getBody();
        	voiceMessage.length = body.getLength();
        	if(null != body.getLocalUrl() && !"null".equals(body.getLocalUrl())) {
        		voiceMessage.file = new File(body.getLocalUrl());
        	}
        	if(null != body.getRemoteUrl() && !"null".equals(body.getRemoteUrl())) {
        		voiceMessage.url = body.getRemoteUrl();
        	}
        	return voiceMessage;
        case VIDEO:
        	VideoMessage videoMessage = new VideoMessage();
        	videoMessage.id = message.getMsgId();
        	videoMessage.from = message.getFrom();
        	videoMessage.orientation = orientation;
        	videoMessage.time = DateTime.parse(message.getMsgTime());
        	return videoMessage;
        default:
        	return null;
        }
	}

	/**
	 * 转为环信消息对象
	 * 
	 * @return 环信消息对象
	 */
	public EMMessage toEMMessage() {
		return null;
	}
}
