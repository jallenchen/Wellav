package com.wellav.dolphin.mmedia.lisenter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class TextClickableSpan extends ClickableSpan {
	
	Context context;
	String id="";
	public TextClickableSpan(String id,Context context){
		super();
		this.context = context;
		this.id = id;
	}
	
	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(Color.RED);
	}

	@Override
	public void onClick(View widget) {
		// TODO Auto-generated method stub
			//DolphinApp.getInstance().JoinConf(id);
		   CommFunc.DisplayToast(context, DolphinApp.getInstance().getResources().getString(R.string.join_ok));

	}

}
