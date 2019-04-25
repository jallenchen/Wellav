package com.wellav.dolphin.mmedia.fragment;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsMessage;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.netease.LoginActivity;
import com.wellav.dolphin.mmedia.netease.LogoutHelper;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class RegisterTwoFragment extends BaseFragment implements
		OnClickListener {
	private RelativeLayout mNoteLayout;
	private RelativeLayout mSecPswLayout;
	private TextView mAcionBarName;
	private ImageView mActionBarPrev;
	private TextView mPhoneNum;
	private TextView mRestSend;
	private TextView mSendNote;
	private TextView mCodeNote;
	private TextView mNewPsw;
	private EditText mCode;
	private EditText mNewSec;
	private Button mNext;
	private String patternCoder = "(?<!\\d)\\d{4}(?!\\d)";
	private String receiveVerfiCode;
	private String sendVverfiCode ="1234";
	private String phoneNum = null;
	private BroadcastReceiver smsReceiver;
	private boolean isUnregister = true;
	private Timer timer = null;
	private CCPRestSDK restAPI;
	private boolean isRegister ;
	private boolean isSecPwd ;
	private HashMap<String, Object> result = null;
	private String familyid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		smsReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Object[] objs = (Object[]) intent.getExtras().get("pdus");
				for (Object obj : objs) {
					byte[] pdu = (byte[]) obj;
					SmsMessage sms = SmsMessage.createFromPdu(pdu);
					String message = sms.getMessageBody();

					String from = sms.getOriginatingAddress();
					if (!TextUtils.isEmpty(from)) {
						String code = patternCode(message);
						if (!TextUtils.isEmpty(code)) {
							receiveVerfiCode = code;
							mHandler.sendEmptyMessage(0);
						}
					}
				}
			}
		};
		if(isUnregister == true){
			getActivity().registerReceiver(smsReceiver, filter);
			isUnregister = false;
		}
		initSmsApi();

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		if(isUnregister == false){
			getActivity().unregisterReceiver(smsReceiver);
			isUnregister = true;
		}
		stopCallTimer();
		mHandler = null;
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmentregistertwo, container,
				false);
		mNoteLayout = (RelativeLayout) view.findViewById(R.id.new_psw_layout);
		mSecPswLayout=(RelativeLayout)view.findViewById(R.id.new_secpsw_layout);
		mNewPsw = (TextView) view.findViewById(R.id.cilent_new_psw);
		mNewSec=(EditText)view.findViewById(R.id.input_second_psw);
		mNext = (Button) view.findViewById(R.id.nextStep);
		mCode = (EditText) view.findViewById(R.id.cilent_psw);
		mRestSend = (TextView) view.findViewById(R.id.rest_send);
		mSendNote = (TextView) view.findViewById(R.id.sendNote);
		mPhoneNum = (TextView) view.findViewById(R.id.cap_phone);
		mActionBarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mAcionBarName = (TextView) view.findViewById(R.id.actionbar_name);
		mAcionBarName.setText(R.string.actionbar_step_two);
		setMyTextColor();
		phoneNum = getArguments().getString("PHONENUM");
		isRegister = getArguments().getBoolean("isRegister",true);
		isSecPwd = getArguments().getBoolean("SEC",false);
		if (isSecPwd==true) {
			isRegister=false;
			familyid = getArguments().getString("familyid");
		}
		initForgetPsw(isRegister, isSecPwd);
		initSecPsw(isSecPwd);
		String num = getString(R.string.phone_code) + " " + phoneNum;
		mPhoneNum.setText(num);
		
	 //   sendVerfiCode(phoneNum);//验证码检验
		
		mActionBarPrev.setOnClickListener(this);
		mNext.setOnClickListener(this);
		mRestSend.setOnClickListener(this);
		return view;
	}

	private void setMyTextColor() {
		String str = getString(R.string.send_note);
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(0xff6699ff), 3, 6,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mSendNote.setText(style);//
	}
	
	private void initForgetPsw(boolean is,boolean sec){
		if(is == false&&sec==false){
			mAcionBarName.setText(R.string.rest_psw);
			mNext.setText(R.string.submit);
		    mNoteLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private void initSecPsw(boolean sec){
		if(sec == true){
			mAcionBarName.setText(R.string.secondpswreset);
			mNext.setText(R.string.submit);
		    mSecPswLayout.setVisibility(View.VISIBLE);
		}
	}

	private void initSmsApi() {
		// init SDK
		restAPI = new CCPRestSDK();
		restAPI.init(SysConfig.serverIP, SysConfig.serverPort);
		restAPI.setAccount(SysConfig.accountSid, SysConfig.accountToken);
		restAPI.setAppId(SysConfig.appId);
	}

	private void sendVerfiCode(String phone) {
		
		if (phone == null) {
			return;
		}
		startCallTimer(1);
		 long code= Math.round(Math.random() * 10000) + 1000;
		 sendVverfiCode = String.valueOf(code);
		CommFunc.PrintLog("sendVerfiCode", sendVverfiCode);

		new Thread() {
			public void run() {
				result = restAPI.sendTemplateSMS(phoneNum, "1", new String[] {
						sendVverfiCode, "1" });

				if ("000000".equals(result.get("statusCode"))) {

					mHandler.sendEmptyMessage(2);

				} else {
					mHandler.sendEmptyMessage(3);
				}
			}
		}.start();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.nextStep:
			 receiveVerfiCode = mCode.getEditableText().toString();
			 if(!sendVverfiCode.equals(receiveVerfiCode)){
				 mHandler.sendEmptyMessage(5);
				 return;
			 }
			if(isRegister){
				if(isUnregister == false){
					getActivity().unregisterReceiver(smsReceiver);
					isUnregister = true;
				}
				
				mHandler.sendEmptyMessage(4);
				RegisterThreeFragment fThreee = new RegisterThreeFragment();
				Bundle bundle = new Bundle();  
				bundle.putString("PHONENUM", phoneNum);
				fThreee.setArguments(bundle); 
				FragmentManager fm = getFragmentManager();
				FragmentTransaction tx = fm.beginTransaction();
				tx.add(R.id.container, fThreee);
				tx.addToBackStack(null);
				tx.commit();
			}else{
				if (isSecPwd) {
					toResetSecPsw();
				} else {
					toForgetPsw();
				}
				
				
			}
		
			break;
		case R.id.actionbar_prev:
			getActivity().getSupportFragmentManager().beginTransaction().remove(RegisterTwoFragment.this).commit();
			break;
		case R.id.rest_send:
			sendVerfiCode(phoneNum);
			break;
		}
	}
	
	private void toForgetPsw(){
		String psw = DolphinUtil.md5(mNewPsw.getText().toString());
		String uploadPsw = RequestString.forgetUserPsw(phoneNum, psw);
		UploadData2Server task = new UploadData2Server(uploadPsw, "ForgetUserPW");
		task.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				if (isSecPwd == false) {
					Message msg = new Message();
					msg.what = 6;
					msg.arg1 = code;
					mHandler.sendMessage(msg);
				}
			}
		});
	}
	
	private void toResetSecPsw(){
		String token=SysConfig.dtoken;
		String newpsw = mNewSec.getText().toString();
		if(checkNewSecPsw(newpsw) == false){
			return;
		}
		newpsw = DolphinUtil.md5(newpsw);
		String ModifyFamilySecPW = RequestString.SetFamilySecPW(token, familyid, newpsw);
		UploadData2Server task = new UploadData2Server(ModifyFamilySecPW, "SetFamilySecPW");
		task.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				getActivity().getSupportFragmentManager().beginTransaction().remove(RegisterTwoFragment.this).commit();
			}
		});
	}
	
    private boolean checkNewSecPsw(String txt){
    	
		if(txt==null){
			CommFunc.DisplayToast(getActivity(),RegisterTwoFragment.this.getResources().getString(R.string.second_password_null));
			return false;
		}else if(txt.length() <6){
			CommFunc.DisplayToast(getActivity(),RegisterTwoFragment.this.getResources().getString(R.string.second_password_long));
			return false;
		}else{
			
			return true;
		}
    }
	

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mCode.setText(receiveVerfiCode);// �Զ�����ֻ���֤��
				break;
			case 1:
				int sencond = 60 - (Integer) msg.obj;
				if (sencond < 1) {
					stopCallTimer();
				} else {
					Resources res = getActivity().getResources();
					String afterSencond = String.format(res.getString(R.string.second_rest_send),sencond);
					mRestSend.setText(afterSencond);
				}
				break;
			case 2:
				Toast.makeText(getActivity(), R.string.vercode_send_succ, Toast.LENGTH_SHORT)
						.show();
				break;
			case 3:
				Toast.makeText(getActivity(), R.string.vercode_send_fail, Toast.LENGTH_SHORT)
						.show();
				break;
			case 4:
				Toast.makeText(getActivity(), R.string.vercode_succ, Toast.LENGTH_SHORT)
						.show();
				stopCallTimer();
				break;
			case 5:
				Toast.makeText(getActivity(), R.string.vercode_fail, Toast.LENGTH_SHORT)
						.show();
				break;
			case 6:
				if(msg.arg1 == 0){
					Toast.makeText(getActivity(), R.string.modify_psw_succ, Toast.LENGTH_SHORT)
					.show();
					LogoutHelper.logout(getActivity(), false);
				}else{
					Toast.makeText(getActivity(), R.string.modify_psw_ng, Toast.LENGTH_SHORT)
					.show();
				}
			
				break;
			}

			super.handleMessage(msg);
		}

	};

	/**
	 * 
	 * ��֤�붨ʱ������
	 * 
	 */
	private int sencond = 0;

	public void startCallTimer(final int type) {
		sencond = 0;
		stopCallTimer();
		if (timer == null) {
			timer = new Timer();
		}
		;
		mRestSend.setEnabled(false);
		mRestSend.setTextColor(Color.parseColor("#bfbfbf"));
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				sencond++;
				Message message = new Message();
				message.what = 1;
				message.obj = sencond;
				mHandler.sendMessage(message);
			}
		}, 0, 1000);
	}

	/**
	 * 
	 * ��֤�붨ʱ���ر�
	 * 
	 */
	public void stopCallTimer() {
		mRestSend.setEnabled(true);
		mRestSend.setTextColor(Color.parseColor("#6699ff"));
		mRestSend.setText(R.string.rest_send);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * 
	 * @param patternContent
	 * @return
	 */
	private String patternCode(String patternContent) {
		if (TextUtils.isEmpty(patternContent)) {
			return null;
		}
		Pattern p = Pattern.compile(patternCoder);
		Matcher matcher = p.matcher(patternContent);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}
