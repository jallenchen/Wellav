package com.wellav.dolphin.bind;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.launcher.BaseFragmentActivity;

public class BindBeManagerActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		String userid = getIntent().getStringExtra("userid");
		return BindBeManagerFragment.newBindBeManager(userid);
	}
}
