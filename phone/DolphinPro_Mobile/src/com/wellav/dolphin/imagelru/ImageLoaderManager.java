package com.wellav.dolphin.imagelru;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ImageView.ScaleType;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

/**
 *
 * 
 * @author cf
 * 
 */
public class ImageLoaderManager {
	public static ImageLoader mImageLoader = null;
	public static ImageLoader getImageLoader ()
	{
		if(mImageLoader==null)
			mImageLoader=new  ImageLoader(
					VolleyRequestQueueManager.newInstance(), ImageCacheUtil.getInstance());
		return mImageLoader;
	}
	/**
	 * ImageListener
	 * 
	 * @param view
	 * @param defaultImage
	 * @param errorImage
	 * @return
	 */
	public static ImageListener getImageListener(final ImageView view,final ProgressBar pb,
			final Bitmap defaultImage, final Bitmap errorImage) {

		return new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(pb!=null)
				{
					pb.setVisibility(View.INVISIBLE);
					
				}
				if (errorImage != null) {
					
					view.setImageBitmap(errorImage);		
					
				}
				
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				
				if (response.getBitmap() != null) {				
					view.setImageBitmap(response.getBitmap());
					if(pb!=null)
					{
						pb.setVisibility(View.INVISIBLE);
					}
					
				} else if (defaultImage != null) {
					view.setImageBitmap(defaultImage);
					
					if(pb!=null)
					{
						pb.setVisibility(View.INVISIBLE);
					}
				}
				
			}
		};

	}
	

	/**
	 *
	 * 
	 * @param url
	 * @param view
	 * @param defaultImage
	 * @param errorImage
	 */
	public static void loadImage(String url, ImageView view,ProgressBar pb,
			Bitmap defaultImage, Bitmap errorImage) {
		getImageLoader().get(url, ImageLoaderManager.getImageListener(view,pb,
				defaultImage, errorImage), 0, 0);
		/*
		 * added by cheng
		 */
		
		VolleyRequestQueueManager.newInstance().getCache().remove(url);
	}

	/**
	 * 
	 * 
	 * @param url
	 * @param view
	 * @param defaultImage
	 * @param errorImage
	 */
	public static void loadImage(String url, ImageView view,ProgressBar pb,
			Bitmap defaultImage, Bitmap errorImage, int maxWidth, int maxHeight) {
		getImageLoader().get(url, ImageLoaderManager.getImageListener(view,pb,
				defaultImage, errorImage), maxWidth, maxHeight);
		/*
		 * added by cheng
		 */
		
		VolleyRequestQueueManager.newInstance().getCache().remove(url);
	}
	
	public static void loadImage(String url, ImageView view,ProgressBar pb,
			Bitmap defaultImage, Bitmap errorImage, ScaleType scaleType) {
		getImageLoader().get(url, ImageLoaderManager.getImageListener(view,pb,
				defaultImage, errorImage), 0, 0,scaleType);
		/*
		 * added by cheng
		 */
		
		VolleyRequestQueueManager.newInstance().getCache().remove(url);
	}
}
