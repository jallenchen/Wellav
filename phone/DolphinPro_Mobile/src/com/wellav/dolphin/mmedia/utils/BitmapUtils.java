package com.wellav.dolphin.mmedia.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.wellav.dolphin.mmedia.commands.SysConfig;

public class BitmapUtils {
	
	public	static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap  
	{  
		
	    int width = drawable.getIntrinsicWidth();// 取drawable的长宽  
	    int height = drawable.getIntrinsicHeight();  
	    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式  
	    Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap  
	    Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布  
	    drawable.setBounds(0, 0, width, height);  
	    drawable.draw(canvas);// 把drawable内容画到画布中  
	    return bitmap;  
	}
	
	/**
	 * base64转为bitmap
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		if(base64Data == null){
			return null;
		}
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	public static byte[] bitmap2Base64(Bitmap bitmap) {

		byte[] result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encode(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	/**
	 * bitmap转为base64
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT).replaceAll("\r|\n", "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static void setPicToView(Bitmap mBitmap,String phoneNum) {
		 String sdStatus = Environment.getExternalStorageState();  
       if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
              return;  
          }  
		FileOutputStream b = null;
		File file = new File(SysConfig.getInstance().getAvatarFolder());
		file.mkdirs();// 创建文件夹
		String fileName =SysConfig.getInstance().getAvatarFolder() + phoneNum+".jpg";//图片名字
		try {
			b = new FileOutputStream(fileName);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				//关闭流
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
