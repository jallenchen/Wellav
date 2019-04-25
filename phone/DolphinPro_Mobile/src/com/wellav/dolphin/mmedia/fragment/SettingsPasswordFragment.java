package com.wellav.dolphin.mmedia.fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.netease.LogoutHelper;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class SettingsPasswordFragment extends BaseFragment implements OnClickListener {
    
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private Button submit;
	private Button forgetpsw;
	private EditText opswInput;
	private EditText npswInput;
	private EditText npswInputagain;
	private String OldPassword="";
	private String NewPassword="";
	private String NewPasswordAgain="";
	 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_settings_pswsetting, container, false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		submit=(Button)view.findViewById(R.id.psw_submit);
		forgetpsw=(Button)view.findViewById(R.id.psw_forget);
		opswInput=(EditText)view.findViewById(R.id.psw_settings_old);
		npswInput=(EditText)view.findViewById(R.id.psw_settings_new);
		npswInputagain=(EditText)view.findViewById(R.id.psw_confirm);
		mActionbarPrev.setOnClickListener(this);
		submit.setOnClickListener(this);
		forgetpsw.setOnClickListener(this);
		mActionbarName.setText(R.string.pswsetting);
    	return view;
	}
	@Override
	public void onClick(View v) {
		DolphinUtil.goneKeyboard(getActivity());
		switch (v.getId()) {
		case R.id.actionbar_prev:
			getActivity().getSupportFragmentManager().popBackStack(null, 0);
			break;

		case R.id.psw_submit:
			String token=SysConfig.dtoken;
			OldPassword=opswInput.getText().toString();
			NewPassword=npswInput.getText().toString();
			NewPasswordAgain=npswInputagain.getText().toString();
			String old = DolphinUtil.md5(OldPassword);
			if(!old.equals(SysConfig.psw)){
				CommFunc.DisplayToast(getActivity(), R.string.psw_not_right);
                return;
			}
			
			if(NewPassword.equals("") || NewPassword == null){
				CommFunc.DisplayToast(getActivity(), R.string.psw_not_null);
                return;
			}
			if (NewPassword.equals(NewPasswordAgain)) {	
				if(NewPassword.length() < 6){
					CommFunc.DisplayToast(getActivity(), R.string.psw_not_long);
					return;
				}
			 
			   String newPsw = DolphinUtil.md5(NewPasswordAgain);
			   String ModifyUserPW=RequestString.ModifyUserPW(token, old, newPsw);
			   requestXml(ModifyUserPW, "ModifyUserPW");
			} else {
				  
				CommFunc.DisplayToast(getActivity(), R.string.psw_not_equal);
                  return;
			}
			
			break;
		case R.id.psw_forget:
			RegisterOneFragment aF=new RegisterOneFragment();
			Bundle bundle = new Bundle();  
			bundle.putBoolean("isRegister", false);
			aF.setArguments(bundle);
		    FragmentManager fm=getActivity().getSupportFragmentManager();
	        FragmentTransaction ft=fm.beginTransaction();
	        ft.add(R.id.container, aF);
	        ft.commit();
		break;
		default:
			break;
		}
	}
	private void requestXml(String requestBody,String request){
		
		UploadData2Server upLoad = new UploadData2Server(requestBody, request);
		upLoad.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				LogoutHelper.logout(getActivity(), false);
			}
		});
	}
}
