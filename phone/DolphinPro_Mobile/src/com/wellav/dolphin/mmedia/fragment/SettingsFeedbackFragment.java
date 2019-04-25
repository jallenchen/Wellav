package com.wellav.dolphin.mmedia.fragment;


import android.os.Bundle;
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
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class SettingsFeedbackFragment extends BaseFragment implements OnClickListener {
	public static final int From_Setting = 0;
	public static final int From_Menu= 1;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private EditText mMessage;
	private Button mSumit;
	private static int mItem = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragmentfeedback, container, false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mMessage = (EditText) view.findViewById(R.id.message_edit);
		mSumit = (Button) view.findViewById(R.id.submit);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.feedback);
		mSumit.setOnClickListener(this);
    	return view;
	}
	
	 public static SettingsFeedbackFragment newInstance(int value) {
		 SettingsFeedbackFragment fragment = new SettingsFeedbackFragment();
		 mItem = value;
	     return fragment;
	    }
	@Override
	public void onClick(View v) {
		goneKeyboard();
		switch (v.getId()) {
		case R.id.actionbar_prev:
			if(mItem == From_Setting){
				getActivity().getSupportFragmentManager().beginTransaction().remove(SettingsFeedbackFragment.this).commit();
			}else if(mItem == From_Menu){
				getActivity().finish();
			}
			break;
		case R.id.submit:
			String message = mMessage.getEditableText().toString();
			if(message.equals("") || message == null){
				CommFunc.DisplayToast(getActivity(), R.string.put_ref_context);
				return;
			}
			String body = RequestString.SetUserFeedback(SysConfig.dtoken, message);
			UploadData2Server task = new UploadData2Server(body,"SetUserFeedback");
			task.getData(new codeDataCallBack() {
				
				@Override
				public void onDataCallBack(String request, int code) {
					// TODO Auto-generated method stub
					if(mItem == From_Setting){
						getActivity().getSupportFragmentManager().beginTransaction().remove(SettingsFeedbackFragment.this).commit();
					}else if(mItem == From_Menu){
						getActivity().finish();
					}
				}
			});
			break;

		default:
			break;
		}
	}
}
