package com.wellav.dolphin.mmedia.dialog;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年5月20日
 * @author Cheng
 */
public class DatepickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	private TextView mView;
	public DatepickerFragment(TextView view) {
		mView=view;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		return new TimePickerDialog(getActivity(), this, hour, min, true);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		String hd=hourOfDay<10?"0"+hourOfDay:String.valueOf(hourOfDay);
		String min=minute<10?"0"+minute:String.valueOf(minute);
		String str=mView.getText().toString().replaceAll("[0-9]|:","")+hd+":"+min;
		mView.setText(str);
	}
	
	
}
