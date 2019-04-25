package com.wellav.dolphin.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.CheckExistData;
import com.wellav.dolphin.bean.InviteMessage;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.CheckUserIsExist;
import com.wellav.dolphin.net.CheckUserIsExist.checkDataCallBack;
import com.wellav.dolphin.net.UploadData2Server;
import com.wellav.dolphin.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.net.XMLBody;
import com.wellav.dolphin.utils.JsonUtils;
import com.wellav.dolphin.utils.Util;

public class AddContactFragment extends BaseFragment implements OnClickListener {
	private static final int BEINVITEEDBYDV = 100;
	private EditText mEdit;
	private Button mConfirm;
	private Button mCancel;
	private String mName;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_addmember, container,
				false);
		mEdit = (EditText) view.findViewById(R.id.edit);
		mConfirm = (Button) view.findViewById(R.id.confirm);
		mCancel = (Button) view.findViewById(R.id.cancel);

		mConfirm.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		goneKeyboard();
		switch (v.getId()) {
		case R.id.confirm:
			mName = mEdit.getText().toString();
			if(mName==null || mName.length() <8){
				Util.DisplayToast(getActivity(), getString(R.string.input_error));
				return;
			}
			addTeamMembers(mName);
			String mBody = XMLBody.UserIfExist(SysConfig.DolphinToken, mName);
			CheckUserIsExist isExist = new CheckUserIsExist(mBody);
			isExist.isCheckUserExist(new checkDataCallBack() {
				
				@Override
				public void onDataCallBack(CheckExistData data) {
					// TODO Auto-generated method stub
					if(data.isExist()){
						userJoinFamilyMsgUpload(data.getUserID());
					}else{
						Util.DisplayToast(getActivity(), getString(R.string.usernotexist));
					}
				}
			});
			break;
		case R.id.cancel:
			getFragmentManager().beginTransaction()
					.remove(AddContactFragment.this).commit();
			break;
		}
	}

	private void addTeamMembers(String name){
		List<String> acc = new ArrayList<String>();
		acc.add(name);
		NIMClient.getService(TeamService.class).addMembers(SysConfig.getInstance().getTeamId(), acc).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                // 返回onSuccess，表示拉人不需要对方同意，且对方已经入群成功了
            }

            @Override
            public void onFailed(int code) {
                // 返回onFailed，并且返回码为810，表示发出邀请成功了，但是还需要对方同意
            }

            @Override
            public void onException(Throwable exception) {
            }
        });
	}


	private void userJoinFamilyMsgUpload(String userid) {
		InviteMessage msg = new InviteMessage();
		JSONObject mJonMsg;
		msg.setType("invite");
		//没有家庭组，第一次添加成员的问题
		msg.setFamilyId(LauncherApp.getInstance().getFamily().getFamilyID());
		msg.setDeviceName(LauncherApp.getInstance().getFamily()
				.getNickName());
		msg.setDeviceId(LauncherApp.getInstance().getFamily()
				.getDeviceID());
		msg.setDeviceUserId(LauncherApp.getInstance().getFamily()
				.getDeviceUserID());
		msg.setManagerId("");
		msg.setManagerName("");
		msg.setManagerNickname("");
		msg.setUserId(userid);

		msg.setAction(BEINVITEEDBYDV);
		msg.setContent(getResources().getString(R.string.invite_join)
				+ LauncherApp.getInstance().getFamily()
						.getNickName());
		mJonMsg = JsonUtils.msgJson(msg);
		String mBody = XMLBody.UploadMessageBox(SysConfig.DolphinToken, 0,
				userid, mJonMsg.toString());

		UploadData2Server task = new UploadData2Server(mBody,
				"UploadMessageBox");
		task.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				LauncherApp.getInstance().notifyMsgRTC2User(mName);
				Util.DisplayToast(getActivity(), getString(R.string.invite_sent));
				getActivity().getSupportFragmentManager().beginTransaction().remove(AddContactFragment.this).commit();
			}
		});

	}


}
