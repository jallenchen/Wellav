package com.wellav.dolphin.mmedia.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.DolphinCmd;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.InviteMessage;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.interfaces.LocateIn;

public class DolphinUtil {
	private static String TAG = "DolphinUtil";

	public static void makeCustomActionBar(Context context,
			ActionBar actionBar, String title) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = mInflater.inflate(R.layout.actionbar_custom2, null);
		TextView actionbar_name = (TextView) v
				.findViewById(R.id.actionbar_name);
		actionbar_name.setText(title);

		actionBar.setCustomView(v, new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT));

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}

	public static boolean checkUid(Context context, String uid) {
		if (null == uid || "".equals(uid)) {
			new AlertDialog.Builder(context).setMessage(R.string.tele_num)
					.setPositiveButton(R.string.conform, null).create().show();
			return false;
		} else if (!uid.matches("^1[3-9]\\d{9}$")) {
			new AlertDialog.Builder(context).setMessage(R.string.correct_num)
					.setPositiveButton(R.string.conform, null).create().show();
			return false;
		}
		return true;
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
				Log.e(TAG, "get data fail");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e("city->", city);
		return city;
	}

	// ��ú���ƴ������ĸ
	public static String getAlpha(String str) {

		if (str.equals("-")) {
			return "&";
		}
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	/**
	 * parameter 2 is contain in parameter 1.
	 * 
	 * @param sourceFlag
	 * @param compareFlag
	 * @return
	 */
	public static boolean isFlagContain(int sourceFlag, int compareFlag) {
		return (sourceFlag & compareFlag) == compareFlag;
	}

	public static boolean isNull(Object o) {
		return o == null ? true : false;
	}

	public static int getInCallStream() {
		/* Archos 5IT */
		if ((android.os.Build.BRAND.equalsIgnoreCase("archos") && android.os.Build.DEVICE
				.equalsIgnoreCase("g7a"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("Huawei") && android.os.Build.DEVICE
						.equalsIgnoreCase("hwC8813Q"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("Huawei") && android.os.Build.DEVICE
						.equalsIgnoreCase("hwc8813"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("Xiaomi") && android.os.Build.MODEL
						.contains("MI 3"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("Xiaomi") && android.os.Build.MODEL
						.contains("MI 2"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("samsung") && android.os.Build.DEVICE
						.equalsIgnoreCase("klte"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("samsung") && android.os.Build.DEVICE
						.equalsIgnoreCase("klteduosctc"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("samsung") && android.os.Build.DEVICE
						.equalsIgnoreCase("hllte"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("samsung") && android.os.Build.DEVICE
						.equalsIgnoreCase("trltechn"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("Huawei") && android.os.Build.DEVICE
						.equalsIgnoreCase("hwB199"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("TCL") && android.os.Build.DEVICE
						.equalsIgnoreCase("Diablo_LTE"))
				|| (android.os.Build.BRAND.equalsIgnoreCase("Xiaomi") && android.os.Build.MODEL
						.contains("MI 4"))) {
			// Since device has no voice call capabilities, voice call stream is
			// not implemented
			// So we have to choose the good stream tag, which is by default
			// falled back to music
			return AudioManager.STREAM_MUSIC;
		}
		// return AudioManager.STREAM_MUSIC;
		return AudioManager.STREAM_VOICE_CALL;
	}

	public static int getCenter(String Authority) {

		return Integer.parseInt(Authority) >> 1 & 1;
	}

	public static boolean getPrivacy(String Authority) {
		if(1==(Integer.parseInt(Authority) >> 2 & 1)){
			return false;
		}else{
			return true;
		}
	}

	public static int getManger(String Authority) {

		return Integer.parseInt(Authority) >> 0 & 1;
	}

	public static String getAge(String birthdate) {
		
		if(birthdate.equals("")){
			return birthdate;
		}

		Calendar calage = Calendar.getInstance();

		int year = calage.get(Calendar.YEAR);
		int month = calage.get(Calendar.MONTH) + 1;
		int day = calage.get(Calendar.DAY_OF_MONTH);

		String agestr[] = birthdate.split("-");

		int birthyear = Integer.parseInt(agestr[0]);
		int birthmonth = Integer.parseInt(agestr[1]);
		int birthday = Integer.parseInt(agestr[2]);

		int age = year - birthyear + 1;

		if (month <= birthmonth) {

			if (month == birthmonth) {

				if (day < birthday) {
					age--;
				}

			} else {
				age--;
			}
		}
		// SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd ");
		// Date date = formatter.parse(birthdate);
		// calage.setTime(date);
		// int birthyear=calage.get(Calendar.YEAR);
		// int age=year-birthyear;
		String mAge = String.valueOf(age);

		return mAge;
	}

	static LocateIn tin;

	public static void setLocateIn(LocateIn in) {
		tin = in;
	}

	public static LocateIn getLocateIn() {
		return tin;
	}

	/**
	 * 使用md5的算法进行加密
	 * 
	 * @param plainText
	 *            要进行加密的数据
	 * @return 加密后的数据
	 */
	public static String md5(String plainText) {
		//TODO TEST
		return plainText;
//		if(plainText == null){
//			plainText = "";
//		}
//		byte[] secretBytes = null;
//		try {
//			secretBytes = MessageDigest.getInstance("md5").digest(
//					plainText.getBytes());
//		} catch (NoSuchAlgorithmException e) {
//			throw new RuntimeException("no md5 this arithmetic");
//		}
//		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
//		// 如果生成数字未满32位，需要前面补0
//		for (int i = 0; i < 32 - md5code.length(); i++) {
//			md5code = "0" + md5code;
//		}
//		return md5code;
	}

	public static List<FamilyUserInfo> removeDuplicate(List<FamilyUserInfo> list) {
		Set<FamilyUserInfo> set = new LinkedHashSet<FamilyUserInfo>();
		set.addAll(list);
		list.clear();
		list.addAll(set);
		return list;
	}

	public static String getCaptureFilePath() {
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
	 * 隐藏软键盘
	 */
	public static void goneKeyboard(Activity ct) {
		InputMethodManager imm = (InputMethodManager) ct.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(ct.getWindow().getDecorView()
					.getWindowToken(), 0);
		}
	}
	
	public static String getVersionName(Context context) {
	    return getPackageInfo(context).versionName;
	}
	 
	//版本号
	public static int getVersionCode(Context context) {
	    return getPackageInfo(context).versionCode;
	}
	 
	private static PackageInfo getPackageInfo(Context context) {
	    PackageInfo pi = null;
	 
	    try {
	        PackageManager pm = context.getPackageManager();
	        pi = pm.getPackageInfo(context.getPackageName(),
	                PackageManager.GET_CONFIGURATIONS);
	 
	        return pi;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	 
	    return pi;
	}
	
	
}
