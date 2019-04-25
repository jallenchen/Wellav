package com.wellav.dolphin.mmedia;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.mmedia.fragment.StatistcsFragment;

public class StatistcsTimeActivity extends BaseFragmentActivity{

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		int pos = getIntent().getIntExtra("familyPos", 0);
		return StatistcsFragment.newInstance(pos);
	}
}
