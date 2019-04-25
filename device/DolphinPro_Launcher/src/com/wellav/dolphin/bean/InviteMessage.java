/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wellav.dolphin.bean;

import com.wellav.dolphin.config.SysConfig;

public class InviteMessage {
    public static final String COLUMN_MSG_ID = "id";
    public static final String COLUMN_MSG_MANAGER_ID = "managerid";
    public static final String COLUMN_MSG_USER_NAME = "username";
    public static final String COLUMN_MSG_USER_ID = "userid";
    public static final String COLUMN_MSG_CONTENT = "content";
    public static final String COLUMN_MSG_DEVICE_NAME = "deviceName";
    public static final String COLUMN_MSG_DEVICE_ID = "deviceid";
    public static final String COLUMN_MSG_FAMILY_ID = "familyid";
    public static final String COLUMN_MSG_TIME = "time";
    public static final String COLUMN_MSG_STATUS = "status";
    public static final String COLUMN_MSG_TYPE= "type";
    private String content="";
  
    //未验证，已同意等状态
   	private int status = SysConfig.InviteMessageStatus;
   	private int action = SysConfig.InviteMessageAction;
   	//群id

   	private String  managerId="";
   	private String manageName="";
   	private String manageNickname="";
   	
   	private String UserId="";
   	private String UserName="";
   	private String UserNickname="";
   	//群名称
   	private String mDeviceId="";
   	private String mDeviceName="";
	private String mDeviceUserId="";
   	private String familyId="";
   	private String type="";
   	private String time="";
   	private int id;
   	
   	public String getUserName() {
   		return UserName;
   	}

   	public void setUserName(String UserName) {
   		this.UserName = UserName;
   	}
   	public String getUserNickname() {
   		return UserNickname;
   	}

   	public void setUserNickname(String UserNickname) {
   		this.UserNickname = UserNickname;
   	}
   	public String getUserId() {
   		return UserId;
   	}

   	public void setUserId(String UserId) {
   		this.UserId = UserId;
   	}

   	public int getStatus() {
   		return status;
   	}

   	public void setStatus(int status) {
   		this.status = status;
   	}
   	public int getAction() {
   		return action;
   	}

   	public void setAction(int action) {
   		this.action = action;
   	}
   	public String getContent() {
   		return content;
   	}

   	public void setContent(String content) {
   		this.content = content;
   	}
   	
   	public int getId() {
   		return id;
   	}

   	public void setId(int id) {
   		this.id = id;
   	}


   	public String getManagerName() {
   		return manageName;
   	}

   	public void setManagerName(String manageName) {
   		this.manageName = manageName;
   	}
   	public String getManagerId() {
   		return managerId;
   	}

   	public void setManagerId(String managerId) {
   		this.managerId = managerId;
   	}
   	public String getManagerNickname() {
   		return manageNickname;
   	}

   	public void setManagerNickname(String manageNickname) {
   		this.manageNickname = manageNickname;
   	}

   	public String getDeviceName() {
   		return mDeviceName;
   	}

   	public void setDeviceName(String mDeviceName) {
   		this.mDeviceName = mDeviceName;
   	}
   	
   	public String getDeviceId() {
   		return mDeviceId;
   	}

   	public void setDeviceId(String deviceuserid) {
   		this.mDeviceId = deviceuserid;
   	}
   	
  	public String getDeviceUserId() {
   		return mDeviceUserId;
   	}

   	public void setDeviceUserId(String deviceuserid) {
   		this.mDeviceUserId = deviceuserid;
   	}
   	public String getFamilyId() {
   		return familyId;
   	}

   	public void setFamilyId(String familyId) {
   		this.familyId = familyId;
   	}
   	
   	public String getType() {
   		return type;
   	}

   	public void setType(String type) {
   		this.type = type;
   	}
   	
   	public String getTime() {
   		return time;
   	}

   	public void setTime(String time) {
   		this.time = time;
   	}
   	
}



