package com.wellav.dolphin.mmedia;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.mmedia.fragment.SecondPswEnterFragment;

public class SecondPswEnterFragmentActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		int pos = getIntent().getIntExtra("myFamilyPos", 0);
			return SecondPswEnterFragment.newInstance(pos);
	}
}
