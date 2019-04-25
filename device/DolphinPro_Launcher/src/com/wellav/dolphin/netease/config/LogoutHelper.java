package com.wellav.dolphin.netease.config;

import android.app.Activity;
import android.content.Context;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.wellav.dolphin.db.PreferenceUtils;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout(Context context, boolean isKickOut) {
    	//PreferenceUtils.getInstance().saveSharePreferences(true,false);
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
}
