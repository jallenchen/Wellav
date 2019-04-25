package com.wellav.dolphin.mmedia;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.mmedia.fragment.AssisanFragment;

public class AssistanHouseFragmentActivity extends BaseFragmentActivity {

	
	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		int pos = getIntent().getIntExtra("Pos", 0);
		return AssisanFragment.newInstance(pos);
	}

}
