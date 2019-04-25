package com.wellav.dolphin.mmedia.fragment;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.SelectCityActivity;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class MyInfoFragment extends BaseFragment implements OnClickListener {
	private static final int To_City = 0;
	private static final int To_CropPhoto = 2;
	private static final int To_TakePic = 1;
	private static final int To_UpAvater = 3;
	private LoadUserAvatarFromLocal userAvatar;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private CircleImageView mheadicon;
	private Button modifynickname;
	private Button modifyhead;
	private Button modifycity;
	private TextView mNickname;
	private TextView msex;
	private TextView mAge;
	private TextView mcity;
	private String mCity = "";
	private String phoneNum = "";
	private Bitmap mHeadBitmap;
	private String Userid = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_myinfo, container, false);
		modifynickname = (Button) view.findViewById(R.id.myinfo_bt_nickname);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		modifyhead = (Button) view.findViewById(R.id.myhead);
		modifycity = (Button) view.findViewById(R.id.mycity);
		mheadicon = (CircleImageView) view.findViewById(R.id.myinfo_headicon);
		mNickname = (TextView) view.findViewById(R.id.myinfo_nickname);
		msex = (TextView) view.findViewById(R.id.myinfo_sex);
		mAge = (TextView) view.findViewById(R.id.myinfo_age);
		mcity = (TextView) view.findViewById(R.id.myinfo_city);
		
		modifyhead.setOnClickListener(this);
		modifycity.setOnClickListener(this);
		modifynickname.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		mheadicon.setOnClickListener(this);
		
		Userid = DolphinApp.getInstance().getMyUserInfo().getUserID();
		userAvatar = new LoadUserAvatarFromLocal();
		mActionbarName.setText(R.string.myInfo);
		Bitmap head = userAvatar.loadImage(DolphinApp.getInstance()
				.getMyUserInfo().getLoginName());
		if (head != null) {
			mheadicon.setImageBitmap(head);
		} else {
			userAvatar.getData(Userid, new userAvatarDataCallBack() {
				@Override
				public void onDataCallBack(int code, Bitmap avatar) {
					mheadicon.setImageBitmap(avatar);
				}
			});
		}

		mNickname.setText(DolphinApp.getInstance().getMyUserInfo()
				.getNickName());
		msex.setText(DolphinApp.getInstance().getMyUserInfo().getSex());
		mAge.setText(DolphinUtil.getAge(DolphinApp.getInstance()
				.getMyUserInfo().getBirthday()));
		mcity.setText(DolphinApp.getInstance().getMyUserInfo().getCity());
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		case R.id.myinfo_bt_nickname:
			toNicknameFragment();
			break;
		case R.id.myhead:
			takePhoto(To_TakePic);
			break;
		case R.id.mycity:
			goCity();
			break;
		default:
			break;
		}
	}
	
	private void toNicknameFragment(){
		NicknameFragment nf = getResultName();
		Bundle bundle = new Bundle();
		bundle.putString("nickname", DolphinApp.getInstance()
				.getMyUserInfo().getNickName());
		bundle.putString("userid", DolphinApp.getInstance().getMyUserInfo()
				.getUserID());
		nf.setArguments(bundle);
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, nf);
		ft.addToBackStack(null);
		ft.commit();
	}

	private NicknameFragment getResultName() {
		NicknameFragment ModifyName = NicknameFragment
				.newInstance();
		ModifyName.setResultListener(new NicknameFragment.INameTextListener() {
			@Override
			public void nameTextListener(String selectID) {
				mNickname.setText(selectID);
			}
		});
		return ModifyName;
	}

	private void goCity() {
		Intent intent = new Intent(getActivity(), SelectCityActivity.class);
		intent.putExtra("isRegister", false);
		startActivityForResult(intent, To_City);
	}

	/**
	 * 
	 * 
	 * @param uri
	 */
	public void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, To_UpAvater);
	}

	public void takePhoto(int id) {
		Intent intent;
		switch (id) {
		case 0://
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
					Environment.getExternalStorageDirectory(), phoneNum
							+ ".jpg")));
			startActivityForResult(intent, To_CropPhoto);//
			break;
		case To_TakePic://
			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, To_TakePic);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case To_City:
			if (data != null) {
				mCity = data.getStringExtra("MYCITY");
				mcity.setText(mCity);
				DolphinApp.getInstance().getMyUserInfo().setCity(mCity);
				PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_CITY, mCity);
				SQLiteManager.getInstance().updateFamilyUserInfo(Userid,
						FamilyUserInfo._FAMILY_USER_CITY, mCity, true);

				String noteUpdate = RequestString.updateMyUserCity(
						SysConfig.dtoken, mCity);
				UploadData2Server mCity = new UploadData2Server(noteUpdate,
						"UpdateUserInfo");
				
				mCity.getData(new codeDataCallBack() {
					@Override
					public void onDataCallBack(String request, int code) {
						uploadMsg(Userid, MsgKey.Up_City);
						
					}
				});
			}
			break;
		case To_TakePic:
			if (resultCode == getActivity().RESULT_OK) {
				cropPhoto(data.getData());
			}
			break;
		case To_CropPhoto:
			if (resultCode == getActivity().RESULT_OK) {
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/" + "head" + ".jpg");
				cropPhoto(Uri.fromFile(temp));
			}
			break;
		case To_UpAvater:
			if (data != null) {
				Bundle extras = data.getExtras();
				mHeadBitmap = extras.getParcelable("data");
				String Icon = BitmapUtils.bitmapToBase64(mHeadBitmap);
				LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
				task.saveAvatar(Icon, SysConfig.uid);
				if (mHeadBitmap != null) {
					/**
					 * 上传服务器代码
					 */
					String token = SysConfig.dtoken;
					String headUpdate = RequestString.updateUserHead(token,
							Userid, Icon);
					UploadData2Server mCity = new UploadData2Server(headUpdate,
							"UpdateUserInfo");
					mCity.getData(new codeDataCallBack() {
						@Override
						public void onDataCallBack(String request, int code) {
							uploadMsg(Userid, MsgKey.Up_Avater);
						}
					});
					mheadicon.setImageBitmap(mHeadBitmap);// 用ImageView显示出来
				}
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void uploadMsg(String userid, int item) {
		final ArrayList<String> familyids = SQLiteManager.getInstance().getFamilyIDByUserId(userid);
		final int familySize = familyids.size();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "update");
			jsonObject.put("userid", userid);
			if (item == MsgKey.Up_City) {
				jsonObject.put("city", mCity);
			}
			jsonObject.put("eventtype", item);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		String leaveBody = RequestString.UploadMessageBox(SysConfig.dtoken, MsgKey.TO_FAMILY,
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
