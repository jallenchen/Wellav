package com.wellav.dolphin.mmedia.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;


public class SecondPswSettingtwoFragment extends BaseFragment implements OnClickListener {
 
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private EditText verfication_input;
	private EditText sec_input;
	private String verficaton;
	private String secPwd;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_secondpsw_settingtwo, container, false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		verfication_input=(EditText)view.findViewById(R.id.cilent_psw);
		sec_input=(EditText)view.findViewById(R.id.input_second_psw);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.secondpswsetting);
		
        return view;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.actionbar_prev:
			getActivity().getSupportFragmentManager().popBackStack(null, 0);
			break;

		default:
			break;
		}
	}
	
	
}
