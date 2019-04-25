package com.wellav.dolphin.mmedia.adapter;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.dialog.CreateDialog;
import com.wellav.dolphin.mmedia.dialog.DolphinDialogLisenter;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class ContactsExpanAdapter extends BaseExpandableListAdapter{
	private static String TAG ="ContactsExpanAdapter";
	private Context mContext;
	@SuppressLint("UseSparseArrays") private HashMap<Integer, List<FamilyUserInfo>> map = new HashMap<Integer, List<FamilyUserInfo>>(); 
	private List<FamilyInfo> familys;
	private FamilyUserInfo user;
	boolean mMyFamily[];
	int size =0;
	boolean isCurrenManager = false;
	boolean isPrevManager = false;
	LoadUserAvatarFromLocal UserAvatar ;
	
	/**
	 * 
	 */
	public ContactsExpanAdapter(Context ct,List<FamilyInfo> familys,HashMap<Integer, List<FamilyUserInfo>> map ) {
		// TODO Auto-generated constructor stub
		this.mContext = ct;
		this.familys = familys;
		this.map = map;
		mMyFamily = new boolean[familys.size()];
		UserAvatar = new LoadUserAvatarFromLocal(); 
		//getFamilyIdUsers();
	}


	@Override
	public int getGroupCount() {
		return familys.size();
	}
	
	

	@Override
	public int getChildrenCount(int groupPosition) {
			if(map == null ||map.get(groupPosition) == null ){
				return 0;
			}
		return map.get(groupPosition).size();
	}
	
	public void refresh(){
		this.map = DolphinApp.getInstance().getFamilyUserMap();
		this.familys = DolphinApp.getInstance().getFamilyInfos();
		mMyFamily = new boolean[familys.size()];
		notifyDataSetChanged();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		int type =2;
		if(childPosition == 0){
			type = 0;
		}else if(childPosition == 1){
			type = 1;
		}
		return type;
	}

	@Override
	public int getChildTypeCount() {
		return 3;
	}


	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder viewHolder = null;
		if(null==convertView){
			viewHolder = new  GroupViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_contacts_group_item, null);
			viewHolder.group_arrow = (ImageView) convertView.findViewById(R.id.group_arrow);
			viewHolder.group_title = (TextView) convertView.findViewById(R.id.group_title);
			viewHolder.group_name = (TextView) convertView.findViewById(R.id.group_name);
			viewHolder.group_view = (View) convertView.findViewById(R.id.view);
			convertView.setTag(viewHolder);
			
		}else{
			viewHolder = (GroupViewHolder) convertView.getTag();
		}

		if(groupPosition == familys.size() - 1){
			viewHolder.group_view.setVisibility(View.INVISIBLE);
		}else{
			viewHolder.group_view.setVisibility(View.VISIBLE);
		}
		
		viewHolder.group_name.setText(familys.get(groupPosition).getNickName());
		

		isCurrenManager = familys.get(groupPosition).getMangerID().equals(SysConfig.userid);
		mMyFamily[groupPosition] = isCurrenManager;
		if (groupPosition > 0) {
			isPrevManager = familys.get(groupPosition-1).getMangerID().equals(SysConfig.userid);
		}
		
		if (isCurrenManager !=  isPrevManager && groupPosition >0) {
			viewHolder.group_title.setVisibility(View.VISIBLE);
			if(isCurrenManager){
				viewHolder.group_title.setText(R.string.mine);
			}else{
				viewHolder.group_title.setText(R.string.join);
			}
		} else if(isCurrenManager ==  isPrevManager && groupPosition >0) {
			viewHolder.group_title.setVisibility(View.GONE);
		}else{
			viewHolder.group_title.setVisibility(View.VISIBLE);
			if(isCurrenManager){
				viewHolder.group_title.setText(R.string.mine);
			}else{
				viewHolder.group_title.setText(R.string.join);
			}
		}
		
		
		if(isExpanded){
			viewHolder.group_arrow.setImageResource(R.drawable.arrow_down2_blue);
		}else{
			viewHolder.group_arrow.setImageResource(R.drawable.arrow_r2_grey);
		}
		
		
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Child1ViewHolder viewHolder1 = null;
		Child2ViewHolder viewHolder2 = null;
		Child0ViewHolder viewHolder0 = null;
		
		int type = getChildType(groupPosition, childPosition);
			
		if(type == 0){
			FamilyUserInfo info = map.get(groupPosition).get(0);
			if(null==convertView){
				viewHolder0 = new Child0ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.fragment_contacts_child_item0, null);
				viewHolder0.child0_icon = (CircleImageView) convertView.findViewById(R.id.child_icon);
				viewHolder0.child0_eye = (ImageView) convertView.findViewById(R.id.child_eye);
				viewHolder0.child0_name = (TextView) convertView.findViewById(R.id.child_home_name);
				viewHolder0.child0_online = (TextView) convertView.findViewById(R.id.child_online);
				convertView.setTag(viewHolder0);
			}else{
				viewHolder0 = (Child0ViewHolder) convertView.getTag();
			}
			Bitmap head = UserAvatar.loadImage(info.getLoginName());
			if(head!=null){
				viewHolder0.child0_icon.setImageBitmap(head);
			}
			
			if(info.getNoteName().equals("")){
				viewHolder0.child0_name.setText(info.getNickName());
			}else{
				viewHolder0.child0_name.setText(info.getNoteName());
			}
			
			
			if(familys.get(groupPosition).getStatus().equals("1")){
				viewHolder0.child0_online.setText(R.string.online);
			}else{
				viewHolder0.child0_online.setText(R.string.no_online);
			}
			
			if(DolphinUtil.getPrivacy(info.getAuthority()) == true){
				viewHolder0.child0_eye.setImageResource(R.drawable.ic_privacy_off);
			}else{
				viewHolder0.child0_eye.setImageResource(R.drawable.ic_privacy_on);
			}
			
		}else if(type == 1){
			
			FamilyUserInfo info = map.get(groupPosition).get(1);
			
			if(null==convertView){
				viewHolder1 = new Child1ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.fragment_contacts_child_item1, null);
				viewHolder1.child1_icon = (CircleImageView) convertView.findViewById(R.id.child1_icon);
				viewHolder1.child1_edit = (ImageView) convertView.findViewById(R.id.child1_edit);
				viewHolder1.child1_type = (ImageView) convertView.findViewById(R.id.child1_type);
				viewHolder1.child1_name = (TextView) convertView.findViewById(R.id.child1_name);
				viewHolder1.child1_manager = (ImageView) convertView.findViewById(R.id.child1_manager);
				viewHolder1.child1_edit.setVisibility(View.INVISIBLE);
				convertView.setTag(viewHolder1);
				
			}else{
				viewHolder1 = (Child1ViewHolder) convertView.getTag();
			}
			
			
			if(DolphinUtil.getManger(info.getAuthority()) == 1){
				viewHolder1.child1_manager.setVisibility(View.VISIBLE);
			}else{
				viewHolder1.child1_manager.setVisibility(View.INVISIBLE);
			}
			
			if(info.getDeviceType().equals("0")){
				viewHolder1.child1_type.setImageResource(R.drawable.ic_type_phone);
			}else{
				viewHolder1.child1_type.setImageResource(R.drawable.ic_type_dolphin);
			}
			
		
			
			Bitmap head = UserAvatar.loadImage(info.getLoginName());
			if(head!=null){
				viewHolder1.child1_icon.setImageBitmap(head);
			}
			
			if(info.getNoteName().equals("")){
				viewHolder1.child1_name.setText(info.getNickName());
			}else{
				viewHolder1.child1_name.setText(info.getNoteName());
			}
			
		}else{
			
			FamilyUserInfo FamilyUser = map.get(groupPosition).get(childPosition);
			if(null==convertView){
				viewHolder2 = new Child2ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.fragment_contacts_child_item1, null);
				viewHolder2.child2_icon = (CircleImageView) convertView.findViewById(R.id.child1_icon);
				viewHolder2.child2_edit = (ImageView) convertView.findViewById(R.id.child1_edit);
				viewHolder2.child2_type = (ImageView) convertView.findViewById(R.id.child1_type);
				viewHolder2.child2_name = (TextView) convertView.findViewById(R.id.child1_name);
				viewHolder2.child2_manager = (ImageView) convertView.findViewById(R.id.child1_manager);
				viewHolder2.view = (View) convertView.findViewById(R.id.view);
				convertView.setTag(viewHolder2);
				
			}else{
				viewHolder2 = (Child2ViewHolder) convertView.getTag();
			}
			
			if(childPosition == map.get(groupPosition).size()-1){
				viewHolder2.view.setVisibility(View.INVISIBLE);
			}else{
				viewHolder2.view.setVisibility(View.VISIBLE);
			}
			
			if(mMyFamily[groupPosition] ){
				viewHolder2.child2_edit.setVisibility(View.VISIBLE);
				viewHolder2.child2_edit.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						user = map.get(groupPosition).get(childPosition);
						showWatchEdit().show();
					}
				});
			}else{
				viewHolder2.child2_edit.setVisibility(View.INVISIBLE);
			}
			
			
			if(DolphinUtil.getManger(FamilyUser.getAuthority()) == 1){
				viewHolder2.child2_manager.setVisibility(View.VISIBLE);
			}else{
				viewHolder2.child2_manager.setVisibility(View.INVISIBLE);
			}
			
			if(FamilyUser.getDeviceType().equals("0")){
				viewHolder2.child2_type.setImageResource(R.drawable.ic_type_phone);
			}else{
				viewHolder2.child2_type.setImageResource(R.drawable.ic_type_dolphin);
				viewHolder2.child2_edit.setVisibility(View.INVISIBLE);
			}
			
			Bitmap head = UserAvatar.loadImage(FamilyUser.getLoginName());
			if(head!=null){
				viewHolder2.child2_icon.setImageBitmap(head);
			}
			
			if(FamilyUser.getNoteName().equals("")){
				viewHolder2.child2_name.setText(FamilyUser.getNickName());
			}else{
				viewHolder2.child2_name.setText(FamilyUser.getNoteName());
			}
			
		}
		
		
		return convertView;
	}
	
	private AlertDialog showWatchEdit(){
    	String mParams[] = mContext.getResources().getStringArray(R.array.select_watch);
    	int privacyWatch =0 ;
    	if(DolphinUtil.getPrivacy(user.getAuthority())){
    		privacyWatch = 0;
    	}else{
    		privacyWatch = 1;
    	}
    	CreateDialog dialogC = new CreateDialog(mContext, listener);
      return  dialogC.singleChoiceDialog(mParams, privacyWatch);
    	
	}
	
	DolphinDialogLisenter listener = new DolphinDialogLisenter() {
		
		@Override
		public void onDialogDismiss(int item_index) {
			// TODO Auto-generated method stub
			int author = Integer.parseInt(user.getAuthority());
			final int mAuthor ;
			if(item_index == 1){
				mAuthor = author | 4;
				
			}else{
				mAuthor = author & 3;
			}
			
			String mWatch = RequestString.SetUserWatchDolphinAuth(SysConfig.dtoken,user.getFamilyID(),user.getUserID(),item_index);
			UploadData2Server task = new UploadData2Server(mWatch, "SetUserWatchDolphinAuth");
			task.getData(new codeDataCallBack() {
				
				@Override
				public void onDataCallBack(String request, int code) {
					// TODO Auto-generated method stub
					if(code ==0){
						String setPrivacy = RequestString.UploadMessageBox(SysConfig.dtoken, 0, user.getUserID(), JsonUtil.getUserWatchAuthJson(user.getFamilyID(),user.getUserID(),mAuthor).toString());
						UploadData2Server task = new UploadData2Server(setPrivacy,"UploadMessageBox");
						task.getData(new codeDataCallBack() {
							
							@Override
							public void onDataCallBack(String request, int code) {
								// TODO Auto-generated method stub
								//DolphinApp.getInstance().notifyMsgRTC2Family(user.getFamilyID());
							}
						});
						user.setAuthority(mAuthor+"");
						SQLiteManager.getInstance().updateFamilyUserInfoAuthor(user.getFamilyID(),user.getUserID(), FamilyUserInfo._FAMILY_USER_AUTHORITY, mAuthor+"", true);
					}
				}
			});
		}
	};
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	private class GroupViewHolder {
		private TextView group_title;
		private TextView group_name;
		private ImageView group_arrow;
		private View group_view;
	}
	

	private class Child2ViewHolder {
		private ImageView child2_manager;
		private CircleImageView child2_icon;
		private ImageView child2_edit;
		private ImageView child2_type;
		private TextView child2_name;
		private View view;
	}
	private class Child1ViewHolder {
		private ImageView child1_manager;
		private CircleImageView child1_icon;
		private ImageView child1_edit;
		private ImageView child1_type;
		private TextView child1_name;
	}

	private class Child0ViewHolder {
		private CircleImageView child0_icon;
		private ImageView child0_eye;
		private TextView child0_name;
		private TextView child0_online;
	}


}
