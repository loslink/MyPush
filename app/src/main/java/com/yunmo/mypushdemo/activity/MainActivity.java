package com.yunmo.mypushdemo.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.yunmo.mypushdemo.R;
import com.yunmo.mypushdemo.util.MyUtil;

import java.util.Set;

public class MainActivity extends Activity implements
		android.view.View.OnClickListener {

	private EditText messageTv;
	public static boolean isForeground;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyUtil.initCustomPushNotificationBuilder(this, 1,
				R.layout.customer_notitfication_layout_one,
				R.drawable.tip_icon, R.drawable.tip_icon,
				Notification.FLAG_AUTO_CANCEL, Notification.DEFAULT_SOUND,
				Notification.DEFAULT_LIGHTS);// 自定义编号1的通知栏

		MyUtil.setAlias(this, "test", new TagAliasCallback() {
			@Override
			public void gotResult(int i, String s, Set<String> set) {
				Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
			}
		});
				((Button) findViewById(R.id.local_alert_btn)).setOnClickListener(this);
		((Button) findViewById(R.id.local_alert_clear_btn))
				.setOnClickListener(this);
		messageTv = ((EditText) findViewById(R.id.messageString));
		registerMessageReceiver();//初始化从Receiver接受自定义消息
	}

	@Override
	protected void onResume() {
		super.onResume();
		isForeground = true;
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		isForeground = false;
		JPushInterface.onPause(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMessageReceiver);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.local_alert_btn:
			MyUtil.showLocalNotification(getApplicationContext(), 1, "本地title",
					"本地content哈哈", 111111,
					java.lang.System.currentTimeMillis() + 10);
			break;
		case R.id.local_alert_clear_btn:
			MyUtil.clearNotificationById(getApplicationContext(), 111111);
			break;

		default:
			break;
		}
	}

	// 从Receiver接受自定义消息
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.yunmo.mypushdemo.permission.JPUSH_MESSAGE";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(MainActivity.this, "xxxxxx", Toast.LENGTH_SHORT).show();
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
				if (!MyUtil.isEmpty(extras)) {
					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
				}
				setCostomMsg(showMsg.toString());
			}
		}
	}

	private void setCostomMsg(String msg) {
		if (null != messageTv) {
			messageTv.setText(msg);
			Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			messageTv.setVisibility(View.VISIBLE);
		}
	}
}