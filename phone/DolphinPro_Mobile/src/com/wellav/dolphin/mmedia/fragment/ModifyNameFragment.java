package com.wellav.dolphin.mmedia.fragment;

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

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class ModifyNameFragment extends BaseFragment implements OnClickListener {
	private static final int Modify_Note = 0;
	private static final int Modify_FamilyNick = 1;
	private ImageView mActionbarPrev;
	private TextView mActionbarName;
	private TextView mNameTitle;
	private EditText mModifyName;
	private Button mFinish;
	private String mName;
	private String key;
	private int mNameType;
	private FamilyUserInfo fUerInfo;

	public interface INameTextListener {
		public void nameTextListener(String selectID);
	}

	private INameTextListener mNameTextListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_modifyname, container,
				false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mNameTitle = (TextView) view.findViewById(R.id.name_title);
		mModifyName = (EditText) view.findViewById(R.id.edit_name);
		mFinish = (Button) view.findViewById(R.id.finish);
		mFinish.setVisibility(View.VISIBLE);

		mNameType = getArguments().getInt("NAMETYPE", Modify_Note);
		fUerInfo = (FamilyUserInfo) getArguments().getSerializable(
				"UsersDetail");
		if (mNameType == Modify_Note) {
			mActionbarName.setText(R.string.actionbar_note);
			mName = fUerInfo.getNoteName();
			mNameTitle.setText(R.string.info_new_note);

		} else {
			mActionbarName.setText(R.string.actionbar_nick);
			mName = fUerInfo.getNickName();
			mNameTitle.setText(R.string.info_new_nick);
		}
		mModifyName.setText(mName);

		mActionbarPrev.setOnClickListener(this);
		mFinish.setOnClickListener(this);
		return view;
	}

	public void setResultListener(INameTextListener listener) {
		mNameTextListener = listener;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		goneKeyboard();
		switch (v.getId()) {
		case R.id.finish:
			String token = SysConfig.dtoken;
			mName = mModifyName.getText().toString();
			if (mName == null || mName.equals("")) {
				CommFunc.DisplayToast(getActivity(), R.string.no_none);
				return;
			}
			if (mNameType == Modify_Note) {
				fUerInfo.setNoteName(mName);
				String noteUpdate = RequestString.updateUserNoteName(token,
						mName, fUerInfo.getUserID());
				UploadData2Server task = new UploadData2Server(noteUpdate,
						"UpdateUserInfo");
				task.getData(new codeDataCallBack() {

					@Override
					public void onDataCallBack(String request, int code) {
						// TODO Auto-generated method stub
						if (code == MsgKey.KEY_RESULT_SUCCESS) {
							SQLiteManager.getInstance().updateFamilyUserInfo(
									fUerInfo.getUserID(), key, mName, true);
						}
					}
				});
				key = FamilyUserInfo._FAMILY_USER_NOTE_NAME;
			} else if (mNameType == Modify_FamilyNick) {
				fUerInfo.setNickName(mName);
				String nickUpdate = RequestString.updateFamilyNickName(token,
						mName, fUerInfo.getFamilyID());
				UploadData2Server task = new UploadData2Server(nickUpdate,
						"UpdateFamilyInfo");
				task.getData(new codeDataCallBack() {

					@Override
					public void onDataCallBack(String request, int code) {
						// TODO Auto-generated method stub
						uploadMsg(fUerInfo);
					}
				});

				key = FamilyUserInfo._FAMILY_USER_NICK_NAME;
			} else if (mNameType == 2) {
				fUerInfo.setNickName(mName);
				key = FamilyUserInfo._FAMILY_USER_NICK_NAME;
				String updateUserNickName = RequestString.updateUserNickName(
						token, mName);
				UploadData2Server task = new UploadData2Server(
						updateUserNickName, "UpdateUserInfo");
				task.getData(new codeDataCallBack() {

					@Override
					public void onDataCallBack(String request, int code) {
						// TODO Auto-generated method stub
						if (code == MsgKey.KEY_RESULT_SUCCESS) {
							SQLiteManager.getInstance().updateFamilyUserInfo(
									fUerInfo.getUserID(), key, mName, true);
						}
					}
				});
			}
			mNameTextListener.nameTextListener(mName);
			break;
		case R.id.actionbar_prev:

			break;

		}
		getFragmentManager().beginTransaction().remove(ModifyNameFragment.this)
				.commit();
	}

	private void uploadMsg(final FamilyUserInfo info) {

		final String familyid = info.getFamilyID();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "update");
			jsonObject.put("userid", info.getUserID());
			jsonObject.put("eventtype", MsgKey.Up_FamilyNickName);
			jsonObject.put("name", info.getNickName());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String leaveBody = RequestString.UploadMessageBox(SysConfig.dtoken,
				MsgKey.TO_FAMILY, familyid, jsonObject.toString());
		UploadData2Server user2Leave = new UploadData2Server(leaveBody,
				"UploadMessageBox");
		user2Leave.getData(new codeDataCallBack() {

			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
			//	DolphinApp.getInstance().notifyMsgRTC2Family(familyid);
				SQLiteManager.getInstance().updateFamilyUserInfo(
						info.getUserID(),
						FamilyUserInfo._FAMILY_USER_NICK_NAME,
						info.getNickName(), true);
				SQLiteManager.getInstance().updateFamilyInfo(info.getUserID(),
						FamilyInfo._FAMILY_NICKNAME, info.getNickName(), true);
			}
		});
	}

	public static ModifyNameFragment newInstance() {
		ModifyNameFragment fragment = new ModifyNameFragment();
		return fragment;
	}

}
