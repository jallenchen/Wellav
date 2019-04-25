package com.wellav.dolphin.mmedia.fragment;

import org.xmlpull.v1.XmlPullParser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.fragment.SecondPswSettingFragment.IifSecPswSet;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.net.XMLParser;
import com.wellav.dolphin.mmedia.net.XMLRequest;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class SecondPswEnterFragment extends BaseFragment implements
		OnClickListener {

	private Button sp_setting;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private ImageView secpsw_icon;
	private TextView secpw_state;
	private CircleImageView mIcon;
	private TextView nName;
	private FamilyInfo info;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_secondpsw_enter,
				container, false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		secpsw_icon = (ImageView) view.findViewById(R.id.sp_img);
		secpw_state = (TextView) view.findViewById(R.id.sp_state);
		sp_setting = (Button) view.findViewById(R.id.sp_set);
		mIcon = (CircleImageView) view.findViewById(R.id.sp_home);
		nName = (TextView) view.findViewById(R.id.sp_title);

		sp_setting.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.secondpsw);

		int pos = getArguments().getInt("myFamilyPos");
		info = DolphinApp.getInstance().getMyFamilyInfos().get(pos);

		nName.setText(info.getNickName());
		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();

		Bitmap head = task.loadImage(info.getDeviceID());
		if (head != null) {
			mIcon.setImageBitmap(head);
		}
		initSecPwState();
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public static SecondPswEnterFragment newInstance(int pos) {
		SecondPswEnterFragment fragment = new SecondPswEnterFragment();
		Bundle args = new Bundle();
		args.putSerializable("myFamilyPos", pos);
		fragment.setArguments(args);
		return fragment;
	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sp_set:
			SecondPswSettingFragment spsFragment = new SecondPswSettingFragment();
			spsFragment.setResultListener(new IifSecPswSet() {
				
				@Override
				public void ifSecPswSet(boolean isSet) {
					// TODO Auto-generated method stub
					setViewDis(isSet);
				}
			});
			Bundle bundle = new Bundle();
			bundle.putString("familyID", info.getFamilyID());
			bundle.putString("deviceid", info.getDeviceID());
			spsFragment.setArguments(bundle);
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.container, spsFragment);
			ft.addToBackStack(null);
			ft.commit();
			break;
		case R.id.actionbar_prev:
			getActivity().finish();
			break;

		default:
			break;
		}
	}

	private void initSecPwState() {
		String token = SysConfig.dtoken;
		String GetIfSecPWSeted = RequestString.GetIfSecPWSeted(token,
				info.getFamilyID());
		requestXml(GetIfSecPWSeted, "GetIfSecPWSeted");

	}
	/**
	 * 
	 * @param code 1:已设置  2：未设置
	 */
	private void setViewDis(boolean isSet){
		SharedPreferences share = getActivity()
				.getSharedPreferences(
						SysConfig.userid + "_"
								+ info.getDeviceID(), 0);
		if (isSet == true) {
			share.edit().putBoolean("HasSecPSw", true).commit();
			secpsw_icon.setImageDrawable(getResources()
					.getDrawable(
							R.drawable.ic_secondpassword_on));
			secpw_state.setText(R.string.protect_by_spw);
			secpw_state.setTextColor(getResources().getColor(
					R.color.color7));
		} else {
			share.edit().putBoolean("HasSecPSw", false)
					.commit();
			secpsw_icon.setImageDrawable(getResources()
					.getDrawable(
							R.drawable.ic_secondpassword_off));
			secpw_state.setText(R.string.not_set);
			secpw_state.setTextColor(getResources().getColor(
					R.color.color5));
		}
	}

	private void requestXml(String requestBody, final String request) {

		XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl + request,
				requestBody, new Response.Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						// TODO Auto-generated method stub
						String Code = XMLParser.responseCode(response);
						boolean isSet = Code.equals("1")? true : false;
						setViewDis(isSet);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.e("TAG", error.getMessage(), error);
					}
				});
		VolleyRequestQueueManager.addRequest(xmlRequest, null);
	}
}
