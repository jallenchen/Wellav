package com.wellav.dolphin.mmedia;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wellav.dolphin.mmedia.fragment.RemoteSettingEnterFragment;

public class RemoteSettingActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		Bundle bundle = getIntent().getExtras();
		return RemoteSettingEnterFragment.newInstance(bundle);
	}

}
