package com.wellav.dolphin.mmedia.dialog;

import com.wellav.dolphin.mmedia.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年6月7日
 * @author Cheng
 */
public class WeekdayPickerFragment extends DialogFragment {
	public interface OnPositiveButtonClickedListener {
		public void onPositiveButtonClicked(int weekdays);
	}

	public WeekdayPickerFragment(int _weekdays,
			OnPositiveButtonClickedListener lis) {
		mLis = lis;
		weekdays = _weekdays;
		chkenItem = new boolean[7];
		for (int i = 0; i < 7; i++) {
			chkenItem[i] = (weekdays & (0x1 << i)) > 0;
		}
	}

	private int weekdays;
	private boolean chkenItem[];
	private OnPositiveButtonClickedListener mLis;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder mBuilder = new Builder(getActivity(),
				R.style.AppTheme);
		mBuilder.setMultiChoiceItems(R.array.weekdays, chkenItem,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						chkenItem[which] = isChecked;
						if (isChecked)
							weekdays |= 0x1 << which;
						else
							weekdays &= ~(1 << which);
					}
				});
		mBuilder.setPositiveButton(R.string.conform, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mLis != null) {
					mLis.onPositiveButtonClicked(weekdays);
				}
			}
		});
		mBuilder.setNegativeButton(R.string.cancel, null);
		return mBuilder.create();
	}

}
