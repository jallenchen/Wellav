package com.wellav.dolphin.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.wellav.dolphin.config.SysConfig;

public class Util {
	private static String TAG ="Util";
	private static boolean logTag = true;
	private static Toast toast;
	
	
	  public static void PrintLog(String tag,String txt){
			if(logTag){
				Log.e(tag, txt);
			}
		}
	  
	  public static void DisplayToast(Context context, String str) {
	        if (toast == null) {// 保持只显示最新的toast，其他的都取消
	            toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
	        }else {
	            toast.setText(str);
	        }
	        toast.show();
	    }
	
	  public static boolean getManger(String Authority){
		  if((Integer.parseInt(Authority) >>0 &1 )== 1){
			  return true;
		  }else{
			  return false;
		  }
		}
	  
	  public static boolean getCenter(String Authority){
		  if((Integer.parseInt(Authority) >>1 &1 )== 1){
			  return true;
		  }else{
			  return false;
		  }
		}
	  public static boolean isPrivacy(String Authority){
		  if(Authority == null){
			  return false;
		  }
		  if((Integer.parseInt(Authority) >>2 &1 )== 1){
			  return false;
		  }else{
			  return true;
		  }
		}
	

	/**
	 * base64转为bitmap
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		if(base64Data == null){
			return null;
		}
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	
	
	/**
	 * bitmap转为base64
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT).replaceAll("\r|\n", "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	@SuppressLint("SimpleDateFormat") public static String getCaptureFilePath() {
		String folder = SysConfig.getInstance().getCaptureFolder();
		File path = new File(folder);
		// 文件
		if (!path.exists()) {
			path.mkdirs();
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = format.format(new Date());
		String filepath = folder + "capture_" + date + ".jpg";
		File file = new File(filepath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return filepath;
	}
	
	/**
	 * 获取呼叫开始时间
	 * @return
	 */
	@SuppressLint("SimpleDateFormat") public static String getStartTime() {
		SimpleDateFormat format = new SimpleDateFormat(SysConfig.SimpleDateFormat,Locale.getDefault()); 
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = format.format(new Date());
		return date;
	}
	
	/**
	 * 获取呼叫结束时间
	 * @return
	 */
	@SuppressLint("SimpleDateFormat") public static String getEndTime() {
		SimpleDateFormat format = new SimpleDateFormat(SysConfig.SimpleDateFormat,Locale.getDefault()); 
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = format.format(new Date());
		return date;
	}
	public static void saveMyBitmap(Bitmap bm, String bitName) throws IOException {

		File f = new File("/mnt/sdcard/" + bitName + ".png");
		FileOutputStream fOut = null;

		f.createNewFile();
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void wakeUpAndUnlock(Context context){
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }
	
	public static String getLocation() {

		String city = "";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpUriRequest req = new HttpGet(SysConfig.locationUrl);
		HttpResponse resp;
		try {
			resp = client.execute(req);
			if (resp.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(resp.getEntity());
				JSONObject ImCmd = new JSONObject(strResult);
				city = ImCmd.getString("city").trim();
			} else {
				//Log.e(TAG, "获取失败");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.e("city->", city);
		return city;
	}
	
	  
	  /** 
	     * 格林威治时间转为本地时间
	     * @param date 格林威治时间
	     * @return str 本地时间
	     */  
	    public static String changeTimeZone(String date) 
	    {  
	    	String str = null;
	        Date dateTmp = null;
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date mDate = null;
			try {
				mDate = sdf.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
	        if (mDate != null)
	        {
	            int timeOffset = TimeZone.getTimeZone("Europe/London").getRawOffset() - TimeZone.getDefault().getRawOffset();
	            dateTmp = new Date(mDate.getTime() - timeOffset);
	            str = sdf.format(dateTmp);
	        }
	        return str;
	    }
		
}
