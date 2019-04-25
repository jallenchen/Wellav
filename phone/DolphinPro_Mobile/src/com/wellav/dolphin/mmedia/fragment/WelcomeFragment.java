package com.wellav.dolphin.mmedia.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wellav.dolphin.mmedia.R;

public class WelcomeFragment extends BaseFragment {
	
	private Button mRegister;
	private Button mLogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_welcome, container, false);
		mRegister = (Button) view.findViewById(R.id.registerBtn);
		mLogin = (Button) view.findViewById(R.id.loginBtn);
		boolean IsLogin = getArguments().getBoolean("IsLogin",false);
		if(IsLogin == true){
			setBtnVisible(View.INVISIBLE);
		}else{
			setBtnVisible(View.VISIBLE);
		}
		return view;
	}
	
	public void setBtnVisible(int visble){
		mRegister.setVisibility(visble);
		mLogin.setVisibility(visble);
	}
	
	public static Fragment newInstance(boolean is){
		WelcomeFragment Welcome = new WelcomeFragment();
	  	Bundle bundle = new Bundle();  
		bundle.putBoolean("IsLogin", is);
		Welcome.setArguments(bundle);
		return Welcome;
	}

}
