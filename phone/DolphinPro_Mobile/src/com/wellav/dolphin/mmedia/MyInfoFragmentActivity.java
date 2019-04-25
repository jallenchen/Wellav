package com.wellav.dolphin.mmedia;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.mmedia.fragment.MyInfoFragment;

public class MyInfoFragmentActivity extends BaseFragmentActivity {
	
	@Override
	protected Fragment createFragment() {
 		  return  new MyInfoFragment();
	}
}
