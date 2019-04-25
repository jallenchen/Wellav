package com.wellav.dolphin.mmedia.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MessageEventType;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.AgentContact;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.net.LoadAgentContactFromServer;
import com.wellav.dolphin.mmedia.net.LoadAgentContactFromServer.AgentContactDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class MemberInfoFragment extends BaseFragment implements OnClickListener {
	private FamilyUserInfo fUerInfo;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private CircleImageView mHeadIcon;
	private TextView mNotename;
	private TextView mNickname;
	private ImageView mSex;
	private ImageView mType;
	private TextView mModifyNotename;
	private TextView mCity;
	private TextView mAge;
	private TextView mAgeTxt;
	private Button mButton;
	private RelativeLayout mMomentLayout;
	private View view2;
	private LoadUserAvatarFromLocal userAvatar;

	private boolean IsCener;
	private boolean isMyFamily;
	private int MyFamilyPos;
	private int MyMemberPos;
	private MemberInfoFragment ctx;

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		this.ctx = this;
		
		View view = inflater.inflate(R.layout.fragment_hmemberinfo, container,
				false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mHeadIcon = (CircleImageView) view.findViewById(R.id.info_icon);
		mSex = (ImageView) view.findViewById(R.id.info_sex);
		mType = (ImageView) view.findViewById(R.id.info_type);
		mNotename = (TextView) view.findViewById(R.id.info_notename);
		mNickname = (TextView) view.findViewById(R.id.info_nickname);
		mModifyNotename = (TextView) view.findViewById(R.id.info_note);
		mCity = (TextView) view.findViewById(R.id.info_city);
		mAge = (TextView) view.findViewById(R.id.info_age_content);
		mAgeTxt = (TextView) view.findViewById(R.id.info_age);
		mMomentLayout = (RelativeLayout) view.findViewById(R.id.moment_layout);
		view2 = (View) view.findViewById(R.id.view2);
		mButton = (Button) view.findViewById(R.id.del_btn);

		initView();
		return view;
	}

	private void initView() {
		fUerInfo =  (FamilyUserInfo) getArguments().getSerializable("UsersDetail");
		IsCener = getArguments().getBoolean("IsCener", false);
		isMyFamily = getArguments().getBoolean("MyFamily", false);
		MyFamilyPos = getArguments().getInt("CurrentFamily");
		MyMemberPos = getArguments().getInt("CurrentMember");

		userAvatar = new LoadUserAvatarFromLocal();
		Bitmap head = userAvatar.loadImage(fUerInfo.getLoginName());
		if (head != null) {
			mHeadIcon.setImageBitmap(head);
		} else {
			if (fUerInfo.getSex().equals(ctx.getResources().getString(R.string.male))) {
				mHeadIcon.setImageResource(R.drawable.ic_defaulthead_male_40dp);
			} else if (fUerInfo.getSex().equals(ctx.getResources().getString(R.string.female))) {
				mHeadIcon
						.setImageResource(R.drawable.ic_defaulthead_female_40dp);
			} else {
				mHeadIcon.setImageResource(R.drawable.ic_defaulthead_home_40dp);
			}
		}
		if (fUerInfo.getSex().equals(ctx.getResources().getString(R.string.male))) {
			mSex.setImageResource(R.drawable.ic_sex_male);
		} else if (fUerInfo.getSex().equals(ctx.getResources().getString(R.string.female))) {
			mSex.setImageResource(R.drawable.ic_sex_female);
		} else {
			mSex.setVisibility(View.INVISIBLE);
			mType.setImageResource(R.drawable.ic_type_dolphin);
		}

		String note = TextUtils.isEmpty(fUerInfo.getNoteName())? fUerInfo.getNickName() : fUerInfo.getNoteName();
		mNotename.setText(note);
		mNickname.setText(ctx.getResources().getString(R.string.nickName) + fUerInfo.getNickName());
		if (fUerInfo.getDeviceType().equals("1")) {
			String body = RequestString.GetDolphinAgentContact(
					SysConfig.dtoken, fUerInfo.getLoginName());
			LoadAgentContactFromServer task = new LoadAgentContactFromServer(
					body);
			task.getAgentContact(new AgentContactDataCallBack() {
				@Override
				public void onDataCallBack(int code, AgentContact data) {
					if (data.getUserNickname().equals("")) {
						mAge.setText(ctx.getResources().getString(R.string.no_setting));
					} else {
						mAge.setText(data.getUserNickname());
					}

				}
			});

			mAgeTxt.setText(R.string.info_contact);
			mMomentLayout.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);
			if (IsCener == true) {
				mButton.setText(R.string.quit_family);
			}

		} else {
			mAge.setText(DolphinUtil.getAge(fUerInfo.getBirthday()));
		}
		mCity.setText(fUerInfo.getCity());
		if (DolphinUtil.getManger(fUerInfo.getAuthority()) == 1
				|| (isMyFamily == false && IsCener == false)) {
			mButton.setVisibility(View.INVISIBLE);
		}

		mButton.setOnClickListener(this);
		mActionbarName.setText(R.string.actionbar_info);
		mActionbarPrev.setOnClickListener(this);
		mModifyNotename.setOnClickListener(this);
	}
	
	public static MemberInfoFragment newInstance(Bundle bundle){
		MemberInfoFragment myMember=new MemberInfoFragment();
		myMember.setArguments(bundle);
		return myMember;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.info_note:
			ModifyNameFragment notename = getResultName(true);
			Bundle bundle = new Bundle();
			bundle.putSerializable("UsersDetail", fUerInfo);
			bundle.putInt("NAMETYPE", 0);
			notename.setArguments(bundle);
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.addToBackStack(null);
			tx.add(R.id.container, notename);
			tx.commit();

			break;

		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		case R.id.del_btn:
			memberLeave();
			break;
		}

	}

	private ModifyNameFragment getResultName(final boolean flag) {
		ModifyNameFragment ModifyName = ModifyNameFragment
				.newInstance();

		ModifyName
				.setResultListener(new ModifyNameFragment.INameTextListener() {

					@Override
					public void nameTextListener(String selectID) {
						if (flag == true) {
							mNotename.setText(selectID);
						} else {
							mNickname.setText(selectID);
						}
					}
				});
		return ModifyName;
	}

	private void memberLeave() {
		String mBody;
		if (IsCener == true) {
			mBody = RequestString.UserLeaveFamily(SysConfig.dtoken,
					SysConfig.uid, fUerInfo.getFamilyID());
			UploadData2Server userLeave = new UploadData2Server(mBody,
					"UserLeaveFamily");
			userLeave.getData(new codeDataCallBack() {
				@Override
				public void onDataCallBack(String request, int code) {
					CommFunc.PrintLog("MemberInfoFragment", "UploadData2Server"
							+ request + code);
					if (code == MsgKey.KEY_RESULT_SUCCESS) {
						uploadMsg(IsCener);
					}
				}
			});
		} else {
			mBody = RequestString.UserLeaveFamily(SysConfig.dtoken,
					fUerInfo.getLoginName(), fUerInfo.getFamilyID());
			UploadData2Server userLeave = new UploadData2Server(mBody,
					"UserLeaveFamily");
			userLeave.getData(new codeDataCallBack() {
				@Override
				public void onDataCallBack(String request, int code) {
					CommFunc.PrintLog("MemberInfoFragment", "UploadData2Server"
							+ request + code);
					if (code == MsgKey.KEY_RESULT_SUCCESS) {
						uploadMsg(IsCener);
					}
				}
			});
		}
	}

	private void uploadMsg(final boolean isCenter) {

		final MessageInform msgInfo = new MessageInform();
		if (isCenter == true) {
			msgInfo.setName(DolphinApp.getInstance().getMyUserInfo()
					.getNickName());
			msgInfo.setUserID(DolphinApp.getInstance().getMyUserInfo()
					.getUserID());
		} else {
			msgInfo.setName(fUerInfo.getNickName());
			msgInfo.setUserID(fUerInfo.getUserID());
		}

		msgInfo.setEventType(MessageEventType.EVENTTYPE_EXIT_FAMILY);
		msgInfo.setDeviceID(DolphinApp.getInstance().getFamilyUserMap()
				.get(MyFamilyPos).get(0).getLoginName());
		msgInfo.setFamilyId(fUerInfo.getFamilyID());
		msgInfo.setDolphinName(DolphinApp.getInstance().getFamilyUserMap()
				.get(MyFamilyPos).get(0).getNickName());

		String msgContent = JsonUtil.getBOxMsgJsonObject(msgInfo);
		String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, 1,
				fUerInfo.getFamilyID(), msgContent);
		UploadData2Server userLeave = new UploadData2Server(mBody,
				"UploadMessageBox");

		if (isCenter == false) {
			String leaveBody = RequestString.UploadMessageBox(SysConfig.dtoken,
					0, fUerInfo.getUserID(), msgContent);
			UploadData2Server user2Leave = new UploadData2Server(leaveBody,
					"UploadMessageBox");
			user2Leave.getData(new codeDataCallBack() {
				@Override
				public void onDataCallBack(String request, int code) {
					//Log.e("user2Leave", "给删除的成员发消息");
					DolphinApp.getInstance().getFamilyUserMap()
							.get(MyFamilyPos).remove(MyMemberPos);
				}
			});
		}

		userLeave.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				CommFunc.PrintLog("MemberInfoFragment", "UploadData2Server"
						+ request + code);
				if (code == MsgKey.KEY_RESULT_SUCCESS) {

//					DolphinApp.getInstance().notifyMsgRTC2Family(
//							fUerInfo.getFamilyID());
					if (isCenter) {
						DolphinApp.getInstance().getFamilyUserMap()
								.remove(MyFamilyPos);
						SQLiteManager.getInstance().saveBoxMessage(msgInfo);
						SQLiteManager.getInstance().deleteFamilyInfoById(
								fUerInfo.getFamilyID(), true);
					} else {
						SQLiteManager.getInstance().deleteFamilyUserInfoById(
								fUerInfo.getFamilyID(), fUerInfo.getUserID(),
								true);
						DolphinApp.getInstance().getFamilyUserMap()
								.get(MyFamilyPos).remove(fUerInfo);
					}
					getActivity().finish();
				}
			}
		});
	}
}
