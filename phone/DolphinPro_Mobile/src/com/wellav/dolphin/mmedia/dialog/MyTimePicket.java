package com.wellav.dolphin.mmedia.dialog;

import android.app.TimePickerDialog;
import android.content.Context;

public class MyTimePicket  extends TimePickerDialog{
	@Override
	protected void onStop() {
//		注释这里，onstop就不会调用回调方法
//		super.onStop();
	}
	
	public MyTimePicket(Context context, OnTimeSetListener callBack,
			int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
	}
	public MyTimePicket(Context context, int theme,
			OnTimeSetListener callBack, int hourOfDay, int minute,
			boolean is24HourView) {
		super(context, theme, callBack, hourOfDay, minute, is24HourView);
	}
}
