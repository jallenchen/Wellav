package com.wellav.dolphin.mmedia.utils;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;

public class DataUtil {

	/**
	 * 待机设置Activity的数据
	 * 待机模式选择时钟时， 时钟的持续时间
	 * 显示一段时候后进入休屏
	 */
	public static int[] duaration_int = {30, 60, 120, 300, 600,1200,1800,3600};
	public static String[] duration_str = DolphinApp.getInstance().getResources().getStringArray(R.array.duration_str);	
	
	/**
	 * 待机模式选择轮播照片时，轮播哪些照片
	 */
	public static String[] photo_whichToPlay_str = DolphinApp.getInstance().getResources().getStringArray(R.array.photo_whichToPlay_str);
	public static int[] photo_whichToPlay_int = {0, 30, 50,100,1};
	
	/**
	 * 待机模式选择轮播照片时，轮播持续时间
	 */
	public static String[] playing_how_long_str = DolphinApp.getInstance().getResources().getStringArray(R.array.playing_how_long_str);
	public static int[] playing_how_long_int={0,30, 60, 120, 180, 300,1200,1800,3600};

	//待机模式选择轮播照片时，播放间隔时间
	public static String[] playing_interval_str = DolphinApp.getInstance().getResources().getStringArray(R.array.playing_interval_str);
	public static int[] playing_interval_int = {3, 4, 5,6, 10, 11,12};
	
	//多久时间进入待机
	public static String[] time_str = DolphinApp.getInstance().getResources().getStringArray(R.array.duration_str);
	public static int[] time_int= {30, 60, 120,300,600,1200,1800,3600};
	
    //待机模式
	public static String[] mode = DolphinApp.getInstance().getResources().getStringArray(R.array.mode);
	
	//高级设置Activity
	public static String[] language_str = DolphinApp.getInstance().getResources().getStringArray(R.array.language_str);
	public static String[] language_system = DolphinApp.getInstance().getResources().getStringArray(R.array.language_system);
	 
	//在数组str中查找数据为data_sp的脚标
	public static int getIndex(int[] str, int data_sp){
	   int i=0;
       for(i=0;i<str.length;i++){
	      if(str[i]==data_sp) {
	    	  break;
	      }		
       }
       return i;
	}
	
	// 在数组str中查找数据为data_sp的脚标
	public static int getIndex(String[] str, String data_sp) {
		int i = 0;
		if(data_sp==null || data_sp.equals("NULL")){
			return i;
		}
		for (i = 0; i < str.length; i++) {
			if (str[i].equals(data_sp)) {
				break;
			}
		}
		return i;
	}
}
