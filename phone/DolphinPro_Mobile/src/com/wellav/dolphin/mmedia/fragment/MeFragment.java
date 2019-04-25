package com.wellav.dolphin.mmedia.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.ChooseOneDeviceActivity;
import com.wellav.dolphin.mmedia.InviteDownloadActivity;
import com.wellav.dolphin.mmedia.MyInfoFragmentActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.SecondPswEnterFragmentActivity;
import com.wellav.dolphin.mmedia.SettingsFragmentActivity;
import com.wellav.dolphin.mmedia.StatistcsTimeActivity;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.CommFunc;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月14日
 * @author Chen
 */
public class MeFragment extends BaseFragment implements OnClickListener{
	
	private Button me_username;
	private Button personal_moment;
	private Button accompany_time;
	private Button second_psw;
	private Button setting;
	private Button invitedownload;
	private CircleImageView headicon;
	private LoadUserAvatarFromLocal userAvatar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view =inflater.inflate(R.layout.fragment_me, container, false);
		me_username=(Button)view.findViewById(R.id.me_username);
		headicon=(CircleImageView)view.findViewById(R.id.headicon);
    	personal_moment=(Button)view.findViewById(R.id.personal_moment);
    	accompany_time=(Button)view.findViewById(R.id.accompany_time);
    	second_psw=(Button)view.findViewById(R.id.second_psw);
    	setting=(Button)view.findViewById(R.id.setting);
    	invitedownload=(Button)view.findViewById(R.id.invitedownload);
    	
    	me_username.setOnClickListener(this);
    	personal_moment.setOnClickListener(this);
    	accompany_time.setOnClickListener(this);
    	second_psw.setOnClickListener(this);
    	setting.setOnClickListener(this);
    	invitedownload.setOnClickListener(this);
    	userAvatar = new LoadUserAvatarFromLocal();
		return view;	
	}

	@Override
	public void onResume() {
			me_username.setText(DolphinApp.getInstance().getMyUserInfo().getNickName());
			String name = DolphinApp.getInstance().getMyUserInfo().getLoginName();
			Bitmap head = userAvatar.loadImage(name);
			if(head != null){
				headicon.setImageBitmap(head);
			}else{
				userAvatar.getData(DolphinApp.getInstance().getMyUserInfo().getUserID(), new userAvatarDataCallBack() {
					@Override
					public void onDataCallBack(int code, Bitmap avatar) {
						headicon.setImageBitmap(avatar);
					}
				});
			}
	    	
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		
		Bundle bundle = new Bundle();
		Intent intent = null;
		
		switch (v.getId()) {	
		case R.id.me_username:
			intent=new Intent(getActivity(),MyInfoFragmentActivity.class);
			startActivity(intent);			
			break;
		case R.id.accompany_time:
			int infosize=DolphinApp.getInstance().getFamilyInfos().size();
			if (infosize == 1) {
 				bundle.putInt("familyPos", 0);
 				intent=new Intent(getActivity(),StatistcsTimeActivity.class);
 				intent.putExtras(bundle);
				startActivity(intent);
			}else if(infosize>1){
				bundle.putInt("Item", ChooseOneDeviceFragment.To_STime);
				intent=new Intent(getActivity(),ChooseOneDeviceActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}else{
				CommFunc.DisplayToast(getActivity(),MeFragment.this.getResources().getString(R.string.not_yet_join));
			}		
			break;
		case R.id.second_psw:
			int myFamilys=DolphinApp.getInstance().getMyFamilyInfos().size();
			if (myFamilys == 1) {
				intent=new Intent(getActivity(),SecondPswEnterFragmentActivity.class);
				startActivity(intent);
			} else if(myFamilys>1){
				bundle.putInt("Item", ChooseOneDeviceFragment.To_SecPsw);
				intent=new Intent(getActivity(),ChooseOneDeviceActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}else if(myFamilys == 0 ){
				CommFunc.DisplayToast(getActivity(),MeFragment.this.getResources().getString(R.string.not_manager_no_second_password));
			}
			
			break;
		case R.id.setting:
			intent=new Intent(getActivity(),SettingsFragmentActivity.class);
			startActivity(intent);
			break;
		case R.id.invitedownload:
			intent=new Intent(getActivity(),InviteDownloadActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
