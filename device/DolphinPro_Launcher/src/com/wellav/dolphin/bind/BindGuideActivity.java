package com.wellav.dolphin.bind;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.launcher.BaseFragmentActivity;

public class BindGuideActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new BindGuideFragment();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
