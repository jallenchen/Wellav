package com.wellav.dolphin.mmedia.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.DolphinImMime;
import com.wellav.dolphin.mmedia.commands.MessageEventType;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.InviteMessage;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.net.LoadAllFamilyUsersFromServer;
import com.wellav.dolphin.mmedia.net.LoadFamilyUserFromServer;
import com.wellav.dolphin.mmedia.net.LoadFamilysFromServer;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.net.XMLParser;
import com.wellav.dolphin.mmedia.net.XMLRequest;
import com.wellav.dolphin.mmedia.net.LoadAllFamilyUsersFromServer.allUserDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadFamilyUserFromServer.familyUserDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadFamilysFromServer.familyInfoDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

@SuppressLint("ViewHolder")
public class NewFriendsAdapter extends BaseAdapter {
    Context context;
    List<InviteMessage> msgs;
    int total = 0;
    InviteMessage itemMsg;
    LoadUserAvatarFromLocal userAvatar;
    int mState = 0;
    @SuppressLint("SdCardPath")
    public NewFriendsAdapter(Context context, List<InviteMessage> msgs) {
        this.context = context;
        this.msgs = msgs;
        userAvatar = new LoadUserAvatarFromLocal();
        total = msgs.size();
        
    }

    @Override
    public int getCount() {
        return msgs.size();
    }
    
    
	public void refresh(HashMap<String, UserInfo> map) {
		notifyDataSetChanged();
	}

