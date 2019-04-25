package com.wellav.dolphin.mmedia;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.fragment.ChooseOneDeviceFragment;
import com.wellav.dolphin.mmedia.fragment.InitiateChatFragment;
import com.wellav.dolphin.mmedia.fragment.InviteDownloadFragment;
import com.wellav.dolphin.mmedia.fragment.NewContactAddFragment;
import com.wellav.dolphin.mmedia.fragment.SettingsFeedbackFragment;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class DolphinAddFragmentActivity extends BaseFragmentActivity {
	private static final int To_NewContact = 0;
	private static final int To_GroupChat = 1;
	private static final int To_Feedback = 2;
	private static final int To_InviteDownload = 3;
	private int mItem;

	private Fragment loadItem(int item){
		switch(item){
		case To_NewContact:
			Intent intent = new Intent(this,AddNewContactFragmentActivity.class);
			startActivity(intent);
			return null;
		case To_GroupChat:
			if(DolphinApp.getInstance().getFamilyInfos().size() == 0){
				CommFunc.DisplayToast(getApplication(),getResources().getString(R.string.no_family_group));
				finish();
			}else if(DolphinApp.getInstance().getFamilyInfos().size() == 1){
				return new InitiateChatFragment();
			}else{
				return ChooseOneDeviceFragment.newInstance(ChooseOneDeviceFragment.To_GChat);
			}
		
		case To_Feedback:
			return SettingsFeedbackFragment.newInstance(SettingsFeedbackFragment.From_Menu);
		case To_InviteDownload:
			return new InviteDownloadFragment();
		}
		return null;
	}
	
	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		mItem = getIntent().getIntExtra("ADDITEM",0);
		
		return loadItem(mItem);
	}

}
