package com.wellav.dolphin.mmedia.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.adapter.ChooseNewAdminAdapter;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.InviteMessage;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class ChooseNewAdminFragment extends BaseFragment implements OnClickListener{
	private List<FamilyUserInfo> familyusers = new ArrayList<FamilyUserInfo>();
	private TextView mGroupName;
	private ListView mList;
	private Button mCancel;
	private Button mConform;
	private ImageView mActionbarPrev;
	private TextView mActionbarName;
	private ChooseNewAdminAdapter adapter;
	private int posChoose;
	private String mFamilyID;
	private String FamilyName;
	private ChooseNewAdminFragment ctx;

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.ctx = this;
		
		View view = inflater.inflate(R.layout.fragment_change_admin, container,false);
		 mList = (ListView) view.findViewById(R.id.list);
		 mGroupName = (TextView) view.findViewById(R.id.group_name);
		 mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		 mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		 mCancel = (Button) view.findViewById(R.id.cancelBtn);
		 mConform = (Button) view.findViewById(R.id.okBtn);
         mFamilyID = getArguments().getString("FamilyId");
         FamilyName = getArguments().getString("FamilyName");
         
		 familyusers= SQLiteManager.getInstance().getFamilyUserInfoByFamilyIDNotDevice(mFamilyID);
		 mGroupName.setText("@ "+FamilyName);
		 mActionbarName.setText(R.string.turn_over);
		 mActionbarPrev.setOnClickListener(this);
		 mCancel.setOnClickListener(this);
		 mConform.setOnClickListener(this);
		 
		 adapter = new ChooseNewAdminAdapter(getActivity(),familyusers);
		 mList.setAdapter(adapter);
		 mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		return view;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.actionbar_prev:
		case R.id.cancelBtn:
			getFragmentManager().beginTransaction().remove(ChooseNewAdminFragment.this).commit();
			break;
		case R.id.okBtn:
			comfirmDialog();
			break;
		}
	}
	
	
	protected void comfirmDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage(ctx.getResources().getString(R.string.give_your_power)); 

		// builder.setTitle("解散该群组");

		builder.setPositiveButton(ctx.getResources().getString(R.string.conform), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				checkIfSetSPsw();
			}
		});

		builder.setNegativeButton(ctx.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}
	
	private void checkIfSetSPsw(){
		String body = RequestString.GetIfSecPWSeted(SysConfig.dtoken,mFamilyID);
		UploadData2Server task = new UploadData2Server(body, "GetIfSecPWSeted");
		task.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				if(code == 1){
					secPswDialog();
				}else if(code == 2){
					uploadAdminMsg();
				}else{
					
				}
			}
		});
	}
	
	private void secPswDialog() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_edittext,
				(ViewGroup) getActivity().findViewById(R.id.dialog));

		final EditText eText = (EditText) layout.findViewById(R.id.etname);
		new AlertDialog.Builder(getActivity()).setTitle(ctx.getResources().getString(R.string.put_second_psaaword))
				.setIcon(android.R.drawable.ic_dialog_info).setView(layout)
				.setPositiveButton(ctx.getResources().getString(R.string.conform), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						checkSecPsw(eText.getEditableText().toString());
					}
				}).setNegativeButton(ctx.getResources().getString(R.string.cancel), null).show();
	}
	private void checkSecPsw(String sec) {
		String sPsw = DolphinUtil.md5(sec);
		String body = RequestString.CheckFamilySecPW(SysConfig.dtoken,
				mFamilyID, sPsw);
		UploadData2Server task = new UploadData2Server(body, "CheckFamilySecPW");
		task.getData(new codeDataCallBack() {

			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				if (code == 0) {
					uploadAdminMsg();
				} else if (code == -1700) {
					CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.second_psaaword_wrong));
				} else {
					CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.second_psaaword_fail));
				}
			}
		});

	}
	
	
		private void uploadAdminMsg(){
			posChoose = adapter.getChoosePos();
			FamilyInfo fInfo= SQLiteManager.getInstance().getFamilyInfoFamilyID(mFamilyID);
			
		InviteMessage msg = new InviteMessage();
		String mJonMsg ;
		msg.setType("invite");
		msg.setDeviceFamilyId(mFamilyID);
		msg.setDeviceName(FamilyName);
		msg.setDeviceId(fInfo.getDeviceID());
		msg.setManagerId(DolphinApp.getInstance().getMyUserInfo().getUserID());
		msg.setManagerName(DolphinApp.getInstance().getMyUserInfo().getLoginName());
		msg.setManagerNickname(DolphinApp.getInstance().getMyUserInfo().getNickName());
		msg.setAction(MsgKey.BEMANAGER);
		msg.setContent(getString(R.string.bmanaget)+" "+FamilyName);
		msg.setUserId(familyusers.get(posChoose).getUserID());
		msg.setUserName(familyusers.get(posChoose).getLoginName());
		msg.setUserNickname(familyusers.get(posChoose).getNickName());
		mJonMsg = JsonUtil.getInviteJson(msg);
		String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, 0, familyusers.get(posChoose).getUserID(),mJonMsg);
		UploadData2Server upMsg = new UploadData2Server(mBody,"UploadMessageBox");
		upMsg.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				if(code == 0){
					//DolphinApp.getInstance().notifyMsgRTC2User(familyusers.get(posChoose).getLoginName(),null);
					getFragmentManager().beginTransaction().remove(ChooseNewAdminFragment.this).commit();	
				}
			
			}
		});
	}
}
