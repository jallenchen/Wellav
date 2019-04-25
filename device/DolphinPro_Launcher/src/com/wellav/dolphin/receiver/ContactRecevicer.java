package com.wellav.dolphin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wellav.dolphin.application.LauncherApp;

public class ContactRecevicer extends BroadcastReceiver {
	static final String ACTION = "com.wellav.dolphin.receiver.contact.action"; 

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
			
			String action = intent.getStringExtra("action"); 
			String type = intent.getStringExtra("type"); 
			Log.e(ACTION, action);
			if(action.equals("del")){
				LauncherApp.getInstance().notifyMsgRTC2Family(context, type);
				}else if(action.equals("add")){
					LauncherApp.getInstance().notifyMsgRTC2User(type);
				}
	}
}
