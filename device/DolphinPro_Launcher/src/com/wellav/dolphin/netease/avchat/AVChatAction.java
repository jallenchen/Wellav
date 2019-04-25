package com.wellav.dolphin.netease.avchat;

import android.content.Context;
import android.widget.Toast;

import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.netease.config.NetworkUtil;

/**
 * Created by hzxuwen on 2015/6/12.
 */
@SuppressWarnings("serial")
public class AVChatAction extends BaseAction {
    private AVChatType avChatType;
    private Context mCxt;
    private String mAcc;

    public AVChatAction(Context ct,String account,AVChatType avChatType) {
        super(avChatType == AVChatType.AUDIO ? R.drawable.message_plus_audio_chat_selector : R.drawable.message_plus_video_chat_selector,
                avChatType == AVChatType.AUDIO ? R.string.input_panel_audio_call : R.string.input_panel_video_call);
        this.avChatType = avChatType;
        this.mCxt = ct;
        this.mAcc = account;
    }

    @Override
    public void onClick() {
        if (NetworkUtil.isNetAvailable(this.mCxt)) {
            startAudioVideoCall(avChatType);
        } else {
            Toast.makeText(this.mCxt, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    /************************ 音视频通话 ***********************/

    public void startAudioVideoCall(AVChatType avChatType) {
        AVChatActivity.start(this.mCxt, this.mAcc, avChatType.getValue(), AVChatActivity.FROM_INTERNAL);
    }
}
