package com.wellav.dolphin.launcher;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.fragment.MessageDeleteDialogFragment;
import com.wellav.dolphin.fragment.MessageNormalFragment;
import com.wellav.dolphin.fragment.MessageSafeFragment;

/**
 * 
 * @author JingWen.Li
 * 
 */
public class MessageActivity extends BaseActivity {

	private static final String TAG = "MessageActivity";
	private MessageNormalFragment mMessageNormal;
	private MessageSafeFragment mMessageSafe;
	private Context ctx;
	private Boolean isDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		
		this.ctx = this;
		isDialog = true;
		// 返回键
		ImageButton back_bt = (ImageButton) findViewById(R.id.back_bt_message);
		back_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageActivity.this.finish();
			}
		});

		// 当点击开消息盒子的时候，向服务器获取新的消息数据
		mMessageNormal = new MessageNormalFragment();

		// 获得fragment管理器
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.message_frameLayout, mMessageNormal);
		ft.commit();

		final Button normal_but = (Button) findViewById(R.id.message_button_normal);
		final Button safe_but = (Button) findViewById(R.id.message_button_safe);

		// 正常模式下的frameLayout
		normal_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				normal_but.setBackgroundColor(getResources().getColor(
						R.color.but_color));
				safe_but.setBackgroundColor(getResources().getColor(
						R.color.half_but_color));
				normal_but.setTextColor(getResources().getColor(R.color.color2));
				safe_but.setTextColor(getResources().getColor(R.color.white));
				// 显示正常模式下的frameLayout
				displayMessageFragment_normal();
			}
		});
		// 安全模式下的frameLayout
		safe_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				safe_but.setBackgroundColor(getResources().getColor(
						R.color.but_color));
				normal_but.setBackgroundColor(getResources().getColor(
						R.color.half_but_color));
				safe_but.setTextColor(getResources().getColor(R.color.color2));
				normal_but.setTextColor(getResources().getColor(R.color.white));
				// 显示安全模式下的frameLayout
				displayMessageFragment_safe();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onClearSafeMsg(View view) {
		// Log.e("~~~~点击了清chuanquan消息", "onClearSafeMsg");
		if(isDialog){
			isDialog = false;
			MessageDeleteDialogFragment messageDeleteDialogFragment = new MessageDeleteDialogFragment(this, 
				new MessageDeleteDialogFragment.MessageDeleteDialogListener() {
					@Override
					public void onClick(View view) {
						switch(view.getId()){   
			            case R.id.ok_btn:
			            	//Toast.makeText(ctx, "点击了清chuanquan消息", Toast.LENGTH_SHORT).show();
			            	LauncherApp.getInstance().getDBHelper().deleteAllMessageInformSafe();
							mMessageSafe.getMsgData();
							isDialog = true;
			                break;
			            case R.id.cancle_btn:
							isDialog = true;
			            	break;
			            default:
			                break;
						}
					}
				}
			);
			messageDeleteDialogFragment.show(getFragmentManager(), "dialog");
			messageDeleteDialogFragment.setCancelable(false);
		}
	}

	public void onClearNormalMsg(View view) {
		// Log.e("~~~~点击了清除普通消息", "onClearNormalMsg");
		if(isDialog){
			isDialog = false;
			MessageDeleteDialogFragment messageDeleteDialogFragment = new MessageDeleteDialogFragment(this, 
				new MessageDeleteDialogFragment.MessageDeleteDialogListener() {
					@Override
					public void onClick(View view) {
						switch(view.getId()){   
			            case R.id.ok_btn:
			            	LauncherApp.getInstance().getDBHelper().deleteAllMessageInform();
							mMessageNormal.getMsgData();
							isDialog = true;
							//Toast.makeText(ctx, "~~~~点击了清除普通消息", Toast.LENGTH_SHORT).show();
			                break;
			            case R.id.cancle_btn:
							isDialog = true;
			            	break;
			            default:
			                break;
						}
					}
				}
		    );
			messageDeleteDialogFragment.show(getFragmentManager(), "dialog");
			messageDeleteDialogFragment.setCancelable(false);
		}
	}

	private void displayMessageFragment_normal() {

		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(mMessageNormal);
		ft.hide(mMessageSafe);
		ft.commit();
	}

	private void displayMessageFragment_safe() {
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (mMessageSafe == null) {
			mMessageSafe = new MessageSafeFragment();
			ft.add(R.id.message_frameLayout, mMessageSafe);
			ft.hide(mMessageNormal);
		} else {
			ft.show(mMessageSafe);
			ft.hide(mMessageNormal);
		}
		ft.commit();
	}

}
