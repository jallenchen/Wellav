package com.wellav.dolphin.mmedia.ui;

import com.wellav.dolphin.mmedia.commands.SysConfig;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年3月7日
 * @author Cheng
 */
public class AlertButton extends Button{
	private OnPositiveButtonClickenListener mLis; 
	public interface OnPositiveButtonClickenListener{
		public void onPositiveClicked();
	}
	public AlertButton(final Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder=new Builder(context);
				builder.setMessage(SysConfig.DOTHIS);
				builder.setPositiveButton(SysConfig.YES, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(mLis!=null)
							mLis.onPositiveClicked();
					}
				});
				builder.setNegativeButton(SysConfig.NO, null);
				builder.create().show();
			}
		});
	}
	
	public void setmLis(OnPositiveButtonClickenListener mLis) {
		this.mLis = mLis;
	}
}
