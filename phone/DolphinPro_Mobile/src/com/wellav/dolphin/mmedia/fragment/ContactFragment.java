package com.wellav.dolphin.mmedia.fragment;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.readystatesoftware.viewbadger.BadgeView;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.AddNewContactFragmentActivity;
import com.wellav.dolphin.mmedia.InfomationFragmentActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.adapter.ContactsExpanAdapter;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.netease.team.SystemMessageFragment;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月14日
 * @author Cheng
 */
public class ContactFragment extends BaseFragment implements  OnClickListener{
	private static final String TAG = "ContactFragment"; 
	private ExpandableListView mExList;
	public ContactsExpanAdapter adapter;
	private LinearLayout mNewContact;
	private ImageView newContactIcon;
	private BadgeView badge;
	private boolean hidden;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_contacts, container, false);
		   mExList = (ExpandableListView) view.findViewById(R.id.el_list);
		   mNewContact= (LinearLayout) view.findViewById(R.id.new_contacts_layout);
		   newContactIcon = (ImageView) view.findViewById(R.id.new_contanct_icon);
		   
		  badge = new BadgeView(getActivity(), newContactIcon);
		  badge.setHeight(getActivity().getResources().getDimensionPixelSize(R.dimen.point_height));
		  badge.setWidth(getActivity().getResources().getDimensionPixelSize(R.dimen.point_width));
		  badge.setBadgeMargin(0);
		  badge.setBackgroundResource(R.drawable.redpoint_10dp);
		  badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		  badge.show();
		   
		   adapter = new ContactsExpanAdapter(getActivity(),DolphinApp.getInstance().getFamilyInfos(),DolphinApp.getInstance().getFamilyUserMap());
		   
		   mExList.setAdapter(adapter);
		   mExList.setOnChildClickListener(clisenter);
		   mNewContact.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
            refresh();
        }
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
	}

	public void refresh(){
		refreshNewPoint();
		adapter.refresh();
	}

	public void refreshNewPoint(){
		if(PreferenceUtils.getInstance().getBooleanSharePreferences(MsgKey.newInviteMsg)){
			badge.show();
		}else{
			badge.hide();
		}
	}


	private boolean isMyFamily(int group){
		boolean isCurrenManager = DolphinApp.getInstance().getFamilyInfos().get(group).getMangerID().equals(SysConfig.userid);
		return isCurrenManager;
	}
	
	private OnChildClickListener clisenter = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			// TODO Auto-generated method stub
			Log.d(TAG,"Click:"+DolphinApp.getInstance().getFamilyUserMap().get(groupPosition).get(childPosition).getLoginName());
			boolean isMyFamily = isMyFamily(groupPosition);
			
			Intent intent = new Intent(getActivity(),InfomationFragmentActivity.class);
			Bundle bundle = new Bundle();
	    	bundle.putBoolean("MyFamily", isMyFamily);
	    	bundle.putInt("CurrentFamily", groupPosition);
	    	bundle.putInt("CurrentMember", childPosition);
	     	intent.putExtras(bundle);
			startActivity(intent);
			
			
			return false;
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(),AddNewContactFragmentActivity.class);
		startActivity(intent);
		//SystemMessageActivity.start(getActivity());
	}

}
