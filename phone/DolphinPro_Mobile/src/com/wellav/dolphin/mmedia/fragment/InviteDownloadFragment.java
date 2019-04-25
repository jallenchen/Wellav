package com.wellav.dolphin.mmedia.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.R.drawable;
import com.wellav.dolphin.mmedia.R.id;
import com.wellav.dolphin.mmedia.R.layout;
import com.wellav.dolphin.mmedia.R.string;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.netease.avchat.AVChatAction;


public class InviteDownloadFragment extends BaseFragment implements OnClickListener {
    private IWXAPI api;
	private TextView mActionbarName;
 	private ImageView mActionbarPrev;
 	private ImageView weixin;
 	private ImageView timeline;
 	private ImageView sms;
 	private String downloadlink;
 	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_download, container,
				false);
		api=WXAPIFactory.createWXAPI(getActivity(), SysConfig.WX_APP_ID, true);
		api.registerApp(SysConfig.WX_APP_ID);
		
		mActionbarName = (TextView)view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView)view.findViewById(R.id.actionbar_prev);
		weixin = (ImageView)view.findViewById(R.id.ic_wetchat);
		timeline = (ImageView)view.findViewById(R.id.ic_moments);
		sms = (ImageView)view.findViewById(R.id.ic_msg);
		mActionbarPrev.setOnClickListener(this);
		weixin.setOnClickListener(this);
		timeline.setOnClickListener(this);
		sms.setOnClickListener(this);
		mActionbarName.setText(R.string.invitedownload);
		return view;
	}


	@Override
	public void onClick(View v) {		
		
		WXWebpageObject webpage=new WXWebpageObject();
		webpage.webpageUrl=SysConfig.downloadLink;
		WXMediaMessage msg = new WXMediaMessage(webpage); 
		
		msg.title = getResources().getString(R.string.msg_title);  
        msg.description = getResources().getString(R.string.msg_description);
        Bitmap thumb=BitmapFactory.decodeResource(getResources(), R.drawable.ic_wellav);
        
		msg.setThumbImage(thumb);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();  
        req.transaction=String.valueOf(System.currentTimeMillis());
        req.message = msg;
		 
        switch (v.getId()) {
	   		case R.id.actionbar_prev:
	   			getActivity().finish();
	   			break;
	   		
	   		case R.id.ic_wetchat:
                req.scene=SendMessageToWX.Req.WXSceneSession;
                api.sendReq(req);
	   			
	   			break;
	   		
	   		case R.id.ic_moments:   			
	   			req.scene=SendMessageToWX.Req.WXSceneTimeline; 
                api.sendReq(req);  
	   			break;
	   		
	   		case R.id.ic_msg:
	   			downloadlink=SysConfig.downloadLink;
	   			sendSMS(downloadlink);
	   			break;
	   		default:
	   			break;
	   		}
		
	}
	  private void sendSMS(String smsBody)  {  
		 
		 Uri smsToUri = Uri.parse("smsto:");  
	     Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);  
	     intent.putExtra("sms_body", smsBody);  
	     startActivity(intent);  
	  }
	  
}
