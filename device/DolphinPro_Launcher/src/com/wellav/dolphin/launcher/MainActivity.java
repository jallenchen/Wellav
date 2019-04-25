package com.wellav.dolphin.launcher;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.fragment.DeskTopFragment;
import com.wellav.dolphin.utils.BooleanInterProcessSharedPreference;
import com.wellav.dolphin.utils.IntegerInterProcessSharedPreference;
import com.wellav.dolphin.utils.NetWorkUtil;

@SuppressWarnings("deprecation")
public class MainActivity extends BaseActivity {
	private static final int Mode_Clock = 0;
	private static final int Mode_Ablume = 1;
	private static final int Mode_ScreenOff = 2;
	private BroadcastReceiver mBr;
	private KeyguardManager mKeyguardManager = null;
	private KeyguardLock mKeyguardLock = null;

	private DeskTopFragment mFrag;
	public static final boolean DEBUG = true;
	private Timer mTimer;
	
	// 网络是否连接，连接了才判断外网
	private static boolean mIsConnectted = true;
	// 有新消息 注意： 本地广播
	private boolean mIsBackground = false;

	public static boolean ismIsConnectted() {
		return mIsConnectted;
	}

	public static void setmIsConnectted(boolean mIsConnectted) {
		MainActivity.mIsConnectted = mIsConnectted;
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//super.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startListenBroadcast();
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock = mKeyguardManager.newKeyguardLock("");
		mKeyguardLock.disableKeyguard();
		mFrag= new DeskTopFragment();
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, mFrag);
		ft.commit();
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (mIsConnectted) {
					URL url;
					try {
						url = new URL(SysConfig.ServerUrl + SysConfig.IfExistMessage);
						URLConnection rulConnection;
						rulConnection = url.openConnection();
						HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
						httpUrlConnection.setConnectTimeout(20000);
						httpUrlConnection.setReadTimeout(20000);
						httpUrlConnection.setDoOutput(true);
						httpUrlConnection.setUseCaches(false);
						httpUrlConnection.setRequestProperty("Content-type", "text/xml; charset=utf-8");
						httpUrlConnection.setRequestProperty("connection", "close");
						httpUrlConnection.setRequestMethod("POST");
						httpUrlConnection.connect();
						OutputStream outStrm = httpUrlConnection.getOutputStream();
						BufferedOutputStream bos = new BufferedOutputStream(outStrm);

						StringBuilder sb = new StringBuilder(
								SysConfig.IfExistMessageRequest);
						sb.append("<Token>");
						sb.append(SysConfig.DolphinToken);
						sb.append("</Token>");
						sb.append(SysConfig.IfExistMessageRequestEnd);
						bos.write(sb.toString().getBytes("utf-8"));
						bos.flush();
						bos.close();
						outStrm.close();
						int code = httpUrlConnection.getResponseCode();
						if (code == 200) {
							sendBroadcast(new Intent(SysConfig.ACTION_BR_NET_OUTABLE));
							InputStream inStrm = httpUrlConnection.getInputStream();
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStrm));
							String line;
							StringBuilder sbResult = new StringBuilder();
							while ((line = bufferedReader.readLine()) != null) {
								sbResult.append(line).append("\n");
							}
							// 有新消息
							if (sbResult.toString().contains("<n:IfExist>1</n:IfExist>")) {
								LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplication());
								lbm.sendBroadcast(new Intent(SysConfig.ACTION_BR_MSG_NEW));
							}
							bufferedReader.close();
							inStrm.close();
						}
						sendBroadcast(new Intent(SysConfig.ACTION_BR_NET_OUTABLE));

					} catch (Exception e) {
						Log.e("mainActivity", "HttpURLConnection err=" + e.toString());
						sendBroadcast(new Intent(SysConfig.ACTION_BR_NET_UNOUTABLE));
					}

				}
			}
		}, 0, 30000);
	}

	@Override
	protected void onStop() {
		mIsBackground = true;
		super.onStop();
	}

	@Override
	protected void onResume() {
		mIsBackground = false;
		super.onResume();
	}

	private void startListenBroadcast() {
		mBr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_SCREEN_OFF)) {
					
					IntegerInterProcessSharedPreference ipsp = new IntegerInterProcessSharedPreference();
					
					int mode = ipsp.getConfig(getApplicationContext(), SysConfig.DOLPHIN_ACTIVITY_ACTION, "standby",
							"mode_sp");
					if (mode == Mode_ScreenOff )
						return;
					if (mode ==Mode_Clock) {

						Integer standby_time_data = ipsp.getConfig(getApplicationContext(), "com.wellav.dolphin.activity",
								"standby", "duration_sp");
						Intent i = new Intent();
						i.putExtra("sb",standby_time_data.intValue());
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.setClass(context, ClockActivity.class);
						context.startActivity(i);
					} else if (mode==Mode_Ablume) {
						BooleanInterProcessSharedPreference ipsp2 = new BooleanInterProcessSharedPreference();
						Boolean is_photo_info = ipsp2.getConfig(getApplicationContext(), SysConfig.DOLPHIN_ACTIVITY_ACTION,
								"standby", "is_photo_info");
						Integer which_photo_sp = ipsp.getConfig(getApplicationContext(), SysConfig.DOLPHIN_ACTIVITY_ACTION,
								"standby", "which_photo_sp");
						Integer standby_time_data = ipsp.getConfig(getApplicationContext(), SysConfig.DOLPHIN_ACTIVITY_ACTION,
								"standby", "playing_how_long_sp");
						Integer interval_sp = ipsp.getConfig(getApplicationContext(), SysConfig.DOLPHIN_ACTIVITY_ACTION,
								"standby", "interval_sp");
						// 该应用的包名
						String pkg = SysConfig.MEDIA_ACTIVITY_ACTION;
						// 应用的主activity类
						StringBuilder sb = new StringBuilder(pkg);
						sb.append(".");
						sb.append("AutoGalleryActivity");
						ComponentName componet = new ComponentName(pkg, sb.toString());
						Intent i = new Intent();
						i.putExtra("sb",standby_time_data.intValue());
						i.putExtra("is_photo_info", is_photo_info);
						i.putExtra("which_photo_sp", which_photo_sp.intValue());
						i.putExtra("interval_sp", interval_sp.intValue());
						i.setComponent(componet);
						startActivity(i);
					}

				} else if (action.equals(SysConfig.SCREEN_SAVER_TIMEOUT_MSG)) {
					if (!DEBUG) {
//						PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//						pm.goToSleep(SystemClock.uptimeMillis());
					}
				} else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					mIsConnectted = NetWorkUtil.isNetworkConnected(getApplicationContext());
					sendBroadcast(new Intent(mIsConnectted ? SysConfig.ACTION_BR_NET_AVAILABLE : SysConfig.ACTION_BR_NET_UNAVAILABLE));
				}

			}
		};

		IntentFilter iF = new IntentFilter();
		iF.addAction(Intent.ACTION_SCREEN_OFF);
		iF.addAction(SysConfig.SCREEN_SAVER_TIMEOUT_MSG);
		iF.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mBr, iF);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBr);
		mKeyguardLock.reenableKeyguard();
		mTimer.cancel();
		mTimer = null;
		super.onDestroy();
	}
}
