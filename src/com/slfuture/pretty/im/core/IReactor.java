package com.slfuture.pretty.im.core;

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
	 */
	public String getAccount();

	/**
	 * 获取登录密码
	 */
	public String getPassword();
}
