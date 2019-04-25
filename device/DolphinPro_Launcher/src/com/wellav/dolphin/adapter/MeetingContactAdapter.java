package com.wellav.dolphin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.interfaces.IMeetingAudioControl;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.netease.config.LogUtil;
import com.wellav.dolphin.netease.config.SendNitificationMsgUtil;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.utils.NameUtils;

public class MeetingContactAdapter extends BaseAdapter {

	private Context context;
	private List<FamilyUserInfo> meetingMebers = new ArrayList<FamilyUserInfo>();
	private ArrayList<String> mMeetingName = new ArrayList<String>();
    HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	private LoadUserAvatarFromLocal mHead;
	private FamilyUserInfo person;
	int mPosition;
	private ViewHold hold;
	private JSONObject obj = new JSONObject();
	
	public MeetingContactAdapter(Context context) {
		super();
		this.context = context;
		mHead = new LoadUserAvatarFromLocal();
	}

	@Override
	public int getCount() {
		if (meetingMebers != null) {
			return meetingMebers.size();
		}
		return 0;
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
	
	public void refreshAdd(String  acc) {
		if(mMeetingName.contains(acc)){
			return;
		}
		mMeetingName.add(acc);
		FamilyUserInfo info = LauncherApp.getInstance().getDBHelper()
				.getFamilyUser(this.context, acc);
		
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
		notifyDataSetChanged();
		}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final FamilyUserInfo person = meetingMebers.get(position);
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.meeting_contact_item, null);
			
			
			hold.head = (CircleImageView) convertView
					.findViewById(R.id.meeting_contact_head_iv);
			hold.type = (ImageView) convertView
					.findViewById(R.id.meeting_contact_type_iv);
			hold.voice = (ImageView) convertView
					.findViewById(R.id.meeting_contact_micr_iv);
			hold.name = (TextView) convertView.findViewById(R.id.meeting_contact_name_tv);
			
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		
		Bitmap head = mHead.loadImage(person.getLoginName());
		if(head!=null){
			hold.head.setImageBitmap(head);
		}else{
			mHead.getData(person.getUserID(), new userAvatarDataCallBack() {
				@Override
				public void onDataCallBack(int code, Bitmap avatar) {
					hold.head.setImageBitmap(avatar);
				}
			});	
		}
		if(person.getDeviceType().equals(MsgKey.PHONE)){
			hold.type.setImageResource(R.drawable.type_phone_white);
		}else{
			hold.type.setImageResource(R.drawable.type_dolphin_white);
		}
			hold.voice.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					if(person.getRemoteAudioMute() == false){
						meetingMebers.get(position).setRemoteAudioMute(true);
					}else{
						meetingMebers.get(position).setRemoteAudioMute(false);
					}
					LogUtil.e("MeetingContactAdapter", meetingMebers.get(position).getRemoteAudioMute()+":"+person.getRemoteAudioMute());
					obj.put("type", "audiocontrol");
			        obj.put("name", person.getLoginName());
			        obj.put("value", person.getRemoteAudioMute());
			        String jsonContent = obj.toJSONString();
					SendNitificationMsgUtil.sendTeamCustomNotification(context, SysConfig.getInstance().getTeamId(), jsonContent);
					notifyDataSetChanged();
				}
			});
			LogUtil.e("MeetingContactAdapter", person.getLoginName()+":"+person.getRemoteAudioMute());
			LogUtil.e("MeetingContactAdapter", meetingMebers.get(position).getLoginName()+":"+meetingMebers.get(position).getRemoteAudioMute());
			if(meetingMebers.get(position).getRemoteAudioMute() == false){
				hold.voice.setSelected(false);
			}else{
				hold.voice.setSelected(true);
			}
		
		if(meetingMebers.get(position).getNoteName().equals("")){
			hold.name.setText(meetingMebers.get(position).getNickName());
		}else{
			hold.name.setText(meetingMebers.get(position).getNoteName());
		}
		return convertView;
	}

	static class ViewHold {
		public CircleImageView head;
		public ImageView type, voice;
		public TextView name;
	}

}
