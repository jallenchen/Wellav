package com.wellav.dolphin.mmedia.wxapi;

import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wellav.dolphin.mmedia.BaseActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
	// IWXAPI 是第三方app和微信通信的openapi接口  
    private IWXAPI api; 
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        api = WXAPIFactory.createWXAPI(this, SysConfig.WX_APP_ID, false);  
        api.handleIntent(getIntent(), this); 
        CommFunc.PrintLog("WXEntryActivity", "onCreate");
        super.onCreate(savedInstanceState);  
    } 
    
	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0; 
	
		 switch (resp.errCode) {  
	        case BaseResp.ErrCode.ERR_OK: 
	        	 result = R.string.errcode_success;  
	            //分享成功  
	            break;  
	        case BaseResp.ErrCode.ERR_USER_CANCEL: 
	        	 result = R.string.errcode_cancel; 
	            //分享取消  
	            break;  
	        case BaseResp.ErrCode.ERR_AUTH_DENIED:
	        	result = R.string.errcode_deny; 
	            //分享拒绝  
	            break;
	            default:
	            	result = R.string.errcode_unknown; 
	            break;
	        }  
	     CommFunc.PrintLog("onResp", resp.errCode+"");
		 CommFunc.DisplayToast(this, result);
		// TODO 微信分享 成功之后调用接口  
	      this.finish();
	}

}
