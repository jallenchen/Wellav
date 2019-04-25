package com.wellav.dolphin.mmedia.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;


public class LoginTwoFragment extends BaseFragment implements OnClickListener{
	private TextView mAcionBarName;
	private ImageView mActionBarPrev;
	private CircleImageView mHeadView;
	private TextView mForgetPsw;
	private TextView mMoreLogin;
	private TextView mNickName;
	private Button mLogin;
	private EditText mPsw;
	private String mLoginName;
	private String nickName;
	private MyLoginTwoLisenter mLisenter;
	private LoadUserAvatarFromLocal userAvatar;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mLisenter = (MyLoginTwoLisenter) activity;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmentlogintwo, container, false);
		mHeadView = (CircleImageView) view.findViewById(R.id.headicon);
		mPsw = (EditText) view.findViewById(R.id.cilent_password);
		mActionBarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mAcionBarName = (TextView) view.findViewById(R.id.actionbar_name);
		mForgetPsw = (TextView) view.findViewById(R.id.forget_pwd);
		mMoreLogin = (TextView) view.findViewById(R.id.more);
		mNickName = (TextView) view.findViewById(R.id.nickname);
		mLogin = (Button) view.findViewById(R.id.login);
		
		mLoginName = getArguments().getString("LOGINNAME");
		nickName = getArguments().getString("NICKNAME");
		
		mNickName.setText(nickName);
		mAcionBarName.setText(R.string.login);
		mForgetPsw.setOnClickListener(this);
		mActionBarPrev.setOnClickListener(this);
		mMoreLogin.setOnClickListener(this);
		mLogin.setOnClickListener(this);
		userAvatar = new LoadUserAvatarFromLocal();
		if(mLoginName!=null){
			Bitmap bt = userAvatar.loadImage(mLoginName);//从Sd中找头像，转换成Bitmap
			if(bt!=null){
				mHeadView.setImageBitmap(bt);
			}else{
				mHeadView.setImageResource(R.drawable.ic_defaulthead_home_48dp);
			}
		}
	
		return view;
	}
	public interface MyLoginTwoLisenter{
		public void OnLoginButtonOnClick(String loginName,String psw);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		case R.id.forget_pwd:
			 RegisterOneFragment agreeFrag=new RegisterOneFragment();
				Bundle bundle = new Bundle();  
				bundle.putBoolean("isRegister", false);
				agreeFrag.setArguments(bundle);
			    FragmentManager fm=getActivity().getSupportFragmentManager();
		        FragmentTransaction ft=fm.beginTransaction();
		        ft.replace(R.id.container, agreeFrag);
		        ft.addToBackStack(null);
		        ft.commit();
			
			break;
		case R.id.more:
			LoginOneFragment logFrag=new LoginOneFragment();
	        FragmentManager fm2=getActivity().getSupportFragmentManager();
	        FragmentTransaction ft2=fm2.beginTransaction();
	        ft2.replace(R.id.container, logFrag);
	        ft2.addToBackStack(null);
	        ft2.commit();
			
			break;
		case R.id.login:
			
			String psw = mPsw.getText().toString();
			mLisenter.OnLoginButtonOnClick(mLoginName,psw);
			
			break;
		}
	}

}
