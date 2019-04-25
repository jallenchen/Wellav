package com.wellav.dolphin.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.wellav.dolphin.bean.FamilyInfo;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.bean.MessageInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.db.PreferenceUtils;
import com.wellav.dolphin.utils.PinYinManager;


public class XMLParser {
	public static String LoginCode = "-1";
	public static String FamilyUsersCode = "-1";
	public static String GetUserInfoCode = "-1";

	public static FamilyInfo ParseXMLLogin(XmlPullParser response) {
		FamilyInfo family = null;;
		try {
			int eventType = response.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:LoginResponse")) {
					}else if (nodeName.equals("n:Code")) {
						LoginCode = response.nextText();
					} else if (nodeName.equals("n:Token")){
						SysConfig.DolphinToken = response.nextText();
					}else if (nodeName.equals("n:RTCAccount")) {
					} else if (nodeName.equals("n:CapabilityToken")) {
						//SysConfig.RTCToken = response.nextText();
					}else if (nodeName.equals("n:UserID")){
						SysConfig.Userid = response.nextText();
					}else if (nodeName.equals("n:Familys")) {
						family = new FamilyInfo();
					}  else if(family != null){
						 if (nodeName.equals("n:FamilyID")) {
							 family.setFamilyID(response.nextText());
							} else if (nodeName.equals("n:DeviceUserID")) {
								family.setDeviceUserID(response.nextText());
							} else if (nodeName.equals("n:MangerID")) {
								family.setMangerID(response.nextText());;
							} else if (nodeName.equals("n:DeviceID")) {
								family.setDeviceID(response.nextText());
							} else if (nodeName.equals("n:Authority")) {
								family.setAuthority(response.nextText());;
							} else if (nodeName.equals("n:Nickname")) {
								family.setNickName(response.nextText());;
							} else if (nodeName.equals("n:Note")) {
								family.setNote(response.nextText());
							}else if (nodeName.equals("n:City")) {
								family.setCity(response.nextText());
							} else if (nodeName.equals("n:Icon")) {
								family.setDeviceIcon(response.nextText());;
							} 
					}
					break;
				case XmlPullParser.END_TAG:
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
		
		PreferenceUtils.getInstance().saveAccountSharePreferences();
		 return family;
	}
	
	public static List<FamilyUserInfo> ParseXMLFamilyUsers(XmlPullParser response) {
		FamilyUserInfo familyUserInfo = null;  
        List<FamilyUserInfo> familyUserInfoes = null;
		try {
			
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
//				 case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理  
//					 familyUserInfoes = new ArrayList<FamilyUserInfo>();  
//                     break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetFamilyUsersResponse")) {
						 familyUserInfoes = new ArrayList<FamilyUserInfo>();  
					}else if (nodeName.equals("n:Code")) {
						FamilyUsersCode = response.nextText();
					} else if (nodeName.equals("n:Users")){
						familyUserInfo = new FamilyUserInfo();
					} else if(familyUserInfo != null){
						 if (nodeName.equals("n:FamilyID")) {
							 familyUserInfo.setFamilyID(response.nextText());
							} else if (nodeName.equals("n:UserID")) {
								familyUserInfo.setUserID(response.nextText());
							} else if (nodeName.equals("n:Authority")) {
								familyUserInfo.setAuthority(response.nextText());;
							} else if (nodeName.equals("n:LoginName")) {
								familyUserInfo.setLoginName(response.nextText());
							} else if (nodeName.equals("n:DeviceType")) {
								familyUserInfo.setDeviceType(response.nextText());
							} else if (nodeName.equals("n:NoteName")) {
								String strutf8 = response.nextText();
							 	familyUserInfo.setNoteName(strutf8);
							} else if (nodeName.equals("n:Sex")) {
							
							 	familyUserInfo.setSex(response.nextText());
							}  else if (nodeName.equals("n:Birthday")) {
								familyUserInfo.setBirthday(response.nextText());
							}else if (nodeName.equals("n:Nickname")) {
							
							 	String name = response.nextText();
							 	familyUserInfo.setNickName(name);
								
							} else if (nodeName.equals("n:Icon")) {
								String icon = response.nextText();
								if(icon != null){
									familyUserInfo.setIcon(icon);
								}
							}else if(nodeName.equals("n:City")){
								familyUserInfo.setCity(response.nextText());
							}
						 
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Users") && familyUserInfo != null) {
						 String[] pinyin = PinYinManager.toPinYin(familyUserInfo.getNoteName());
						 familyUserInfo.setFirstChar(pinyin[0]);
						familyUserInfoes.add(familyUserInfo);
						familyUserInfo = null;
					}
					break;
				}

				eventType = response.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return familyUserInfoes;
	}
	
	
	public static String responseCode(XmlPullParser response){
		String code = "-1";
		
		try {
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					 if (nodeName.equals("n:Code")) {
						 code = response.nextText();
					} else if (nodeName.equals("n:Description")){
						
					} 
					break;
				case XmlPullParser.END_TAG:
					
					break;
				}

				eventType = response.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return code;
	}
	
	// 消息盒数据
	public static String GetMessageBoxCode = "-1";

	public static List<MessageInfo> ParseXMLMessage(XmlPullParser response) {
		List<MessageInfo> msgs = new ArrayList<MessageInfo>();
		MessageInfo info = null;
		try {
			int eventType = response.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					// Log.e("***************", nodeName);

					if (nodeName.equals("n:GetMessageBoxResponse")) {
					} else if (nodeName.equals("n:Code")) {
						GetMessageBoxCode = response.nextText();
					} else if (nodeName.equals("n:Messages")) {
						info = new MessageInfo();
					}
					if (info != null) {
						if (nodeName.equals("n:SendID")) {
							info.setSendID(response.nextText());
						} else if (nodeName.equals("n:UploadTime")) {
							info.setUploadTime(response.nextText());
						} else if (nodeName.equals("n:Message")) {
							info.setMessage(response.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Messages")
							&& info != null) {
						msgs.add(info);
						info = null;
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
		return msgs;
	}
}
