package com.wellav.dolphin.mmedia.fragment;

import java.lang.reflect.Constructor;

import android.support.v4.app.Fragment;
import android.util.Log;


public class FragmentFactory {
	public static Fragment createFragment(String packgeNam,String clsName) 
	{
		try {
			Class<?> cls=Class.forName(packgeNam+clsName);
			Constructor<?> [] cons=cls.getConstructors();
			return (Fragment) cons[0].newInstance();
		} catch (Exception e) {
			Log.e("FragmentFactory", "e="+e.toString());
			return null;
		}
		
	}
}
