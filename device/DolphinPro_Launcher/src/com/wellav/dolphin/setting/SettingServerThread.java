package com.wellav.dolphin.setting;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class SettingServerThread {

	// 判断service是否还在运行
	@SuppressWarnings("rawtypes")
	private Class parseWay = SettingParseWay.class;
	
	private Map<String, Method> allMethods = new HashMap<String, Method>();
	
	// 解析客户端传来数据的方法
	public synchronized void parseCommands(Context ctx, String oneData, String familyID) {
		Method method = null ;
		if (oneData != null) {
			try {
				JSONObject parseJsonData = new JSONObject(oneData);
				String parseData = parseJsonData.getString("setting");

				// parseData根据函数名获取函数方法
				method = getMethod(parseData);
				
				method.invoke(null,oneData, ctx, familyID);
				
			} catch (Exception e) {
				;
				Log.e("", "Parameter=" + method.getParameterTypes());
				Log.e("", "e=" + e.toString());
			}
		}
	}

	// 根据函数名获取函数
	public Method getMethod(String functionName) {
		
		if (allMethods.containsKey(functionName)) {
			return allMethods.get(functionName);
		}
		try {
			Method[] parseFunctions = parseWay.getMethods();
			for (int i = 0; i < parseFunctions.length; i++) {
				if (parseFunctions[i].getName().equals(functionName)) {
					allMethods.put(functionName, parseFunctions[i]);
					return parseFunctions[i];
				}
			}
		} catch (Exception e) {
			Log.e("",  "e=" + e.toString());
		}
		return null;
	}

}
