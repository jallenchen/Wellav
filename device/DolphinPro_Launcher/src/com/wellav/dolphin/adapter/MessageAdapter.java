package com.wellav.dolphin.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInform;
import com.wellav.dolphin.launcher.R;

@SuppressLint("ResourceAsColor") 
public class MessageAdapter extends BaseAdapter {

	private List<MessageInform> messageInfos;
	private MessageInform messageInfo;
	private LayoutInflater inflater;
	private Context mContext;
	/**
	 * @param context
	 * @param list
	 * @author xiaowenzi
	 */
	public MessageAdapter(Context context, List<MessageInform> messageInfos) {
		this.mContext = context;
		this.messageInfos = messageInfos;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return messageInfos.size();
	}
	
	public void refresh(List<MessageInform> messageInfos) {
		this.messageInfos = messageInfos;
		notifyDataSetChanged();
	}

	public MessageInform getItem(int position) {
		return messageInfos.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public boolean isEnabled(int position) {        
	    return true;  
	}  

	@SuppressLint("InflateParams") public View getView(int position, View convertView, ViewGroup parent) {

		MESSAGE_ADAPTER mESSAGE_ADAPTER = null;
		messageInfo = messageInfos.get(position);
		
		if (convertView == null) {
			mESSAGE_ADAPTER = new MESSAGE_ADAPTER();
			convertView = inflater
					.inflate(R.layout.item_message, null);
			// 年月
			mESSAGE_ADAPTER.text_yearAndMonth = (TextView) convertView
					.findViewById(R.id.txt_date_yearAndMonth);
			// 月日，时分
			mESSAGE_ADAPTER.text_monthAndDate = (TextView) convertView
					.findViewById(R.id.txt_date_monthAndDate);
			// 名称
			mESSAGE_ADAPTER.text_information_name = (TextView) convertView
					.findViewById(R.id.txt_information_name);
			// 内容
			mESSAGE_ADAPTER.text_information_content = (TextView) convertView
					.findViewById(R.id.txt_information_content);
			// 紧急呼叫
			mESSAGE_ADAPTER.text_urgency = (TextView) convertView
					.findViewById(R.id.text_urgency);
			// 异常照片视频
			mESSAGE_ADAPTER.text_wrong = (TextView) convertView
					.findViewById(R.id.text_wrong);
			// 是否应答
			mESSAGE_ADAPTER.text_yesORno = (TextView) convertView
					.findViewById(R.id.txt_yesORno);

			convertView.setTag(mESSAGE_ADAPTER);
		} else {
			mESSAGE_ADAPTER = (MESSAGE_ADAPTER) convertView.getTag();
		}
		if (messageInfo.getEventType() != null) {
			
			// 设置时间,年月
			//第一条数据，肯定显示时间标题
            if (position == 0) {
            	mESSAGE_ADAPTER.text_yearAndMonth.setVisibility(View.VISIBLE);
            	mESSAGE_ADAPTER.text_yearAndMonth.setText(messageInfos.get(0).getYearAndMonth());
            } else { 
            	// 不是第一条数据 // 本条数据和上一条数据的时间戳相同，时间标题不显示
                if (messageInfos.get(position).getYearAndMonth().equals(messageInfos.get(position - 1).getYearAndMonth())) {
                    mESSAGE_ADAPTER.text_yearAndMonth.setVisibility(View.INVISIBLE);
                            
                } else {
                    //本条数据和上一条的数据的时间戳不同的时候，显示数据
                    mESSAGE_ADAPTER.text_yearAndMonth.setVisibility(View.VISIBLE);
                    mESSAGE_ADAPTER.text_yearAndMonth.setText(messageInfo.getYearAndMonth());
                }
            }
			// 根据事件种类设定内容和是否需要应答
			int i = Integer.parseInt(messageInfo.getEventType());
			switch (i) {
			case MessageEventType.LOOK_YOU:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.look_you));
				mESSAGE_ADAPTER.text_information_content
				        .setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				mESSAGE_ADAPTER.text_yesORno.setText("");
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
				
			case MessageEventType.CALL_YOU:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.call_you));
				mESSAGE_ADAPTER.text_information_content
						.setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				// 判断是否应答
				if (messageInfo.getBeenAnswered().equals(MessageEventType.NO_ANSWERED)) {
					mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.no_answered));
				} else {
					mESSAGE_ADAPTER.text_yesORno
							.setText(mContext.getResources().getString(R.string.been_answered));
				}
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
				
			case MessageEventType.INVITE_TALK:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(mContext.getResources().getString(R.string.my));

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.invite_talk));
				mESSAGE_ADAPTER.text_information_content
						.setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				// 判断是否结束
				if (messageInfo.getBeenAnswered().equals(MessageEventType.NO_ANSWERED)) {
					mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.but_come));
				}else {
					mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.invite_talk_over));
				}
				
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
				
			case MessageEventType.INVITE_MEETTING:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				
				mESSAGE_ADAPTER.text_information_name.setText(mContext.getResources().getString(R.string.my));

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.invite_meetting));
				mESSAGE_ADAPTER.text_information_content
						.setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				// 判断是否结束
				if (messageInfo.getBeenAnswered().equals(MessageEventType.NO_ANSWERED)) {
					mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.but_come));
				}else {
					mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.invite_meetting_over));
				}
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
				
			case MessageEventType.CALL_URGENCY:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.call_urgency));
				mESSAGE_ADAPTER.text_information_content
						.setTextColor(Color.RED);
				
				// 判断是否应答
				if (messageInfo.getBeenAnswered().equals(MessageEventType.NO_ANSWERED)) {
					mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.no_answered));
				} else {
					mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.been_answered));
				}
				mESSAGE_ADAPTER.text_urgency.setText(mContext.getResources().getString(R.string.call_urgency_you));
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
				
			case MessageEventType.POST_PIC:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.post_pic_do));
				mESSAGE_ADAPTER.text_information_content
		        		.setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.but_check));
				mESSAGE_ADAPTER.text_urgency.setText(messageInfo.getNum());
				mESSAGE_ADAPTER.text_wrong.setText(mContext.getResources().getString(R.string.post_pic));
				break;
				
			case MessageEventType.PHONE_MANAGER:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.phone_manager));
				mESSAGE_ADAPTER.text_information_content
		        		.setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				mESSAGE_ADAPTER.text_yesORno.setText("");
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
				
			case MessageEventType.JOIN_FANILY:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.join_family));
				mESSAGE_ADAPTER.text_information_content
						.setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				mESSAGE_ADAPTER.text_yesORno.setText("");
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
				
			case MessageEventType.EXIT_FAMILY:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.exit_family));
				mESSAGE_ADAPTER.text_information_content
						.setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				mESSAGE_ADAPTER.text_yesORno.setText("");
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
				
			case MessageEventType.DISTANCE_SETTING:
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.distance_setting));
				mESSAGE_ADAPTER.text_information_content
						.setTextColor(mContext.getResources().getColor(R.color.color1_1));
				
				mESSAGE_ADAPTER.text_yesORno.setText("");
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
			case MessageEventType.RELEASE_FAMILY:
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.release_family));
				mESSAGE_ADAPTER.text_yesORno.setText("");
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
			case MessageEventType.BEENMANAGER:
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfo
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.been_manager));
				mESSAGE_ADAPTER.text_yesORno.setText("");
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
			case MessageEventType.LOOK_PIC:
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfo
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(mContext.getResources().getString(R.string.my));

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.look_photo));
				mESSAGE_ADAPTER.text_yesORno.setText("");
				mESSAGE_ADAPTER.text_urgency.setText("");
				mESSAGE_ADAPTER.text_wrong.setText("");
				break;
			default:
				break;
			}	
		}
		return convertView;
	}

	class MESSAGE_ADAPTER {
		private TextView text_yearAndMonth;
		private TextView text_monthAndDate;
		private TextView text_information_name;

		private TextView text_information_content;
		private TextView text_urgency;
		private TextView text_wrong;
		private TextView text_yesORno;
	}

}
