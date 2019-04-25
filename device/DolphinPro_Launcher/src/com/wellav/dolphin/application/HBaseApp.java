package com.wellav.dolphin.application;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.wellav.dolphin.bean.FamilyInfo;
import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInform;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.net.UploadData2Server;
import com.wellav.dolphin.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.net.XMLBody;
import com.wellav.dolphin.utils.CrashHandler;
import com.wellav.dolphin.utils.JsonUtils;
import com.wellav.dolphin.utils.Util;

/**
 * <p>
 * desc: HBaseApp
 * <p>
 * Copyright: Copyright(c) 2015
 * </p>
 * 
 * @author
 * @data 2015-12-5
 * @time 下午05:01:50
 */
public class HBaseApp extends Application {

	protected static HBaseApp s_instance = null;
	private static String LOGTAG = "HBaseApp";
	Map<String, Object> mGlobalObjs = null;
	private IntentFilter batteryLevelFilter;

	public static HBaseApp getInstance() {
		if (s_instance == null)
			Util.PrintLog(LOGTAG, "HBaseApp object is null,fetal ERROR!!"); // 此处不能打印日志这时会出现栈错误
		return s_instance;
	}

	@Override
	public void onCreate() {
		Util.PrintLog(LOGTAG, "get instance object now!");
		s_instance = this;
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext()); 
		batteryLevelFilter = new IntentFilter();
		batteryLevelFilter.addAction(SysConfig.ContactRecevicerAction);
		batteryLevelFilter.addAction(SysConfig.PhotoRecevicerAction);
		batteryLevelFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		batteryLevelFilter.addAction(MsgKey.BROADCAST_MANAGER_ACTION);
		batteryLevelFilter.addAction(SysConfig.ACTION_SHUTDOWNAction);
		batteryLevelFilter.addAction(SysConfig.SavePhotoRecevicerAction);
		
