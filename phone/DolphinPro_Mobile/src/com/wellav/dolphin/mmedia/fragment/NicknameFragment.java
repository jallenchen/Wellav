package com.wellav.dolphin.mmedia.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class NicknameFragment extends BaseFragment implements OnClickListener {

	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private String nickname;
	private String userid;
	private EditText nicknameinput;
	private Button finish;

	public interface INameTextListener {
		public void nameTextListener(String selectID);
	}

	private INameTextListener mNameTextListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_nickname, container,
				false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		nicknameinput = (EditText) view.findViewById(R.id.new_nickname);
		finish = (Button) view.findViewById(R.id.finish);
		finish.setVisibility(View.VISIBLE);

		nickname = getArguments().getString("nickname");
		userid = getArguments().getString("userid");
		nicknameinput.setText(nickname);
		finish.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.nickname_change);
		return view;
	}

	@Override
	public void onClick(View v) {
		DolphinUtil.goneKeyboard(getActivity());
		switch (v.getId()) {
		case R.id.actionbar_prev:
			getActivity().getSupportFragmentManager().popBackStack(null, 0);
			break;

		case R.id.finish:
			String token = SysConfig.dtoken;
			nickname = nicknameinput.getText().toString();

			String updateUserNickName = RequestString.updateUserNickName(token,
					nickname);
			UploadData2Server nick = new UploadData2Server(updateUserNickName,
					"UpdateUserInfo");
			nick.getData(new codeDataCallBack() {

				@Override
				public void onDataCallBack(String request, int code) {
					// TODO Auto-generated method stub
					if (code == 0) {
						Toast.makeText(getActivity(), R.string.modify_succeed,
								Toast.LENGTH_SHORT).show();
						DolphinApp.getInstance().getMyUserInfo()
								.setNickName(nickname);
						PreferenceUtils.getInstance().saveSharePreferences(
								UserInfo._USER_NICK_NAME, nickname);
						updateMsg();

					}else{
						Toast.makeText(getActivity(), R.string.modify_fail,
								Toast.LENGTH_SHORT).show();
						getActivity().getSupportFragmentManager().popBackStack(null, 0);
					}
				}
			});

			mNameTextListener.nameTextListener(nickname);
			break;
		default:
			break;
		}
	}

	private void updateMsg() {
		uploadMsg(userid);
		getActivity().getSupportFragmentManager().popBackStack(null, 0);
	}

	private void uploadMsg(String userid) {

		final ArrayList<String> familyids = SQLiteManager.getInstance()
				.getFamilyIDByUserId(userid);
		final int familySize = familyids.size();

		SQLiteManager.getInstance().updateFamilyUserInfo(userid,
				FamilyUserInfo._FAMILY_USER_NICK_NAME, nickname, true);

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "update");
			jsonObject.put("userid", userid);
			jsonObject.put("name", nickname);
			jsonObject.put("eventtype", MsgKey.Up_Nickname);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String updateBody = RequestString.UploadMessageBox(SysConfig.dtoken,
				MsgKey.TO_FAMILY, familyids, jsonObject.toString());
		UploadData2Server userUpdate = new UploadData2Server(updateBody,
				"UploadMessageBox");
		userUpdate.getData(new codeDataCallBack() {

			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				for (int i = 0; i < familySize; i++) {
//					DolphinApp.getInstance().notifyMsgRTC2Family(
//							familyids.get(i));
				}
			}
		});

	}

	public void setResultListener(INameTextListener listener) {
		mNameTextListener = listener;
	}

	public static NicknameFragment newInstance() {
		NicknameFragment fragment = new NicknameFragment();
		return fragment;
	}
}
