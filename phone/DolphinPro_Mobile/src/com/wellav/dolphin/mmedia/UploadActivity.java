package com.wellav.dolphin.mmedia;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.imagelru.ImageCacheUtil;
import com.wellav.dolphin.mmedia.commands.MessageEventType;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.utils.Base64Util;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class UploadActivity extends BaseActivity {
	private static final int To_Add = 0;
	private List<PhotoInfo> mChkedSrc = new LinkedList<PhotoInfo>();// 选中的数据
	private Button mBtnUpload;
	private ImageView mBtnBackwd;
	private EditText mTxtDesc;
	private ProgressBar mPb;
	private GridView gridView1;  
	private Bitmap bmp;                              
	private ArrayList<HashMap<String, Object>> imageItem = new ArrayList<HashMap<String, Object>>();;
	private SimpleAdapter simpleAdapter;  
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		if (mChkedSrc.size() == 0) {
			mChkedSrc.addAll((List<PhotoInfo>) getIntent()
					.getSerializableExtra("ChkedSrc"));

		}
		
		mBtnBackwd=(ImageView)findViewById(R.id.uploadActivity_btnBackwardId);
		 gridView1 = (GridView) findViewById(R.id.gridView1);
		mBtnBackwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mPb=(ProgressBar)findViewById(R.id.uploadActivity_pbId);
		mTxtDesc=(EditText)findViewById(R.id.uploadActivity_txtDescId);
		initIMG();
		mBtnUpload = (Button) findViewById(R.id.uploadActivity_btnUploadId);
		mBtnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final int size = mChkedSrc.size();
				if(size==0)
					return;
				mPb.setVisibility(View.VISIBLE);
				final String mmsg=TextUtils.isEmpty(mTxtDesc.getText().toString())?" " : mTxtDesc.getText().toString();
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						URL url;
						try {
							url = new URL(SysConfig.ServerUrl + "UploadManyPhoto");
							URLConnection rulConnection;
							rulConnection = url.openConnection();
							HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
							httpUrlConnection.setConnectTimeout(20000);
							httpUrlConnection.setReadTimeout(20000);
							httpUrlConnection.setDoOutput(true);
							httpUrlConnection.setUseCaches(false);
							httpUrlConnection.setRequestProperty("Content-type", "text/xml; charset=utf-8");
							httpUrlConnection.setRequestProperty("connection", "close");
							httpUrlConnection.setRequestMethod("POST");
							httpUrlConnection.connect();
							OutputStream outStrm = httpUrlConnection.getOutputStream();
							BufferedOutputStream bos = new BufferedOutputStream(outStrm);

							StringBuilder sb = new StringBuilder(
									"<UploadManyPhotoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
							sb.append("<Token>").append(SysConfig.dtoken).append("</Token>");
							sb.append("<FamilyID>").append(SysConfig.familyIdForPic).append("</FamilyID>");
							sb.append( "<Description>").append(mmsg).append("</Description>");
							sb.append("<PhotoType>").append(1).append("</PhotoType>");
							bos.write(sb.toString().getBytes("utf-8"));
							Bitmap bitmap=null;
							
							for(int i=0;i<size;i++)
							{		
								sb.delete(0, sb.length());
								sb.append("<ManyPhotoInfos>");
								sb.append("<FileExt>").append(".jpg").append("</FileExt>");
								bos.write(sb.toString().getBytes("utf-8"));
								bitmap=NativeImageLoader.decodeThumbBitmapForFile(mChkedSrc.get(i).getmUrl(), 1280,800);
								String bs64 = Base64Util.bitmapToBase64(
										bitmap).replaceAll("\r|\n", "");
								
								bitmap.recycle();
								bitmap=null;
								bos.write("<Content>".getBytes("utf-8"));
								bos.write(bs64.getBytes("utf-8"));
								bos.write("</Content>".getBytes("utf-8"));
								bos.write("</ManyPhotoInfos>".getBytes("utf-8"));
								
							}
							
							sb.delete(0, sb.length());	
							
							sb.append("</UploadManyPhotoRequest>");
							bos.write(sb.toString().getBytes("utf-8"));
							bos.flush();
							bos.close();
							outStrm.close();
							int code = httpUrlConnection.getResponseCode();
							if (code == 200) {
								
								InputStream inStrm = httpUrlConnection.getInputStream();
								BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStrm));
								String line;
								StringBuilder sbResult = new StringBuilder();
								
								while ((line = bufferedReader.readLine()) != null) {
									sbResult.append(line).append("\n");
								}
//								成功
								if (sbResult.toString().contains("<n:Code>0</n:Code>")) {
									
									runOnUiThread(new  Runnable() {
										public void run() {
											Toast.makeText(getApplicationContext(),getResources().getString(R.string.upload_ok), Toast.LENGTH_SHORT).show();
											uploadMsg(size);
											mPb.setVisibility(View.INVISIBLE);
											finish();
										}
									});
								}
								bufferedReader.close();
								inStrm.close();
							}else{
								runOnUiThread(new  Runnable() {
									public void run() {
										Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_fail1), Toast.LENGTH_SHORT).show();
										mPb.setVisibility(View.INVISIBLE);
										finish();
									}
								});
							}
							

						} catch (Exception e) {
							runOnUiThread(new  Runnable() {
								public void run() {
 								//	Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_fail2), Toast.LENGTH_SHORT).show();
									Toast.makeText(getApplicationContext(),getResources().getString(R.string.upload_ok), Toast.LENGTH_SHORT).show();
									uploadMsg(size);
									mPb.setVisibility(View.INVISIBLE);
									finish();
								}
							});
						}
					}
				}).start();
				
			}
		});

	}
	@SuppressWarnings("unused")
	private static String getCacheKey(String url, int maxWidth, int maxHeight,
			ScaleType scaleType) {
		return new StringBuilder(url.length() + 12).append("#W")
				.append(maxWidth).append("#H").append(maxHeight).append("#S")
				.append(scaleType.ordinal()).append(url).toString();
	}
	
	private void addIMG(List<PhotoInfo> chkedSrc ){
		int size = chkedSrc.size();
		    Bitmap bitmap;
	        
		for(int i=0;i<size;i++){
			HashMap<String, Object> myMap = new HashMap<String, Object>();
			bitmap = ImageCacheUtil.getInstance().getCacheFromMemory(
					chkedSrc.get(i).getmUrl());
			if(bitmap == null){//磁盘缓存被清除时，从本地获取
				 bitmap = NativeImageLoader.decodeThumbBitmapForFile(chkedSrc.get(i).getmUrl(),480,480);
			}
			 myMap.put("itemImage", bitmap);
			 imageItem.add(myMap);
		}
	}
	
	private void initIMG(){
		    bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); 
	        HashMap<String, Object> map = new HashMap<String, Object>();
	        map.put("itemImage", bmp);
	        imageItem.add(map);
	        
		   addIMG(mChkedSrc);
		   
	        simpleAdapter = new SimpleAdapter(this, 
	        		imageItem, R.layout.griditem_addpic, 
	                new String[] { "itemImage"}, new int[] { R.id.imageView1}); 
	        simpleAdapter.setViewBinder(new ViewBinder() {  
			    @Override  
			    public boolean setViewValue(View view, Object data,  
			            String textRepresentation) {  
			        // TODO Auto-generated method stub  
			        if(view instanceof ImageView && data instanceof Bitmap){  
			            ImageView i = (ImageView)view;  
			            i.setImageBitmap((Bitmap) data);  
			            return true;  
			        }  
			        return false;  
			    }
	        });  
	        gridView1.setAdapter(simpleAdapter);
	        
	        gridView1.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					 if(position == To_Add){
						if(imageItem.size() == ShareActivity.MAX_COUNT+1){
							CommFunc.DisplayToast(UploadActivity.this, getResources().getString(R.string.most_num));
							return;
						}
						Intent intent2 = new Intent(UploadActivity.this,ShareActivity.class);
						Bundle bundle2 = new Bundle();
						bundle2.putString("FamilyId",SysConfig.familyIdForPic);
						bundle2.putBoolean("addmore", true);
						bundle2.putInt("addedcnt", imageItem.size()-1);
						intent2.putExtras(bundle2);
						startActivityForResult(intent2, 0);
					}else{
						dialog(position);
					}
				}
	        	
	        });
	}
	
    protected void dialog(final int position) {
    	AlertDialog.Builder builder = new Builder(UploadActivity.this);
    	builder.setMessage(getResources().getString(R.string.select_del_picture));
    	builder.setPositiveButton(getResources().getString(R.string.conform), new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			
    			imageItem.remove(position);
    			mChkedSrc.remove(position-1);
    	        simpleAdapter.notifyDataSetChanged();
    	        dialog.dismiss();
    		}
    	});
    	builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.dismiss();
    			}
    		});
    	builder.create().show();
    }
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		List<PhotoInfo> photoInfo = new LinkedList<PhotoInfo>()  ;
		if(arg1 == RESULT_OK){
			
			photoInfo = ShareActivity.chkedSrc;
  		    mChkedSrc.addAll(photoInfo);
			
			addIMG(photoInfo);
			 simpleAdapter = new SimpleAdapter(this, 
		        		imageItem, R.layout.griditem_addpic, 
		                new String[] { "itemImage"}, new int[] { R.id.imageView1}); 
		        simpleAdapter.setViewBinder(new ViewBinder() {  
				    @Override  
				    public boolean setViewValue(View view, Object data,  
				            String textRepresentation) {  
				        // TODO Auto-generated method stub  
				        if(view instanceof ImageView && data instanceof Bitmap){  
				            ImageView i = (ImageView)view;  
				            i.setImageBitmap((Bitmap) data);  
				            return true;  
				        }  
				        return false;  
				    }
		        }); 
		        gridView1.setAdapter(simpleAdapter);
		        simpleAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	private void uploadMsg(int size){
		
		FamilyInfo info = SQLiteManager.getInstance().getFamilyInfoFamilyID(SysConfig.familyIdForPic);
		
		String num =String.valueOf(size);
		MessageInform message = new MessageInform();
		message.setDeviceID(info.getDeviceID());
		message.setFamilyId(info.getFamilyID());
		message.setDolphinName(info.getNickName());
		message.setName(DolphinApp.getInstance().getMyUserInfo().getNickName());
		message.setNum(num);
		message.setEventType(MessageEventType.EVENTTYPE_POST_PIC);
		String content = JsonUtil.getBOxMsgJsonObject(message);
		
	   	String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, MsgKey.TO_FAMILY,info.getFamilyID(),content );
    	UploadData2Server task = new UploadData2Server(mBody,"UploadMessageBox");
    	task.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				if(code == MsgKey.KEY_RESULT_SUCCESS){
				//	DolphinApp.getInstance().notifyMsgRTC2Family(SysConfig.familyIdForPic);
				}
			}
		});
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ImageCacheUtil.getInstance().release();
		NativeImageLoader.getInstance().release();
		imageItem = null;
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