    @Override
    public InviteMessage getItem(int position) {
        // TODO Auto-generated method stub
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final InviteMessage msg = getItem(position);
  
        if(convertView == null){
        	  holder = new ViewHolder();
        	 convertView = View.inflate(context, R.layout.item_newfriendsmsag, null);
             holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
             holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
             holder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
             holder.tv_added = (TextView) convertView.findViewById(R.id.tv_added);
             holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
             holder.iv_type = (ImageView) convertView.findViewById(R.id.iv_type);
             holder.view = (View) convertView.findViewById(R.id.view);
             holder.tv_added.setVisibility(View.GONE);
             holder.btn_add.setVisibility(View.GONE);
             
             convertView.setTag(holder);
        }else{
        	holder = (ViewHolder) convertView.getTag();
        }
        if(position== getCount()-1){
        	holder.view.setVisibility(View.INVISIBLE);
        }else{
        	holder.view.setVisibility(View.VISIBLE);
        }
       
        if(msg.getAction() == MsgKey.BEAPPLYED ||msg.getAction() == MsgKey.BELINK){
            holder.tv_name.setText(msg.getUserNickname());
            showUserAvatar(holder.iv_avatar,msg.getUserName(),msg.getUserId());
        }else if(msg.getAction() == MsgKey.BEINVITEED || msg.getAction() == MsgKey.BEMANAGER ){
        	 holder.tv_name.setText(msg.getManagerNickname());
             showUserAvatar(holder.iv_avatar,msg.getManagerName(),msg.getManagerId());
        }else if(msg.getAction() == MsgKey.BEINVITEEDBYDV){
        	holder.tv_name.setText(msg.getDeviceName());
        	holder.iv_type.setImageResource(R.drawable.ic_type_phone);
            showUserAvatar(holder.iv_avatar,msg.getDeviceId(),msg.getDeviceUserID());
        }

        holder.tv_reason.setText(msg.getContent());
        if (msg.getStatus() == MsgKey.AGREED) {

            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
        } else {
            holder.tv_added.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.btn_add.setTag(msg);
            holder.btn_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    acceptInvitation(holder.btn_add, msg, msg.getAction(),holder.tv_added);
                }

            });

        }
        
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_avatar;
        ImageView iv_type;
        TextView tv_name;
        TextView tv_reason;
        TextView tv_added;
        Button btn_add;
        View view;

    }
    private void showUserAvatar(final ImageView iamgeView, String name,String userid) {
      Bitmap bitmap = userAvatar.loadImage(name);
      if(bitmap != null){
    	  iamgeView.setImageBitmap(bitmap);
      }else{
    	  userAvatar.getData(userid, new userAvatarDataCallBack() {
			
			@Override
			public void onDataCallBack(int code, Bitmap avatar) {
				// TODO Auto-generated method stub
				iamgeView.setImageBitmap(avatar);
			}
		});
      }
    }
    /**
     * 同意好友请求或者群申请
     * 
     * @param button
     * @param username
     */
    private void acceptInvitation(final Button button, final InviteMessage msg,int action,
            final TextView textview) {
    	this.itemMsg = msg;
    	this.mState = msg.getAction();
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage(context.getString(R.string.agreeing));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        userJoinFamily(msg);
        
        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                	
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            textview.setVisibility(View.VISIBLE);
                            button.setEnabled(false);
                            button.setVisibility(View.GONE);
                            msg.setStatus(MsgKey.AGREED);
                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessage.COLUMN_MSG_STATUS, msg
                                    .getStatus());
                            SQLiteManager.getInstance().updateInviteMessage(msg.getId(), values);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, context.getString(R.string.agreeing_fail) + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }
    
    
    String name = null;
    private void userJoinFamily(final InviteMessage msg){
    	
    	String mBody = null;
    	String mRequest = null;
    	if(msg.getAction()== MsgKey.BEINVITEED || msg.getAction() == MsgKey.BEINVITEEDBYDV){
    		name = SysConfig.uid;
    		mBody = RequestString.UserJoinFamily(SysConfig.dtoken, name, msg.getDeviceFamilyId());
    		mRequest = "UserJoinFamily";
    	}else if(msg.getAction() == MsgKey.BEAPPLYED){
    		name = msg.getUserName();
    		mBody = RequestString.UserJoinFamily(SysConfig.dtoken, name, msg.getDeviceFamilyId());
    		mRequest = "UserJoinFamily";
    	}else if(msg.getAction() == MsgKey.BELINK){
    		name =  msg.getUserName();
    		 mBody = RequestString.LinkTwoDevice(SysConfig.dtoken, name, msg.getDeviceId());
    		 mRequest = "LinkTwoDevice";
    	}else if(msg.getAction() == MsgKey.BEMANAGER){
    		name = SysConfig.uid;
    		mBody = RequestString.ModifyFamilyAdmin(SysConfig.dtoken, msg.getDeviceFamilyId(), DolphinApp.getInstance().getAccountInfo().getUserId());
    		mRequest = "ModifyFamilyAdmin";
    	}else{
    		return;
    	}
    	
    	UploadData2Server task = new UploadData2Server(mBody,mRequest);
    	task.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request,int code) {
				// TODO Auto-generated method stub
				String body = null ;
				Log.e("NewFriendsAdapter", "UploadData2Server:"+request+":"+code);
				if(code == 0){
					if(msg.getAction()== MsgKey.BEINVITEED || msg.getAction() == MsgKey.BEINVITEEDBYDV){
						 body = RequestString.getFamilyIfo(SysConfig.dtoken, msg.getDeviceFamilyId());
						 LoadFamilysFromServer  family = new LoadFamilysFromServer(body);
						 family.getFamilyData(new familyInfoDataCallBack() {
							
							@Override
							public void onDataCallBack(int code, FamilyInfo data) {
								// TODO Auto-generated method stub
								Log.e("NewFriendsAdapter", "LoadFamilysFromServer:"+code);
								if(code == 0){
									String body = RequestString.getFamilyUsers(SysConfig.dtoken, msg.getDeviceFamilyId());
									LoadAllFamilyUsersFromServer allFamilyUsers = new LoadAllFamilyUsersFromServer(body);
									 allFamilyUsers.getAllUserData(new allUserDataCallBack() {
										
										@Override
										public void onDataCallBack(int code, List<FamilyUserInfo> data) {
											// TODO Auto-generated method stub
											Log.e("NewFriendsAdapter", "LoadAllFamilyUsersFromServer:"+code);
											uploadMsg();
										}
									});
								}
								
							}
						});
						 
						 
					}else if(msg.getAction() == MsgKey.BEAPPLYED){
						 body = RequestString.getFamilyUserInfo(SysConfig.dtoken, msg.getDeviceFamilyId(), msg.getUserId());
						 LoadFamilyUserFromServer  familyUser = new LoadFamilyUserFromServer(body);
							familyUser.getFamilyUserData(new familyUserDataCallBack() {
								
								@Override
								public void onDataCallBack(int code, FamilyUserInfo data) {
									// TODO Auto-generated method stub
									Log.e("NewFriendsAdapter", "LoadFamilyUserFromServer:"+code);
									
									uploadMsg();
								}
							});
			    	}else if(msg.getAction() == MsgKey.BELINK){
			    		 body = RequestString.getFamilyUserInfo(SysConfig.dtoken, msg.getDeviceFamilyId(), msg.getUserId());
			    		 LoadFamilyUserFromServer  familyUser = new LoadFamilyUserFromServer(body);
							familyUser.getFamilyUserData(new familyUserDataCallBack() {
								
								@Override
								public void onDataCallBack(int code, FamilyUserInfo data) {
									// TODO Auto-generated method stub
									Log.e("NewFriendsAdapter", "LoadFamilyUserFromServer:"+code);
									uploadMsg();
								}
							});
			    	}else if(msg.getAction() == MsgKey.BEMANAGER){
						SQLiteManager.getInstance().updateFamilyAdmin(msg.getDeviceFamilyId(), msg.getManagerId(), msg.getUserId(),true);
						uploadMsg();
					}
				}
					
				}
		});
    	
    }
    
    private void uploadMsg(){ 
    	final MessageInform  msgInfo = new MessageInform();
    	
    	if(this.mState == MsgKey.BEINVITEED || this.mState == MsgKey.BEINVITEEDBYDV  ){
    		msgInfo.setDolphinName(itemMsg.getDeviceName());
        	msgInfo.setDeviceID(itemMsg.getDeviceId());
    		msgInfo.setUserID(DolphinApp.getInstance().getMyUserInfo().getUserID());
    		msgInfo.setFamilyId(itemMsg.getDeviceFamilyId());
    		msgInfo.setName(DolphinApp.getInstance().getMyUserInfo().getNickName());
    		msgInfo.setEventType(MessageEventType.EVENTTYPE_JOIN_FANILY);
    	}else if(this.mState == MsgKey.BEAPPLYED){
    		msgInfo.setDolphinName(itemMsg.getDeviceName());
        	msgInfo.setDeviceID(itemMsg.getDeviceId());
    		msgInfo.setUserID(itemMsg.getUserId());
    		msgInfo.setName(itemMsg.getUserNickname());
    		msgInfo.setFamilyId(itemMsg.getDeviceFamilyId());
    		msgInfo.setEventType(MessageEventType.EVENTTYPE_JOIN_FANILY);
    	}else if(this.mState == MsgKey.BELINK){
    		
    		upLoadLinkMsg1(msgInfo,0);
    		upLoadLinkMsg1(msgInfo,1);
    		return;
    	}else if(this.mState == MsgKey.BEMANAGER){
    		msgInfo.setDolphinName(itemMsg.getDeviceName());
        	msgInfo.setDeviceID(itemMsg.getDeviceId());
    		msgInfo.setUserID(DolphinApp.getInstance().getMyUserInfo().getUserID());
    		msgInfo.setFamilyId(itemMsg.getDeviceFamilyId());
    		msgInfo.setName(DolphinApp.getInstance().getMyUserInfo().getNickName());
    		msgInfo.setEventType(MessageEventType.EVENTTYPE_PHONE_MANAGER);
    	}
    	
    	
    	
    	String msgContent = JsonUtil.getBOxMsgJsonObject(msgInfo).toString();
    	
    	String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, 1, itemMsg.getDeviceFamilyId(),msgContent );
    	UploadData2Server task = new UploadData2Server(mBody,"UploadMessageBox");
    	task.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				Log.e("NewFriendsAdapter", "UploadMessageBox:"+code);
				if(code == 0){
					//DolphinApp.getInstance().notifyMsgRTC2Family(msgInfo.getFamilyId());
				}
			}
		});
    	
    }
    
    private void upLoadLinkMsg1(final MessageInform  msgInfo,int item){
    	String msgContent;
    	if(item == 0){
    		msgInfo.setDolphinName(itemMsg.getDeviceName());
        	msgInfo.setDeviceID(itemMsg.getDeviceId());
    		msgInfo.setUserID(itemMsg.getUserId());
    		msgInfo.setName(itemMsg.getUserNickname());
    		msgInfo.setEventType(MessageEventType.EVENTTYPE_JOIN_FANILY);
    		msgInfo.setFamilyId(itemMsg.getDeviceFamilyId());
    		 msgContent = JsonUtil.getBOxMsgJsonObject(msgInfo).toString();
    	}else{
    		msgInfo.setDolphinName(itemMsg.getUserNickname());
        	msgInfo.setDeviceID(itemMsg.getUserName());
    		msgInfo.setUserID(itemMsg.getDeviceUserID());
    		msgInfo.setName(itemMsg.getDeviceName());
    		msgInfo.setEventType(MessageEventType.EVENTTYPE_JOIN_FANILY);
    		msgInfo.setFamilyId(itemMsg.getUserFamilyId());
    		 msgContent = JsonUtil.getBOxMsgJsonObject(msgInfo).toString();
    	}
    	String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, 1, msgInfo.getFamilyId(),msgContent );
    	UploadData2Server task = new UploadData2Server(mBody,"UploadMessageBox");
    	task.getData(new codeDataCallBack() {
			
			@Override
			public void onDataCallBack(String request, int code) {
				// TODO Auto-generated method stub
				Log.e("NewFriendsAdapter", "UploadMessageBox:"+code);
				if(code == 0){
					//DolphinApp.getInstance().notifyMsgRTC2Family(msgInfo.getFamilyId());
				}
			}
		});
    }
}
