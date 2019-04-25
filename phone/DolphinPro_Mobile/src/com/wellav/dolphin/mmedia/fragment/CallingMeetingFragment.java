//package com.wellav.dolphin.mmedia.fragment;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//import jni.util.Utils;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import rtc.sdk.common.RtcConst;
//import rtc.sdk.iface.Connection;
//import rtc.sdk.iface.GroupMgr;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.media.AudioManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.SlidingDrawer;
//import android.widget.TextView;
//
//import com.wellav.dolphin.application.DolphinApp;
//import com.wellav.dolphin.mmedia.R;
//import com.wellav.dolphin.mmedia.adapter.MeetingContactAdapter;
//import com.wellav.dolphin.mmedia.db.SQLiteManager;
//import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
//import com.wellav.dolphin.mmedia.interfaces.ICallingVideo;
//import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
//import com.wellav.dolphin.mmedia.utils.CommFunc;
//
//public class CallingMeetingFragment extends BaseFragment implements ICallingVideo,OnClickListener{
//	public final static String BROADCAST_MEETINGCALLING_ACTION = "com.wellav.meetingcalling";
//	private static final String TAG = "CallingMeetingFragment";
//	//private DrawerLayout meetingDl;
//	private RelativeLayout video_meeting_rl;
//	private RelativeLayout layout_volume, layout_our;
//	private AudioManager mAudioManager;// 系统声音控制
//	private ImageButton menuOpen,menuClose;
//	SlidingDrawer SlidingDrawer;
//	private TextView creatorName;
//	private ImageView creatorHead;
//	private ImageView myVolume;
//	private ImageView myMicro;
//	private List<FamilyUserInfo> meetingMebers;
//	private FamilyUserInfo creator;
//	private MeetingContactAdapter meetingMeberAdapter;
//	private Button exitMeeting;
//	private ListView listView;
//	public static boolean micOff = false;
//	boolean myVolumeFlag = false;
//	boolean myMicroFlag = false;
//	private SurfaceView callerLocal; 
//	private SurfaceView callerRemote;
//	private Connection call ;
//	private CallingMeetingFragment ctx ;
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public View onCreateView(LayoutInflater inflater,
//			 ViewGroup container, Bundle savedInstanceState) {
//		this.ctx = this;
//		
//		View view = inflater.inflate(R.layout.activity_meeting, container, false);
//		video_meeting_rl = (RelativeLayout) view.findViewById(R.id.video_meeting_rl);
//		SlidingDrawer = (SlidingDrawer) view.findViewById(R.id.SlidingDrawer);
//		layout_volume = (RelativeLayout) view.findViewById(R.id.video_volume_layout);
//		myVolume = (ImageView) view.findViewById(R.id.video_volume_control_iv);
//		myMicro = (ImageView) view.findViewById(R.id.video_micr__control_iv);
//		creatorHead = (ImageView) view.findViewById(R.id.meeting_manage_head_iv);
//		creatorName = (TextView) view.findViewById(R.id.meeting_manage_name_tv);
//		layout_our = (RelativeLayout) view.findViewById(R.id.calling_layout_video_our);
//		exitMeeting = (Button) view.findViewById(R.id.meeting_over_btn);
//		menuOpen = (ImageButton) view.findViewById(R.id.handle);
//		listView = (ListView) SlidingDrawer.findViewById(R.id.meeting_right_mebers_lv);
//		
//		meetingMeberAdapter = new MeetingContactAdapter(getActivity(), meetingMebers);
//        listView.setAdapter(meetingMeberAdapter);
//        OnOpt_getMemberlist();
//        
//        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
//        
//        myVolume.setOnClickListener(this);
//		myMicro.setOnClickListener(this);
//		exitMeeting.setOnClickListener(this);
//		//menuOpen.setOnClickListener(this);
//		//menuClose.setOnClickListener(this);
//		SlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener()
//		{
//			public void onDrawerOpened() {
//				CommFunc.PrintLog(TAG, "onDrawerOpened");
//				layout_volume.setVisibility(View.VISIBLE);
//			}
//
//		});
//
//		SlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener(){
//
//			public void onDrawerClosed() {
//				CommFunc.PrintLog(TAG, "onDrawerClosed");
//			}
//
//		});
//		
//		return view;
//	}
//	
//	@SuppressWarnings("deprecation")
//	private void setViewVisible(int visible){
//		if(layout_volume.getVisibility() != visible){
//			layout_volume.setVisibility(visible);
//		}
//		
//		if(visible == View.VISIBLE && SlidingDrawer.isOpened() == false){
//			SlidingDrawer.open();
//		}else if(visible == View.INVISIBLE && SlidingDrawer.isOpened() == true){
//			SlidingDrawer.close();
//		}
//		
//		
//	}
//
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		SlidingDrawer.destroyDrawingCache();
//		super.onDestroy();
//	}
//
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch(v.getId()){
//		case R.id.meeting_over_btn:
//			DolphinApp.getInstance().onConfCallHangup();
//			getActivity().finish();
//			break;
//		case R.id.video_volume_control_iv:
//			if(myVolumeFlag ==false){
//				DolphinApp.getInstance().getRtcClient().enableSpeaker(mAudioManager, false);
//				myVolumeFlag = true;
//			}else{
//				DolphinApp.getInstance().getRtcClient().enableSpeaker(mAudioManager, true);
//				myVolumeFlag = false;
//			}
//			myVolume.setSelected(myVolumeFlag);
//			
//			break;
//		case R.id.video_micr__control_iv:
//			if(myMicroFlag ==true && DolphinApp.getInstance().getGroupCall() != null){
//				DolphinApp.getInstance().getGroupCall().setMuted(true);
//				myMicroFlag = false;
//			}else if(myMicroFlag ==false && DolphinApp.getInstance().getGroupCall() != null){
//				DolphinApp.getInstance().getGroupCall().setMuted(false);
//				myMicroFlag = true;
//			}
//			myMicro.setSelected(myMicroFlag);
//			break;
//		}
//	}
//	/** 定时隐藏 */
//	 @SuppressLint("HandlerLeak") Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			//int value = msg.arg1;
//			switch(msg.what){
//			case 5:
//				creatorName.setText(creator.getNickName());
//				
//				LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
//				Bitmap head = task.loadImage(creator.getLoginName());
//				if(head!=null){
//					creatorHead.setImageBitmap(head);
//				}else{
//					creatorHead.setBackgroundResource(R.drawable.ic_defaulthead_home_48dp);	
//				}
//				break;
//			
//			}
//			Log.e(TAG, "mHandler:"+msg.what);
//			
//		}
//	};
//	
//	 /**
//     * 配置SurfaceView 配置主叫视频与被叫视频的入口 TODO 因为现在无视频流，所以双方现在只显示本地摄像头采集的视频
//     */
//    private void initSurfaceView() {
//    	  call = DolphinApp.getInstance().getGroupCall();
//        if( call!=null)
//        {
//           
//                if (callerLocal==null) {
//                    callerLocal = (SurfaceView)call.createVideoView(true, getActivity(),true);
//                    callerLocal.setVisibility(View.VISIBLE);
//                    layout_our.removeAllViews();
//                    layout_our.addView(callerLocal);
//                }
//                if (callerRemote==null) {
//                    callerRemote = (SurfaceView)call.createVideoView(false, getActivity(),true);
//                    callerRemote.setVisibility(View.VISIBLE);
//                    video_meeting_rl.removeAllViews();
//                    video_meeting_rl.addView(callerRemote);
//                }
//                call.buildVideo(callerRemote);
//            }
//
//    }
//	
//	private void OnParserGetMemberList(JSONObject response) {
//        CommFunc.PrintLog(5, TAG, "OnParserGetMemberList:");
//        JSONObject jsonrsp = response;
//        JSONArray jsonArr;
//         List<FamilyUserInfo> mMebers = new  ArrayList<FamilyUserInfo>();
//        try {
//            jsonArr = jsonrsp.getJSONArray("memberInfoList");
//            StringBuffer members = new StringBuffer();
//            for (int i = 0; i < jsonArr.length(); i++) 
//            {  
//                JSONObject itemObject = jsonArr.getJSONObject(i);
//                String userid = itemObject.getString("appAccountID");
//                int memberstatus = itemObject.getInt("memberStatus");      //2代表已加入 3代表未加入  4代表被删除出
//                FamilyUserInfo userinfo =  SQLiteManager.getInstance().geFamilyUserInfoLoginName(userid);
//                userinfo.setAudioState(itemObject.getInt("upAudioState"));
//                userinfo.setState(memberstatus);
//                
//                
//                if(itemObject.getInt("role")==1)
//                {
//                	creator = userinfo;
//                	mHandler.sendEmptyMessage(5);
//                    {   
//                        CommFunc.PrintLog(5, TAG, "OnParserGetMemberList setGroupCreator:"+userid);                      
//                   
//                    }
//                    if(userid.equals(DolphinApp.getInstance().getAccountInfo().getUserId())==false) //创建者不是自己在成员中加入
//                        members.append(userid).append(";"); //添加成员，多个成员用";"隔开
//                        
//                } else {
//                    if(memberstatus == 2){
//                    	mMebers.add(userinfo);
//                      }
//                }
//            }
//            members = members.deleteCharAt(members.lastIndexOf(";"));
//            CommFunc.PrintLog(5, TAG, "OnParserGetMemberList members:"+members);
//    		meetingMeberAdapter.refresh(mMebers);
//
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//	
//	
//	  public void OnOpt_getMemberlist() // 获取成员列表
//	    {
//	        CommFunc.PrintLog(5, TAG, "OnOpt_getMemberlist:");
//	        GroupMgr grpmgr = DolphinApp.getInstance().getGrpMgr();
//
//	        if (grpmgr == null)
//	            return;
//
//	        grpmgr.groupCall(RtcConst.groupcall_opt_getmemberlist, null);
//	    }
//
//	  private void onResponse_grpgetMemberList(String parameters) {
//	        CommFunc.PrintLog(5, TAG, "onResponse_grpvgetMemberLis resopnse:"
//	                + parameters);
//	        try {
//	            if (parameters == null || parameters.equals("")) {
//	                Utils.PrintLog(5, TAG,
//	                "onResponse_grpvgetMemberLis fail result: null");
//	                return;
//	            }
//	            JSONObject jsonrsp = new JSONObject(parameters);
//	            String code = jsonrsp.getString("code");
//	            String reason = jsonrsp.getString("reason");
//	            Utils.PrintLog(5, TAG, "onResponse_grpvgetMemberLis code:"
//	                    + code + " reason:" + reason);
//	            if (code.equals("0")) {
//	                OnParserGetMemberList(jsonrsp);
//	            } else {
//	                CommFunc.PrintLog(5, TAG, ctx.getResources().getString(R.string.get_members_fail)+code+" reason:"+reason);
//	            }
//	        } catch (JSONException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//
//	    }
//	@Override
//	public void opt_Request(String parm) {
//		// TODO Auto-generated method stub
//		OnOpt_getMemberlist();
//	}
//
//
//	@Override
//	public void opt_Response(int action, String parm) {
//		// TODO Auto-generated method stub
//		if(action == RtcConst.groupcall_opt_getmemberlist){
//			onResponse_grpgetMemberList(parm);
//		}
//		
//	}
//
//
//	@Override
//	public void ViewAction(int type) {
//		// TODO Auto-generated method stub
//		initSurfaceView();
//	}
//	public void closeUI(){
//		getFragmentManager().beginTransaction().remove(CallingMeetingFragment.this).commit();
//	}
//
//
//	@Override
//	public void setViewUIVisible(int visible) {
//		// TODO Auto-generated method stub
//		CommFunc.PrintLog(TAG, "setViewUIVisible:"+visible);
//		setViewVisible(visible);
//	}
//}
