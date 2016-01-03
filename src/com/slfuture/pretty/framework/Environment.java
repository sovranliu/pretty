package com.slfuture.pretty.framework;

import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.config.Configuration;
import com.slfuture.pluto.sensor.LocationSensor;
import com.slfuture.pretty.Program;
import com.slfuture.pretty.base.Logger;

/**
 * 程式环境
 */
public class Environment {
	/**
	 * 初始化
	 * 
	 * @return 执行结果
	 */
	public static boolean initialize() {
		// 初始化配置
		if(!Configuration.initialize(Program.application)) {
			Logger.e("Configuration initialize failed");
			return false;
		}
		// 初始化网络
		if(!Host.initialize()) {
			Logger.e("Host initialize failed");
			return false;
		}
		// 初始化动态配置
		if(!DynamicConfig.initialize()) {
			Logger.e("DynamicConfig initialize failed");
			return false;
		}
		if(!LocationSensor.initialize(Program.application)) {
			Logger.e("LocationSensor initialize failed");
			return false;
		}
		return true;
	}

	/**
	 * 终结
	 */
	public static void terminate() {
		// 终结网络
		Host.terminate();
		// 终结配置
		Configuration.terminate();
		// 终结动态配置
		DynamicConfig.terminate();
	}
}
