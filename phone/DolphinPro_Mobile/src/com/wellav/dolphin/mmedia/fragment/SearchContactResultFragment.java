package com.wellav.dolphin.mmedia.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;

public class SearchContactResultFragment extends BaseFragment implements OnClickListener{
	private CircleImageView mUserIcon;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private TextView mUserName;
	private ImageView mUserType;
	private RelativeLayout mUserLayout;
	private UserInfo info;
	private boolean isOnlyDevice;
	private String mInfoNickName;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_search_result, container, false);
		mUserLayout = (RelativeLayout) view.findViewById(R.id.search_result);
		mUserIcon = (CircleImageView) view.findViewById(R.id.device_icon);
		mUserName = (TextView) view.findViewById(R.id.device_name);
		mUserType = (ImageView) view.findViewById(R.id.device_type);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		info = (UserInfo) getArguments().getSerializable("USERINFO");
		isOnlyDevice =getArguments().getBoolean("OnlyDevice", false);
		if(isOnlyDevice){
			 mInfoNickName = getArguments().getString("DeviceName");
		}
		 
		mUserName.setText(info.getNickName());
		if(info.getDeviceType().equals("0")){
			mUserType.setImageResource(R.drawable.ic_type_phone);
		}
		
		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
		Bitmap bitmap = task.loadImage(info.getLoginName());
		if(bitmap != null){
			mUserIcon.setImageBitmap(bitmap);
		}
		
		mActionbarName.setText(R.string.search_result);
		mActionbarPrev.setOnClickListener(this);
		mUserLayout.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.actionbar_prev:
			getFragmentManager().beginTransaction().remove(SearchContactResultFragment.this).commit();
			break;
		case R.id.search_result:
			getUserInfo();
			break;
		}
		
	}
	private void getUserInfo(){
		SearchResultInfoFragment MemberInfo=new SearchResultInfoFragment();
		Bundle bundle = new Bundle(); 
		bundle.putSerializable("UsersDetail", info);
		bundle.putBoolean("OnlyDevice", isOnlyDevice);
		bundle.putString("DeviceName", mInfoNickName);
		
		MemberInfo.setArguments(bundle);
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.add(R.id.container,MemberInfo);
		tx.commit();
	}

}
