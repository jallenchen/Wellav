package com.wellav.dolphin.bind;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInform;
import com.wellav.dolphin.bean.UserInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.fragment.BaseFragment;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadFamilyUserFromServer;
import com.wellav.dolphin.net.LoadFamilyUserFromServer.familyUserDataCallBack;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.net.LoadUsersFromServer;
import com.wellav.dolphin.net.LoadUsersFromServer.userDataCallBack;
import com.wellav.dolphin.net.UploadData2Server;
import com.wellav.dolphin.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.net.XMLBody;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.utils.JsonUtils;
import com.wellav.dolphin.utils.Util;

public class BindBeManagerFragment extends BaseFragment implements OnClickListener {
	private TextView name;
	private CircleImageView icon;
	private Button mrefused;
	private Button maccept;
	private UserInfo info;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_be_manager, container,
				false);
		name = (TextView) view.findViewById(R.id.name);
		icon = (CircleImageView) view.findViewById(R.id.icon);
		mrefused = (Button) view.findViewById(R.id.refused);
		maccept = (Button) view.findViewById(R.id.accept);

		initData();

		mrefused.setOnClickListener(this);
		maccept.setOnClickListener(this);
		return view;
	}

	public static Fragment newBindBeManager(String userid) {
		BindBeManagerFragment BindBeManager = new BindBeManagerFragment();
		Bundle bundle = new Bundle();
		bundle.putString("userid", userid);
		BindBeManager.setArguments(bundle);
		return BindBeManager;
	}

	private void initData() {
		String userid = getArguments().getString("userid");
		String body = XMLBody.getUserInfo(SysConfig.DolphinToken, userid);
		LoadUsersFromServer task = new LoadUsersFromServer(body);
		task.getData(new userDataCallBack() {
			@Override
			public void onDataCallBack(int code, UserInfo data) {
				if (code == 0) {
					info = data;
					initView(data);
				}
			}
		});
	}

	private void initView(UserInfo data) {
		name.setText(data.getNickName());
		LoadUserAvatarFromLocal avatar = new LoadUserAvatarFromLocal();
		Bitmap head = avatar.loadImage(data.getLoginName());
		if (head != null) {
			icon.setImageBitmap(head);
		}
	}

	private void join2Manager(String name) {
		String body = XMLBody.AddUser2Family(SysConfig.DolphinToken,
				LauncherApp.getInstance().getFamily().getFamilyID(), name);
		UploadData2Server task = new UploadData2Server(body, "UserJoinFamily");
		task.getData(new codeDataCallBack() {

			@Override
			public void onDataCallBack(String request, int code) {
				if (code == 0) {
					LauncherApp.getInstance().getFamily()
							.setMangerID(info.getUserID());
					getFamilyUser();
				}
			}
		});
	}

	private void getFamilyUser() {
		String body = XMLBody.getFamilyUserInfo(SysConfig.DolphinToken,
				LauncherApp.getInstance().getFamily().getFamilyID(),
				info.getUserID());
		LoadFamilyUserFromServer task = new LoadFamilyUserFromServer(
				getActivity(), body);
		task.getFamilyUserData(new familyUserDataCallBack() {
			@Override
			public void onDataCallBack(int code, FamilyUserInfo data) {
				LauncherApp.getInstance().setFamilyManager(data);
				uploadMsg(data);
			}
		});
	}

	private void uploadMsg(final FamilyUserInfo data) {
		MessageInform msgInfo = new MessageInform();
		msgInfo.setDolphinName(LauncherApp.getInstance().getFamily()
				.getNickName());
		msgInfo.setDeviceID(LauncherApp.getInstance().getFamily().getDeviceID());

		msgInfo.setUserID(data.getUserID());
		msgInfo.setFamilyID(data.getFamilyID());
		msgInfo.setName(data.getNickName());
		msgInfo.setEventType(MessageEventType.EVENTTYPE_BEEN_MANAGER);

		String msgContent = JsonUtils.getJsonObject(msgInfo, "normal")
				.toString();

		String mBody = XMLBody.UploadMessageBox(SysConfig.DolphinToken, 1,
				data.getFamilyID(), msgContent);
		UploadData2Server task = new UploadData2Server(mBody,
				"UploadMessageBox");
		task.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				Log.e("BindBeManagerFragment", "UploadMessageBox:" + code);
				if (code == 0) {
					LauncherApp.getInstance().notifyMsgRTC2Family(
							getActivity(), "normal");
				}
				getActivity().finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accept:
			if (info != null) {
				join2Manager(info.getLoginName());
			} else {
				Util.DisplayToast(getActivity(), "绑定失败");
			}
			break;
		case R.id.refused:
			getActivity().finish();
			break;
		}
	}

}
