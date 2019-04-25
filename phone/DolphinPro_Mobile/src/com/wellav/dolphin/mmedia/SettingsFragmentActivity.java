package com.wellav.dolphin.mmedia;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.mmedia.fragment.SettingsEnterFragment;

public class SettingsFragmentActivity extends BaseFragmentActivity {
	
	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new SettingsEnterFragment();
	}
}
