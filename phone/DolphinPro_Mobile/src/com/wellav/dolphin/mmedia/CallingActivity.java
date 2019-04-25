package com.wellav.dolphin.mmedia;


public class CallingActivity extends BaseActivity {

//	public final static String BROADCAST_CALLING_ACTION = "com.wellav.calling";
//
//	private static final String TAG = "CallingActivity";
//	private FamilyUserInfo user;
//	private ICallingVideo viewListener;
//	private DailOrAccepterFragment DailAccept;
//	private CallingFragment Calling;
//	private CallingMonitorFragment Monitor;
//	private CallingMeetingFragment Meeting;
//	private int callType = 0;
//	private boolean isMute = false;
//	private boolean isRear = false;
//	private boolean isRecord = false;
//	private boolean mAccept = false;
//	private String userName = "";
//	private CountTimeThread mCountTimeThread;
//	private boolean  isTimeThread  = false;
//
//	@Override
//	public void onAttachFragment(Fragment fragment) {
//		try {
//			viewListener = (ICallingVideo) fragment;
//		} catch (Exception e) {
//			throw new ClassCastException(this.toString()
//					+ " must implement OnViewListener");
//		}
//		super.onAttachFragment(fragment);
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		setContentView(R.layout.main);
//		registerReceiver(receiver, new IntentFilter(BROADCAST_CALLING_ACTION));
//		CommFunc.PrintLog(5, LOGTAG, getResources().getString(R.string.wakeLock_keep_awake));
//		getIntentData();
//		
//
//	}
//
//	private void getIntentData() {
//
//		Bundle bundle = getIntent().getExtras();
//		callType = bundle.getInt("CallType");
//		userName = bundle.getString("CallName");
//		CommFunc.PrintLog(TAG, "getIntentData()" + callType);
//
//		if (callType == SysConfig.ChatAccept
//				|| callType == SysConfig.MutilAccept
//				|| callType == SysConfig.ChatCall) {
//			newAcceptFragment(callType, userName);
//		} else if (callType == SysConfig.MutilCall) {
//			newCallingFragment(callType);
//		} else if (callType == SysConfig.MonitorAccept) {
//			newInstanceMonitor();
//		} else if (callType == SysConfig.MeetingAccept) {
//			newAcceptFragment(callType, userName);
//			// uploadMeetingAcceptMsg();
//		} else if (callType == SysConfig.MutilJoin) {
//			newCallingFragment(callType);
//			callType = SysConfig.MeetingAccept;
//		} else if (callType == SysConfig.MeetingJoin) {
//			newMeetingFragment();
//			DolphinApp.getInstance().onConfCallAccept();
//			callType = SysConfig.MeetingAccept;
//		}
//
//	}
//	/**
//	 * 拨打和接听的提示界面
//	 * @param calltype
//	 * @param userName
//	 */
//	private void newAcceptFragment(int calltype, String userName) {
//		user = SQLiteManager.getInstance().geFamilyUserInfoLoginName(userName);
//		if(user.getLoginName() == null){
//			DolphinApp.getInstance().onAllCallHangup();
//			finish();
//			return;
//		}
//		DailAccept = DailOrAccepterFragment.newInstance();
//		Bundle bundle = new Bundle();
//		bundle.putInt("CallType", calltype);
//		bundle.putSerializable("userinfo", user);
//		DailAccept.setArguments(bundle);
//		FragmentManager fm = this.getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		ft.replace(R.id.container, DailAccept);
//		ft.commit();
//	}
//	/**
//	 * 群聊和点对点的通话解密那
//	 * @param calltype
//	 */
//	private void newCallingFragment(int calltype) {
//		user = SQLiteManager.getInstance().geFamilyUserInfoLoginName(userName);
//		Calling = new CallingFragment();
//		Bundle bundle = new Bundle();
//		bundle.putInt("CallType", calltype);
//		Calling.setArguments(bundle);
//		FragmentManager fm = this.getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		ft.replace(R.id.container, Calling);
//		ft.commit();
//	}
//	/**
//	 * 观看界面
//	 */
//	private void newInstanceMonitor() {
//		Monitor = new CallingMonitorFragment();
//		FragmentManager fm = this.getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		ft.replace(R.id.container, Monitor);
//		ft.commit();
//	}
//	/**
//	 * 会议模式的界面
//	 */
//	private void newMeetingFragment() {
//		Meeting = new CallingMeetingFragment();
//		FragmentManager fm = this.getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		ft.replace(R.id.container, Meeting);
//		ft.commit();
//	}
//
//	@Override
//	protected void onDestroy() {
//		isTimeThread = false;
//		mCountTimeThread = null;
//		unregisterReceiver(receiver);
//		NameUtils.remoteNamesClean();
//		SysConfig.getInstance().setStatus(SysConfig.Free);
//		super.onDestroy();
//	}
//
//	@Override
//	public void onBackPressed() {
//		DolphinApp.getInstance().onAllCallHangup();
//		super.onBackPressed();
//	}
//
//
//	@Override
//	protected void onActivityResult(int arg0, int arg1, Intent data) {
//		//邀请群聊
//		if (data != null) {
//			String checkedName = data.getStringExtra("InviteName");
//			CommFunc.PrintLog(TAG, checkedName);
//			if (callType == SysConfig.ChatCall
//					|| callType == SysConfig.ChatAccept) {
//				DolphinApp.getInstance().onCallHangup();
//				NameUtils.remoteNamesAdd(checkedName);
//				callType = SysConfig.MutilCall;
//				Calling.createMultiCall();
//			} else {
//				NameUtils.remoteNamesAdd(checkedName);
//				Calling.onOpt_InvitedMebmer(checkedName);
//				Calling.onOpt_Mdisp();
//			}
//		}
//		super.onActivityResult(arg0, arg1, data);
//	}
//
//	/**
//	 * 邀请群聊 通话中邀请群聊
//	 * 
//	 * @param view
//	 */
//	public void onAddMember(View view) {
//		HBaseApp.post2WorkRunnable(new Runnable() {
//			
//			@Override
//			public void run() {
//				Intent intent = new Intent(CallingActivity.this,
//						AddingCallingFragmentActivity.class);
//				startActivityForResult(intent, 0);
//			}
//		});
//	
//	}
//
//	/**
//	 * 静音 mute true 静音 mute false 非静音
//	 * 
//	 * @param view
//	 */
//	public void onMute(View view) {
//		Connection Call = DolphinApp.getInstance().getCall();
//		if (Call == null) {
//			return;
//		}
//		isMute = !isMute;
//		view.setSelected(isMute);
//
//		Call.setMuted(isMute);
//	}
//
//	/**
//	 * 截屏
//	 * 
//	 * @param view
//	 */
//	public void onCaptureWnd(View view) {
//		String path = DolphinUtil.getCaptureFilePath();
//		Connection mCall = DolphinApp.getInstance().getCall();
//		if (mCall == null) {
//			return;
//		}
//		int ret = mCall.takeRemotePicture(path); // 0
//		if (ret == 0) {
//			upLoadPhoto(path);
//		} else {
//			CommFunc.DisplayToast(this, getResources().getString(R.string.screen_shot_fail));
//		}
//	}
//	
//	/**
//	 * 截屏图片的上传服务器
//	 * @param path
//	 */
//	private void upLoadPhoto(final String path){
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				URL url;
//				try {
//					url = new URL(SysConfig.ServerUrl + "UploadPhoto");
//					URLConnection rulConnection;
//					rulConnection = url.openConnection();
//					HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
//					httpUrlConnection.setConnectTimeout(20000);
//					httpUrlConnection.setReadTimeout(20000);
//					httpUrlConnection.setDoOutput(true);
//					httpUrlConnection.setUseCaches(false);
//					httpUrlConnection.setRequestProperty("Content-type", "text/xml; charset=utf-8");
//					httpUrlConnection.setRequestProperty("connection", "close");
//					httpUrlConnection.setRequestMethod("POST");
//					httpUrlConnection.connect();
//					OutputStream outStrm = httpUrlConnection.getOutputStream();
//					BufferedOutputStream bos = new BufferedOutputStream(outStrm);
//
//					StringBuilder sb = new StringBuilder(
//							"<UploadPhotoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
//					sb.append("<Token>").append(SysConfig.dtoken).append("</Token>");
//					sb.append("<FamilyID>").append(user.getFamilyID()).append("</FamilyID>");
//					sb.append( "<Description>").append(" ").append("</Description>");
//					sb.append("<PhotoType>").append(2).append("</PhotoType>");
//					
//					bos.write(sb.toString().getBytes("utf-8"));
//					Bitmap bitmap=null;
//					
//						sb.delete(0, sb.length());
//						sb.append("<FileExt>").append(".jpg").append("</FileExt>");
//						bos.write(sb.toString().getBytes("utf-8"));
//						bitmap = BitmapFactory.decodeFile(path);
//						Log.i("upload", "path:"+path);
//						String bs64 = Base64Util.bitmapToBase64(
//								bitmap).replaceAll("\r|\n", "");
//						bitmap.recycle();
//						bitmap=null;
//						bos.write("<Content>".getBytes("utf-8"));
//						Log.i("upload", "bs="+bs64.length());
//						bos.write(bs64.getBytes("utf-8"));
//						bos.write("</Content>".getBytes("utf-8"));
//					
//					sb.delete(0, sb.length());	
//					
//					sb.append("</UploadPhotoRequest>");
//					bos.write(sb.toString().getBytes("utf-8"));
//					bos.flush();
//					bos.close();
//					outStrm.close();
//					int code = httpUrlConnection.getResponseCode();
//					Log.i("upload", "code=" + code);
//					if (code == 200) {
//						
//						InputStream inStrm = httpUrlConnection.getInputStream();
//						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStrm));
//						String line;
//						StringBuilder sbResult = new StringBuilder();
//						while ((line = bufferedReader.readLine()) != null) {
//							sbResult.append(line).append("\n");
//						}
//						Log.i("upload", "result=" + sbResult);
////						成功
//						if (sbResult.toString().contains("<n:Code>0</n:Code>")) {
//							
//							runOnUiThread(new  Runnable() {
//								public void run() {
//									CommFunc.DisplayToast(getApplicationContext(),getResources().getString(R.string.screen_shot_ok));
//								}
//							});
//						}
//						bufferedReader.close();
//						inStrm.close();
//					}else{
//						runOnUiThread(new  Runnable() {
//							public void run() {
//								CommFunc.DisplayToast(getApplicationContext(),getResources().getString(R.string.upload_photo_fail));
//							}
//						});
//					}
//					
//
//				} catch (Exception e) {
//					Log.i("upload", "HttpURLConnection err=" + e.toString());
//					runOnUiThread(new  Runnable() {
//						public void run() {
//							CommFunc.DisplayToast(getApplicationContext(),getResources().getString(R.string.upload_photo_fail));
//						}
//					});
//				}
//			}
//		}).start();
//		
//	}
//	
//
//	/**
//	 * 切换摄像头
//	 * 
//	 * @param view
//	 */
//	public void onSwitchCamera(View view) {
//		isRear = !isRear;
//		
//		HBaseApp.post2WorkRunnable(new Runnable() {
//
//			@Override
//			public void run() {
//				 Connection mCall = DolphinApp.getInstance().getCall();
//				if (mCall == null) {
//					return;
//				}
//				mCall.setCamera(isRear ? 0 : 1);
//			}
//		});
//	}
//
//	/**
//	 * 录制
//	 * 
//	 * @param view
//	 */
//	public void onRecordView(View view) {
//		if(DolphinApp.getInstance().getCall() == null){
//			return;
//		}
//		isRecord = !isRecord;
//		view.setSelected(isRecord);
//		callRecordVideo(isRecord);
//		Calling.setChronometer(isRecord);
//	}
//
//	/**
//	 * 拒绝呼叫
//	 * 
//	 * @param view
//	 */
//	public void onRefused(View view) {
//	 if(DolphinApp.getInstance().getCall() != null){
//			DolphinApp.getInstance().getCall().reject();
//			DolphinApp.getInstance().clearCall();
//			// uploadChatMutilAcceptMsg(callType);
//		}
//		SysConfig.getInstance().setbIncoming(false);
//
//		closeUI(500);
//	}
//
//	/**
//	 * 结束通话
//	 * 
//	 * @param view
//	 */
//	public void onHangUp(View view) {
//		CommFunc.PrintLog(TAG, "onHangUp()");
//		if (Calling != null) {
//			if (isRecord == true) {
//				CommFunc.DisplayToast(this,getResources().getString(R.string.taking_vedio));
//				return;
//			}
//		}
//
//		DolphinApp.getInstance().onAllCallHangup();
//		closeUI(500);
//	}
//
//	/**
//	 * 接听通话
//	 * @param view
//	 */
//	public void onAccept(View view) {
//		NameUtils.remoteNamesClean();
//		mAccept = true;
//		if(callType == SysConfig.ChatAccept){
//			NameUtils.remoteNamesAdd(userName);
//			newCallingFragment(callType);
//			DolphinApp.getInstance().onCallAccept();
//		}else  if (callType == SysConfig.MeetingAccept) {
//			newMeetingFragment();
//			DolphinApp.getInstance().onConfCallAccept();
//		} else {
//			NameUtils.remoteNamesAdd(userName);
//			newCallingFragment(callType);
//			DolphinApp.getInstance().onConfCallAccept();
//			// uploadChatMutilAcceptMsg(callType);
//		}
//
//	};
//	
//	/**
//	 * 视频录制的处理
//	 * @param isRecording
//	 */
//	private void callRecordVideo(final boolean isRecording) {
//		CommFunc.PrintLog(TAG, "isRecording:" + isRecording);
//		HBaseApp.post2WorkRunnable(new Runnable() {
//
//			@Override
//			public void run() {
//				Connection mCall = DolphinApp.getInstance().getCall();
//				if(mCall == null){
//					return;
//				}
//				if (isRecording) {
//					String folder = SysConfig.getInstance().getRecordFolder();
//					File path = new File(folder);
//					if (!path.exists()) {
//						path.mkdirs();
//					}
//					if (mCall != null) {
//						mCall.callRecordStart(folder);
//					}
//
//				} else {
//					if (mCall != null) {
//						mHandler.sendEmptyMessage(0);
//						int code = mCall.callRecordStop();
//						if (code == 0) {
//							mHandler.sendEmptyMessage(1);
//						} else {
//							mHandler.sendEmptyMessage(2);
//						}
//
//					}
//				}
//
//			}
//		});
//
//	}
//
//	@SuppressLint("HandlerLeak")
//	Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 0:
//				loadingDialog(getResources().getString(R.string.close_taking_vedio));
//				break;
//			case 1:
//				CommFunc.DisplayToast(CallingActivity.this,getResources().getString(R.string.close_ok));
//				dismissLoadDialog();
//				break;
//			case 2:
//				CommFunc.DisplayToast(CallingActivity.this,getResources().getString(R.string.close_fail));
//				dismissLoadDialog();
//				break;
//			}
//			super.handleMessage(msg);
//		}
//
//	};
//
//	/**
//	 * 接收通话的消息广播
//	 */
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//		@SuppressLint("NewApi")
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(BROADCAST_CALLING_ACTION)) {
//
//				Bundle bundle = intent.getExtras();
//				if (bundle != null) {
//					Message msg = new Message();
//					msg.what = bundle.getInt("what", 0);
//					int msgtype = bundle.getInt("arg2",
//							MsgKey.grpv_listener_onResponse); //
//
//					if (msgtype == MsgKey.grpv_listener_onRequest) {
//						BroadMsg_OnRequest(bundle.getString("arg1"));
//					} else if (msgtype == MsgKey.grpv_listener_onResponse) {
//						BroadMsg_OnResponse(msg.what, bundle.getString("arg1"));
//					} else if (msgtype == MsgKey.broadmsg_sip) {
//						msg.arg1 = bundle.getInt("arg1", 0);
//						if (bundle.containsKey("info"))
//							msg.obj = bundle.getString("info");
//						handleCallMessage(msg);
//					} else {
//						// Util.PrintLog(TAG, "unknowmsg broad");
//						if (bundle.containsKey("info"))
//							msg.obj = bundle.getString("info");
//						//handleCallMessage(msg);
//					}
//
//				}
//			}
//		}
//	};
//
//	private void BroadMsg_OnRequest(String params) {
//		CommFunc.PrintLog(TAG, "BroadMsg_OnRequest:" + "  params:" + params);
//		// 需要解析notify 刷新成员状态
//		if (Calling != null) {
//			Calling.ParseNotifyInfo(params);
//		}
//		if (Meeting != null) {
//			viewListener.opt_Request(params);
//		}
//
//	}
//	/**
//	 * 加入群聊的成功或是失败的反馈
//	 * @param parameters
//	 */
//	private void onResponse_grpJoin(String parameters) {
//		try {
//			Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin:" + parameters);
//			if (parameters == null || parameters.equals(""))
//				return;
//			JSONObject jsonrsp = new JSONObject(parameters);
//			if (jsonrsp.isNull("code") == false) {
//				String code = jsonrsp.getString(RtcConst.kcode);
//				String reason = jsonrsp.getString(RtcConst.kreason);
//				Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin code:" + code
//						+ " reason:" + reason);
//				if (code.equals("0")) {
//					Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin:success");
//				} else {
//					Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin:fail");
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void BroadMsg_OnResponse(int action, String params) {
//		switch (action) {
//		case RtcConst.groupcall_opt_join:
//			onResponse_grpJoin(params);
//			break;
//		}
//		if (Meeting != null) {
//			viewListener.opt_Response(action, params);
//		}
//	}
//
//	/**
//	 * 处理消息
//	 * @param msg
//	 */
//	private void handleCallMessage(Message msg) {
//		Log.e("CallingActivity", "handleCallMessage msg.what: " + msg.what);
//		switch (msg.what) {
//		case MsgKey.SLN_180Ring:
//			DailAccept.playRing(R.raw.ring180, false); // 正在呼叫铃声
//			break;
//		case MsgKey.SLN_CallAccepted:
//			NameUtils.remoteNamesAdd(userName);
//			if (DailAccept != null) {
//				DailAccept.closeUI(500);
//				DailAccept = null;
//			}
//			newCallingFragment(callType);
//			break;
//		case MsgKey.SLN_CallVideo:
//			SysConfig.getInstance().setStatus(callType);
//			viewListener.ViewAction(callType);
//			startCountTimeThread();
//			break;
//		case MsgKey.SLN_CallClosed:
//			DolphinApp.getInstance().onAllCallHangup();
//			finish();
//			break;
//		case MsgKey.SLN_CallHasAccepted:
//			break;
//		case MsgKey.SLN_CallFailed:
//			SysConfig.getInstance().setStatus(SysConfig.Free);
//			Log.e("CallingActivity", "handleCallMessage SLN_CallFailed: "
//					+ msg.arg1);
//			if (mAccept == false && callType == SysConfig.ChatAccept) {
//				// uploadChatMutilAcceptMsg(callType);
//			}
//
//			if (callType == SysConfig.ChatAccept
//					|| callType == SysConfig.MutilAccept || callType == SysConfig.MeetingAccept) {
//				closeUI(0);
//				break;
//			}
//
//			if (SysConfig.getInstance().isCalling() == false && DailAccept != null) {
//				if (msg.arg1 == RtcConst.CallCode_Busy ) { // 用户忙486
//					DailAccept.playRing(R.raw.ring486, true);
//					closeUI(3000);
//				} else if (msg.arg1 == RtcConst.CallCode_Reject) { // 正在通话 603
//					DailAccept.playRing(R.raw.ring486, true);
//					closeUI(3000);
//				} else if (msg.arg1 >= RtcConst.CallCode_RequestErr
//						&& msg.arg1 < RtcConst.CallCode_ServerErr) { // 408铃声-对方无法接通.lkkkk
//					DailAccept.playRing(R.raw.ring4xx, true);
//					closeUI(3000);
//				} else if (msg.arg1 >= RtcConst.CallCode_ServerErr
//						&& msg.arg1 < RtcConst.CallCode_GlobalErr) {
//					DailAccept.playRing(R.raw.ring4xx, true);
//					closeUI(3000);
//				} else if (msg.arg1 >= RtcConst.CallCode_GlobalErr) {
//					DailAccept.playRing(R.raw.ring4xx, true);
//					closeUI(3000);
//				} else {
//					closeUI(0);
//				}
//			} else {
//				closeUI(0);
//			}
//			break;
//		case MsgKey.SLN_NetWorkChange:
//			break;
//		case MsgKey.SLN_WebRTCStatus:
//			break;
//		case MsgKey.SLN_Monitor2ChatCall:
//			callType = SysConfig.ChatCall;
//			newAcceptFragment(callType, userName);
//			break;
//		case MsgKey.SLN_NewCallMessage:
//			Toast.makeText(CallingActivity.this, "新的来电已被拒接，请通话结束后回拨", Toast.LENGTH_LONG).show();
//			break;
//		}
//	}
//
//	public void closeUI(int time) {
//
//		HBaseApp.post2UIDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				Log.e(TAG, "closeUI");
//				if (DailAccept != null) {
//					DailAccept.closeUI(0);
//				}
//
//				finish();
//			}
//
//		}, time);
//	}
//
//	/**
//	 * 开始启动线程控制按钮组件的显示.
//	 */
//	private void startCountTimeThread() {
//		isTimeThread = true;
//		mCountTimeThread = new CountTimeThread(5);
//		mCountTimeThread.start();
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (event.getAction() == MotionEvent.ACTION_DOWN
//				&& mCountTimeThread != null) {
//			// 重置mControllButtonLayout已经显示的时间
//			mCountTimeThread.reset();
//
//			viewListener.setViewUIVisible(View.VISIBLE);
//			return true;
//
//		}
//
//		return super.onTouchEvent(event);
//	}
//
//	private class CountTimeThread extends Thread {
//		private final long maxVisibleTime;
//		private long startVisibleTime;
//
//		/**
//		 * @param second
//		 *            设置按钮控件最大可见时间,单位是秒
//		 */
//		public CountTimeThread(int second) {
//			// 将时间换算成毫秒
//			maxVisibleTime = second * 1000;
//
//			// 设置为后台线程.
//			setDaemon(true);
//		}
//
//		/**
//		 * 每当界面有操作时就需要重置mControllButtonLayout开始显示的时间,
//		 */
//		public synchronized void reset() {
//			startVisibleTime = System.currentTimeMillis();
//		}
//
//		public void run() {
//			startVisibleTime = System.currentTimeMillis();
//
//			while (isTimeThread) {
//				// 如果已经到达了最大显示时间, 则隐藏功能控件.
//				if ((startVisibleTime + maxVisibleTime) < System
//						.currentTimeMillis()) {
//					// 发送隐藏按钮控件消息.
//					mHandler2.sendHideControllMessage();
//					startVisibleTime = System.currentTimeMillis();
//				}
//
//				try {
//					// 线程休眠1s.
//					Thread.sleep(1000);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//		}
//	}
//
//	/**
//	 * 隐藏控制按钮的控件
//	 */
//	private void hide() {
//		Log.d(TAG, "receive hide message.");
//		viewListener.setViewUIVisible(View.INVISIBLE);
//	}
//
//	private MainHandler mHandler2 = new MainHandler(CallingActivity.this);
//
//	private static class MainHandler extends Handler {
//		/** 隐藏按钮消息id */
//		private final int MSG_HIDE = 0x0001;
//
//		private WeakReference<CallingActivity> weakRef;
//
//		public MainHandler(CallingActivity activity) {
//			weakRef = new WeakReference<CallingActivity>(activity);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			final CallingActivity callingActivity = weakRef.get();
//			if (callingActivity != null) {
//				switch (msg.what) {
//				case MSG_HIDE:
//					callingActivity.hide();
//					break;
//				}
//			}
//
//			super.handleMessage(msg);
//		}
//
//		public void sendHideControllMessage() {
//			Log.d(TAG, "send hide message");
//			obtainMessage(MSG_HIDE).sendToTarget();
//		}
//	};
}
