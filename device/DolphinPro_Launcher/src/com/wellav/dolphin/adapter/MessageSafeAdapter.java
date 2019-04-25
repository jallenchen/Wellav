package com.wellav.dolphin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInformSafe;
import com.wellav.dolphin.launcher.R;

public class MessageSafeAdapter extends BaseAdapter {

	private List<MessageInformSafe> messageInfoSafes;
	private MessageInformSafe messageInfoSafe;
	private LayoutInflater inflater;
	private Context mContext;

	/**
	 * @param context
	 * @param list
	 * @author xiaowenzi
	 */
	public MessageSafeAdapter(Context context, List<MessageInformSafe> messageInfoSafes) {
		mContext = context;
		this.messageInfoSafes = messageInfoSafes;
		inflater = LayoutInflater.from(mContext);
	}

	public int getCount() {
		return messageInfoSafes.size();
	}

	public Object getItem(int position) {
		return messageInfoSafes.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}
	
	
	public void refresh(List<MessageInformSafe> messageInfos) {
		this.messageInfoSafes = messageInfos;
		notifyDataSetChanged();
	}

	 

	public View getView(int position, View convertView, ViewGroup parent) {

		MESSAGE_ADAPTER mESSAGE_ADAPTER = null;
		messageInfoSafe = messageInfoSafes.get(position);
		
		if (convertView == null) {
			mESSAGE_ADAPTER = new MESSAGE_ADAPTER();
			convertView = inflater.inflate(R.layout.item_message_safe, null);
			// 年月
			mESSAGE_ADAPTER.text_yearAndMonth = (TextView) convertView
					.findViewById(R.id.txt_date_yearAndMonth_safe);
			// 月日，时分
			mESSAGE_ADAPTER.text_monthAndDate = (TextView) convertView
					.findViewById(R.id.txt_date_monthAndDate_safe);
			// 名称
			mESSAGE_ADAPTER.text_information_name = (TextView) convertView
					.findViewById(R.id.txt_information_name_safe);
			// 内容
			mESSAGE_ADAPTER.text_information_content = (TextView) convertView
					.findViewById(R.id.txt_information_content_safe);
			// 紧急呼叫
			mESSAGE_ADAPTER.text_urgency = (TextView) convertView
					.findViewById(R.id.text_urgency_safe);
			// 异常照片视频
			mESSAGE_ADAPTER.text_wrong = (TextView) convertView
					.findViewById(R.id.text_wrong_safe);
			// 是否应答
			mESSAGE_ADAPTER.text_yesORno = (TextView) convertView
					.findViewById(R.id.txt_yesORno_safe);

			convertView.setTag(mESSAGE_ADAPTER);
		} else {
			mESSAGE_ADAPTER = (MESSAGE_ADAPTER) convertView.getTag();
		}
		if (messageInfoSafe.getEventType() != null) {
			
			//第一条数据，肯定显示时间标题
            if (position == 0) {
            	mESSAGE_ADAPTER.text_yearAndMonth.setVisibility(View.VISIBLE);
            	mESSAGE_ADAPTER.text_yearAndMonth.setText(messageInfoSafes.get(0).getYearAndMonth());
            } else { 
            	// 不是第一条数据 // 本条数据和上一条数据的时间戳相同，时间标题不显示
                if (messageInfoSafes.get(position).getYearAndMonth().equals(messageInfoSafes.get(position - 1).getYearAndMonth())) {
                    mESSAGE_ADAPTER.text_yearAndMonth.setVisibility(View.INVISIBLE);
                            
                } else {
                    //本条数据和上一条的数据的时间戳不同的时候，显示数据
                    mESSAGE_ADAPTER.text_yearAndMonth.setVisibility(View.VISIBLE);
                    mESSAGE_ADAPTER.text_yearAndMonth.setText(messageInfoSafe.getYearAndMonth());
                }
            }
			
			int i = Integer.parseInt(messageInfoSafe.getEventType());
			switch (i) {
			case MessageEventType.WRONG_PIC:

				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfoSafe
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfoSafe
						.getName());
				// 设置内容
				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.wrong_pic_do));
				
				// 设置几张异常图片，数量
//				mESSAGE_ADAPTER.text_urgency.setText(messageInfoSafe
//						.getNum());
				
				mESSAGE_ADAPTER.text_wrong
						.setText(mContext.getResources().getString(R.string.wrong_pic));
				
				mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.but_check));
				break;

			case MessageEventType.WRONG_VIDEO:
				
				// 设置时间,月日时分
				mESSAGE_ADAPTER.text_monthAndDate.setText(messageInfoSafe
						.getMonthAndDay());
				// 设置姓名
				mESSAGE_ADAPTER.text_information_name.setText(messageInfoSafe
						.getName());

				mESSAGE_ADAPTER.text_information_content
						.setText(mContext.getResources().getString(R.string.wrong_video_do));
				
				// 设置几张异常图片，数量
				mESSAGE_ADAPTER.text_urgency.setText(messageInfoSafe
						.getNum());
				
				mESSAGE_ADAPTER.text_wrong
						.setText(mContext.getResources().getString(R.string.wrong_video));
				
				mESSAGE_ADAPTER.text_yesORno.setText(mContext.getResources().getString(R.string.but_check));
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
		private TextView text_yesORno;
		private TextView text_urgency;
		private TextView text_wrong;
	}

}
