package com.wellav.dolphin.mmedia.fragment;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.SelectCityActivity;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.entity.RegisterUserInfo;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.netease.DialogMaker;
import com.wellav.dolphin.mmedia.netease.LoginActivity;
import com.wellav.dolphin.mmedia.netease.LogoutHelper;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class RegisterThreeFragment extends BaseFragment implements OnClickListener,
		OnCheckedChangeListener {
	private CircleImageView mHeadView;
	private Button mSubmit;
	private TextView mAcionBarName;
	private TextView mPswRule;
	private ImageView mActionBarPrev;
	private CheckBox mPswVisible;
	private TextView mPlaceCity;
	private EditText mPsw;
	private EditText mNickname;
	private RadioGroup mSex;
	private RadioButton checkRadioButton;
	private TextView mBirthday;
	private String mCity = " ";
	private String phoneNum = "";
	private OverlayThread overlayThread;
	private RegisterUserInfo rUserInfo;
	private Bitmap mHeadBitmap = null;
	private int mYear, mMonth, mDay;
	private RegisterThreeFragment ctx;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.ctx = this;
		View view = inflater.inflate(R.layout.fragmentregisterthree, container,
				false);
		mHeadView = (CircleImageView) view.findViewById(R.id.headicon);
		mSubmit = (Button) view.findViewById(R.id.submit);
		mPswRule = (TextView) view.findViewById(R.id.psw_rule);
		mPswVisible = (CheckBox) view.findViewById(R.id.pswvisible);
		mPlaceCity = (TextView) view.findViewById(R.id.cilent_place);
		mPsw = (EditText) view.findViewById(R.id.cilent_password);
		mNickname = (EditText) view.findViewById(R.id.nick_name);
		mSex = (RadioGroup) view.findViewById(R.id.cilent_sex);
		checkRadioButton = (RadioButton) mSex.findViewById(mSex
				.getCheckedRadioButtonId());
		mBirthday = (TextView) view.findViewById(R.id.cilent_born);
		mActionBarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mAcionBarName = (TextView) view.findViewById(R.id.actionbar_name);
		mAcionBarName.setText(R.string.actionbar_step_three);
		setMyTextColor();
		setMyBirthday();
		thread.start();

		rUserInfo = new RegisterUserInfo();
		phoneNum = getArguments().getString("PHONENUM");

		mPlaceCity.setOnClickListener(this);
		mActionBarPrev.setOnClickListener(this);
		mSubmit.setOnClickListener(this);
		mPswVisible.setOnCheckedChangeListener(this);
		mHeadView.setOnClickListener(this);
		mSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				checkRadioButton = (RadioButton) mSex.findViewById(checkedId);
				if(checkRadioButton.getText().equals(ctx.getResources().getString(R.string.male))){
					mHeadView.setImageResource(R.drawable.ic_defaulthead_male_40dp);
				}else{
					mHeadView.setImageResource(R.drawable.ic_defaulthead_female_40dp);
				}
			}

		});
		return view;
	}

	private void setMyBirthday() {

		// 初始化Calendar日历对象
		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		Date mydate = new Date(); // 获取当前日期Date对象
		mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期

		mYear = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		mMonth = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		mDay = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
		mBirthday.setText(mYear + "-" + (mMonth + 1) + "-" + mDay); // 显示当前的年月日

		// 添加单击事件--设置日期
		mBirthday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 创建DatePickerDialog对象
				DatePickerDialog dpd = new DatePickerDialog(getActivity(),
						Datelistener, mYear, mMonth, mDay);
				dpd.show();// 显示DatePickerDialog组件
			}
		});
	}

	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
		// 当DatePickerDialog关闭时，更新日期显示
		private void updateDate() {
			// 在TextView上显示日期
			mBirthday.setText(mYear + "-" + (mMonth + 1) + "-" + mDay); // 显示当前的年月日
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			// 更新日期
			updateDate();
		}
	};

	private void setMyTextColor() {
		String str = getString(R.string.psw_rule);
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		// SpannableStringBuilderʵ��CharSequence�ӿ�
		style.setSpan(new ForegroundColorSpan(0xff6699ff), 3, 5,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mPswRule.setText(style);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			sendRegisterInfo();

			break;
		case R.id.actionbar_prev:
			getActivity().getSupportFragmentManager().beginTransaction().remove(RegisterThreeFragment.this).commit();
			break;
		case R.id.cilent_place:
			goCity();
			break;
		case R.id.headicon:
			takePhoto(1);
			break;

		}
	}

	private void sendRegisterInfo() {
		rUserInfo.setLoginName(phoneNum);
		rUserInfo.setNickname(mNickname.getText().toString());
		rUserInfo.setDeviceType(MsgKey.PHONE);// 0 phone ,1 device
		String Psw = DolphinUtil.md5(mPsw.getText().toString());
		rUserInfo.setPassword(Psw);
		rUserInfo.setBirthday(mBirthday.getText().toString());
		rUserInfo.setSex(checkRadioButton.getText().toString());
		if (mHeadBitmap == null) {
			//Drawable drawable = this.getResources().getDrawable(R.drawable.login_head);
			Drawable drawable = mHeadView.getDrawable();
			mHeadBitmap = BitmapUtils.drawableToBitmap(drawable);
		}
		rUserInfo.setIcon(BitmapUtils.bitmapToBase64(mHeadBitmap));
		rUserInfo.setCity(mCity);
		BitmapUtils.setPicToView(mHeadBitmap,phoneNum);//保存在SD卡中
		userRegister();
	}

	private void userRegister() {
		
		if(rUserInfo.getLoginName().equals("") || rUserInfo.getPassword().equals("")){
			Toast.makeText(getActivity(), R.string.login_fail,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(mPsw.getText().toString().length()<6){
			Toast.makeText(getActivity(), R.string.psw_short_fail,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(rUserInfo.getNickname().equals("")||rUserInfo.getNickname() ==null){
			Toast.makeText(getActivity(), R.string.nickname_fail,
					Toast.LENGTH_SHORT).show();
			return;
		}
		DialogMaker.showProgressDialog(getActivity(), "注册中", false);

		String register = RequestString.userRegister(rUserInfo);
		UploadData2Server upload = new UploadData2Server(register, "Register");
		upload.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				DialogMaker.dismissProgressDialog();
				switch (code) {
				case 0:
					Toast.makeText(getActivity(), R.string.register_succ,
							Toast.LENGTH_SHORT).show();
					break;
				case -1002:
					Toast.makeText(getActivity(), R.string.fail_name_exist,
							Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(getActivity(), R.string.fail_unknow,
							Toast.LENGTH_SHORT).show();
					break;
				}
				HBaseApp.post2UIDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						LogoutHelper.logout(getActivity(), false);
					}
				}, 1000);
				
			}
		});
	}
	

	Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mCity = DolphinUtil.getLocation();
			Message msg = new Message();
			msg.what = 0;
			msg.obj = mCity;
			mHandler.sendMessageDelayed(msg, 1000);

		}
	});

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String city = (String) msg.obj;
				if (city != null) {
					mPlaceCity.setText(city);
				} else {
					mPlaceCity.setText(R.string.city_gps_fail);
				}
				break;
			case 1: {
				
				
			}
				break;

			}

			super.handleMessage(msg);
		}

	};
	
	

	private void toLogin() {
		SysConfig.uid = rUserInfo.getLoginName();
		SysConfig.nickname = rUserInfo.getNickname();
		SysConfig.psw = rUserInfo.getPassword();
		PreferenceUtils.getInstance().saveSharePreferences(true,true);
		Intent intent = new Intent(getActivity(),LoginActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			mPsw.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		} else {
			mPsw.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}

	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case 0:
			if (data != null) {
				mCity = data.getStringExtra("MYCITY");
				mPlaceCity.setText(mCity);
			}
			break;
		case 1:
			if (resultCode == getActivity().RESULT_OK) {
				cropPhoto(data.getData());// 裁剪图片
			}
			break;
		case 2:
			if (resultCode == getActivity().RESULT_OK) {
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/"+phoneNum+".jpg");
				cropPhoto(Uri.fromFile(temp));// 裁剪图片
			}
			break;
		case 3:
			if (data != null) {
				Bundle extras = data.getExtras();
				mHeadBitmap = extras.getParcelable("data");
				mHeadView.setImageBitmap(mHeadBitmap);// 用ImageView显示出来
			}
			break;
		
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 调用系统的裁剪
	 * 
	 * @param uri
	 */
	public void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}
	
    public void takePhoto(int id) {
		Intent intent;
		switch (id) {
//		case 0:// 调用相机拍照
//			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
//					Environment.getExternalStorageDirectory(), phoneNum+".jpg")));
//			startActivityForResult(intent, 2);// 采用ForResult打开
//			break;
		case 1:// 从相册里面取照片
			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 1);
			break;
		default:
			break;
		}
	}
	private void goCity() {
		Intent intent = new Intent(getActivity(), SelectCityActivity.class);
		intent.putExtra("isRegister", true);
		startActivityForResult(intent, 0);

		overlayThread = new OverlayThread();
		Handler handler = new Handler();
		handler.postDelayed(overlayThread, 500);
	}

	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			DolphinUtil.getLocateIn().getCityName(mCity);
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		overlayThread = null;
		mHeadBitmap = null;
		super.onDestroy();
	}
	
	
}
