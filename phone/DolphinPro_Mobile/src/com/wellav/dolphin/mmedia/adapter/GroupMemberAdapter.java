package com.wellav.dolphin.mmedia.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.fragment.InitiateChatFragment;
import com.wellav.dolphin.mmedia.fragment.InitiateChatFragment.MyListener;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.NameUtils;

public class GroupMemberAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HashMap<String, Object>> data;
	public HashMap<String, Boolean> checkedMap = new HashMap<String, Boolean>();
	public List<FamilyUserInfo> familyUsers;
	private LoadUserAvatarFromLocal userAvatar;
	private int mBeenSelect=2;

	/**
	 * 
	 */
	public GroupMemberAdapter(Context ct, List<FamilyUserInfo> familyusers,HashMap<String, Boolean> checkedMap2) {
		super();
		// TODO Auto-generated constructor stub
		this.mContext = ct;
		this.familyUsers = familyusers;
		userAvatar = new LoadUserAvatarFromLocal();
		this.checkedMap = checkedMap2;
		mBeenSelect = checkedMap.size();
	}


	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public void refresh(HashMap<String, Boolean> checkedMap2){
		this.checkedMap = checkedMap2;
		notifyDataSetChanged();
	}

	private class ChildViewHolder {
		private ImageView memberIcon;
		private ImageView memberType;
		private ToggleButton memberBox;
		private TextView memberName;
		private View view;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return familyUsers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChildViewHolder viewHolder = null;
		FamilyUserInfo info = familyUsers.get(position);
	    final MyListener myListener = new InitiateChatFragment().new MyListener();
		
		if(null==convertView){
			viewHolder = new ChildViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_initate_child_item, null);
			
			viewHolder.memberIcon = (ImageView) convertView.findViewById(R.id.membericon);
			viewHolder.memberType = (ImageView) convertView.findViewById(R.id.membertype);
			viewHolder.memberBox = (ToggleButton) convertView.findViewById(R.id.checkbox);
			viewHolder.memberName = (TextView) convertView.findViewById(R.id.membername);
			viewHolder.view = (View) convertView.findViewById(R.id.view);
			convertView.setTag(viewHolder);
			
		}else{
			viewHolder = (ChildViewHolder) convertView.getTag();
		}
		if(familyUsers.size()-1 == position){
			viewHolder.view.setVisibility(View.INVISIBLE);
		}else{
			viewHolder.view.setVisibility(View.VISIBLE);
		}
		
		Bitmap head = userAvatar.loadImage(info.getLoginName());
		if(head != null){
			viewHolder.memberIcon.setImageBitmap(head);
		}
		
		if(info.getDeviceType().equals("0")){
			viewHolder.memberType.setImageResource(R.drawable.ic_type_phone);
		}else{
			viewHolder.memberType.setImageResource(R.drawable.ic_type_dolphin);
		}
		
		if(SysConfig.getInstance().isCalling()){
			if(NameUtils.remoteNamesContain(info.getLoginName()) || info.getLoginName().equals(SysConfig.uid)){
				viewHolder.memberBox.setChecked(true);
				viewHolder.memberBox.setEnabled(false);
			}else{
				if(!checkedMap.containsKey(info.getLoginName())){
					viewHolder.memberBox.setChecked(false);
				}else{
					viewHolder.memberBox.setChecked(checkedMap.get(info.getLoginName()));
				}
				
				viewHolder.memberBox.setEnabled(true);
			}
		}else{
			if(position < 2 ){
				
				viewHolder.memberBox.setChecked(true);
				viewHolder.memberBox.setEnabled(false);
			}else{
				if(!checkedMap.containsKey(info.getLoginName())){
					viewHolder.memberBox.setChecked(false);
				}else{
					viewHolder.memberBox.setChecked(checkedMap.get(info.getLoginName()));
				}
				
				viewHolder.memberBox.setEnabled(true);
			}
		}
		
		if(info.getNoteName().equals("")){
			viewHolder.memberName.setText(info.getNickName());
		}else{
			viewHolder.memberName.setText(info.getNoteName());
		}
		
		viewHolder.memberBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myListener.onClick(v, position);
			}
		});
		
		return convertView;
	}

}
