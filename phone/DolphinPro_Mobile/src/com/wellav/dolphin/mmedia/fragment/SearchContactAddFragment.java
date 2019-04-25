package com.wellav.dolphin.mmedia.fragment;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.net.LoadUsersFromServer;
import com.wellav.dolphin.mmedia.net.LoadUsersFromServer.userDataCallBack;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class SearchContactAddFragment extends BaseFragment implements OnClickListener{
	private EditText mSearchEdit;
	private TextView mSearchTxt;
	private RelativeLayout mSearchLayout;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private String mSearchNum="";
	private boolean isOnlyDevice ;
	private String mInfoNickName;

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_search_contact, container, false);
		mSearchEdit = (EditText) view.findViewById(R.id.search_edit);
		mSearchTxt = (TextView) view.findViewById(R.id.search_txt);
		mSearchLayout = (RelativeLayout) view.findViewById(R.id.search_rl);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		
	    isOnlyDevice = getArguments().getBoolean("OnlyDevice", false);
	  
	    if(isOnlyDevice){
	    	  mInfoNickName = getArguments().getString("DeviceName"); 
	    	mSearchEdit.setHint(R.string.hint_devicenumber);
	    	mSearchEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});  
	    }
		mActionbarName.setText(R.string.search);
		mSearchLayout.setOnClickListener(this);
		mSearchEdit.addTextChangedListener(textWatcher);
		mActionbarPrev.setOnClickListener(this);
		return view;
	}
	
	private TextWatcher textWatcher = new TextWatcher(){

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
			mSearchNum = s.toString().replaceAll(" ", "");
			mSearchTxt.setText(mSearchNum);
		}
		
	};
	
	public static Fragment newIstanceSearch(Bundle bundle){
		SearchContactAddFragment SearchContactAdd = new SearchContactAddFragment();
		SearchContactAdd.setArguments(bundle);
		
		return SearchContactAdd;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		goneKeyboard();
		switch(v.getId()){
		case R.id.actionbar_prev:
			getFragmentManager().beginTransaction().remove(SearchContactAddFragment.this).commit();
			break;
		case R.id.search_rl:
			if(mSearchNum.length()==8 ||mSearchNum.length()==11 ){
				String SearchName = RequestString.SearchUser(SysConfig.dtoken, mSearchNum);
				LoadUsersFromServer users = new LoadUsersFromServer(SearchName, "SearchUser");
				users.getData(new userDataCallBack() {
					
					@Override
					public void onDataCallBack(int code, List<UserInfo> data) {
						// TODO Auto-generated method stub
						if(data.size() != 0){
							getSearchResult(data.get(0));
						}else{
							Toast.makeText(getActivity(), R.string.account_inexistence, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}else{
				if(mSearchNum==null || mSearchNum.equals("")){
					CommFunc.DisplayToast(getActivity(), R.string.account_null);
				}else{
					CommFunc.DisplayToast(getActivity(), R.string.account_inexistence);
				}
				
			}
			break;
		}
		
	}
	
	private void getSearchResult(UserInfo info){
			SearchContactResultFragment choosefra = new SearchContactResultFragment();
		  	FragmentManager fm = getActivity().getSupportFragmentManager();
		  	Bundle bundle = new Bundle(); 
		  	bundle.putSerializable("USERINFO",(Serializable)info);//对象集合存入方式；
		  	bundle.putBoolean("OnlyDevice", isOnlyDevice);
			bundle.putString("DeviceName", mInfoNickName);
			choosefra.setArguments(bundle);
			FragmentTransaction tx = fm.beginTransaction();
			tx.add(R.id.container, choosefra);
			tx.commit();
	}
}
