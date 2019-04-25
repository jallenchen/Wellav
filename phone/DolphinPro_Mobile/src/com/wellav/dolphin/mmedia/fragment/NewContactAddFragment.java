package com.wellav.dolphin.mmedia.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.ChooseOneDeviceActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.SearchMemberActivity;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class NewContactAddFragment extends BaseFragment implements OnClickListener{
	private RelativeLayout mSearchLayout;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private RelativeLayout mLinkDevice;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_add_newcontact, container, false);
		mSearchLayout =(RelativeLayout) view.findViewById(R.id.search_layout);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mLinkDevice = (RelativeLayout) view.findViewById(R.id.addlink_layout);
		mActionbarName.setText(R.string.new_contacts);
		PreferenceUtils.getInstance().saveBooleanSharePreferences(MsgKey.newInviteMsg,false);
		mSearchLayout.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		mLinkDevice.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch(v.getId()){
		case R.id.addlink_layout:
			List<FamilyInfo> info =DolphinApp.getInstance().getMyFamilyInfos();
			if(info.size()>1){
				intent = new Intent(getActivity(), ChooseOneDeviceActivity.class);
				intent.putExtra("Item", ChooseOneDeviceFragment.To_LinkDev);
				startActivity(intent);
			}else if(info.size()==1){
				intent = new Intent(getActivity(), SearchMemberActivity.class);
			  	Bundle bundle = new Bundle(); 
				bundle.putBoolean("OnlyDevice", true);
				bundle.putString("DeviceName", info.get(0).getNickName());
				intent.putExtra("bundle", bundle);
				startActivity(intent);
			}else{
				CommFunc.DisplayToast(getActivity(), R.string.nolinke_device);
				return;
			}
			
			break;
		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		case R.id.search_layout:
			intent = new Intent(getActivity(), SearchMemberActivity.class);
		 	Bundle bundle1 = new Bundle(); 
			bundle1.putBoolean("OnlyDevice", false);
			intent.putExtra("bundle", bundle1);
			startActivity(intent);
			break;
		}
	}
	
	
}
