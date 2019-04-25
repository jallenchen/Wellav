package com.wellav.dolphin.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.ui.CircleImageView;

public class AdapterForHomeContacts extends Adapter<AdapterForHomeContacts.MyViewHolder>{
	//private static final String TAG="AdapterForHomeContacts";
	private LayoutInflater mInflator;
	private OnItemClickListener mListener;
	private List<FamilyUserInfo> mUsers;
	public interface OnItemClickListener{
		public void onItemClick(String username);
	}
	

	public AdapterForHomeContacts(Context ct,List<FamilyUserInfo> users,OnItemClickListener lis) {
		mInflator=LayoutInflater.from(ct);
		mListener=lis;
		mUsers = users;
		
	}

	class MyViewHolder extends ViewHolder{
		CircleImageView mHead;
		ImageView mType;
		TextView mName;
		public MyViewHolder(View itemView) {
			super(itemView);
			mHead = (CircleImageView) itemView.findViewById(R.id.item_home);
			mType=(ImageView)itemView.findViewById(R.id.item_type);
			mName=(TextView)itemView.findViewById(R.id.item_name);
		}
	}

	public void refresh(List<FamilyUserInfo>  listmerber) {
		this.mUsers = listmerber;
		notifyDataSetChanged();
	}
	
	@Override
	public int getItemCount() {
		return mUsers.size();
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int id) {
		final FamilyUserInfo user = mUsers.get(id);
		LoadUserAvatarFromLocal avatar = new LoadUserAvatarFromLocal();
		Bitmap head = avatar.loadImage(user.getLoginName());
		if(head !=null){
			holder.mHead.setImageBitmap(head);
		}else{
			avatar.getData(user.getUserID(), new userAvatarDataCallBack() {
				@Override
				public void onDataCallBack(int code, Bitmap avatar) {
					holder.mHead.setImageBitmap(avatar);
				}
			});
		}
		
		if(user.getNoteName().equals("")){
			holder.mName.setText(user.getNickName());
		}else{
			holder.mName.setText(user.getNoteName());
		}
		
		if(user.getDeviceType().equals(MsgKey.DEVICE)){
			holder.mType.setImageResource(R.drawable.type_dolphin_blue);
		}
		
		holder.mHead.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			mListener.onItemClick(user.getLoginName());
		}
	});
		
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		MyViewHolder holder=new MyViewHolder(mInflator.inflate(R.layout.home_contact_item, arg0, false));
		return holder;
	}

}
