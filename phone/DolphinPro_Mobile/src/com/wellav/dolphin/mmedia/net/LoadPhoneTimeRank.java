package com.wellav.dolphin.mmedia.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.MyStatistcsInfo;
import com.wellav.dolphin.mmedia.entity.MyTime;
import com.wellav.dolphin.mmedia.entity.Rank;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class LoadPhoneTimeRank {
	private String mBody;
	private String mRequest;
	private Handler handler ;
	private int Code=-1;

	public LoadPhoneTimeRank(String body) {
		mBody = body;
		mRequest = "StatPhoneRecord";
	}

	@SuppressLint("HandlerLeak")
    public void getPhoneTimeData(final phoneTimeDataCallBack dataCallBack) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Code && dataCallBack != null) {
                	MyStatistcsInfo info = (MyStatistcsInfo) msg.obj;
                    if (info != null) {
                        dataCallBack.onDataCallBack(Code,info);
                    } else {

                    }
                }
            }
        };
        
        new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				requestXml(mBody,mRequest);
			}
        }.start();
    }
    
	
	private void requestXml(String requestBody,final String request){		
 		XMLRequest xmlRequest=new XMLRequest(
 				SysConfig.ServerUrl+request, 
 				requestBody, new Response.Listener<XmlPullParser>(){
 				   
 					
					@Override
 					public void onResponse(XmlPullParser response) {
 						// TODO Auto-generated method stub
                        MyStatistcsInfo  info =  ParseXMLStatistcs(response);
						 Message msg = new Message();
                        msg.what = Code;
                        msg.obj = info;
                        handler.sendMessage(msg);
                        
 					}
 				}, new Response.ErrorListener() {
 					
 					@Override
 					public void onErrorResponse(VolleyError error) {
 						// TODO Auto-generated method stub
 						Log.e("TAG", error.getMessage(), error);
 					}
 				});
 		VolleyRequestQueueManager.addRequest(xmlRequest, null);
 	}
	
	public  MyStatistcsInfo ParseXMLStatistcs(XmlPullParser response){
		 MyStatistcsInfo myStatistcsInfo=new MyStatistcsInfo();
		 MyTime myTime=null;
		 Rank myRank=null;
		 List<MyTime> myTimes = new ArrayList<MyTime>();
		 List<Rank> myRanks=new ArrayList<Rank>();
		try {
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:StatPhoneRecordResponse")) {
					}else if (nodeName.equals("n:Code")) {
						Code=Integer.valueOf(response.nextText());
					} else if (nodeName.equals("n:MyTimes")){
						myTime=new MyTime();
					} else if (myTime!=null) {
						if (nodeName.equals("n:Day")){
						    myTime.setDay(response.nextText());
					} else if (nodeName.equals("n:Times")){
						    myTime.setTimes(response.nextText());						
						}
					}
					else if (nodeName.equals("n:Ranks")) {
						myRank=new Rank();
					}else if (myRank!=null) {
						if (nodeName.equals("n:RankID")) {
					  myRank.setRankID(response.nextText());
					}else if (nodeName.equals("n:UserID")) {
					  myRank.setUserID(response.nextText());
					}else if (nodeName.equals("n:UserName")) {
					  myRank.setUserName(response.nextText());
					}else if (nodeName.equals("n:Times")) {
					  myRank.setTimes(response.nextText());
					}	
					}					
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:MyTimes")&&myTime!=null) {
						myTimes.add(myTime);
					    myTime=null;
					    myStatistcsInfo.setMyTime(myTimes);
					}
					if (response.getName().equalsIgnoreCase("n:Ranks")&&myRank!=null) {
						myRanks.add(myRank);
					    myRank=null;
					    myStatistcsInfo.setRanks(myRanks);
					}
					break;
				case XmlPullParser.END_DOCUMENT:				
					break;				
				}
				eventType = response.next();
			}
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 return myStatistcsInfo;
	}
	  /**
     * 网路访问调接口
     * 
     */
    public interface phoneTimeDataCallBack {
        void onDataCallBack(int code,MyStatistcsInfo data);
    }
}
