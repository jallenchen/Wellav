package com.wellav.dolphin.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.wellav.dolphin.adapter.MeetingContactAdapter;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.interfaces.IMeetingAudioControl;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.utils.Util;

@SuppressWarnings("deprecation")
public class MeetingFragment extends BaseFragment implements 
		OnClickListener,IMeetingAudioControl {
	private static final String TAG = "MeetingFragment";
	private SurfaceView mvLocal = null;
	private SurfaceView mvRemote = null;
	private SlidingDrawer SlidingDrawer;
	private RelativeLayout video_meeting_rl, layout_our;
	private TextView creatorName;
	private TextView meetingCount;
	private ImageView creatorHead;
	private ImageView myVolume;
	private ImageView myMicro;
	// private ImageButton mHandle;
	private ListView listView;
	private Button mirc_btn, exitMeeting;
	private LoadUserAvatarFromLocal mHead;

	private MeetingContactAdapter meetingMeberAdapter;
	private List<FamilyUserInfo> meetingMebers = new ArrayList<FamilyUserInfo>();
	private ArrayList<String> callingMebers = new ArrayList<String>();
	private FamilyUserInfo creator;
	private static String mCallNumber;
	private boolean myVolumeFlag = false;
	private boolean myMicroFlag = false;
	private boolean enabelAll = true;
	private static int isviter = SysConfig.MeetingCall;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meeting, container,
				false);
		SlidingDrawer = (SlidingDrawer) view.findViewById(R.id.SlidingDrawer);
		// mHandle = (ImageButton) view.findViewById(R.id.handle);
		video_meeting_rl = (RelativeLayout) view
				.findViewById(R.id.video_meeting_rl);
		myVolume = (ImageView) view.findViewById(R.id.video_volume_control_iv);
		myMicro = (ImageView) view.findViewById(R.id.video_micr__control_iv);
		creatorHead = (ImageView) view
				.findViewById(R.id.meeting_manage_head_iv);
		creatorName = (TextView) view.findViewById(R.id.meeting_manage_name_tv);
		meetingCount = (TextView) view.findViewById(R.id.meeting_member_count);
		layout_our = (RelativeLayout) view
				.findViewById(R.id.calling_layout_video_our);
		mirc_btn = (Button) view.findViewById(R.id.meeting_disable_mirc_btn);
		exitMeeting = (Button) view.findViewById(R.id.meeting_over_btn);
		listView = (ListView) SlidingDrawer
				.findViewById(R.id.meeting_right_mebers_lv);

		mHead = new LoadUserAvatarFromLocal();
		meetingMeberAdapter = new MeetingContactAdapter(getActivity());
		listView.setAdapter(meetingMeberAdapter);

		myVolume.setOnClickListener(this);
		myMicro.setOnClickListener(this);
		mirc_btn.setOnClickListener(this);
		exitMeeting.setOnClickListener(this);

		Util.PrintLog(TAG, "callType:" + isviter);
		if (isviter == SysConfig.MeetingAccept) {
			mirc_btn.setVisibility(View.GONE);
		} else {
		}

		SlidingDrawer
				.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
					public void onDrawerOpened() {
						Util.PrintLog(TAG, "onDrawerOpened");
					}

				});

		SlidingDrawer
				.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {

					public void onDrawerClosed() {
						Util.PrintLog(TAG, "onDrawerClosed");
					}

				});

		return view;
	}

	public static MeetingFragment newInstance(String name, int type) {
		MeetingFragment fragment = new MeetingFragment();
		mCallNumber = name;
		isviter = type;
		return fragment;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	

	/** 定时隐藏 */
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 5:
				creatorName.setText(creator.getNickName());

				Bitmap head = mHead.loadImage(creator.getLoginName());
				if (head != null) {
					creatorHead.setImageBitmap(head);
				} else {
					// creatorHead.setBackgroundResource(R.drawable.call_video_default_avatar);
				}
				break;
			}
		}
	};






	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.meeting_disable_mirc_btn:
			if (enabelAll == false) {
				mirc_btn.setText(R.string.forbid_talking);
				enabelAll = true;
			} else {
				mirc_btn.setText(R.string.cancel_forbid_talking);
				enabelAll = false;
			}

			break;
		case R.id.meeting_over_btn:
			getActivity().finish();
			break;
		case R.id.video_volume_control_iv:

			if (myVolumeFlag == false) {
				myVolumeFlag = true;
			} else {
				myVolumeFlag = false;
			}
			myVolume.setSelected(myVolumeFlag);
			break;
		case R.id.video_micr__control_iv:

			break;
		}
	}

	public void closeUI() {
		getFragmentManager().beginTransaction().remove(MeetingFragment.this)
				.commit();
	}


	@Override
	public void AudioControl(String loginname, boolean ischecked) {
		// TODO Auto-generated method stub
		
	}
}
