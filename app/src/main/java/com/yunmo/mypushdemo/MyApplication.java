package com.yunmo.mypushdemo;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;

/** 
 * @author yangshuai
 * @version 创建时间：2015-4-13 上午10:16:44 
 * 类说明 :自定义application
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}
}
