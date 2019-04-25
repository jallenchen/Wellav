package com.wellav.dolphin.mmedia.netease;

import android.content.Context;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.wellav.dolphin.mmedia.commands.SysConfig;

public class SendNitificationMsgUtil {

	   public static void sendCustomNotification(final Context ct,String account, String content) {

	        CustomNotification notification = new CustomNotification();
	        notification.setFromAccount(SysConfig.uid);
	        notification.setSessionId(account);
	        notification.setSendToOnlineUserOnly(true);
	        notification.setSessionType(SessionTypeEnum.P2P);
	        notification.setContent(content);
			CustomNotificationConfig config = new CustomNotificationConfig();
			config.enablePush = true;
			notification.setConfig(config);
	        NIMClient.getService(MsgService.class).sendCustomNotification(notification).setCallback(new RequestCallback<Void>() {
	            @Override
	            public void onSuccess(Void param) {
	                Toast.makeText(ct, "成功", Toast.LENGTH_SHORT).show();
	            }

	            @Override
	            public void onFailed(int code) {
	                Toast.makeText(ct, "失败", Toast.LENGTH_SHORT).show();
	            }

	            @Override
	            public void onException(Throwable exception) {
	                Toast.makeText(ct, "失败", Toast.LENGTH_SHORT).show();
	            }
	        });
	    }
}
