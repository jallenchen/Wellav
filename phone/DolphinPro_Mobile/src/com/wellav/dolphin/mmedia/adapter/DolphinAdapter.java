package com.wellav.dolphin.mmedia.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.MainActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class DolphinAdapter extends BaseAdapter {
	private Context context_ = null;
	private List<FamilyInfo> familys;
	private String MyUserId;
	private LoadUserAvatarFromLocal task;
	private BadgeView badge;
	
	public DolphinAdapter(Context context,List<FamilyInfo> familys) {
		// TODO Auto-generated constructor stub
		this.context_ = context;
		this.familys = familys;
		task = new LoadUserAvatarFromLocal();
		MyUserId = DolphinApp.getInstance().getAccountInfo().getUserId();
	}
	
	public void refresh(List<FamilyInfo> familys,boolean isChkState){
		this.familys = familys;
		notifyDataSetChanged();
	}
	public void refresh(List<FamilyInfo> familys){
		this.familys = familys;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return familys.size()+1;
	}

	@Override
	public FamilyInfo getItem(int position) {
		// TODO Auto-generated method stub
		return familys.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		int type = 0;
		if(position != 0){
			type = 1;
		}
		return type;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GroupViewHolder viewHolder = null;
		GroupMsgViewHolder msgViewHolder = null;
		int type = getItemViewType(position);
		if(type == 0){
			if(null==convertView){
				msgViewHolder = new GroupMsgViewHolder();
				convertView = LayoutInflater.from(context_).inflate(
						R.layout.dolphin_group_item0, null);
				
				msgViewHolder.group_msg_layout = (RelativeLayout) convertView
						.findViewById(R.id.msg_layout);
				msgViewHolder.group_msg_content = (TextView) convertView
						.findViewById(R.id.msg_content);
				msgViewHolder.group_msg_icon= (ImageView) convertView
						.findViewById(R.id.msg_iv);
				msgViewHolder.view =  (View) convertView
						.findViewById(R.id.view);
				
				msgViewHolder.badge = new BadgeView(context_, msgViewHolder.group_msg_icon);
				msgViewHolder.badge.setHeight(context_.getResources().getDimensionPixelSize(R.dimen.point_height));
				msgViewHolder.badge.setWidth(context_.getResources().getDimensionPixelSize(R.dimen.point_width));
				msgViewHolder.badge.setBadgeMargin(0);
				msgViewHolder.badge.setBackgroundResource(R.drawable.redpoint_10dp);
				msgViewHolder.badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
				msgViewHolder.badge.show();
				convertView.setTag(msgViewHolder);
			}else{
				msgViewHolder = (GroupMsgViewHolder) convertView.getTag();
			}
			
			if(position == familys.size()){
				msgViewHolder.view.setVisibility(View.INVISIBLE);
			}else{
				msgViewHolder.view.setVisibility(View.VISIBLE);
			}
			
			if(PreferenceUtils.getInstance().getBooleanSharePreferences(MsgKey.newBoxMsg)){
				msgViewHolder.badge.show();
				//MessageInform msg = SQLiteManager.getInstance().getLastBoxMessageInfo();
			}else{
				msgViewHolder.badge.hide();
			}
		}else{
			if (null == convertView) {
				viewHolder = new GroupViewHolder();
				convertView = LayoutInflater.from(context_).inflate(
						R.layout.dolphin_group_item, null);
				viewHolder.group_layout = (RelativeLayout) convertView
						.findViewById(R.id.group_layout);
				viewHolder.group_name = (TextView) convertView
						.findViewById(R.id.id_group_name);
				viewHolder.group_arrow = (ImageView) convertView
						.findViewById(R.id.id_group_arrow);
				viewHolder.group_manager = (ImageView) convertView
						.findViewById(R.id.id_group_manager);
				viewHolder.group_head = (ImageView) convertView
						.findViewById(R.id.id_group_head);
				viewHolder.group_online = (TextView) convertView
						.findViewById(R.id.id_group_online);
				viewHolder.group_view =(View) convertView
						.findViewById(R.id.view);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (GroupViewHolder) convertView.getTag();
			}
			
			int gPosition = position - 1;
			if(gPosition == familys.size()-1){
				viewHolder.group_view.setVisibility(View.INVISIBLE);
			}else{
				viewHolder.group_view.setVisibility(View.VISIBLE);
			}
		//	viewHolder.group_layout.setOnClickListener(myListener);
			viewHolder.group_name.setText(familys.get(gPosition).getNickName());
//			if(familys.get(gPosition).getNote().equals("")){
//				viewHolder.group_name.setText(familys.get(gPosition).getNickName());
//			}else{
//				viewHolder.group_name.setText(familys.get(gPosition).getNote());
//			}
			
			Bitmap head = task.loadImage(familys.get(gPosition).getDeviceID());	
			if(head!=null){
				viewHolder.group_head.setImageBitmap(head);
			}
			
			if(!familys.get(gPosition).getMangerID().equals(MyUserId)){
				viewHolder.group_manager.setVisibility(View.INVISIBLE);
			}else{
				viewHolder.group_manager.setVisibility(View.VISIBLE);
			}
				viewHolder.group_arrow
						.setBackgroundResource(R.drawable.arrow_r_gray);
				if(MainActivity.isCheckUpdate == true){
					viewHolder.group_online.setTextColor(R.color.color105);
					viewHolder.group_online.setText(R.string.checking);
				}else{
					if(DolphinApp.getInstance().getFamilyOnlineMap().containsKey(familys.get(gPosition).getDeviceID())){
						
						if(DolphinApp.getInstance().getFamilyOnlineMap().get(familys.get(gPosition).getDeviceID())){
							viewHolder.group_online.setText(R.string.online);
							viewHolder.group_online.setTextColor(0xff6699ff);
						}else{
							viewHolder.group_online.setTextColor(R.color.color105);
							viewHolder.group_online.setText(R.string.no_online);
							DolphinApp.getInstance().getFamilyOnlineMap().put(familys.get(gPosition).getDeviceID(), false);
						}
					}else{
						viewHolder.group_online.setTextColor(R.color.color105);
						viewHolder.group_online.setText(R.string.no_online);
						DolphinApp.getInstance().getFamilyOnlineMap().put(familys.get(gPosition).getDeviceID(), false);
					}
				}
				
			
		}
		
		return convertView;
	}
	
	private class GroupViewHolder {
		private TextView group_name;
		private TextView group_online;
		private ImageView group_arrow;
		private ImageView group_head;
		private ImageView group_manager;
		private RelativeLayout group_layout;
		private View group_view;
	}
	
	private class GroupMsgViewHolder{
		private TextView group_msg_title;
		private TextView group_msg_content;
		private ImageView group_msg_icon;
		private RelativeLayout group_msg_layout;
		private View view;
		private BadgeView badge;
	}

}
