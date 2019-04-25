package com.wellav.dolphin.mmedia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wellav.dolphin.mmedia.BaseFragmentActivity;
import com.wellav.dolphin.mmedia.fragment.RegisterOneFragment;

public class RegisterActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		boolean isRegister = getIntent().getBooleanExtra("isRegister", true);
		return onRegister(isRegister);
	}
	
	public Fragment onRegister(boolean isRegister ) {
		RegisterOneFragment disFrag = new RegisterOneFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isRegister", isRegister);
		disFrag.setArguments(bundle);
		return disFrag;
	}
}
