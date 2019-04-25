package com.wellav.dolphin.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.utils.Util;

public class CheckedContactAdapter extends BaseAdapter {
	private Context mContext;
	private List<FamilyUserInfo> mContacts;
	private LoadUserAvatarFromLocal mHead;
	public List<FamilyUserInfo> getData() {
		return mContacts;
	}

	public CheckedContactAdapter(Context ct, List<FamilyUserInfo> data) {
		this.mContext = ct;
		this.mContacts = data;
		mHead = new LoadUserAvatarFromLocal();
	}

	public void refresh(List<FamilyUserInfo>  list) {
		this.mContacts = list;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mContacts.size();
	}

	@Override
	public FamilyUserInfo getItem(int position) {
		return mContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FamilyUserInfo checkedContact = mContacts.get(position);
		
		if(checkedContact == null){
			return null;
		}
	    String nickName = checkedContact.getNickName();

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.request_checked_item, null);

			viewHolder = new ViewHolder();
			viewHolder.headicon = (CircleImageView) convertView
					.findViewById(R.id.headicon);
			viewHolder.type = (ImageView) convertView
					.findViewById(R.id.type);
			viewHolder.manager = (ImageView) convertView
					.findViewById(R.id.manager);
			viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
			if(checkedContact.getNoteName().equals("")){
				viewHolder.nameTv.setText(nickName);
			}else{
				viewHolder.nameTv.setText(checkedContact.getNoteName());
			}
			Bitmap head = mHead.loadImage(checkedContact.getLoginName());
			if(head!=null){
				viewHolder.headicon.setImageBitmap(head);
			}else{
				viewHolder.headicon
				.setBackgroundResource(R.drawable.defaulthead_dolphin_48dp);	
			}
			if(checkedContact.getDeviceType().equals(MsgKey.DEVICE)){
				viewHolder.type.setImageResource(R.drawable.type_dolphin_blue);
			}else{
				viewHolder.type.setImageResource(R.drawable.type_phone_blue);
			}
			if(Util.getManger(checkedContact.getAuthority()) == true){
				viewHolder.manager.setVisibility(View.VISIBLE);
			}else{
				viewHolder.manager.setVisibility(View.INVISIBLE);
			}
			
		return convertView;
	}


	static class ViewHolder {
		String id;
		CircleImageView headicon;
		ImageView type;
		ImageView manager;
		TextView nameTv;
	}
}