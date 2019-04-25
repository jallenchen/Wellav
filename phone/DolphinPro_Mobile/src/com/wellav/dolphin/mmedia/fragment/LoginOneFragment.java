package com.wellav.dolphin.mmedia.fragment;

import android.app.Activity;
import android.content.Intent;
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
import com.wellav.dolphin.mmedia.activity.RegisterActivity;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;

public class LoginOneFragment extends BaseFragment implements OnClickListener{
	private TextView mAcionBarName;
	private TextView mForgetPsw;
	private ImageView mActionBarPrev;
	private EditText mLoginName;
	private EditText mPsw;
	private Button mLogin;
	private MyLoginOneLisenter mLisenter;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mLisenter = (MyLoginOneLisenter) activity;
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmentloginone, container, false);
		mLoginName = (EditText) view.findViewById(R.id.cilent_phone);
		mPsw = (EditText) view.findViewById(R.id.cilent_psw);
		mActionBarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mAcionBarName = (TextView) view.findViewById(R.id.actionbar_name);
		mForgetPsw = (TextView) view.findViewById(R.id.forget_pwd);
		mLogin = (Button) view.findViewById(R.id.login);
		
		mAcionBarName.setText(R.string.login);
		mForgetPsw.setOnClickListener(this);
		mActionBarPrev.setOnClickListener(this);
		mLogin.setOnClickListener(this);
		DolphinApp.getInstance().setSqulManagerNull();
		PreferenceUtils.getInstance().clearSharePreferencesAccount();
		return view;
	}
	
	public interface MyLoginOneLisenter{
		public void OnLoginButtonOnClick(String loginName,String psw);
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.forget_pwd:
			Intent inte = new Intent(getActivity(),
					RegisterActivity.class);
			inte.putExtra("isRegister", false);
			startActivity(inte);
			break;
		case R.id.login:
			
			String loginName = mLoginName.getText().toString();
			String psw = mPsw.getText().toString();
			mLisenter.OnLoginButtonOnClick(loginName,psw);
			break;
		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		}
	}

	

	
}
