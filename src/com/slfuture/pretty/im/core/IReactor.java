package com.slfuture.pretty.im.core;

import com.slfuture.carrie.base.type.core.ITable;

import android.graphics.Bitmap;

/**
 * 即时通信回调接口
 */
public interface IReactor {
	/**
	 * 获取指定用户的头像
	 * 
	 * @param userId 用户ID
	 * @return 头像位图
	 */
	public Bitmap getPhoto(String userId);

	/**
	 * 获取指定用户的称呼
	 * 
	 * @param userId 用户ID
	 * @return 称呼
	 */
	public String getName(String userId);

	/**
	 * 获取登录帐号
	 * 
	 * @return 登录帐号
	 */
	public String getUserId();

	/**
	 * 获取登录密码
	 * 
	 * @return 登录密码
	 */
	public String getPassword();

	/**
	 * 冲突掉线回调
	 */
	public void onConflict();

	/**
	 * 透传命令回调
	 * 
	 * @param from 消息投递者
	 * @param action 动作
	 * @param data 属性
	 */
	public void onCommand(String from, String action, ITable<String, Object>data);
}
