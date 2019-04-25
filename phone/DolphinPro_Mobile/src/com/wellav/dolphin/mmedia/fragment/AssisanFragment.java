package com.wellav.dolphin.mmedia.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.SetWatchClockActivity;
import com.wellav.dolphin.mmedia.commands.DolphinImMime;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.AssistantSetting;
import com.wellav.dolphin.mmedia.entity.ClockInfo;
import com.wellav.dolphin.mmedia.entity.ClockInfoImpl;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.net.LoadAssistantSetting;
import com.wellav.dolphin.mmedia.net.LoadAssistantSetting.assistantInfoDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.DateUtils;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class AssisanFragment extends BaseFragment implements OnClickListener{
	private ImageView mActionBarPrev;
	private TextView mActionBarName;
	private CircleImageView mIcon;
	private TextView mNoteName;
	private TextView mNikeName;
	private TextView mContent;
	private TextView mContentTime;
	private TextView mContentDay;
	private RelativeLayout mLayout;
	private RelativeLayout mDefineTimeLayout;
	private RelativeLayout mWorkdayLayout;
	private LinearLayout mEnableLayout;
	private ToggleButton mTogg1,mTogg2;
	private FamilyInfo mInfo;
	private AssisanFragment ctx;
	private ClockInfo mClockInfo;
	private View view1,view2;
	private int dayItem=0;

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		this.ctx = this;
		
		View view = inflater.inflate(R.layout.fragment_assistant, container, false);
		mActionBarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mActionBarName = (TextView) view.findViewById(R.id.actionbar_name);
		mIcon = (CircleImageView) view.findViewById(R.id.info_icon);
		mNoteName = (TextView) view.findViewById(R.id.info_notename);
		mNikeName = (TextView) view.findViewById(R.id.info_nickname);
		mContent = (TextView) view.findViewById(R.id.assis_content);
		mContentTime = (TextView) view.findViewById(R.id.content_time);
		mContentDay = (TextView) view.findViewById(R.id.content_day);
		mTogg1 = (ToggleButton) view.findViewById(R.id.assis_enable_tb);
		mTogg2 = (ToggleButton) view.findViewById(R.id.assis_workdate_tb);
		mLayout = (RelativeLayout) view.findViewById(R.id.moment_layout);
		mEnableLayout = (LinearLayout) view.findViewById(R.id.enable_layout);
		mWorkdayLayout = (RelativeLayout) view.findViewById(R.id.workday_layout);
		view1 = view.findViewById(R.id.view2);
		view2 = view.findViewById(R.id.view3);
		mDefineTimeLayout = (RelativeLayout) view.findViewById(R.id.mytime_layout);
		int pos = getArguments().getInt("Pos", 0);
		mInfo = DolphinApp.getInstance().getMyFamilyInfos().get(pos);
		
		String note = TextUtils.isEmpty(mInfo.getNote()) ? mInfo.getNickName() : mInfo.getNote();
		mNoteName.setText(note);
		mNikeName.setText(ctx.getResources().getString(R.string.nickName)+mInfo.getNickName());
		mActionBarName.setText(ctx.getResources().getString(R.string.look_home_assistant));
		
		LoadUserAvatarFromLocal tast = new LoadUserAvatarFromLocal();
		Bitmap head = tast.loadImage(mInfo.getDeviceID());
		if(head != null){
			mIcon.setImageBitmap(head);
		}
		getAssistantSetting();
		mActionBarPrev.setOnClickListener(this);
		mTogg1.setOnClickListener(this);
		mTogg2.setOnClickListener(this);
		mLayout.setOnClickListener(this);
		mDefineTimeLayout.setOnClickListener(this);
		return view;
	}
	
	private void getAssistantSetting(){
		String body = RequestString.getAssitant(SysConfig.dtoken, mInfo.getFamilyID());
		LoadAssistantSetting assis = new LoadAssistantSetting(body);
		assis.getAssistantData(new assistantInfoDataCallBack() {
			
			@Override
			public void onDataCallBack(int code, AssistantSetting data) {
				// TODO Auto-generated method stub
				if(data.getIsHousekeeping() == 1){
					mEnableLayout.setVisibility(View.VISIBLE);
				}else{
					mEnableLayout.setVisibility(View.GONE);
				}
				mClockInfo = data.getCustomPeriod();	
				mTogg1.setChecked(data.getIsHousekeeping() == 1);
				mTogg2.setChecked(data.getIsOnlyWorkingDay() == 1);
				dayItem =data.getHousekeepingPeriod();
				mContent.setText(ClockInfoImpl.daytime_str[data.getHousekeepingPeriod()]);
				if(data.getHousekeepingPeriod() == 2){
					mDefineTimeLayout.setVisibility(View.VISIBLE);
					view2.setVisibility(View.VISIBLE);
					mWorkdayLayout.setVisibility(View.GONE);
					view1.setVisibility(View.GONE);
					mContentTime.setText(mClockInfo.getmStartTime()+"  -  "+ mClockInfo.getmEndTime());
					mContentDay.setText(DateUtils.getWeekdays(mClockInfo.getmDays()));
				}
				
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	 public static AssisanFragment newInstance(int index) {
		 AssisanFragment fragment = new AssisanFragment();
	        Bundle args = new Bundle();
	        args.putInt("Pos", index);
	        fragment.setArguments(args);
	        return fragment;
	    }
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(data != null){
			mClockInfo = (ClockInfo) data.getSerializableExtra("ClockInfo");
			mContentTime.setText( mClockInfo.getmStartTime()+"  -  "+ mClockInfo.getmEndTime());
			mContentDay.setText(DateUtils.getWeekdays(mClockInfo.getmDays()));
			//DolphinApp.getInstance().makeSendIm(mInfo.getDeviceID(),  DolphinImMime.DplMime_settingResult_json, JsonUtil.getAssistantCustomJson(mClockInfo));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		case R.id.assis_enable_tb:
			ToggleButton view1 = (ToggleButton) v;
			int isCheck1 = view1.isChecked()? 1:0 ;
			if(isCheck1 == 1){
				mEnableLayout.setVisibility(View.VISIBLE);
			}else{
				mEnableLayout.setVisibility(View.GONE);
			}
		//	DolphinApp.getInstance().makeSendIm(mInfo.getDeviceID(),  DolphinImMime.DplMime_settingResult_json, JsonUtil.getAssistantEnableJson(isCheck1));
			
			break;
		case R.id.assis_workdate_tb:
			ToggleButton view2 = (ToggleButton) v;
			int isCheck2 = view2.isChecked()? 1:0 ;
			//DolphinApp.getInstance().makeSendIm(mInfo.getDeviceID(),  DolphinImMime.DplMime_settingResult_json, JsonUtil.getAssistantWorkdayJson(isCheck2));
			break;
		case R.id.moment_layout:
			showDialog();
			break;
		case R.id.mytime_layout:
			Intent in = new Intent(getActivity(),SetWatchClockActivity.class);
			in.putExtra("clockInfo", mClockInfo);
			startActivityForResult(in, 0);
			break;
		}
		
	}
	
	private void showDialog() {
		new AlertDialog.Builder(getActivity())
		.setIcon(android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(ClockInfoImpl.daytime_str, dayItem,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mContent.setText(ClockInfoImpl.daytime_str[which]);
						if(which != 2){
							mDefineTimeLayout.setVisibility(View.GONE);
							view2.setVisibility(View.GONE);
							mWorkdayLayout.setVisibility(View.VISIBLE);
							view1.setVisibility(View.VISIBLE);
						}else{
							mDefineTimeLayout.setVisibility(View.VISIBLE);
							view2.setVisibility(View.VISIBLE);
							mWorkdayLayout.setVisibility(View.GONE);
							view1.setVisibility(View.GONE);
							mContentTime.setText( mClockInfo.getmStartTime()+"  -  "+ mClockInfo.getmEndTime());
							mContentDay.setText(DateUtils.getWeekdays( mClockInfo.getmDays()));
						}
					//	DolphinApp.getInstance().makeSendIm(mInfo.getDeviceID(),  DolphinImMime.DplMime_settingResult_json, JsonUtil.getAssistantPeriodJson(which));
						dialog.dismiss();
					}
			
		}).show();
	}
	

}
