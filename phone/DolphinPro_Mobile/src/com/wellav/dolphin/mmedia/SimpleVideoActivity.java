package com.wellav.dolphin.mmedia;

import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class SimpleVideoActivity extends BaseActivity {
	private String url;
	private VideoView videoView;  
    MediaController  mediaco;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(url==null)
			url=getIntent().getStringExtra("Url");
		setContentView(R.layout.activity_simple_video);
	    videoView = (VideoView)findViewById(R.id.videoView1);  
        /*** 
         * 将播放器关联上一个音频或者视频文件 
         * videoView.setVideoURI(Uri uri) 
         * videoView.setVideoPath(String path) 
         * 以上两个方法都可以。 
         */  
		Log.e("", "url="+url);
        videoView.setVideoPath(url);  
          
        /** 
         * w为其提供一个控制器，控制其暂停、播放……等功能 
         */  
        mediaco=new MediaController(this);
        videoView.setMediaController(mediaco); 
        mediaco.setMediaPlayer(videoView);
        videoView.requestFocus();
        videoView.start();
          
    }  
}
