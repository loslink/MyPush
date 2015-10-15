package com.yunmo.mypushdemo.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.yunmo.mypushdemo.activity.MainActivity;
import com.yunmo.mypushdemo.util.MyUtil;

/**
 * @author yangshuai
 * @version 创建时间：2015-4-13 上午10:07:10 
 * 类说明 :自定义Receive接受推送
 */
public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Bundle bundle = arg1.getExtras();

		// 消息标题，对应 API 消息内容的 title 字段，Portal 推送消息界上不作展示
		if (bundle.containsKey(JPushInterface.EXTRA_TITLE)) {
			String titleString = bundle.getString(JPushInterface.EXTRA_TITLE);
		}

		// 附加字段,是个 JSON 字符串,对应 API 消息内容的 extras 字段.
		// 对应 Portal推送消息界面上的“可选设置”里的附加字段
		if (bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
			String extrasString = bundle.getString(JPushInterface.EXTRA_EXTRA);
		}

		// 内容类型，对应 API 消息内容的 content_type 字段
		if (bundle.containsKey(JPushInterface.EXTRA_CONTENT_TYPE)) {
			String typeString = bundle
					.getString(JPushInterface.EXTRA_CONTENT_TYPE);
		}

		// 唯一标识消息的 ID, 可用于上报统计等。
		if (bundle.containsKey(JPushInterface.EXTRA_MSG_ID)) {
			String msgIdString = bundle.getString(JPushInterface.EXTRA_MSG_ID);
		}

		/**
		 * 对action行为的判断
		 */
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(arg1.getAction())) {
			// SDK 向 JPush Server 注册所得到的注册 全局唯一的 ID ，可以通过此 ID 向对应的客户端发送消息和通知。
			String registrationIdString = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(arg1
				.getAction())) {
			System.out.println("收到了自定义消息。消息内容是："
					+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			// 自定义消息不会展示在通知栏，完全要开发者写代码去处理

			// 消息内容,对应 API 消息内容的 message 字段,对应 Portal 推送消息界面上的"自定义消息内容”字段
			if (bundle.containsKey(JPushInterface.EXTRA_MESSAGE)) {
				String messageString = bundle
						.getString(JPushInterface.EXTRA_MESSAGE);
			}
			processCustomMessage(arg0, bundle);//传送内容到主界面上

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(arg1
				.getAction())) {
			System.out.println("收到了通知");
			// 在这里可以做些统计，或者做些其他工作

			// 通知的标题,对应 API 通知内容的 n_title 字段,对应 Portal 推送通知界面上的“通知标题”字段
			String notificationTileString = bundle
					.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			// 通知内容,对应 API 通知内容的 n_content 字段,对应 Portal 推送通知界面上的“通知内容”字段
			String alertString = bundle.getString(JPushInterface.EXTRA_ALERT);
			// 通知栏的Notification ID，可以用于清除Notification
			int notificationId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(arg1
				.getAction())) {
			System.out.println("用户点击打开了通知");
			// 在这里可以自己写代码去定义用户点击后的行为
			Toast.makeText(arg0, "你表点人家啦。。。。", Toast.LENGTH_SHORT).show();
			JPushInterface.reportNotificationOpened(arg0,
					bundle.getString(JPushInterface.EXTRA_MSG_ID));// 用于上报用户的通知栏被打开

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(arg1
				.getAction())) {// 网络断开，连接
			boolean connected = arg1.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			// Toast.makeText(arg0, "网络连接" + connected,
			// Toast.LENGTH_SHORT).show();

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(arg1
				.getAction())) {
			// 接受富推送

			// 富文本页面 Javascript 回调API，获取参数参数 ”params“
			if (bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
				String params = arg1.getStringExtra(JPushInterface.EXTRA_EXTRA);
			}

			// 富媒体通消息推送下载后的文件路径和文件名。
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_FILE_PATH)) {
				String filePathString = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
			}

			// 富媒体通知推送下载的HTML的文件路径,用于展现WebView。
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_HTML_PATH)) {
				String fileHtmlPath = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
			}

			// 富媒体通知推送下载的图片资源的文件名,多个文件名用 “，” 分开,路径为 fileHtmlPath
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_HTML_RES)) {
				String fileImageStr = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_HTML_RES);
				String[] fileNames = fileImageStr.split(",");
			}
		} else {
			Log.d("其它的action行为", "Unhandled intent - " + arg1.getAction());
		}
	}

	// send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!MyUtil.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (null != extraJson && extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {

				}

			}
			context.sendBroadcast(msgIntent);
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
}
