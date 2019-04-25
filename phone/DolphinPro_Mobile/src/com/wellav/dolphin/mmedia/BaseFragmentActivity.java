package com.wellav.dolphin.mmedia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public abstract class BaseFragmentActivity extends BaseActivity {
	protected abstract Fragment createFragment();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.main);
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.container);
		
		if(fragment == null){
			fragment = createFragment();
			if(fragment == null){
				finish();
			}else{
				fm.beginTransaction().replace(R.id.container, fragment).commit();
			}
			
		}
	}
	
}
