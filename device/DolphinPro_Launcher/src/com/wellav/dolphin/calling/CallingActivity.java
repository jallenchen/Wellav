package com.wellav.dolphin.calling;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.CallDuration;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInform;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.fragment.CallingFragment;
import com.wellav.dolphin.fragment.DailAcceptFragment;
import com.wellav.dolphin.fragment.MeetingFragment;
import com.wellav.dolphin.fragment.MonitorFragment;
import com.wellav.dolphin.interfaces.ICallingVideo;
import com.wellav.dolphin.launcher.AddCallingMemberActivity;
import com.wellav.dolphin.launcher.BaseActivity;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.UploadData2Server;
import com.wellav.dolphin.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.utils.JsonUtils;
import com.wellav.dolphin.utils.NameUtils;
import com.wellav.dolphin.utils.Util;

public class CallingActivity extends BaseActivity {

	private static final String TAG = "CallingActivity";
	private FamilyUserInfo user;
	private ICallingVideo viewListener;
	private DailAcceptFragment DailAccept;
	private CallingFragment Calling;
	private MonitorFragment Monitor;
	private MeetingFragment Meeting;
	private int callType = 0;
	private boolean isMute = false;
	private boolean mAccept = false;
	private String userName = "";
	private CallDuration mCallTime;
	boolean CallTimeRecord = false;
	private Context ctx;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Util.wakeUpAndUnlock(this);
		this.ctx = CallingActivity.this;
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		sendCalling(MsgKey.Broadcast_start_videoPhone);
		getIntentData();
	}

	@Override
	protected void onResume() {
		Util.PrintLog(TAG, "onResume()");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		HBaseApp.post2WorkRunnable(new Runnable() {
			@Override
			public void run() {
				sendCalling(MsgKey.Broadcast_end_videoPhone);
			}
		});

		NameUtils.remoteNamesClean();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (Monitor != null) {
		}
		super.onBackPressed();
	}

	private void getIntentData() {

		Bundle bundle = getIntent().getExtras();
		callType = bundle.getInt("CallType");
		userName = bundle.getString("CallName");
		Util.PrintLog(TAG, "getIntentData():" + callType +":"+ userName);
		if (callType == SysConfig.ChatAccept
				|| callType == SysConfig.MutilAccept
				|| callType == SysConfig.ChatCall) {
			newAcceptFragment(callType, userName);
			if (callType == SysConfig.MutilAccept) {
				// uploadChatMutilAcceptMsg(callType);
			}

		} else if (callType == SysConfig.MutilCall) {
			newCallingFragment(callType);
			// uploadChatMutilAcceptMsg(callType);
		} else if (callType == SysConfig.MonitorCall) {
			newInstanceMonitor(userName);
		} else if (callType == SysConfig.MeetingCall) {
			newMeetingFragment();
		} else if (callType == SysConfig.MeetingAccept) {
			newAcceptFragment(callType, userName);
		} else if (callType == SysConfig.MutilJoin) {
			newCallingFragment(callType);
			callType = SysConfig.MutilAccept;
		} else if (callType == SysConfig.MeetingJoin) {
			newMeetingFragment();
			callType = SysConfig.MeetingAccept;
		}

	}

	private void newAcceptFragment(int calltype, String userName) {
		user = LauncherApp.getInstance().getDBHelper()
				.getFamilyUser(this, userName);
		if(user.getLoginName() == null){
			finish();
			return;
		}
		
		DailAccept = new DailAcceptFragment().newInstance(user);
		Bundle bundle = new Bundle();
		bundle.putInt("CallType", calltype);
		DailAccept.setArguments(bundle);
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, DailAccept);
		ft.commit();
	}

	private void newCallingFragment(int calltype) {
		Calling = new CallingFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("CallType", calltype);
		Calling.setArguments(bundle);
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, Calling);
		ft.commit();
	}

	private void newInstanceMonitor(String name) {
		Monitor = new MonitorFragment();
		Bundle bundle = new Bundle();
		bundle.putString("CallName", name);
		Monitor.setArguments(bundle);
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, Monitor);
		ft.commit();
	}

	private void newMeetingFragment() {
		Meeting = MeetingFragment.newInstance(userName, callType);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.container, Meeting);
		ft.commit();
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		try {
			viewListener = (ICallingVideo) fragment;
		} catch (Exception e) {
			throw new ClassCastException(this.toString()
					+ " must implement OnViewListener");
		}
		super.onAttachFragment(fragment);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		if (data != null) {
			String checkedName = data.getStringExtra("InviteName");

			Util.PrintLog(TAG, "checkedName:" + checkedName);
			if (callType == SysConfig.ChatCall
					|| callType == SysConfig.ChatAccept) {
				if (!checkedName.equals("")) {
					NameUtils.remoteNamesAdd(checkedName);
				}

				callType = SysConfig.MutilCall;
			} else {
				if (!checkedName.equals("")) {
					NameUtils.remoteNamesAdd(checkedName);
				}

			}
		}
		super.onActivityResult(arg0, arg1, data);
	}

	/**
	 * 邀请群聊 通话中邀请群聊
	 * 
	 * @param view
	 */
	public void onAddMember(View view) {
		Intent intent = new Intent(CallingActivity.this,
				AddCallingMemberActivity.class);
		startActivityForResult(intent, 0);
	}

	/**
	 * 静音 mute true 静音 mute false 非静音
	 * 
	 * @param view
	 */
	public void onMute(View view) {
	}

	/**
	 * 截屏
	 * 
	 * @param view
	 */
	public void onCaptureWnd(View view) {
	}

	/**
	 * 拒绝呼叫
	 * 
	 * @param view
	 */
	public void onRefused(View view) {
	}

	/**
	 * 结束通话
	 * 
	 * @param view
	 */
	public void onHangUp(View view) {
	}

	/**
	 * 
	 * @param view
	 */
	public void onAccept(View view) {

	}

	private void upLoadPhoto(final String path) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				try {
					url = new URL(SysConfig.ServerUrl + SysConfig.UploadPhotoUrl);
					URLConnection rulConnection;
					rulConnection = url.openConnection();
					HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
					httpUrlConnection.setConnectTimeout(20000);
					httpUrlConnection.setReadTimeout(20000);
					httpUrlConnection.setDoOutput(true);
					httpUrlConnection.setUseCaches(false);
					httpUrlConnection.setRequestProperty("Content-type",
							"text/xml; charset=utf-8");
					httpUrlConnection.setRequestProperty("connection", "close");
					httpUrlConnection.setRequestMethod("POST");
					httpUrlConnection.connect();
					OutputStream outStrm = httpUrlConnection.getOutputStream();
					BufferedOutputStream bos = new BufferedOutputStream(outStrm);

					StringBuilder sb = new StringBuilder(
							SysConfig.UploadPhotoRequest);
					sb.append("<Token>").append(SysConfig.DolphinToken)
							.append("</Token>");
					sb.append("<FamilyID>").append(SysConfig.DeviceFamilyId)
							.append("</FamilyID>");
					sb.append("<Description>").append(ctx.getString(R.string.chat))
							.append("</Description>");
					sb.append("<PhotoType>").append(2).append("</PhotoType>");

					bos.write(sb.toString().getBytes("utf-8"));
					Bitmap bitmap = null;

					sb.delete(0, sb.length());
					sb.append("<FileExt>").append(".jpg").append("</FileExt>");
					bos.write(sb.toString().getBytes("utf-8"));
					bitmap = BitmapFactory.decodeFile(path);
					Log.i("upload", "path:" + path);
					String bs64 = Util.bitmapToBase64(bitmap);
					bitmap.recycle();
					bitmap = null;
					bos.write("<Content>".getBytes("utf-8"));
					Log.i("upload", "bs=" + bs64.length());
					bos.write(bs64.getBytes("utf-8"));
					bos.write("</Content>".getBytes("utf-8"));

					sb.delete(0, sb.length());

					sb.append(SysConfig.UploadPhotoRequestEnd);
					bos.write(sb.toString().getBytes("utf-8"));
					bos.flush();
					bos.close();
					outStrm.close();
					int code = httpUrlConnection.getResponseCode();
					Log.i("upload", "code=" + code);
					if (code == 200) {

						InputStream inStrm = httpUrlConnection.getInputStream();
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(inStrm));
						String line;
						StringBuilder sbResult = new StringBuilder();
						while ((line = bufferedReader.readLine()) != null) {
							sbResult.append(line).append("\n");
						}
						Log.i("upload", "result=" + sbResult);
						// 成功
						if (sbResult.toString().contains("<n:Code>0</n:Code>")) {

							runOnUiThread(new Runnable() {
								public void run() {
									Util.DisplayToast(getApplicationContext(),
											ctx.getString(R.string.vedio_recording_success));
								}
							});
						}
						bufferedReader.close();
						inStrm.close();
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								Util.DisplayToast(getApplicationContext(),
										ctx.getString(R.string.fail_upload_picture));
							}
						});
					}

				} catch (Exception e) {
					Log.i("upload", "HttpURLConnection err=" + e.toString());
					runOnUiThread(new Runnable() {
						public void run() {
							Util.DisplayToast(getApplicationContext(),
									ctx.getString(R.string.fail_upload_chat_picture));
						}
					});
				}
			}
		}).start();

	}

	/**
     * 
     */




	public void sendCalling(String action) {
		Log.e("CallingActivity", "action: " + action);
		Intent intent = new Intent(action);
		sendBroadcast(intent);
	}


	private void uploadMsg(int type,FamilyUserInfo user) {
	}
	
	private void uploadrejectMsg(int type,FamilyUserInfo user) {
	}

	public void closeUI(int time) {
		Log.e(TAG, "closeUI");
		HBaseApp.post2UIDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, time);
	}
}
