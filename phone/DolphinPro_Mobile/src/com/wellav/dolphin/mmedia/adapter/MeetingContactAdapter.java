package com.wellav.dolphin.mmedia.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.NameUtils;

public class MeetingContactAdapter extends BaseAdapter {

	private Context context;
	private List<FamilyUserInfo> meetingMebers = new ArrayList<FamilyUserInfo>();;
	private LoadUserAvatarFromLocal userAvatar;
	private ArrayList<String> mMeetingName = new ArrayList<String>();
	private HashMap<String, Boolean> mAudioState = new HashMap<>();

	public MeetingContactAdapter(Context context) {
		super();
		this.context = context;
		userAvatar = new LoadUserAvatarFromLocal();
	}

	@Override
	public int getCount() {
		if (meetingMebers != null) {
			return meetingMebers.size();
		}
		return 0;
	}
	
	public void refreshAdd(String  acc) {
		if(mMeetingName.contains(acc)){
			return;
		}
		mMeetingName.add(acc);
		FamilyUserInfo info = SQLiteManager.getInstance().geFamilyUserInfoLoginName(acc);
		
		meetingMebers.add(info);
		notifyDataSetChanged();
		}
	public void refreshRemove(String  acc) {
		if(!mMeetingName.contains(acc)){
			return;
		}
		int index = mMeetingName.indexOf(acc);
		mMeetingName.remove(acc);
		meetingMebers.remove(index);
		//meetingMebers = NameUtils.loginName2UserInfo(mMeetingName);
		notifyDataSetChanged();
		}
	
	public void refreshMuteState( HashMap<String, Boolean> audioMap){
		mAudioState = audioMap;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		if (meetingMebers != null) {
			return meetingMebers.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold hold;
		MyListener myListener=null;
		FamilyUserInfo person = meetingMebers.get(position);
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.meeting_contact_item, null);
			myListener=new MyListener(position);
			
			hold.headicon = (CircleImageView) convertView
					.findViewById(R.id.headicon);
			hold.voice = (ImageButton) convertView
					.findViewById(R.id.voice);
			hold.name = (TextView) convertView.findViewById(R.id.name);
			
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		
		Bitmap head = userAvatar.loadImage(person.getLoginName());
		if(head!=null){
			hold.headicon.setImageBitmap(head);
		}else{
			hold.headicon
			.setBackgroundResource(R.drawable.ic_defaulthead_female_40dp);	
		}
		if(mAudioState.containsKey(person.getLoginName())){
			hold.voice.setSelected(mAudioState.get(person.getLoginName()));
		}else{
			hold.voice.setSelected(false);
		}
		hold.voice.setOnClickListener(myListener);
		//hold.meetingVoiceIv.setImageResource(meetingMebers.get(position).getVoiceImage());
		
		if(meetingMebers.get(position).getNoteName().equals("")){
			hold.name.setText(meetingMebers.get(position).getNickName());
		}else{
			hold.name.setText(meetingMebers.get(position).getNoteName());
		}
		return convertView;
	}
	
	
	 private class MyListener implements OnClickListener{
	        int mPosition;
	        String name;
	        
	        public MyListener(int inPosition){
	            mPosition= inPosition;
	            name  =  meetingMebers.get(mPosition).getLoginName();
	        }
	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	        }
	        
	        
	    }
	 

	static class ViewHold {
		public CircleImageView headicon;
		public ImageButton voice;
		public TextView name;
	}

}
