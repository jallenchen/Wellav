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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.CheckExistData;
import com.wellav.dolphin.mmedia.net.CheckUserIsExist;
import com.wellav.dolphin.mmedia.net.CheckUserIsExist.checkDataCallBack;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class RegisterOneFragment extends BaseFragment implements OnClickListener{
	private TextView mAcionBarName;
	private ImageView mActionBarPrev;
	private Button mNext;
	private TextView mNote;
	private EditText mPhoneNum;
	private String phoneNum;
	private boolean isRegister ;
	private RelativeLayout mNoteLayout;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragmentregisterone, container, false);
		mNoteLayout = (RelativeLayout) view.findViewById(R.id.note_layout);
		mNext = (Button) view.findViewById(R.id.nextStep);
		mNote = (TextView) view.findViewById(R.id.note);
		mPhoneNum = (EditText) view.findViewById(R.id.cilent_phone);
		mActionBarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mAcionBarName = (TextView) view.findViewById(R.id.actionbar_name);
		mAcionBarName.setText(R.string.actionbar_step_one);
		mActionBarPrev.setOnClickListener(this);
		mNext.setOnClickListener(this);
		mNote.setOnClickListener(this);
		
		isRegister = getArguments().getBoolean("isRegister",true);
		isForgetPsw(isRegister);
		
		return view;
	}
	
	private void isForgetPsw(boolean is){
		if(isRegister == false){
			mAcionBarName.setText(R.string.rest_psw);
		    mNoteLayout.setVisibility(View.INVISIBLE);
		}
	
	}
	
	private void stepTwo(){
		RegisterTwoFragment twoFrag=new RegisterTwoFragment();
		Bundle bundle = new Bundle();  
		bundle.putString("PHONENUM", phoneNum);
		bundle.putBoolean("isRegister", isRegister);
		twoFrag.setArguments(bundle); 
        FragmentManager fm=getActivity().getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.add(R.id.container, twoFrag);
        ft.addToBackStack(null);
        ft.commit();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.nextStep:
			phoneNum = mPhoneNum.getText().toString();
			if(DolphinUtil.checkUid(getActivity(),phoneNum)){
				if(isRegister == false){
					stepTwo();
					return;
				}
				String body = RequestString.UserIfExist(phoneNum);
				CheckUserIsExist isExist = new CheckUserIsExist(body);
				isExist.isCheckUserExist(new checkDataCallBack() {
					
					@Override
					public void onDataCallBack(CheckExistData data) {
						// TODO Auto-generated method stub
						if(data.isExist()){
							CommFunc.DisplayToast(getActivity(), getActivity().getString(R.string.name_exist));
						}else{
							stepTwo();
						}
						
					}
				});
			}
			
			break;
		case R.id.note:
			DolphinProtocolFragment agreeFrag=new DolphinProtocolFragment();
			 FragmentManager fm2=getActivity().getSupportFragmentManager();
		        FragmentTransaction ft2=fm2.beginTransaction();
		        ft2.addToBackStack(null);
		        ft2.add(R.id.container, agreeFrag);
		        ft2.addToBackStack(null);
		        ft2.commit();
			
			break;
		case R.id.actionbar_prev:
//			DolphinUtil.goneKeyboard(getActivity());
//			getActivity().getSupportFragmentManager().beginTransaction().remove(RegisterOneFragment.this).commit();
			getActivity().finish();
			break;
		}


	}
			
}
