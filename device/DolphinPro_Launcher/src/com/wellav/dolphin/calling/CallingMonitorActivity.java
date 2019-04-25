package com.wellav.dolphin.calling;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.fragment.MonitorFragment;
import com.wellav.dolphin.launcher.BaseFragmentActivity;

public class CallingMonitorActivity extends BaseFragmentActivity{

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		String name = getIntent().getStringExtra("CallName");
		return MonitorFragment.newInstanceMonitor(name);
	}

}
