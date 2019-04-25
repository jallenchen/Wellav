package com.wellav.dolphin.mmedia.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.AdminInfo;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.InviteMessage;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.net.LoadDeviceAdmin;
import com.wellav.dolphin.mmedia.net.LoadDeviceAdmin.deviceAdminDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class SearchResultInfoFragment extends BaseFragment implements OnClickListener{
	private UserInfo fUerInfo;
	private AdminInfo adminInfo;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private RelativeLayout mAgeLayout;
	private CircleImageView mHeadIcon;
	private TextView mNotename;
	private TextView mNickname;
	private ImageView mSex;
	private ImageView mType;
	private TextView mModifyNotename;
	private TextView mCity;
	private TextView mLinkInfo;
	private TextView mAge;
	private View view1;
	private Button mJoin;
	private FamilyInfo mFamilyInfo;
	private boolean isLinkDevice;
	private SearchResultInfoFragment ctx;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		this.ctx = this;
		View view = inflater.inflate(R.layout.fragment_result_info, container, false);
	
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mHeadIcon = (CircleImageView) view.findViewById(R.id.info_icon);
		mSex = (ImageView) view.findViewById(R.id.info_sex);
		mType= (ImageView) view.findViewById(R.id.info_type);
		mNotename = (TextView) view.findViewById(R.id.info_notename);
		mNickname = (TextView) view.findViewById(R.id.info_nickname);
		mLinkInfo = (TextView) view.findViewById(R.id.link_info);
		mModifyNotename = (TextView) view.findViewById(R.id.info_note);
		mAgeLayout = (RelativeLayout) view.findViewById(R.id.info_age_layout);
		view1 = view.findViewById(R.id.view1);
		mJoin = (Button) view.findViewById(R.id.join);
		mCity = (TextView) view.findViewById(R.id.info_city);
		mAge = (TextView) view.findViewById(R.id.info_age_content);
		mModifyNotename.setVisibility(View.INVISIBLE);
		initView();
		
		
		return view;
	}
	
	private void initView(){
		fUerInfo = (UserInfo) getArguments().getSerializable("UsersDetail");
		isLinkDevice =getArguments().getBoolean("OnlyDevice", false);
		
		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
		String type = fUerInfo.getDeviceType();
		
		Bitmap head = task.loadImage(fUerInfo.getLoginName());	
		if(head!=null){
			mHeadIcon.setImageBitmap(head);
		}
		if(type.equals("1")){
			mSex.setVisibility(View.INVISIBLE);
			mAgeLayout.setVisibility(View.GONE);
			view1.setVisibility(View.INVISIBLE);
			if(isLinkDevice){
				String DeviceName = getArguments().getString("DeviceName");
				mJoin.setText(R.string.want_link);
				mLinkInfo.setVisibility(View.VISIBLE);
				mLinkInfo.setText(ctx.getResources().getString(R.string.and)+DeviceName);
				if(DeviceName.equals(fUerInfo.getNickName())){
					mJoin.setEnabled(false);
				}
			}else{
				mJoin.setText(R.string.want_join);
			}
			
			mType.setImageResource(R.drawable.ic_type_dolphin);
			
		}else{
			if(fUerInfo.getSex().equals(ctx.getResources().getString(R.string.male))){
				mSex.setImageResource(R.drawable.ic_sex_male);
			}else if(fUerInfo.getSex().equals(ctx.getResources().getString(R.string.female))){
				mSex.setImageResource(R.drawable.ic_sex_female);
			}
			mAge.setText(DolphinUtil.getAge(fUerInfo.getBirthday()));
		}
		
		String note = TextUtils.isEmpty(fUerInfo.getNoteName()) ? fUerInfo.getNickName(): fUerInfo.getNoteName();
		mNotename.setText(note);
		mNickname.setText(ctx.getResources().getString(R.string.nickName)+fUerInfo.getNickName());
		mCity.setText(fUerInfo.getCity());
		mActionbarName.setText(R.string.actionbar_info);
		mActionbarPrev.setOnClickListener(this);
		mJoin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.actionbar_prev:
			getFragmentManager().beginTransaction().remove(SearchResultInfoFragment.this).commit();
			break;
		case R.id.join:
			
			if(fUerInfo.getDeviceType().equals("1")){
					//getDeviceAdmin();	
				String reason = "我要加入家庭组";
				String teamid ="";//1.从我们服务器获取要加入的teamid 2.通过发送IM消息请求和接收TeamId；
				//TODO
				onJoinTeamApply(teamid,reason);
			}else{
				//getFamilytId();
				onAddTeamMember(fUerInfo.getLoginName());
			}
			
			break;
		}
	}
	
	private void onJoinTeamApply(String teamId,String reason){
		NIMClient.getService(TeamService.class)
	    .applyJoinTeam(teamId, reason)
	    .setCallback(new RequestCallback<Team>() {

			@Override
			public void onException(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailed(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Team arg0) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	private void onAddTeamMember(String name){
		List<String> acc = new ArrayList<String>();
		acc.add(name);
		NIMClient.getService(TeamService.class).addMembers(SysConfig.getInstance().getMyTeamID().get(0), acc).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                // 返回onSuccess，表示拉人不需要对方同意，且对方已经入群成功了
            }

            @Override
            public void onFailed(int code) {
                // 返回onFailed，并且返回码为810，表示发出邀请成功了，但是还需要对方同意
            }

            @Override
            public void onException(Throwable exception) {
            }
        });
	}
	
	
	
	
	private void getFamilytId(){
		List<FamilyInfo> info =DolphinApp.getInstance().getMyFamilyInfos();
	
		if(info.size() >1){
			ChooseOneDeviceFragment ChooseOneDevice = ChooseOneDeviceFragment.newInstance(ChooseOneDeviceFragment.Get_FamilyPos);

			ChooseOneDevice.setResultListener(new ChooseOneDeviceFragment.IfamilyEventListener() {
				
				@Override
				public void familyEventListener(int selectID) {
					// TODO Auto-generated method stub
					mFamilyInfo = DolphinApp.getInstance().getFamilyInfos().get(selectID);
					userJoinFamilyMsgUpload();
				}
			});
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.add(R.id.container, ChooseOneDevice);
			tx.commit();	
		}else if(info.size() ==1){
			mFamilyInfo = DolphinApp.getInstance().getMyFamilyInfos().get(0);
			userJoinFamilyMsgUpload();
		}else{
			CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.no_family_group));
			return;
		}
		
		
	}
	private void getDeviceAdmin(){
		String mBody = RequestString.GetDeviceAdmin(SysConfig.dtoken, fUerInfo.getLoginName());
		LoadDeviceAdmin admin = new LoadDeviceAdmin(mBody);
		admin.getAdminData(new deviceAdminDataCallBack() {
			
			@Override
			public void onDataCallBack(int code, AdminInfo data) {
				// TODO Auto-generated method stub
				if(code == MsgKey.KEY_RESULT_SUCCESS){
					adminInfo = data;
					if(adminInfo.getAdminUserId().equals("0")){
						beenManagerMsgUpload(adminInfo);
					}else{
						wantJoinFamilyMsgUpload(adminInfo);	
					}	
				}
				
				
			}
		});
	}
	
	private void beenManagerMsgUpload(final AdminInfo adminInfo ){
		InviteMessage msg = new InviteMessage();
		UserInfo myInfo = DolphinApp.getInstance().getMyUserInfo();
		msg.setType("invite");
		msg.setContent(getString(R.string.bapply)+" "+fUerInfo.getNickName());
		
		JSONObject ImCmd = new JSONObject(); 
		try {
			ImCmd.put("type","invite");
			ImCmd.put("userid",myInfo.getUserID());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, 0, adminInfo.getDeviceUserId(),ImCmd.toString());
		UploadData2Server msgUpdate = new UploadData2Server(mBody,"UploadMessageBox");
		msgUpdate.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				
				if(code == MsgKey.KEY_RESULT_SUCCESS){
				//	DolphinApp.getInstance().notifyMsgRTC2User(fUerInfo.getLoginName(),null);
					getActivity().finish();
				}
			}
		});
		
	}
	private void wantJoinFamilyMsgUpload(final AdminInfo adminInfo ){
		InviteMessage msg = new InviteMessage();
		String mJonMsg ;
		
		if(isLinkDevice){
			FamilyInfo  fInfo = DolphinApp.getInstance().getMyFamilyInfos().get(SysConfig.getInstance().getFamilyPos());
			msg.setAction(MsgKey.BELINK);
			msg.setUserId(fInfo.getDeviceUserID());
			msg.setUserName(fInfo.getDeviceID());
			msg.setUserFamilyId(fInfo.getFamilyID());
			msg.setUserNickname(fInfo.getNickName());
			msg.setContent(getString(R.string.blink)+" "+fUerInfo.getNickName());
			
		}else{
			UserInfo myInfo = DolphinApp.getInstance().getMyUserInfo();
			msg.setUserId(myInfo.getUserID());
			msg.setUserName(myInfo.getLoginName());
			msg.setUserNickname(myInfo.getNickName());
			msg.setAction(MsgKey.BEAPPLYED);
			msg.setContent(getString(R.string.bapply)+" "+fUerInfo.getNickName());
		}
	
		msg.setType("invite");
		msg.setDeviceFamilyId(adminInfo.getFamilyId());
		msg.setDeviceName(fUerInfo.getNickName());
		msg.setDeviceId(fUerInfo.getLoginName());
		msg.setDeviceUserID(fUerInfo.getUserID());
		msg.setManagerId(adminInfo.getAdminUserId());
		msg.setManagerName(adminInfo.getAdminUserName());
		mJonMsg =JsonUtil.getInviteJson(msg);
		
		String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, 0, adminInfo.getAdminUserId(),mJonMsg);
		UploadData2Server msgUpdate = new UploadData2Server(mBody,"UploadMessageBox");
		msgUpdate.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				
				if(code == MsgKey.KEY_RESULT_SUCCESS){
					//DolphinApp.getInstance().notifyMsgRTC2User(adminInfo.getAdminUserName(),null);
					getActivity().finish();
				}
			}
		});
	}
	
	private void userJoinFamilyMsgUpload(){
		InviteMessage msg = new InviteMessage();
		UserInfo myInfo = DolphinApp.getInstance().getMyUserInfo();
		String mJonMsg ;
		msg.setType("invite");
		msg.setDeviceFamilyId(mFamilyInfo.getFamilyID());
		msg.setDeviceName(mFamilyInfo.getNickName());
		msg.setDeviceId(mFamilyInfo.getDeviceID());
		msg.setManagerId(mFamilyInfo.getMangerID());
		msg.setManagerName(myInfo.getLoginName());
		msg.setManagerNickname(myInfo.getNickName());
		msg.setUserId(fUerInfo.getUserID());
		msg.setUserName(fUerInfo.getLoginName());
		msg.setUserNickname(fUerInfo.getNickName());
		msg.setAction(MsgKey.BEINVITEED);
		msg.setContent(getString(R.string.binvite)+" "+mFamilyInfo.getNickName());
		mJonMsg = JsonUtil.getInviteJson(msg);
		String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, 0, fUerInfo.getUserID(),mJonMsg);
		UploadData2Server msgUpdate = new UploadData2Server(mBody,"UploadMessageBox");
		msgUpdate.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				if(code == MsgKey.KEY_RESULT_SUCCESS){
				//	DolphinApp.getInstance().notifyMsgRTC2User(fUerInfo.getLoginName(),null);
					getActivity().finish();
				}
			}
		});
	}
}
