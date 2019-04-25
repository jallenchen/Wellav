package com.wellav.dolphin.mmedia;

import java.util.List;

import android.support.v4.app.Fragment;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.fragment.ChooseOneDeviceFragment;
import com.wellav.dolphin.mmedia.fragment.InitiateChatFragment;

public class AddingCallingFragmentActivity extends BaseFragmentActivity {

	@Override
	protected Fragment createFragment() {
		List<FamilyInfo> info = DolphinApp.getInstance().getFamilyInfos();
    	if(SysConfig.getInstance().isCalling() == true || info.size() == 1){
    		return new InitiateChatFragment();
    	}else if(SysConfig.getInstance().isCalling() == false && info.size()>1){
    			return ChooseOneDeviceFragment.newInstance(0); 
    	}else{
    		return null;
    	}
    	
	}

}
