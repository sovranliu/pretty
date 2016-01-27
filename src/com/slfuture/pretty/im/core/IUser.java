package com.slfuture.pretty.im.core;

/**
 * 即时通信用户接口
 */
public interface IUser {
	/**
	 * 登录回调
	 */
	public void onLogin();

	/**
	 * 退等回调
	 */
	public void onLogout();
}
