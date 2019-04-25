package com.wellav.dolphin.launcher;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.format.Time;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.setting.Data;
import com.wellav.dolphin.utils.GetLunarDate;
import com.wellav.dolphin.utils.GetWeatherHttp;

public class ClockActivity extends BaseActivity {
	private long SCREEN_SAVER_TIMEOUT = 15 * 1000; // 15 sec
	// Internal message IDs.
	private final int SCREEN_SAVER_TIMEOUT_MSG = 0x2000;
	private final Handler mHandy = new Handler() {
		@Override
		public void handleMessage(Message m) {
			if (m.what == SCREEN_SAVER_TIMEOUT_MSG) {
				Window win = getWindow();
				win.clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
				if (!MainActivity.DEBUG) {
//					PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//					pm.goToSleep(SystemClock.uptimeMillis());
//					PowerManager.WakeLock wakeLock = pm.newWakeLock(
//							PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
//					wakeLock.acquire();
//					wakeLock.release();
				}

			}
		}
	};
	private RelativeLayout mRl;
	private Point mPoint = new Point();
	private TextView timeMinute;
	private TextView todayWeather;
	private TextView monthDay;
	private TextView bigMonthDay;
	private TextView tomorrow;
	private TextView tomorrowWeather;
	
	private Time mTime;
	private Calendar c;
	private final Handler mHandler = new Handler();
	
	private Context ctx;
	@SuppressWarnings("unchecked")
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock);
		this.ctx = this;
		
		int index = getIntent().getIntExtra("sb", 0);
		SCREEN_SAVER_TIMEOUT = Data.duaration_int[index] * 1000;
		timeMinute = (TextView) findViewById(R.id.time_minute);
		todayWeather = (TextView) findViewById(R.id.today_weather);
		monthDay = (TextView) findViewById(R.id.month_day);
		bigMonthDay = (TextView) findViewById(R.id.big_month_day);
		tomorrow = (TextView) findViewById(R.id.tomorrow);
		tomorrowWeather = (TextView) findViewById(R.id.tomorrow_weather);
	
		// 设置时间
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

		ctx.registerReceiver(mIntentReceiver, filter, null, mHandler);
		mTime = new Time();
		//开始计时
		counter.start();
		
		//获取日历时间
		c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		tomorrow.setText(getResources().getString(R.string.tomorrow));
		bigMonthDay.setText(getLunarDate(c));
		
		Window win = getWindow();
		win.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		win.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
		win.setBackgroundDrawable(null);
		mRl = (RelativeLayout) findViewById(R.id.contentId);
		Animation anim = new AlphaAnimation(0f, 1f);
		anim.setDuration(5000);
		Interpolator interpolator = new LinearInterpolator();
		anim.setInterpolator(interpolator);
		mRl.startAnimation(anim);
		
		// 获取天气信息
		QueryAsyncTask asyncTask = new QueryAsyncTask();
		asyncTask.execute("");
	}

	/**
	 * @author add by JingWen.Li
	 * 获取天气方法
	 */
	@SuppressWarnings("rawtypes")
	private class QueryAsyncTask extends AsyncTask {
		@Override
		protected void onPostExecute(Object result) {
			if(result!=null){
				try {
					JSONObject jsonObject = new JSONObject((String)result);
					JSONArray jsonArray = jsonObject.getJSONArray("results");
			        if (jsonArray.length() > 0) {
			        	JSONObject object = jsonArray.getJSONObject(0);
						JSONArray array = object.getJSONArray("weather_data");
			        	//今天天气
			            todayWeather.setText(todayParse(array.getJSONObject(0)));
			        	tomorrowWeather.setText(tommrowParse(array.getJSONObject(1)));
			        }
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
			}
			super.onPostExecute(result);
		}
		@Override
		protected Object doInBackground(Object... params) {
			return GetWeatherHttp.getWeather(LauncherApp.getInstance().getFamily().getCity());
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mIntentReceiver);
		super.onDestroy();
	}
	/**
	 * 方法名：todayParse 
	 * 功能：今天天气,明天天气
	 * @param weather
	 */
	private String todayParse(JSONObject weather){
		String todayWeather="";
		try {
			String temp;
			temp = weather.getString("date").split("：")[1];
			todayWeather = weather.getString("weather") + "   "  + temp.substring(0, temp.length()-1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return todayWeather;
	}
	private String tommrowParse(JSONObject weather){
		String tomorrowWeather="";
		try {
			tomorrowWeather = weather.getString("weather") + "   "  + weather.getString("temperature");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tomorrowWeather;
	}
	
	//获取农历日期的方法
	private String getLunarDate(Calendar cal){
		 GetLunarDate lunar = new GetLunarDate(cal);  
		 return GetLunarDate.getChinaMonthString(lunar.lunarMonth) + getResources().getString(R.string.month) + GetLunarDate.getChinaDayString(lunar.lunarDay);
	}
	
	
	@Override
	public void onUserInteraction() {
		finish();
		super.onUserInteraction();
	}

	@Override
	protected void onPause() {
		mHandy.removeMessages(SCREEN_SAVER_TIMEOUT_MSG);
		super.onPause();
	}

	@Override
	protected void onResume() {
		HBaseApp.post2WorkDelayed(new Runnable() {
			@Override
			public void run() {
				mHandy.removeMessages(SCREEN_SAVER_TIMEOUT_MSG);
				mHandy.sendEmptyMessage(SCREEN_SAVER_TIMEOUT_MSG);
			}
		}, SCREEN_SAVER_TIMEOUT);
		super.onResume();
	}

	private String getWeek(int wd) {
		String wdd = ctx.getString(R.string.week_day);
		return String.valueOf(wdd.charAt(wd - 1));
	}
	
	MyCount counter = new MyCount(10000, 1000);
	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		@Override
		public void onFinish() {
			counter.start();
		}
		@Override
		public void onTick(long millisUntilFinished) {
			mTime.setToNow();
			timeMinute.setText(StrTime(mTime.hour) + " : " + StrTime(mTime.minute));
			monthDay.setText((c.get(Calendar.MONTH) + 1) + getResources().getString(R.string.month)
					+ c.get(Calendar.DAY_OF_MONTH) + getResources().getString(R.string.day) 
					+ "   " + getResources().getString(R.string.week) + getWeek(c.get(Calendar.DAY_OF_WEEK)));
		}
	}
	
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				mTime = new Time(TimeZone.getTimeZone(tz).getID());
			}
		}
	};
	
	private String StrTime(int timeNum){
		String StrNum = "";
		if(timeNum<10){
			StrNum = "0" + timeNum;
		}else{
			StrNum = timeNum + "";
		}
		return StrNum;
	}
}
