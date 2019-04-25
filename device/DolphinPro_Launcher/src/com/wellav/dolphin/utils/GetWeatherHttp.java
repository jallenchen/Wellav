package com.wellav.dolphin.utils;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.wellav.dolphin.config.SysConfig;

import android.content.Context;

public class GetWeatherHttp {

	public static Context context;
	private static final String TIME_OUT = "连接超时，请稍候再试";
	private static final String QRY_FAIL = "查询失败";
	
	/**
	 * 方法名：getWeather 
	 * 功能：天气查询
	 * @param city
	 * @return
	 */
	public static String getWeather(String city){   
		String result=null;
		String url = SysConfig.WEATHER_BAIDUURL.replace("[?]", city);
	    try{   
	    	DefaultHttpClient client = getDefaultHttpClient2();   
	    	HttpGet mothod = new HttpGet(url);   
	    	HttpResponse httpResponse = client.execute(mothod);
			if (httpResponse.getStatusLine().getStatusCode() == 200){  
	              result = EntityUtils.toString(httpResponse.getEntity(),"gb2312");  
	         }  
	    }catch(Exception ex){   
	        ex.printStackTrace();   
	        DataUtil.Alert(context, ex.getMessage());
	    }   
	    return result;   
	}   
	
	/**
	 * 方法名：getDefaultHttpClient2 
	 * 功能：获取  httpclient 超时时间为 20秒
	 * @return
	 */
	private static DefaultHttpClient getDefaultHttpClient2() {
		DefaultHttpClient client;
		HttpParams httpParams = new BasicHttpParams();
		// 设置代理
		String host = android.net.Proxy.getDefaultHost();
		int port = android.net.Proxy.getDefaultPort();
		if (host != null) {
			HttpHost httpHost = new HttpHost(host, port);
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
		}
		// 设置超时
		HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
		HttpConnectionParams.setSoTimeout(httpParams, 20000);
		
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(httpParams, schReg);
		client = new DefaultHttpClient(conMgr,httpParams);

		return client;
	}
}
