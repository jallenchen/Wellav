package com.wellav.dolphin.mmedia.fragment;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MessageEventType;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.AgentContact;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.net.LoadAgentContactFromServer;
import com.wellav.dolphin.mmedia.net.LoadAgentContactFromServer.AgentContactDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class MyFamilyInfoFragment extends BaseFragment implements OnClickListener,
		OnCheckedChangeListener {
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private CircleImageView mHeadIcon;
	private TextView mNotename;
	private TextView mNickname;
	private ImageView mSex;
	private ImageView mType;
	private TextView mModifyNotename;
	private TextView mCity;
	private TextView mItem2;
	private TextView mItem2Content;
	private Button mTurnOver, mDissolve, test;
	private FamilyUserInfo fUerInfo;
	private boolean isMyFamily;
	private TextView mModifyNickname;
	private TextView mModifyHead;
	private View View3, View4;
	private RelativeLayout mPrivacyModeLayout;
	private ToggleButton mPrivacyMode;
	private int mFamilyPos = 0;
	private LoadUserAvatarFromLocal userAvatar;
	private MyFamilyInfoFragment ctx;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		this.ctx = this;
		
		View view = inflater.inflate(R.layout.fragment_myfamilyinfo, container,
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
		mItem2 = (TextView) view.findViewById(R.id.info_item2);
		mItem2Content = (TextView) view.findViewById(R.id.info_item2_content);

		mModifyNickname = (TextView) view
				.findViewById(R.id.info_modifynickname);
		mModifyHead = (TextView) view.findViewById(R.id.info_modifyhead);
		mTurnOver = (Button) view.findViewById(R.id.info_turn_over);
		mDissolve = (Button) view.findViewById(R.id.info_dissolve);
		mPrivacyMode = (ToggleButton) view.findViewById(R.id.info_switch);

		initView();

		return view;
	}

	private void initView() {

		fUerInfo = (FamilyUserInfo) getArguments().getSerializable("UsersDetail");
		mFamilyPos = getArguments().getInt("CurrentFamily", 0);
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
		if (DolphinUtil.getPrivacy(fUerInfo.getAuthority())) {
			mPrivacyMode.setChecked(true);
		} else {
			mPrivacyMode.setChecked(false);
		}

		String contactUid = SQLiteManager.getInstance().getAgentContactIDByDeviceUserID(fUerInfo.getUserID());
		if (contactUid.equals("0")) {
			String body = RequestString.GetDolphinAgentContact(
					SysConfig.dtoken, fUerInfo.getLoginName());
			LoadAgentContactFromServer task = new LoadAgentContactFromServer(
					body);
			task.getAgentContact(new AgentContactDataCallBack() {

				@Override
				public void onDataCallBack(int code, AgentContact data) {
					if (data.getUserNickname().equals("")) {
						mItem2Content.setText(ctx.getResources().getString(R.string.no_setting));
					} else {
						mItem2Content.setText(data.getUserNickname());
					}

				}
			});

		} else {
			FamilyUserInfo contactInfo = SQLiteManager.getInstance()
					.geFamilyUserInfoUserId(contactUid);
			mItem2Content.setText(contactInfo.getNickName());
		}
		String note = TextUtils.isEmpty(fUerInfo.getNoteName())? fUerInfo.getNickName() : fUerInfo.getNoteName();
		mNotename.setText(note);
		mNickname.setText(ctx.getResources().getString(R.string.nickName) + fUerInfo.getNickName());
		mCity.setText(fUerInfo.getCity());
		mActionbarName.setText(R.string.actionbar_info);

		mActionbarPrev.setOnClickListener(this);
		mModifyNotename.setOnClickListener(this);
		mModifyNickname.setOnClickListener(this);
		mModifyHead.setOnClickListener(this);
		mTurnOver.setOnClickListener(this);
		mDissolve.setOnClickListener(this);
		mPrivacyMode.setOnCheckedChangeListener(this);
	}
	
	public static MyFamilyInfoFragment newInstance(Bundle bundle){
		MyFamilyInfoFragment myFamily=new MyFamilyInfoFragment();
		myFamily.setArguments(bundle);
		return myFamily;
	}

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();

		switch (v.getId()) {
		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		case R.id.info_note:

			ModifyNameFragment notename = getResultName(true);
			bundle.putSerializable("UsersDetail", fUerInfo);
			bundle.putInt("NAMETYPE", 0);
			notename.setArguments(bundle);
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.addToBackStack(null);
			tx.add(R.id.container, notename);
			tx.commit();

			break;
		case R.id.info_modifynickname:

			ModifyNameFragment nickname = getResultName(false);
			bundle.putSerializable("UsersDetail", fUerInfo);
			bundle.putInt("NAMETYPE", 1);
			nickname.setArguments(bundle);
			FragmentManager fm2 = getActivity().getSupportFragmentManager();
			FragmentTransaction tx2 = fm2.beginTransaction();
			tx2.addToBackStack(null);
			tx2.add(R.id.container, nickname);
			tx2.commit();
			break;
		case R.id.info_modifyhead:
			takePhoto(1);
			break;
		case R.id.info_turn_over:
			ChooseNewAdminFragment NewAdmin = new ChooseNewAdminFragment();
			bundle.putString("FamilyId", fUerInfo.getFamilyID());
			bundle.putString("FamilyName", fUerInfo.getNoteName());
			NewAdmin.setArguments(bundle);
			FragmentManager fm3 = getActivity().getSupportFragmentManager();
			FragmentTransaction tx3 = fm3.beginTransaction();
			tx3.add(R.id.container, NewAdmin, "ChooseNewAdmin");
			tx3.commit();
			break;
		case R.id.info_dissolve:
			comfirmDialog();

			break;
		}
	}

	protected void comfirmDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage(ctx.getResources().getString(R.string.delete_all_message));

		// builder.setTitle("解散该群组");

		builder.setPositiveButton(ctx.getResources().getString(R.string.conform), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				checkIfSetSPsw();
			}
		});

		builder.setNegativeButton(ctx.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}
	
	private void checkIfSetSPsw(){
		String body = RequestString.GetIfSecPWSeted(SysConfig.dtoken,fUerInfo.getFamilyID());
		UploadData2Server task = new UploadData2Server(body, "GetIfSecPWSeted");
		task.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				if(code == 1){
					secPswDialog();
				}else if(code == 2){
					releaseFamily();
				}else{
					
				}
			}
		});
	}

	private void secPswDialog() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_edittext,
				(ViewGroup) getActivity().findViewById(R.id.dialog));

		final EditText eText = (EditText) layout.findViewById(R.id.etname);
		new AlertDialog.Builder(getActivity()).setTitle(ctx.getResources().getString(R.string.put_second_psaaword))
				.setIcon(android.R.drawable.ic_dialog_info).setView(layout)
				.setPositiveButton(ctx.getResources().getString(R.string.conform), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						checkSecPsw(eText.getEditableText().toString());
					}
				}).setNegativeButton(ctx.getResources().getString(R.string.cancel), null).show();
	}

	private void checkSecPsw(String sec) {
		String sPsw = DolphinUtil.md5(sec);
		String body = RequestString.CheckFamilySecPW(SysConfig.dtoken,
				fUerInfo.getFamilyID(), sPsw);
		UploadData2Server task = new UploadData2Server(body, "CheckFamilySecPW");
		task.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				if (code == 0) {
					releaseFamily();
				} else if (code == -1700) {
					CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.second_psaaword_wrong));
				} else {
					CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.second_psaaword_fail));
				}
			}
		});

	}

	private ModifyNameFragment getResultName(final boolean flag) {
		ModifyNameFragment ModifyName = ModifyNameFragment
				.newInstance();

		ModifyName.setResultListener(new ModifyNameFragment.INameTextListener() {
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

	private void releaseFamily() {
		String mBody = RequestString.RealseFamily(SysConfig.dtoken,
				fUerInfo.getFamilyID());
		UploadData2Server release = new UploadData2Server(mBody, "RealseFamily");
		release.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				uploadMsg();
			}
		});
	}

	private void uploadMsg() {

		final MessageInform msgInfo = new MessageInform();
		msgInfo.setName(DolphinApp.getInstance().getMyUserInfo().getNickName());
		msgInfo.setUserID(DolphinApp.getInstance().getMyUserInfo().getUserID());

		msgInfo.setEventType(MessageEventType.EVENTTYPE_RELEASE_FAMILY);
		msgInfo.setDeviceID(fUerInfo.getLoginName());
		msgInfo.setFamilyId(fUerInfo.getFamilyID());
		msgInfo.setDolphinName(fUerInfo.getNickName());

		String msgContent = JsonUtil.getBOxMsgJsonObject(msgInfo);

		ArrayList<String> userids = SQLiteManager.getInstance()
				.getUseridByFamilyId(fUerInfo.getFamilyID());
		String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, 0,
				userids, msgContent);

		UploadData2Server userLeave = new UploadData2Server(mBody,
				"UploadMessageBox");
		userLeave.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				CommFunc.PrintLog("MemberInfoFragment", "UploadData2Server"
						+ request + code);
				if (code == 0) {
					// SQLiteManager.getInstance().saveBoxMessage(msgInfo);
					DolphinApp.getInstance().getFamilyUserMap()
							.remove(mFamilyPos);
//					DolphinApp.getInstance().notifyMsgRTC2Family(
//							fUerInfo.getFamilyID());
					SQLiteManager.getInstance().deleteFamilyInfoById(
							fUerInfo.getFamilyID(), true);
					getActivity().finish();
				}

			}
		});

		// if(isCenter == true){
		// DolphinApp.getInstance().getFamilyUserMap().remove(MyFamilyPos);
		// }
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView,
			final boolean isChecked) {
		String token = SysConfig.dtoken;
		String isprivacy="0";
		if(isChecked == true){
			isprivacy = "1";
		}else{
			isprivacy = "0";
		}
		String setPrivacy = RequestString.SetDolphinConfig(token,
				fUerInfo.getFamilyID(), "ISPrivacy", isprivacy);
		UploadData2Server release = new UploadData2Server(setPrivacy,
				"SetDolphinConfig");
		release.getData(new codeDataCallBack() {

			@Override
			public void onDataCallBack(String request, int code) {
				int mAuthor = 6;
				if (code == 0) {
					int author = Integer.parseInt(fUerInfo.getAuthority());

					if (isChecked == false) {
						mAuthor = author | 4;

					} else {
						mAuthor = author & 3;
					}
					CommFunc.PrintLog("MyFamilyInfoFragment", "mAuthor:"
							+ mAuthor);
					DolphinApp.getInstance().getFamilyUserMap().get(mFamilyPos)
							.get(0).setAuthority(mAuthor + "");
					SQLiteManager.getInstance().updateFamilyUserInfoAuthor(
							fUerInfo.getFamilyID(), fUerInfo.getUserID(),
							FamilyUserInfo._FAMILY_USER_AUTHORITY,
							mAuthor + "", false);
					SQLiteManager.getInstance().updateFamilyInfo(
							fUerInfo.getUserID(), FamilyInfo._FAMILY_AUTHORITY,
							mAuthor + "", true);

				}

				String setPrivacy = RequestString.UploadMessageBox(
						SysConfig.dtoken,
						1,
						fUerInfo.getFamilyID(),
						JsonUtil.getUserWatchAuthJson(fUerInfo.getFamilyID(),
								fUerInfo.getUserID(), mAuthor).toString());
				UploadData2Server task = new UploadData2Server(setPrivacy,
						"UploadMessageBox");
				task.getData(new codeDataCallBack() {
					@Override
					public void onDataCallBack(String request, int code) {
//						DolphinApp.getInstance().notifyMsgRTC2Family(
//								fUerInfo.getFamilyID());
					}
				});

			}
		});

	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == getActivity().RESULT_OK) {
				cropPhoto(data.getData());// 裁剪图片
			}
			break;
		case 2:
			if (resultCode == getActivity().RESULT_OK) {
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/" + fUerInfo.getLoginName() + ".jpg");
				cropPhoto(Uri.fromFile(temp));// 裁剪图片
			}
			break;
		case 3:
			if (data != null) {
				Bundle extras = data.getExtras();
				Bitmap HeadBitmap = extras.getParcelable("data");
				String Icon = BitmapUtils.bitmapToBase64(HeadBitmap);
				LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
				task.saveAvatar(Icon, fUerInfo.getLoginName());

				// fUerInfo.setIcon(Icon);
				// SQLiteManager.getInstance().updateFamilyUserInfo(fUerInfo.getUserID(),FamilyUserInfo._FAMILY_USER_PHOTO_ID,Icon,true);

				if (HeadBitmap != null) {
					/**
					 * 上传服务器代码
					 */
					String token = SysConfig.dtoken;
					String headUpdate = RequestString.updateUserHead(token,
							fUerInfo.getUserID(), Icon);
					UploadData2Server release = new UploadData2Server(
							headUpdate, "UpdateUserInfo");
					release.getData(new codeDataCallBack() {
						@Override
						public void onDataCallBack(String request, int code) {
							uploadMsg(fUerInfo.getUserID(), 0);
						}
					});

					mHeadIcon.setImageBitmap(HeadBitmap);// 用ImageView显示出来
				}
			}
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 调用系统的裁剪
	 * 
	 * @param uri
	 */
	public void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	public void takePhoto(int id) {
		Intent intent;
		switch (id) {
		case 0:// 调用相机拍照
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
					Environment.getExternalStorageDirectory(), fUerInfo
							.getLoginName() + ".jpg")));
			startActivityForResult(intent, 2);// 采用ForResult打开
			break;
		case 1:// 从相册里面取照片
			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 1);
			break;
		default:
			break;
		}
	}

	private void uploadMsg(String userid, int item) {

		final ArrayList<String> familyids = SQLiteManager.getInstance()
				.getFamilyIDByUserId(userid);
		final int familySize = familyids.size();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "update");
			jsonObject.put("userid", userid);
			jsonObject.put("eventtype", item);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String leaveBody = RequestString.UploadMessageBox(SysConfig.dtoken, 1,
				familyids, jsonObject.toString());
		UploadData2Server user2Leave = new UploadData2Server(leaveBody,
				"UploadMessageBox");
		user2Leave.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				for (int i = 0; i < familySize; i++) {
//					DolphinApp.getInstance().notifyMsgRTC2Family(
//							familyids.get(i));
				}
			}
		});

	}
}
