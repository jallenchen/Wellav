package com.wellav.dolphin.mmedia.fragment;

import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class SecondPswSettingFragment extends BaseFragment implements OnClickListener {
	private static final int PSW_ERROR = -1700;
	private RelativeLayout layoutold;
	private Button sp_submit;
	private Button sp_forget;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private EditText oldsec_input;
	private EditText newsec_input;
	private String oldsec;
	private String newsec;
	private String familyid;
	private String deviceid;
	private SharedPreferences share;
	private boolean isSet = false;
	private SecondPswSettingFragment ctx;
	 public interface IifSecPswSet {
	        public void ifSecPswSet(boolean isSet);
	    }
	    private IifSecPswSet mIfSecPswSet;
	
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	 this.ctx = this;
    	 View view = inflater.inflate(R.layout.fragment_secondpsw_setting, container, false);
         mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
 		 mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
 		 layoutold =  (RelativeLayout) view.findViewById(R.id.old_layout);
 		 oldsec_input=(EditText)view.findViewById(R.id.sp_old);
 		 newsec_input=(EditText)view.findViewById(R.id.sp_new);
         sp_submit=(Button)view.findViewById(R.id.sp_submit);
         sp_forget=(Button)view.findViewById(R.id.sp_forget);
         sp_submit.setOnClickListener(this);
         sp_forget.setOnClickListener(this);
         mActionbarPrev.setOnClickListener(this);
         
 		 mActionbarName.setText(R.string.secondpswmodify);
 		 familyid=getArguments().getString("familyID");
 		 deviceid=getArguments().getString("deviceid");
 		 share = getActivity().getSharedPreferences(DolphinApp.getInstance().getAccountInfo().getUserId()+"_"+deviceid, 0);
 		 isSet= share.getBoolean("HasSecPSw", false);
 		 if(isSet == false){
 			layoutold.setVisibility(View.GONE);
 			mActionbarName.setText(R.string.secondpswsetting);
 		 }
         return view;
    }
     
     public void setResultListener(IifSecPswSet listener){
    	 mIfSecPswSet = listener;
	    }
     
    private boolean checkNewSecPsw(String txt){
    	
		if(txt==null){
			CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.second_password_null));
			return false;
		}else if(txt.length() <6){
			CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.second_password_long));
			return false;
		}else{
			newsec = DolphinUtil.md5(newsec_input.getText().toString());
			return true;
		}
    }
     @Override
    public void onClick(View v) {
    	 DolphinUtil.goneKeyboard(getActivity());
    	switch (v.getId()) {
		case R.id.sp_submit:
			String newInput = newsec_input.getText().toString();
			if(isSet == false){
				if(checkNewSecPsw(newInput) == false){
					return;
				}
				String SetFamilySecPW=RequestString.SetFamilySecPW(SysConfig.dtoken, familyid, newsec);
				UploadData2Server task = new UploadData2Server(SetFamilySecPW, "SetFamilySecPW");
				task.getData(new codeDataCallBack() {
					@Override
					public void onDataCallBack(String request, int code) {
						if(code == MsgKey.KEY_RESULT_SUCCESS){
							CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.seeting_ok));
							share = getActivity().getSharedPreferences(SysConfig.userid+"_"+deviceid, 0);
							share.edit().putBoolean("HasSecPSw", true);
							share.edit().putString("SecPSw", newsec);
							share.edit().commit();
							mIfSecPswSet.ifSecPswSet(true);
							getActivity().getSupportFragmentManager().beginTransaction().remove(SecondPswSettingFragment.this).commit();
						}else{
							mIfSecPswSet.ifSecPswSet(false);
						}
					}
				});
			}else{
				String oldInput = oldsec_input.getText().toString();
				if(oldInput == null ||oldInput.equals("") ){
					CommFunc.DisplayToast(getActivity(), ctx.getResources().getString(R.string.old_second_password));
					return;
				}else{
					oldsec = DolphinUtil.md5(oldsec_input.getText().toString());
					if(checkNewSecPsw(newInput) == false){
						return;
					}
					String SetFamilySecPW=RequestString.ModifyFamilySecPW(SysConfig.dtoken, familyid,oldsec, newsec);
					UploadData2Server task = new UploadData2Server(SetFamilySecPW, "ModifyFamilySecPW");
					task.getData(new codeDataCallBack() {
						@Override
						public void onDataCallBack(String request, int code) {
							if(code == MsgKey.KEY_RESULT_SUCCESS){
								CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.change_ok));
								share = getActivity().getSharedPreferences(SysConfig.userid+"_"+deviceid, 0);
								share.edit().putString("SecPSw", newsec);
								share.edit().commit();
								mIfSecPswSet.ifSecPswSet(true);
								getActivity().getSupportFragmentManager().beginTransaction().remove(SecondPswSettingFragment.this).commit();
							}else if(code==PSW_ERROR){
								CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.this_second_password));
							}
						}
					});
				}
			}
			break;
		case R.id.actionbar_prev:
			getActivity().getSupportFragmentManager().beginTransaction().remove(SecondPswSettingFragment.this).commit();	        
   			break;
		case R.id.sp_forget:
			RegisterTwoFragment rtFragment=new RegisterTwoFragment() ;
			Bundle bundle = new Bundle();
			bundle.putBoolean("SEC", true);
			bundle.putString("familyid", familyid);
			bundle.putString("PHONENUM", DolphinApp.getInstance().getMyUserInfo().getLoginName());
			
			rtFragment.setArguments(bundle);
			FragmentManager fm=getActivity().getSupportFragmentManager();
	        FragmentTransaction ft=fm.beginTransaction();
	        ft.add(R.id.container, rtFragment);
	        ft.addToBackStack(null);
	        ft.commit();  	        
   			break;
		default:
			break;
		}
    }
}
