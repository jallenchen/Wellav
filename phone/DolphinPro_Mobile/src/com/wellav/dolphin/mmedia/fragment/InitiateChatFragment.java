package com.wellav.dolphin.mmedia.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.CallingActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.activity.CallingGroupChatActivity;
import com.wellav.dolphin.mmedia.adapter.CheckedImgAdapter;
import com.wellav.dolphin.mmedia.adapter.GroupMemberAdapter;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.lisenter.IOnClickPositon;
import com.wellav.dolphin.mmedia.ui.HorizontalListView;
import com.wellav.dolphin.mmedia.utils.NameUtils;

public class InitiateChatFragment extends BaseFragment implements OnClickListener,OnItemClickListener{
	private ImageView mActionbarPrev;
	private TextView mActionbarName;
	private TextView mCurrentName;
	private Button mCancelBtn,mCallBtn;
	private ListView mExList;
	public  static GroupMemberAdapter adapter;
	private HorizontalListView checkedContack;
	private static  CheckedImgAdapter checkedAdapter;
	private String mFamilyId;
	private static List<FamilyUserInfo> familyusers;
	private  static List<FamilyUserInfo> checkedusers;
	private ArrayList<String> stringList=new ArrayList<>();
	public static HashMap<String, Boolean> checkedMap = new HashMap<String, Boolean>();
	private InitiateChatFragment ctx;

	private void initCheckedList() {
		checkedusers = new ArrayList<FamilyUserInfo>();
		checkedMap.clear();
	    if(SysConfig.getInstance().isCalling() == true){
	    	Log.e("InitiateChatFragment", NameUtils.getRemoteNames().size()+"true");
	    	checkedMap.put(familyusers.get(1).getLoginName(),true);
	    	List<String> names = NameUtils.getRemoteNames();
	    	int size = names.size();
	    	
	    	for(int i=0;i<size;i++){
	    		checkedMap.put(names.get(i), true);
	    	}
	    	checkedusers = NameUtils.loginName2UserInfo(names);
	    	if(checkedusers.size() == 0){
	    		checkedusers.add(0, familyusers.get(1));
	    	}else{
	    		checkedusers.add(1, familyusers.get(1));
	    	}
	    	
	    }else{
	    	Log.e("InitiateChatFragment", "false");
	    	checkedusers.add(0, familyusers.get(0));
	    	checkedusers.add(1, familyusers.get(1));
	    	checkedMap.put(familyusers.get(0).getLoginName(), true);
	    	checkedMap.put(familyusers.get(1).getLoginName(), true);
	    }
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		this.ctx = this;
		
		View view = inflater.inflate(R.layout.fragment_initate_chat, container, false);
		   mExList = (ListView) view.findViewById(R.id.el_list);
		   checkedContack = (HorizontalListView) view.findViewById(R.id.horizonMenu);
		   mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		   mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		   mCurrentName= (TextView) view.findViewById(R.id.currentgroup);
		   mCallBtn = (Button) view.findViewById(R.id.callBtn);
		   mCancelBtn = (Button) view.findViewById(R.id.cancelBtn);
		  // mFamilyId = DolphinApp.getInstance().getSysConfig().getFamilyId();
		   int familyPos = DolphinApp.getInstance().getSysConfig().getFamilyPos();
		   
		   familyusers= DolphinApp.getInstance().getFamilyUserMap().get(familyPos);
		   initCheckedList();
		   adapter = new GroupMemberAdapter(getActivity(),familyusers,checkedMap);
		   mExList.setAdapter(adapter);
		   
			
			checkedAdapter = new CheckedImgAdapter(getActivity(), checkedusers);
			checkedContack.setAdapter(checkedAdapter);
		   mCurrentName.setText(ctx.getResources().getString(R.string.recent_group)+familyusers.get(0).getNickName());
		   mActionbarName.setText(R.string.initiate_chat);
		   mActionbarPrev.setOnClickListener(this);
		   mCallBtn.setOnClickListener(this);
		   mCancelBtn.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.actionbar_prev:
			getFragmentManager().beginTransaction().remove(InitiateChatFragment.this).commit();
		case R.id.cancelBtn:
			getActivity().finish();
			break;
		case R.id.callBtn:
			addMemberCall();
			CallingGroupChatActivity.start(getActivity(), stringList);
			getActivity().finish();
			break;
		
		}
		
	}
	private void addMemberCall(){
		String callList;
		int size = NameUtils.remoteNamesCount();
	
		if(SysConfig.getInstance().isCalling() == true){
			for(int i=size+1;i<checkedusers.size();i++){
				if(checkedusers.get(i).getLoginName().equals(SysConfig.uid)){
					continue;
				}
				stringList.add(checkedusers.get(i).getLoginName());
			}
			callList = NameUtils.listToString(stringList);
			
			Intent intent = new Intent();
			intent.putExtra("InviteName", callList);
			getActivity().setResult(0, intent);
			
		}else{
			for(int i=size;i<checkedusers.size();i++){
				if(checkedusers.get(i).getLoginName().equals(SysConfig.uid)){
					continue;
				}
				stringList.add(checkedusers.get(i).getLoginName());
			}
//			callList = NameUtils.listToString(stringList);
//			Intent in = new Intent(getActivity(),CallingActivity.class);
//			in.putExtra("CallType", SysConfig.MutilCall);
//			in.putExtra("CallName", familyusers.get(0).getLoginName());
//			NameUtils.remoteNamesAdd(callList);
//			startActivity(in);
		}
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		adapter = null;
		checkedAdapter = null;
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.e("onItemClick","view:"+view+ "position:"+position);
	}
	
	public  class MyListener implements IOnClickPositon{

		@Override
		public void onClick(View v, int index) {
			// TODO Auto-generated method stub
			ToggleButton view = (ToggleButton) v;
			boolean isChecked = view.isChecked();
			FamilyUserInfo user= familyusers.get(index);
			
			
			if (isChecked) {
				
				addToCheckedList(user);
			} else {
				removeByName(user);
			}
		    checkedMap.put(user.getLoginName(), isChecked);
			checkedAdapter.notifyDataSetChanged();
			//adapter.refresh(checkedMap);
		}
		
	}
	
	/**
	 * �Ƴ�ˮƽͼƬ.
	 * 
	 * @param _name
	 */
	private void removeByName(FamilyUserInfo user) {
		checkedusers.remove(user);
	}

	/**
	 * ѡ��checkbox�����ˮƽͼƬ.
	 * 
	 * @param _name
	 * @param _id
	 * @param _touxiang
	 */
	private void addToCheckedList(FamilyUserInfo user) {
		checkedusers.add(user);
	}
	


}
