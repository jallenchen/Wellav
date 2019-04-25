package com.wellav.dolphin.netease.config;

import android.app.Activity;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;
import com.wellav.dolphin.launcher.MainActivity;

/**
 * Created by huangjun on 2016/3/15.
 */
public class FlavorDependent implements IFlavorDependent{

    @Override
    public String getFlavorName() {
        return "meeting";
    }

    @Override
    public Class<? extends Activity> getMainClass() {
    	return MainActivity.class;
    }

    @Override
    public MsgAttachmentParser getMsgAttachmentParser() {
        return new CustomAttachParser();
    }

    @Override
    public void onLogout() {
        ChatRoomHelper.logout();
    }

    public static FlavorDependent getInstance() {
        return InstanceHolder.instance;
    }

    public static class InstanceHolder {
        public final static FlavorDependent instance = new FlavorDependent();
    }

    @Override
    public void onApplicationCreate() {
        ChatRoomHelper.init();
    }
}
