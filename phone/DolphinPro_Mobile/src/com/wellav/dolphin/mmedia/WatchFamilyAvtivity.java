package com.wellav.dolphin.mmedia;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.fragment.CallingMonitorFragment;
import com.wellav.dolphin.mmedia.fragment.WatchFragment;
import com.wellav.dolphin.mmedia.netease.avchat.AVChatAction;
import com.wellav.dolphin.mmedia.utils.Base64Util;
import com.wellav.dolphin.mmedia.utils.CommFunc;


public class WatchFamilyAvtivity extends BaseActivity implements AVChatStateObserver {
	private static final String TAG = "WatchFamilyAvtivity";
	private CountTimeThread mCountTimeThread;
	private boolean  isTimeThread  = false;
	private WatchFragment Watch = null;
	private CallingMonitorFragment Monitor = null;;
	public static String callName ="";
	public  FamilyInfo info;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);
		int pos = getIntent().getIntExtra("FamilyPos",0);
		if(DolphinApp.getInstance().getFamilyInfos().size() != 0){
			info = DolphinApp.getInstance().getFamilyInfos().get(pos);
		callName = info.getDeviceID();
		Watch = new WatchFragment();
		Monitor = new CallingMonitorFragment();
		newWatchFragment();
		registerObservers(true);
		registerReceiver(receiver, new IntentFilter(SysConfig.Broadcast_Action_Roomname));
		}
	}
	
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		Log.e(TAG, "onConfigurationChanged()");
		if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
			// Nothing need to be done here 
				newMonitorShowFragment();
				startCountTimeThread();
			
			} else { 
				newWatchShowFragment();
				stopCountTimeThread();
			} 
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopCountTimeThread();
		registerObservers(false);
		unregisterReceiver(receiver);
		super.onDestroy();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	   /************************* 监听 ************************************/

    private void registerObservers(boolean register) {
        AVChatManager.getInstance().observeAVChatState(this, register);
    }

	private void newWatchFragment(){
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, Watch);
		ft.add(R.id.container, Monitor);
		ft.hide(Monitor);
		ft.show(Watch);
		ft.commit();
	}
	private void newWatchShowFragment(){
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.hide(Monitor);
		ft.show(Watch);
		ft.commit();
	}
	
	private void newMonitorShowFragment(){
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.hide(Watch);
		ft.show(Monitor);
		ft.commit();
	}
	
    public void isSmallShow(View view){
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    public void isFullShow(View view){
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    private static final long CLICK_DELAY = 1500l;
    private long lastClickTime;
    public void onChange2Call(View view){
    	CommFunc.PrintLog(TAG, "onChange2Call()");
    	
    	 if(System.currentTimeMillis() - lastClickTime < CLICK_DELAY){
             return;
         }
    	
      
      	
      	 lastClickTime = System.currentTimeMillis();
      	new AVChatAction(this,callName,AVChatType.VIDEO).onClick();
  	
    	//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    public void onPushPhoto(View view){
    	Intent intent2 = new Intent(this,ShareActivity.class);
		Bundle bundle2 = new Bundle();
		bundle2.putString("FamilyId",info.getFamilyID());
		intent2.putExtras(bundle2);
		startActivity(intent2);
    }
    
    
    public void onSetting(View view){
    	
    }
    
	/**
	 * 截屏
	 * 
	 * @param view
	 */
	public void onCaptureWnd(View view) {
		AVChatManager.getInstance().takeSnapshot(callName);
	}
	
	/**
//	 * 接收通话的消息广播
//	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Watch.initLiveVideo(intent.getStringExtra("roomname"));
		}
	};
	
	private void upLoadPhoto(final String path){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				URL url;
				try {
					url = new URL(SysConfig.ServerUrl + "UploadPhoto");
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
							"<UploadPhotoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
					sb.append("<Token>").append(SysConfig.dtoken).append("</Token>");
					sb.append("<FamilyID>").append(info.getFamilyID()).append("</FamilyID>");
					sb.append( "<Description>").append("聊天").append("</Description>");
					sb.append("<PhotoType>").append(2).append("</PhotoType>");
					
					bos.write(sb.toString().getBytes("utf-8"));
					Bitmap bitmap=null;
					
						sb.delete(0, sb.length());
						sb.append("<FileExt>").append(".jpg").append("</FileExt>");
						bos.write(sb.toString().getBytes("utf-8"));
						bitmap = BitmapFactory.decodeFile(path);
						Log.i("upload", "path:"+path);
						String bs64 = Base64Util.bitmapToBase64(
								bitmap).replaceAll("\r|\n", "");
						bitmap.recycle();
						bitmap=null;
						bos.write("<Content>".getBytes("utf-8"));
						Log.i("upload", "bs="+bs64.length());
						bos.write(bs64.getBytes("utf-8"));
						bos.write("</Content>".getBytes("utf-8"));
					
					sb.delete(0, sb.length());	
					
					sb.append("</UploadPhotoRequest>");
					bos.write(sb.toString().getBytes("utf-8"));
					bos.flush();
					bos.close();
					outStrm.close();
					int code = httpUrlConnection.getResponseCode();
					Log.i("upload", "code=" + code);
					if (code == 200) {
						
						InputStream inStrm = httpUrlConnection.getInputStream();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStrm));
						String line;
						StringBuilder sbResult = new StringBuilder();
						while ((line = bufferedReader.readLine()) != null) {
							sbResult.append(line).append("\n");
						}
						Log.i("upload", "result=" + sbResult);
//						成功
						if (sbResult.toString().contains("<n:Code>0</n:Code>")) {
							
							runOnUiThread(new  Runnable() {
								public void run() {
									CommFunc.DisplayToast(getApplicationContext(),getResources().getString(R.string.screen_shot_ok));
								}
							});
						}
						bufferedReader.close();
						inStrm.close();
					}else{
						runOnUiThread(new  Runnable() {
							public void run() {
								CommFunc.DisplayToast(getApplicationContext(),getResources().getString(R.string.upload_photo_fail));
							}
						});
					}
					

				} catch (Exception e) {
					Log.i("upload", "HttpURLConnection err=" + e.toString());
					runOnUiThread(new  Runnable() {
						public void run() {
							CommFunc.DisplayToast(getApplicationContext(),getResources().getString(R.string.upload_photo_fail));
						}
					});
				}
			}
		}).start();
		
	}	
	  
	
	/**
     * 
     */
	
	 
	 
		/**
		 * 开始启动线程控制按钮组件的显示.
		 */
		private void startCountTimeThread() {
			isTimeThread = true;
			mCountTimeThread = new CountTimeThread(5);
			mCountTimeThread.start();
		}
		
		private void stopCountTimeThread(){
			isTimeThread = false;
			//mCountTimeThread.stop();
			mCountTimeThread = null;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					&& mCountTimeThread != null) {
				// 重置mControllButtonLayout已经显示的时间
				mCountTimeThread.reset();

				return true;

			}

			return super.onTouchEvent(event);
		}

		private class CountTimeThread extends Thread {
			private final long maxVisibleTime;
			private long startVisibleTime;

			/**
			 * @param second
			 *            设置按钮控件最大可见时间,单位是秒
			 */
			public CountTimeThread(int second) {
				// 将时间换算成毫秒
				maxVisibleTime = second * 1000;

				// 设置为后台线程.
				setDaemon(true);
			}

			/**
			 * 每当界面有操作时就需要重置mControllButtonLayout开始显示的时间,
			 */
			public synchronized void reset() {
				startVisibleTime = System.currentTimeMillis();
			}

			public void run() {
				startVisibleTime = System.currentTimeMillis();

				while (isTimeThread) {
					// 如果已经到达了最大显示时间, 则隐藏功能控件.
					if ((startVisibleTime + maxVisibleTime) < System
							.currentTimeMillis()) {
						// 发送隐藏按钮控件消息.
						mHandler2.sendHideControllMessage();

						startVisibleTime = System.currentTimeMillis();
					}

					try {
						// 线程休眠1s.
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}

		/**
		 * 隐藏控制按钮的控件
		 */
		private void hide() {
			Log.d(TAG, "receive hide message.");
		}

		private MainHandler mHandler2 = new MainHandler(WatchFamilyAvtivity.this);

		private static class MainHandler extends Handler {
			/** 隐藏按钮消息id */
			private final int MSG_HIDE = 0x0001;

			private WeakReference<WatchFamilyAvtivity> weakRef;

			public MainHandler(WatchFamilyAvtivity activity) {
				weakRef = new WeakReference<WatchFamilyAvtivity>(activity);
			}

			@Override
			public void handleMessage(Message msg) {
				final WatchFamilyAvtivity watchFamilyAvtivity = weakRef.get();
				if (watchFamilyAvtivity != null) {
					switch (msg.what) {
					case MSG_HIDE:
						watchFamilyAvtivity.hide();
						break;
					}
				}

				super.handleMessage(msg);
			}

			public void sendHideControllMessage() {
				Log.d(TAG, "send hide message");
				obtainMessage(MSG_HIDE).sendToTarget();
			}
		}

		@Override
		public int onAudioFrameFilter(AVChatAudioFrame arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onAudioFrameFilter()");
			return 0;
		}

		@Override
		public void onCallEstablished() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onCallEstablished()");
		}

		@Override
		public void onConnectionTypeChanged(int arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onConnectionTypeChanged()");
		}

		@Override
		public void onDeviceEvent(int arg0, String arg1) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onDeviceEvent()");
		}

		@Override
		public void onDisconnectServer() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onDisconnectServer()");
		}

		@Override
		public void onFirstVideoFrameAvailable(String arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onFirstVideoFrameAvailable()"+arg0);
		}

		@Override
		public void onFirstVideoFrameRendered(String arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onFirstVideoFrameRendered()");
		}

		@Override
		public void onJoinedChannel(int arg0, String arg1, String arg2) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onJoinedChannel()");
		}

		@Override
		public void onLeaveChannel() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onLeaveChannel()");
		}

		@Override
		public void onLocalRecordEnd(String[] arg0, int arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetworkQuality(String arg0, int arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProtocolIncompatible(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTakeSnapshotResult(String arg0, boolean arg1, String arg2) {
			// TODO Auto-generated method stub
			upLoadPhoto(arg2);
			CommFunc.DisplayToast(WatchFamilyAvtivity.this, arg2);
		}

		@Override
		public void onUserJoined(String arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onUserJoined()"+arg0);
			if(Watch.isHidden() == false && arg0.equals(callName)){
				Watch.onJoin(callName);
			}
		}

		@Override
		public void onUserLeave(String arg0, int arg1) {
			// TODO Auto-generated method stub
			if(Watch.isHidden() == false && arg0.equals(callName)){
				Watch.clearChatRoom();
			}else if(Monitor.isHidden() == false && arg0.equals(callName)){
				Watch.clearChatRoom();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			Log.e(TAG, "onUserLeave()"+arg0);
		}

		@Override
		public void onVideoFpsReported(String arg0, int arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public int onVideoFrameFilter(AVChatVideoFrame arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onVideoFrameFilter()");
			return 0;
		}

		@Override
		public void onVideoFrameResolutionChanged(String arg0, int arg1,
				int arg2, int arg3) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onVideoFrameResolutionChanged()");
		}



		@Override
		public void onAudioOutputDeviceChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void onReportSpeaker(Map<String, Integer> arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void onStartLiveResult(int arg0) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void onStopLiveResult(int arg0) {
			// TODO Auto-generated method stub
			
		};
	
}
