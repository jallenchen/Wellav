package com.wellav.dolphin.mmedia;

import com.wellav.dolphin.mmedia.fragment.ChooseOneDeviceFragment;

import android.support.v4.app.Fragment;

public class ChooseOneDeviceActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		int item = getIntent().getIntExtra("Item", 0);
		 return  ChooseOneDeviceFragment.newInstance(item);
	}

}
