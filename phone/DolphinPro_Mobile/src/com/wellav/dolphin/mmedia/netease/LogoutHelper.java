package com.wellav.dolphin.mmedia.netease;

import android.app.Activity;
import android.content.Context;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.wellav.dolphin.application.DemoCache;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.net.VolleyRequestQueueManager;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout(Context context, boolean isKickOut) {
    	PreferenceUtils.getInstance().saveSharePreferences(true,false);
        // 清理缓存&注销监听&清除状态
        DemoCache.getImageLoaderKit().clear();
        // flavor do logout
        FlavorDependent.getInstance().onLogout();
        DemoCache.clear();

        NIMClient.getService(AuthService.class).logout();

        // 启动登录
        LoginActivity.start(context, isKickOut);
        ((Activity)context).finish();
    }
	
    public static void resetApp(Context context) {
        // 清理缓存&注销监听&清除状态
        DemoCache.getImageLoaderKit().clear();
        // flavor do logout
        FlavorDependent.getInstance().onLogout();
        DemoCache.clear();

        NIMClient.getService(AuthService.class).logout();

        // 启动登录
        WelcomeActivity.start(context);
        ((Activity)context).finish();
    }

	
    public static void quit() {
    	PreferenceUtils.getInstance().saveSharePreferences(true,false);
        // 清理缓存&注销监听&清除状态
        DemoCache.getImageLoaderKit().clear();
        // flavor do logout
        FlavorDependent.getInstance().onLogout();
        DemoCache.clear();

        NIMClient.getService(AuthService.class).logout();
        DolphinApp.getInstance().getLocalActManager().finishActivity(); 
        VolleyRequestQueueManager.release(null);
        HBaseApp.post2WorkDelayed(new Runnable() {
			@Override
			public void run() {
				System.exit(0);
				int pid = android.os.Process.myPid();
				android.os.Process.killProcess(pid);
			}
		}, 0);
    }
}