		registerReceiver(Receiver, batteryLevelFilter);
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		Util.PrintLog(LOGTAG,
				"onLowMemory,some object or class would be recycled!");
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		Util.PrintLog(LOGTAG, "onTerminate,app exit,release instance object!");
		s_instance = null;
		super.onTerminate();
	}

	// 线程同步/定时器部分
	Handler mUiHandler = null; // 用于向界面发送执行代码
	Handler mWorkHandler = null; // 用于向工作线程发送执行代码
	HandlerThread mWorkThread = null; // 工作线程处理耗时操作,防止在主线程中执行界面卡

	/**
	 * 获取Handler简化跨线程执行代码,实现同步/定时器等功能 1.获取指定线程Handler 2.实现Runnable:run()代码
	 * 2.调用Handler:post/postAt postDelayed传入Runnable对 获取界面Handler
	 */
	public Handler getUiHandler() {
		return (mUiHandler != null) ? (mUiHandler) : (mUiHandler = new Handler(
				getMainLooper()));
	}

	/**
	 * 获取工作线程Handler
	 * 
	 * @return
	 */
	public Handler getWorkHandler() {
		return (mWorkHandler != null) ? (mWorkHandler)
				: (mWorkHandler = new Handler(getWorkLooper()));
	}

	/**
	 * 获取Looper实现自定义Handler,在指定线程(非当前线程)处理消息 获取工作线程Looper,注:获取主线程Looper请调用
	 * getMainLooper()
	 */
	public Looper getWorkLooper() {
		if (mWorkThread == null) { // 如果工作线程未开启,则开启工作线程
			mWorkThread = new HandlerThread("Rtcclient_WorkThread");
			mWorkThread.start();
		}
		return mWorkThread.getLooper();
	}

	/**
	 * 释放工作线程
	 */
	public void clearWorkThread() {
		if (mWorkHandler != null)
			mWorkHandler = null;
		if (mWorkThread != null) {
			if (mWorkThread.isAlive()) {
				mWorkThread.quit();
				try {
					mWorkThread.join(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			mWorkThread = null;
		}
	}

	/**
	 * 静态工具函数 免创建Handler实现同步和定时器 直接向ui线程执行代码
	 */
	static public boolean post2UIRunnable(Runnable r) {
		return (s_instance != null) ? s_instance.getUiHandler().post(r) : false;
	}

	/**
	 * UI线程中的定时器
	 * 
	 * @param r
	 * @param uptimeMillis
	 * @return
	 */
	static public boolean post2UIAtTime(Runnable r, long uptimeMillis) {
		return (s_instance != null) ? s_instance.getUiHandler().postAtTime(r,
				uptimeMillis) : false;
	}

	static public boolean post2UIDelayed(Runnable r, long delayMillis) {
		return (s_instance != null) ? s_instance.getUiHandler().postDelayed(r,
				delayMillis) : false;
	}

	/**
	 * 直接向工作线程线程执行代码
	 * 
	 * @param r
	 * @return
	 */
	static public boolean post2WorkRunnable(Runnable r) {
		return (s_instance != null) ? s_instance.getWorkHandler().post(r)
				: false;
	}

	/**
	 * 工作线程中的定时器
	 * 
	 * @param r
	 * @param uptimeMillis
	 * @return
	 */
	static public boolean post2WorkAtTime(Runnable r, long uptimeMillis) {
		return (s_instance != null) ? s_instance.getWorkHandler().postAtTime(r,
				uptimeMillis) : false;
	}

	static public boolean post2WorkDelayed(Runnable r, long delayMillis) {
		return (s_instance != null) ? s_instance.getWorkHandler().postDelayed(
				r, delayMillis) : false;
	}

	/**
	 * 数据对象存储(全局) 通过名字存储单例对象 sKey建议直接使用类名
	 */

	Map<String, Object> getGlobalObjs() {
		return (mGlobalObjs != null) ? (mGlobalObjs)
				: (mGlobalObjs = new HashMap<String, Object>());
	}

	public Object addGlobalObjs(String sKey, Object obj) {
		return getGlobalObjs().put(sKey, obj);
	}

	public Object findGlobalObjs(String sKey) {
		return (mGlobalObjs != null) ? mGlobalObjs.get(sKey) : null;
	}

	public Object removeGlobalObjs(String sKey) {
		return (mGlobalObjs != null) ? mGlobalObjs.remove(sKey) : null;
	}

	/**
	 * 静态工具函数 免创建类的静态单例,避免静态变量被回收问题 注:注意释放 否则对象一直存在 占用内存 保存全局对象在app 防止被回收
	 */
	@SuppressWarnings("unchecked")
	static public <T> T setGlobalObjs(String sKey, T newObj) {
		return (T) ((s_instance != null) ? s_instance.addGlobalObjs(sKey,
				newObj) : null);
	}

	/**
	 * 获取已保存在app中的全局对象
	 * 
	 * @param <T>
	 * @param sKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static public <T> T getGlobalObjs(String sKey) {
		return (T) ((s_instance != null) ? s_instance.findGlobalObjs(sKey)
				: null);
	}

	/**
	 * 删除指定全局对象
	 * 
	 * @param sKey
	 */
	static public void unsetGlobalObjs(String sKey) {
		if (s_instance != null)
			s_instance.removeGlobalObjs(sKey);
	}

	public BroadcastReceiver Receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Util.PrintLog("Receiver", intent.getAction());
			if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				int batteryLevel = intent.getIntExtra(
						BatteryManager.EXTRA_LEVEL, 0);
				// 获取最大电量，如未获取到具体数值，则默认为100
				int batteryScale = intent.getIntExtra(
						BatteryManager.EXTRA_SCALE, 100);

				String power = (batteryLevel * 100 / batteryScale) + "%";
				SysConfig.getInstance().setmPower(power);
				LauncherApp.getInstance().getNet();
			
			}else if(intent.getAction().equals(SysConfig.ACTION_SHUTDOWNAction)){
			}else if (intent.getAction().equals(MsgKey.BROADCAST_MANAGER_ACTION)) {
				String userid = intent.getStringExtra("userid");
				LauncherApp.getInstance().BeManagerFragment(userid);
					
		}else if(intent.getAction().equals(SysConfig.ContactRecevicerAction)){
				String action ="";
				String type ="";
				action = intent.getStringExtra("action");
				type = intent.getStringExtra("type");
			    String	num = intent.getStringExtra("to");
				if (action.equals("del")) {
					if(num != null){
						LauncherApp.getInstance()
						.notifyMsgRTC2User(num);
					}
					LauncherApp.getInstance()
							.notifyMsgRTC2Family(context, type);
				} else if (action.equals("add")) {
					LauncherApp.getInstance().notifyMsgRTC2User(type);
				}else if(action.equals("update")){
					LauncherApp.getInstance()
					.notifyMsgRTC2Family(context, type);
				}
			}else if(intent.getAction().equals(SysConfig.PhotoRecevicerAction)){
				String action ="";
				String type ="";
				action = intent.getStringExtra("action");
				type = intent.getStringExtra("type");
				uploadMsg(context,MessageEventType.EVENTTYPE_LOOK_PIC,"normal");
			}else if(intent.getAction().equals(SysConfig.SavePhotoRecevicerAction)){
				uploadMsg(context,MessageEventType.EVENTTYPE_WRONG_PIC,"safe");
			}
		}
	};
	
	
	private void uploadMsg(final Context context,final String eventtype,String type){
		
		FamilyInfo info = LauncherApp.getInstance().getFamily();
		final MessageInform message = new MessageInform();
		message.setDeviceID(info.getDeviceID());
		message.setFamilyID(info.getFamilyID());
		message.setDolphinName(info.getNickName());
		message.setEventType(eventtype);
		String content = JsonUtils.getJsonObject(message,type);
		
	   	String mBody = XMLBody.UploadMessageBox(SysConfig.DolphinToken, 1,info.getFamilyID(),content );
    	UploadData2Server task = new UploadData2Server(mBody,"UploadMessageBox");
    	task.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				Log.e("PhotoRecevicer", "UploadMessageBox:"+code);
				if(code == 0){
					if(eventtype.equals(MessageEventType.EVENTTYPE_LOOK_PIC)){
						LauncherApp.getInstance().notifyMsgRTC2Family(s_instance,JsonUtils.getNotifyJson(message.getName(),message.getDolphinName(),message.getEventType()));
					}
					LauncherApp.getInstance().notifyMsgRTC2User(message.getDeviceID());
				}
			}
		});
	}
}
