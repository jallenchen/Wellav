package com.wellav.dolphin.mmedia.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;

public class ChooseNewAdminAdapter extends BaseAdapter {
	private Context mContext;
	private  List<FamilyUserInfo> data;
	private String[] beans;
	  // 用于记录每个RadioButton的状态，并保证只可选一个
    HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	public static int pos;
	private LoadUserAvatarFromLocal userAvatar;

	public ChooseNewAdminAdapter(Context ct, List<FamilyUserInfo> userdata) {
		this.mContext = ct;
		this.data = userdata;
		userAvatar = new LoadUserAvatarFromLocal();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
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
		ViewHolder viewHolder= null;
		final FamilyUserInfo info = data.get(position);
		if(null==convertView){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_change_admin_item, null);
			
			viewHolder.memberIcon = (ImageView) convertView.findViewById(R.id.membericon);
			//viewHolder.memberBox = (RadioButton) convertView.findViewById(R.id.radiobox);
			viewHolder.memberName = (TextView) convertView.findViewById(R.id.membername);
			convertView.setTag(viewHolder);
			
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		if(info.getNoteName().equals("")){
			viewHolder.memberName.setText(info.getNickName());
		}else{
			viewHolder.memberName.setText(info.getNoteName());
		}
		
		Bitmap bitmap = userAvatar.loadImage(info.getLoginName());
		if(bitmap !=null ){
			viewHolder.memberIcon.setImageBitmap(bitmap);
		}else{
			if(info.getSex().equals("girl")){
				viewHolder.memberIcon.setImageResource(R.drawable.ic_defaulthead_female_40dp);
			}else{
				viewHolder.memberIcon.setImageResource(R.drawable.ic_defaulthead_male_40dp);
			}
		}
		
		 final RadioButton radio=(RadioButton) convertView.findViewById(R.id.radiobox);  
		 viewHolder.memberBox = radio;  
		 viewHolder.memberBox.setOnClickListener(new View.OnClickListener() {

	      public void onClick(View v) {

	        // 重置，确保最多只有一项被选中
	        for (String key : states.keySet()) {
	          states.put(key, false);

	        }
	        Log.e("ChooseNewAdminAdapter", data.get(position).getNickName());
	        pos =  position;
	        states.put(info.getUserID(), radio.isChecked());
	        ChooseNewAdminAdapter.this.notifyDataSetChanged();
	      }
	    });

	    boolean res = false;
	    Log.e("ChooseNewAdminAdapter1", info.getUserID());
	    if (states.get(info.getUserID()) == null
	        || states.get(info.getUserID()) == false) {
	      res = false;
	      states.put(info.getUserID(), false);
	    } else
	      res = true;

	    viewHolder.memberBox.setChecked(res);
		
		return convertView;
	}
	
	public int getChoosePos(){
		
		return pos;
	}

	private class ViewHolder {
		private ImageView memberIcon;
		private RadioButton memberBox;
		private TextView memberName;
	}

}
