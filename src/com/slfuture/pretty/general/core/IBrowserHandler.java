package com.slfuture.pretty.general.core;

import java.io.Serializable;

import com.slfuture.carrie.base.model.core.IEventable;

/**
 * 浏览器回调接口
 */
public interface IBrowserHandler extends Serializable {
	/**
	 * 获取函数名称
	 * 
	 * @return 函数名称
	 */
	public String name();

	/**
	 * JS回调
	 * 
	 * @param parameter JS实参
	 * @param callback 回调
	 */
	public void on(String parameter, IEventable<String> callback);
}
