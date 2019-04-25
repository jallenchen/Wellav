package com.wellav.dolphin.mmedia.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MessageEventType;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.lisenter.TextClickableSpan;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class MsgAdapter extends BaseAdapter {
	private Context mContext;
    List<MessageInform> msgs;
    MessageInform itemMsg;
    LoadUserAvatarFromLocal mAvatar;
    int total = 0;

	public MsgAdapter(Context ct, List<MessageInform> msgList) {
		super();
		// TODO Auto-generated constructor stub
		this.mContext = ct;
		this.msgs = msgList;
		total = msgs.size();
		mAvatar = new LoadUserAvatarFromLocal();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgs.size();
	}

	@Override
	public MessageInform getItem(int position) {
		// TODO Auto-generated method stub
		return msgs.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	public void refresh(List<MessageInform> msgList){
		this.msgs = msgList;
		notifyDataSetChanged();
	}
	

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;
		   final MessageInform msg = getItem(position);
		if(convertView!=null)
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}else {
			convertView=LayoutInflater.from(mContext).inflate(R.layout.fragmentmsg_item, null);
			viewHolder=new ViewHolder();
			viewHolder.mHeadicon = (CircleImageView) convertView.findViewById(R.id.msg_icon);
			viewHolder.mTime = (TextView) convertView.findViewById(R.id.msg_time);
			viewHolder.mName = (TextView) convertView.findViewById(R.id.msg_name);
			viewHolder.mMsg = (TextView) convertView.findViewById(R.id.msg);
			convertView.setTag(viewHolder);
		}
		Bitmap headbitmap = mAvatar.loadImage(msg.getDeviceID());
		if(headbitmap != null){
			viewHolder.mHeadicon.setImageBitmap(headbitmap);
		}else{
			viewHolder.mHeadicon.setImageResource(R.drawable.ic_defaulthead_home_40dp);
		}
		//if(info.getDeviceType().equals("1")){
		//	viewHolder.mType.setImageResource(R.drawable.type_dolphin);
		//}
		
		viewHolder.mName.setText(msg.getDolphinName());
		switch (msg.getEventType()) {
		case MessageEventType.EVENTTYPE_JOIN_FANILY:
			viewHolder.mMsg.setText(msg.getName() + mContext.getString(R.string.join_my_home));
			break;
		case MessageEventType.EVENTTYPE_EXIT_FAMILY:
			viewHolder.mMsg.setText(msg.getName()+""+mContext.getString(R.string.leave_my_home));
			break;
		case MessageEventType.EVENTTYPE_PHONE_MANAGER:
			viewHolder.mMsg.setText(msg.getName()+mContext.getString(R.string.be_my_manager));
			break;
		case MessageEventType.EVENTTYPE_DISTANCE_SETTING:
			viewHolder.mMsg.setText(msg.getName()+mContext.getString(R.string.remote_to_me));
			break;
		case MessageEventType.EVENTTYPE_LOOK_HOME:
			viewHolder.mMsg.setText(msg.getName()+mContext.getString(R.string.check_me));
			break;
		case MessageEventType.EVENTTYPE_CALL_HOME:
			viewHolder.mMsg.setText(msg.getName()+mContext.getString(R.string.talking_me));
			break;
		case MessageEventType.EVENTTYPE_INVITE_TALK:
			viewHolder.mMsg.setText(msg.getName()+""+mContext.getString(R.string.chat_me));
			String textClick = "  " + mContext.getString(R.string.click_join);
			SpannableString spanttt = new SpannableString(textClick);
			ClickableSpan clickttt = new TextClickableSpan(msg.getTalkingID(), mContext);
			spanttt.setSpan(clickttt, 0, textClick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			viewHolder.mMsg.append(spanttt);
			viewHolder.mMsg.setMovementMethod(LinkMovementMethod.getInstance());
			break;
		case MessageEventType.EVENTTYPE_INVITE_MEETTING:
			viewHolder.mMsg.setText(msg.getName()+""+mContext.getString(R.string.meet_me));
			String textClick2 = "  " + mContext.getString(R.string.click_join);
			SpannableString spanttt2 = new SpannableString(textClick2);
			ClickableSpan clickttt2 = new TextClickableSpan(msg.getMeettingID(), mContext);
			spanttt2.setSpan(clickttt2, 0, textClick2.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			viewHolder.mMsg.append(spanttt2);
			viewHolder.mMsg.setMovementMethod(LinkMovementMethod.getInstance());
			break;	
		case MessageEventType.EVENTTYPE_CALL_URGENCY:
			viewHolder.mMsg.setText(msg.getName()+""+ mContext.getString(R.string.call_mercy) +"  ");
			break;
		case MessageEventType.EVENTTYPE_BEEN_MANAGER:
			viewHolder.mMsg.setText(msg.getName()+""+mContext.getString(R.string.first_manager));
			break;
		case MessageEventType.EVENTTYPE_RELEASE_FAMILY:
			viewHolder.mMsg.setText(msg.getName()+""+mContext.getString(R.string.no_family_more));
			break;
		case MessageEventType.EVENTTYPE_POST_PIC:
			viewHolder.mMsg.setText(msg.getName()+""+ mContext.getString(R.string.uploaded) +msg.getNum()+mContext.getString(R.string.pictures));
			break;
		case MessageEventType.EVENTTYPE_LOOK_PIC:
			viewHolder.mMsg.setText(mContext.getString(R.string.i_check_photo));
			break;		
			
		default:
			CommFunc.DisplayToast(mContext, msg.getEventType());
			break;
		}
		
		
		long time = CommFunc.dateToLang(CommFunc.SplicDate(msg.getTime()));
		CommFunc.toRecordDate(time);
		viewHolder.mTime.setText(CommFunc.toRecordDate(time));
		
		return convertView;
	}
	
	private static class ViewHolder{
		CircleImageView mHeadicon;
		TextView mTime;
		TextView mName;
		TextView mMsg;
	}

}
