package com.wellav.dolphin.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;
import com.wellav.dolphin.adapter.MoreListAdapter;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bind.BindGuideActivity;
import com.wellav.dolphin.calling.CallingMeetingActivity;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.AddCallingMemberActivity;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.netease.config.DialogMaker;
import com.wellav.dolphin.netease.config.LogUtil;
import com.wellav.dolphin.utils.Util;

public class MoreFunctionFragment extends BaseFragment implements OnItemClickListener ,OnClickListener{
	private static final int To_Bind = 0;
	private static final int To_AddContact = 1;
	private static final int To_GChat = 2;
	private static final int To_Meeting = 3;
	private ListView mList;
	private MoreListAdapter adapter;
	private ImageButton mActionBarPre;
	private TextView mActionBarName;
	private MoreFunctionFragment ctx;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.ctx = this;
		View view = inflater.inflate(R.layout.fragment_more, container, false);
		mList = (ListView) view.findViewById(R.id.more_list);
		mActionBarPre = (ImageButton) view.findViewById(R.id.actionbar_prev);
		mActionBarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionBarName.setText(R.string.action_more);
		
		adapter = new MoreListAdapter(getActivity());
		mList.setAdapter(adapter);
		mList.setOnItemClickListener(this);
		mActionBarPre.setOnClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch(position){
		case To_Bind:
			Intent bintent = new Intent(getActivity(), BindGuideActivity.class);
			startActivity(bintent);
			break;
		case To_AddContact:
			newInstanceAddFragment();
			break;
		case To_GChat:
			Intent rintent = new Intent(getActivity(), AddCallingMemberActivity.class);
			startActivity(rintent);
			break;
		case To_Meeting:
			ArrayList<String> names = LauncherApp.getInstance().getDBHelper().getAllFamilyUsersName(getActivity());
			names.remove(SysConfig.uid);
			CallingMeetingActivity.start(getActivity(),names);
			break;
		}
	}
	
	private void newInstanceAddFragment(){
		AddContactFragment  AddMember =new AddContactFragment();
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, AddMember);
		ft.addToBackStack(null);
		ft.commit();
	}


	@Override
	public void onClick(View v) {
		 getFragmentManager().beginTransaction().remove(MoreFunctionFragment.this).commit();
	}

}
