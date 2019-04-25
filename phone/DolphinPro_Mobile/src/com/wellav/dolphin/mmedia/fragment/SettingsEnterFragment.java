package com.wellav.dolphin.mmedia.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.netease.LogoutHelper;

public class SettingsEnterFragment extends BaseFragment implements OnClickListener {

	private Button message_alert;
	private Button psw_change;
	private Button about;
	private Button mLogout;
	private Button mQuit;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_settings_enter,
				container, false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		message_alert = (Button) view.findViewById(R.id.message_alert);
		psw_change = (Button) view.findViewById(R.id.psw_change);
		mLogout = (Button) view.findViewById(R.id.settings_logout);
		mQuit = (Button) view.findViewById(R.id.settings_quit);
		about = (Button) view.findViewById(R.id.about);

		message_alert.setOnClickListener(this);
		psw_change.setOnClickListener(this);
		about.setOnClickListener(this);
		mLogout.setOnClickListener(this);
		mQuit.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.setting);
		
		registerObservers(true);
		return view;
	}
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		registerObservers(false);
		super.onDestroy();
	}

    private void registerObservers(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
    }

    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            if (statusCode.wontAutoLogin()) {
                LogoutHelper.logout(getActivity(), true);
            }
        }
    };



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_alert:
			SettingsMessageFragment msf = new SettingsMessageFragment();
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.container, msf);
			ft.addToBackStack(null);
			ft.commit();
			break;
		case R.id.psw_change:
			SettingsPasswordFragment psf = new SettingsPasswordFragment();
			FragmentManager fm1 = getActivity().getSupportFragmentManager();
			FragmentTransaction ft1 = fm1.beginTransaction();
			ft1.replace(R.id.container, psf);
			ft1.addToBackStack(null);
			ft1.commit();
			break;
		case R.id.about:
			SettingsAboutFragment af = new SettingsAboutFragment();
			FragmentManager fm2 = getActivity().getSupportFragmentManager();
			FragmentTransaction ft2 = fm2.beginTransaction();
			ft2.replace(R.id.container, af);
			ft2.addToBackStack(null);
			ft2.commit();
			break;
		case R.id.settings_logout:
			// DolphinApp.getInstance().disposeSipRegister();
			// DolphinApp.getInstance().getLocalActManager().finishActivity();
			PreferenceUtils.getInstance().clearSharePreferencesAccount();
			//DolphinApp.getInstance().exit();
			
			LogoutHelper.logout(getActivity(), false);
			break;
		case R.id.settings_quit:
			PreferenceUtils.getInstance().saveSharePreferences(true, false);
			LogoutHelper.quit();
			break;
		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		default:
			break;
		}

	}
}
