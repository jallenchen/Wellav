package com.wellav.dolphin.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.ui.ImageButtonText;
import com.wellav.dolphin.utils.Util;

public class CallingFragment extends BaseFragment implements 
		OnClickListener {
	private static final String TAG = "CallingFragment";
	private AudioManager mAudioManager;
	private RelativeLayout layout_other;
	private RelativeLayout layout_our;
	private RelativeLayout layout_mour;
	private RelativeLayout layout_chronometer;
	private Chronometer chronometer;
	private ImageButtonText mRecord;
	public boolean isRecord = false;
	private int callType;
	private String mMutilTxt="";
	private TextView calling_layout_netstatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAudioManager = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_calling, container,
				false);
		layout_other = (RelativeLayout) view
				.findViewById(R.id.calling_layout_video_other);
		layout_our = (RelativeLayout) view
				.findViewById(R.id.calling_layout_video_our);
		layout_mour = (RelativeLayout) view
				.findViewById(R.id.calling_layout_multivideo_our);
		layout_chronometer = (RelativeLayout) view
				.findViewById(R.id.Chronometer_layout);
		calling_layout_netstatus = (TextView) view.findViewById(R.id.calling_layout_netstatus);
		chronometer = (Chronometer) view.findViewById(R.id.chronometer_video);
		mRecord = (ImageButtonText) view.findViewById(R.id.btn_call_record);
		mRecord.setOnClickListener(this);

		getIntentData();
		return view;
	}

	public static CallingFragment newInstance() {
		CallingFragment Calling = new CallingFragment();

		return Calling;
	}

	private void getIntentData() {
		callType = getArguments().getInt("CallType");
		if (callType == SysConfig.ChatCall) {

		} else if (callType == SysConfig.MutilCall) {
			
		} else if (callType == SysConfig.MutilJoin) {
			callType = SysConfig.MeetingAccept;
		}
	}

	@Override
	public void onResume() {

		Util.PrintLog(TAG, "onResume()");
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * �������С�����View
	 */

	public void onRecord(boolean isCheck) {
	}


	/**
	 * ͨ����״̬ ¼��ʱ��
	 */

	private void setChronometer() {
		layout_chronometer.setVisibility(View.VISIBLE);
		chronometer.setBase(SystemClock.elapsedRealtime());
		chronometer.setFormat("%s");
		chronometer
				.setOnChronometerTickListener(new OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer ch) {
					}
				});
		chronometer.start();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_call_record:
			onRecord(isRecord);
			break;
		}

	}

}
