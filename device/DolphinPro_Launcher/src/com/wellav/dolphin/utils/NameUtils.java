package com.wellav.dolphin.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.SysConfig;

public class NameUtils {
	private static final String TAG = "Utils";
	private File dir;
	private static ArrayList<String> mRemoteNames = new ArrayList<String>();

	public File createFolder() {
		File defaultDir = Environment.getExternalStorageDirectory();
		String path = defaultDir.getAbsolutePath() + File.separator + "V"
				+ File.separator;// 创建文件夹存放视频
		dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	public static String listToString(List<String> stringList) {
		if (stringList == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (String string : stringList) {
			if (flag) {
				result.append(",");
			} else {
				flag = true;
			}
			result.append(string);
		}
		Log.e(TAG, "listToString:" + result.toString());
		return result.toString();
	}

	public static int remoteNamesCount() {
		// Log.i(TAG, "remoteNamesCount 获取远端用户表数量为" + mRemoteNames.size());
		return mRemoteNames.size();
	}

	public static String remoteNameString() {
		// Log.i(TAG, "remoteNameString 获取远端用户表 逗号 间隔字符串");
		if (remoteNamesCount() == 0) {
			// Log.i(TAG, "远端用户表用户数量为0，所以字符串返回\"\"");
			return "";
		}
		StringBuffer nameString = new StringBuffer();
		for (int i = 0; i < remoteNamesCount(); i++) {
			// Log.i(TAG, "加入第" + i + "个用户的ID：" + remoteNames(i));
			nameString.append(remoteNames(i) + ",");
		}
		// Log.i(TAG, "去除" + nameString.toString() + "的末尾的逗号,并回传");
		nameString.deleteCharAt(nameString.length() - 1);
		return nameString.toString();
	}

	public static String getRemoteName() {
		if (remoteNamesCount() == 0) {
			// Log.i(TAG, "远端用户表用户数量为0，所以字符串返回\"\"");
			return "";
		}
		String listName;
		listName = listToString(mRemoteNames);
		// Log.e(TAG, "远端用户表用户"+listName);
		return listName;
	}

	public static ArrayList<String> getRemoteNames() {
		if (remoteNamesCount() == 0) {
			// Log.i(TAG, "远端用户表用户数量为0，所以字符串返回\"\"");
		}
		return mRemoteNames;
	}

	public static String remoteNames(int index) {
		// Log.i(TAG, "remoteNames 想获取远端用户表第" + index + "个用户名");
		// Log.i(TAG, "总个数" + mRemoteNames.size() + "个");
		if (mRemoteNames.size() < index) {
			return "";
		} else
			return mRemoteNames.get(index).toString();
	}

	public static boolean remoteNamesAdd(String name) {// 需要加入逗号是被分拣
		if (name == null || name.equals("")) {
			return false;
		}
		String[] userIds = name.split(",");
		for (int i = 0; i < userIds.length; i++) {
			if (mRemoteNames.contains(userIds[i])) {
				// Log.i(TAG, "remoteNamesAdd 想将" + userIds[i] +
				// "加入远端名单，但已经包含");
				break;
			} else if (userIds[i].equals(SysConfig.uid)) {
				continue;
			} else {
				 Log.e(TAG, "remoteNamesAdd 将" + userIds[i] + "加入了远端名单");
				mRemoteNames.add(userIds[i]);
			}
		}
		return true;
	}

	public static boolean remoteNamesContain(String name) {
		if (mRemoteNames.contains(name)) {
			// Log.i(TAG, "remoteNamesContain 远端名单包含" + name);
			return true;
		} else
			// Log.i(TAG, "remoteNamesContain 远端名单不包含" + name);
			return false;
	}

	public static boolean remoteNamesRemove(String name) {
		if (remoteNamesContain(name)) {
			// Log.e(TAG, "remoteNamesRemove 远端名单包含" + name + ",移除成功");
			mRemoteNames.remove(name);
		} else {
			// Log.i(TAG, "remoteNamesRemove 远端名单不包含" + name + ",移除失败");
			return false;
		}
		return true;
	}

	public static void remoteNamesClean() {
		// Log.e(TAG, "remoteNamesClean 清除远端用户列表");
		mRemoteNames.clear();
	}

	public static String userInfo2LoginName(List<FamilyUserInfo> users,
			int callingmeber) {
		String callList = null;
		List<String> stringList = new ArrayList<String>();
		for (int i = callingmeber; i < users.size(); i++) {
			callList = users.get(i).getLoginName();
			if (callList.equals(SysConfig.uid)) {
				continue;
			}
			stringList.add(callList);
		}

		return listToString(stringList);
	}

	public static List<FamilyUserInfo> loginName2Users(Context ct,
			List<String> stringList) {
		List<FamilyUserInfo> Users = new ArrayList<FamilyUserInfo>();
		if (stringList == null) {

		}
		for (int i = 0; i < stringList.size(); i++) {
			if (stringList.get(i) == null || stringList.get(i) == "")
				continue;
			Users.add(LauncherApp.getInstance().getDBHelper()
					.getFamilyUser(ct, stringList.get(i)));
		}

		return Users;
	}

	// 将中英文字串转换成纯英文字串

	public static String toTureAsciiStr(String str) {

		StringBuffer sb = new StringBuffer();

		byte[] bt = str.getBytes();

		for (int i = 0; i < bt.length; i++) {

			if (bt[i] < 0) {

				// 　　是汉字去高位1

				sb.append((char) (bt[i] & 0x7f));

			} else {// 是英文字符 补0作记录

				sb.append((char) 0);

				sb.append((char) bt[i]);

			}

		}

		return sb.toString();

	}

	// 将经转换的字串还原

	public static String unToTrueAsciiStr(String str) {

		byte[] bt = str.getBytes();

		int i, l = 0, length = bt.length, j = 0;

		for (i = 0; i < length; i++) {

			if (bt[i] == 0) {

				l++;

			}

		}

		byte[] bt2 = new byte[length - l];

		for (i = 0; i < length; i++) {

			if (bt[i] == 0) {

				i++;

				bt2[j] = bt[i];

			} else {

				bt2[j] = (byte) (bt[i] | 0x80);

			}

			j++;

		}

		String tt = new String(bt2);
		return tt;
	}
}
