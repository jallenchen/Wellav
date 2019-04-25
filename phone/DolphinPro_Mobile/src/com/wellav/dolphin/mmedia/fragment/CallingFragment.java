package com.wellav.dolphin.mmedia.fragment;

import java.sql.Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.interfaces.ICallingVideo;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.NameUtils;

//public class CallingFragment extends BaseFragment  implements ICallingVideo{
//	private static final String TAG = "CallingFragment";
//	private AudioManager mAudioManager;
//	private RelativeLayout layout_other;
//	private RelativeLayout layout_our;
//	private RelativeLayout layout_mour;
//	private RelativeLayout layout_chronometer;
//	private LinearLayout layout_function;
//	private RelativeLayout layout_dirct;
//	private RelativeLayout layout_hangup;
//	private Chronometer chronometer;
//	private SurfaceView mvLocal = null;
//	private SurfaceView mvRemote = null;
//	private Connection call;
//	private int callType;
//	private CallingFragment ctx;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mAudioManager = (AudioManager) getActivity().getSystemService(
//				Context.AUDIO_SERVICE);
//		this.ctx = this;
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater,
//			ViewGroup container, Bundle savedInstanceState) {
//
//		View view = inflater.inflate(R.layout.activity_calling, container,
//				false);
//		layout_other = (RelativeLayout) view
//				.findViewById(R.id.calling_layout_video_other);
//		layout_our = (RelativeLayout) view
//				.findViewById(R.id.calling_layout_video_our);
//		layout_mour = (RelativeLayout) view
//				.findViewById(R.id.calling_layout_multivideo_our);
//		layout_chronometer = (RelativeLayout) view
//				.findViewById(R.id.chronometer_layout);
//		layout_function = (LinearLayout) view.findViewById(R.id.calling_layout_video_function);
//		
//		layout_dirct = (RelativeLayout) view
//				.findViewById(R.id.video_dir_layout);
//
//		chronometer = (Chronometer) view.findViewById(R.id.chronometer_video);
//		layout_hangup = (RelativeLayout) view
//				.findViewById(R.id.layout_calling_video_hangup);
//		
//		getIntentData();
//		return view;
//	}
//	
//	private void setUIVisible(int visible){
//		layout_function.setVisibility(visible);
//		layout_dirct.setVisibility(visible);
//		layout_hangup.setVisibility(visible);
//	}
//
//	public static CallingFragment newInstance() {
//		CallingFragment Calling = new CallingFragment();
//
//		return Calling;
//	}
//
//	private void getIntentData() {
//		callType = getArguments().getInt("CallType");
//		if (callType == SysConfig.ChatCall) {
//
//		} else if (callType == SysConfig.MutilCall) {
//			createMultiCall();
//		} else if (callType == SysConfig.MutilJoin) {
//			DolphinApp.getInstance().onConfCallAccept();
//		}
//	}
//
//	@Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		if (call != null) {
//			call.resetVideoViews();
//		}
//
//		CommFunc.PrintLog(TAG, "onResume()");
//		super.onResume();
//	}
//	
//
//	@Override
//	public void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//	}
//
//	@Override
//	public void onStop() {
//		// TODO Auto-generated method stub
//		super.onStop();
//	}
//
//	/**
//	 * �������С�����View
//	 */
//	public void createMultiCall() {
//		DolphinApp.getInstance().onAllCallHangup();
//		call = null;
//		mvLocal = null;
//		mvRemote = null;
//
//		HBaseApp.post2WorkRunnable(new Runnable() {
//			@Override
//			public void run() {
//				JSONObject jsonObj = new JSONObject();
//				try {
//					jsonObj.put(RtcConst.kgvctype,
//							RtcConst.grouptype_multigrpchatAV); // yes
//					jsonObj.put(RtcConst.kgvcname, SysConfig.uid + ":" + ctx.getResources().getString(R.string.group_chat)); // yes
//					jsonObj.put(RtcConst.kgvcinvitedList,
//							NameUtils.getRemoteName());
//					jsonObj.put(RtcConst.kgvcpassword, "kong");
//					jsonObj.put("codec", 1);
//					/**
//					 * jsonObj.put("screenSplit", 0); 0, �ɺ�ָ̨�� 1, 1x1 2, 1*2
//					 * ��Ҫƽ̨����������Ч 3, 2x2 4, 2x3 5, 3*3 ��Ҫƽ̨����������Ч
//					 * 
//					 * groupCall����RtcConst.groupcall_opt_mdisp
//					 */
//					/**
//					 * jsonObj.put("screenSplit", 0); 0, 由后台指定 1, 1x1 2, 1*2
//					 * 需要平台单独配置生效 3, 2x2 4, 2x3 5, 3*3 需要平台单独配置生效
//					 * 
//					 * groupCall操作RtcConst.groupcall_opt_mdisp
//					 */
//					if (NameUtils.remoteNamesCount() <= 0) {
//						NameUtils.remoteNamesClean();
//					} else if (NameUtils.remoteNamesCount() > 0
//							&& NameUtils.remoteNamesCount() <= 3) {
//						jsonObj.put("screenSplit", 3);
//					} else if (NameUtils.remoteNamesCount() > 3
//							&& NameUtils.remoteNamesCount() <= 5) {
//						jsonObj.put("screenSplit", 4);
//					} else if (NameUtils.remoteNamesCount() > 5) {
//						jsonObj.put("screenSplit", 5);
//					}
//					CommFunc.PrintLog(TAG,
//							"createGroupCallJson:" + jsonObj.toString());
//
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				DolphinApp.getInstance().CreateConf(jsonObj.toString());
//			}
//		});
//	}
//
//	/**
//	 * ͨ����״̬ ¼��ʱ��
//	 */
//
//	public void setChronometer(boolean isRecording) {
//		if(isRecording){
//			layout_chronometer.setVisibility(View.VISIBLE);
//			chronometer.setBase(SystemClock.elapsedRealtime());
//			chronometer.setFormat("%s");
//			chronometer
//					.setOnChronometerTickListener(new OnChronometerTickListener() {
//						@Override
//						public void onChronometerTick(Chronometer ch) {
//							// ����ӿ�ʼ��ʱ�����ڳ�����20s��
//							// callDuration = SystemClock.elapsedRealtime()
//							// - ch.getBase();
//						}
//					});
//			chronometer.start();
//		}else{
//			if (chronometer != null) {
//				chronometer.stop();
//				layout_chronometer.setVisibility(View.INVISIBLE);
//			}
//		}
//	}
//
//	/**
//     * 
//     */
//	private void initChatVideoView() {
//		if (mvLocal != null) {
//			return;
//		}
//		call = DolphinApp.getInstance().getMCall();
//
//		if (call != null) {
//			mvLocal = (SurfaceView) call.createVideoView(true, getActivity(),
//					true);
//			mvLocal.setVisibility(View.INVISIBLE);
//			layout_our.addView(mvLocal);
//			mvLocal.setZOrderMediaOverlay(true);
//			mvLocal.setZOrderOnTop(true);
//
//			mvRemote = (SurfaceView) call.createVideoView(false, getActivity(),
//					true);
//			mvRemote.setVisibility(View.INVISIBLE);
//			layout_other.removeAllViews();
//			layout_other.addView(mvRemote);
//
//			call.buildVideo(mvRemote);
//			call.setCameraAngle(0);// 0:0 1:90 2:180 3:270
//			DolphinApp.getInstance().getRtcClient()
//					.enableSpeaker(mAudioManager, true);
//		}
//	}
//
//	private void initMultiVideoView() {
//		call = DolphinApp.getInstance().getGroupCall();
//		if (call != null) {
//			
//			if (mvLocal == null) {
//				mvLocal = (SurfaceView) call.createVideoView(true,
//						getActivity(), true);
//				mvLocal.setVisibility(View.INVISIBLE);
//				layout_mour.removeAllViews();
//				layout_mour.addView(mvLocal);
//				mvLocal.setZOrderMediaOverlay(true);
//				mvLocal.setZOrderOnTop(true);
//			}
//			if (mvRemote == null) {
//				mvRemote = (SurfaceView) call.createVideoView(false,
//						getActivity(), true);
//				mvRemote.setVisibility(View.INVISIBLE);
//				layout_other.removeAllViews();
//				layout_other.addView(mvRemote);
//			}
//			call.buildVideo(mvRemote);
//			call.setCameraAngle(0);// 0:0 1:90 2:180 3:270
//			DolphinApp.getInstance().getRtcClient()
//					.enableSpeaker(mAudioManager, true);
//		}
//
//	}
//
//	/**
//	 * 
//	 * @param visible
//	 */
//	void setVideoSurfaceVisibility(int visible) {
//		CommFunc.PrintLog(TAG, "mvLocal:" + mvLocal);
//		if (mvLocal == null) {
//			return;
//		}
//		mvLocal.setVisibility(visible);
//		mvRemote.setVisibility(visible);
//	}
//
//
//	@Override
//	public void opt_Request(String parm) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void opt_Response(int action, String parm) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void ViewAction(int type) {
//		// TODO Auto-generated method stub
//		CommFunc.PrintLog(TAG, "onViewAction:" + type);
//		if (type == SysConfig.ChatCall || type == SysConfig.ChatAccept) {
//			layout_our.setVisibility(View.VISIBLE);
//			initChatVideoView();
//		} else {
//			layout_our.setVisibility(View.GONE);
//			initMultiVideoView();
//		}
//		SysConfig.getInstance().setCalling(true);
//		setVideoSurfaceVisibility(View.VISIBLE);
//	}
//
//	public void ParseNotifyInfo(String infos) {
//		try {
//			JSONArray mebmberArray = new JSONArray(infos);
//			for (int i = 0; i < mebmberArray.length(); i++) {
//				JSONObject itemObject = mebmberArray.getJSONObject(i);
//				String userid = itemObject.getString("appAccountID");
//
//				if (itemObject.has("memberStatus")) {
//					int status = itemObject.getInt("memberStatus");
//					if (status == 2) {
//						NameUtils.remoteNamesAdd(userid);
//					} else {
//						NameUtils.remoteNamesRemove(userid);
//					}
//				}
//			}
//			CommFunc.PrintLog(TAG, NameUtils.getRemoteName());
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//
//	public void onOpt_InvitedMebmer(String remoteuri) {
//		CommFunc.PrintLog(5,TAG, "onOpt_InvitedMebmer:" + remoteuri);
//		GroupMgr grpmgr = DolphinApp.getInstance().getGrpMgr();
//		if (grpmgr == null)
//			return;
//
//		grpmgr.groupCall(RtcConst.groupcall_opt_invitedmemberlist, remoteuri); // 多人列表取被叫
//																				// 逗号
//																				// 间隔
//	}
//
//	public void onOpt_Mdisp() {
//		GroupMgr grpmgr = DolphinApp.getInstance().getGrpMgr();
//		if (grpmgr == null)
//			return;
//		JSONObject jsonObj = new JSONObject();
//		try {
//			// 分屏数设置，该参数也可以在刚开始创建多人视频时就指定
//			// 0：由后台指定
//			// 1：1*1
//			// 2：1*2 需要平台单独配置生效
//			// 3：2*2
//			// 4：2*3
//			// 5：3*3 需要平台单独配置生效
//			CommFunc.PrintLog(5,TAG, ctx.getResources().getString(R.string.far_end_people)  +NameUtils.remoteNamesCount());
//			if (NameUtils.remoteNamesCount() <= 0) {
//				NameUtils.remoteNamesClean();
//				SysConfig.getInstance().setStatus(MsgKey.DOLPHINSTATE_FREE);
//			} else if (NameUtils.remoteNamesCount() > 0
//					&& NameUtils.remoteNamesCount() <= 3) {
//				jsonObj.put("screenSplit", 3);
//			} else if (NameUtils.remoteNamesCount() > 3
//					&& NameUtils.remoteNamesCount() <= 5) {
//				jsonObj.put("screenSplit", 4);
//			} else if (NameUtils.remoteNamesCount() > 5) {
//				jsonObj.put("screenSplit", 5);
//			}
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		grpmgr.groupCall(RtcConst.groupcall_opt_mdisp, jsonObj.toString());
//	}
//
//	@Override
//	public void setViewUIVisible(int visible) {
//		// TODO Auto-generated method stub
//		CommFunc.PrintLog(TAG," setViewUIVisible:"+visible);
//		if(layout_function.getVisibility() != visible){
//			setUIVisible(visible);
//		}
//	}
//}
