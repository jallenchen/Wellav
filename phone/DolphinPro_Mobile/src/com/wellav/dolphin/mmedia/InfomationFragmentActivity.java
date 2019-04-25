package com.wellav.dolphin.mmedia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.fragment.MemberInfoFragment;
import com.wellav.dolphin.mmedia.fragment.MyFamilyInfoFragment;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;

public class InfomationFragmentActivity extends BaseFragmentActivity{
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private FamilyUserInfo fUerInfo;
	private boolean isMyFamily ;
	private int CurrentFamily;
	private int CurrentMember;

	private Fragment LoadInfo(){
		boolean isCenter = DolphinUtil.getCenter(fUerInfo.getAuthority())==1;
		Bundle bundle = new Bundle();  
		bundle.putSerializable("UsersDetail", fUerInfo);
		bundle.putInt("CurrentFamily", CurrentFamily);
		
		if(isMyFamily == true && isCenter){
			return MyFamilyInfoFragment.newInstance(bundle);
		}else if(fUerInfo.getDeviceType().equals("0") || isCenter == false || ( isCenter == true && isMyFamily== false )){
			bundle.putBoolean("IsCener", isCenter);
			bundle.putBoolean("MyFamily", isMyFamily);
			bundle.putInt("CurrentMember", CurrentMember);
			return MemberInfoFragment.newInstance(bundle);
		}else{
			return null;
		}
	}

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		isMyFamily = getIntent().getBooleanExtra("MyFamily", false);
		CurrentFamily = getIntent().getIntExtra("CurrentFamily", 0);
		CurrentMember = getIntent().getIntExtra("CurrentMember", 0);
		fUerInfo = DolphinApp.getInstance().getFamilyUserMap().get(CurrentFamily).get(CurrentMember);
		return LoadInfo();
	}

}
