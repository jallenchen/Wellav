package com.wellav.dolphin.mmedia.net;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.AdminInfo;
import com.wellav.dolphin.mmedia.entity.DolphinConfigInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.MessageInfo;
import com.wellav.dolphin.mmedia.entity.MyStatistcsInfo;
import com.wellav.dolphin.mmedia.entity.MyTime;
import com.wellav.dolphin.mmedia.entity.Rank;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.utils.PinYinManager;

public class XMLParser {
	public static String FamilyUsersCode = "-1";
	public static String GetUserInfoCode = "-1";
	public static String GetMessageBoxCode = "-1";
	public static String GetStatPhoneRecordCode = "-1";

	public static String responseCode(XmlPullParser response) {
		String code = "-1";
		try {
			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:Code")) {
						code = response.nextText();
					} else if (nodeName.equals("n:Description")) {
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

	public static List<FamilyUserInfo> ParseXMLFamilyUsers(XmlPullParser response) {
		FamilyUserInfo familyUserInfo = null;
		List<FamilyUserInfo> familyUserInfoes = null;
		try {

			int eventType = response.getEventType();
			Charset decoderObj = Charset.forName("UTF-8");

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
				// familyUserInfoes = new ArrayList<FamilyUserInfo>();
				// break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetFamilyUsersResponse")) {
						familyUserInfoes = new ArrayList<FamilyUserInfo>();
					} else if (nodeName.equals("n:Code")) {
						FamilyUsersCode = response.nextText();
					} else if (nodeName.equals("n:Users")) {
						familyUserInfo = new FamilyUserInfo();
						// loginResponse.setToken(response.nextText());
					} else if (familyUserInfo != null) {
						if (nodeName.equals("n:FamilyID")) {
							familyUserInfo.setFamilyID(response.nextText());
						} else if (nodeName.equals("n:UserID")) {
							familyUserInfo.setUserID(response.nextText());
						} else if (nodeName.equals("n:Authority")) {
							familyUserInfo.setAuthority(response.nextText());
							;
						} else if (nodeName.equals("n:LoginName")) {
							familyUserInfo.setLoginName(response.nextText());
						} else if (nodeName.equals("n:DeviceType")) {
							familyUserInfo.setDeviceType(response.nextText());
						} else if (nodeName.equals("n:NoteName")) {
							String strutf8 = response.nextText();
							// System.out.println(new String(b,"UTF-16"));
							familyUserInfo.setNoteName(strutf8);
							// Log.e("n:NoteName", strChinese);
						} else if (nodeName.equals("n:Sex")) {

							familyUserInfo.setSex(response.nextText());
						} else if (nodeName.equals("n:Birthday")) {
							familyUserInfo.setBirthday(response.nextText());
						} else if (nodeName.equals("n:Nickname")) {

							String name = response.nextText();
							familyUserInfo.setNickName(name);

						} else if (nodeName.equals("n:Icon")) {
							// UserInfo.setIcon(response.nextText());
							// byte[] bytes = Base64.decode(response.nextText(),
							// Base64.DEFAULT);
							String icon = response.nextText();
							if (icon != null) {
								familyUserInfo.setIcon(icon);
							}
						} else if (nodeName.equals("n:City")) {
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

	public static List<UserInfo> ParseXMLGetUserInfo(XmlPullParser response) {
		UserInfo UserInfo = null;
		List<UserInfo> UserInfoes = null;

		try {
			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
				// familyUserInfoes = new ArrayList<FamilyUserInfo>();
				// break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetUserInfoResponse")) {
						// UserInfoes = new ArrayList<UserInfo>();
					} else if (nodeName.equals("n:Code")) {
						GetUserInfoCode = response.nextText();
						UserInfoes = new ArrayList<UserInfo>();
					} else if (nodeName.equals("n:Users")) {
						UserInfo = new UserInfo();
						// loginResponse.setToken(response.nextText());
					} else if (UserInfo != null) {
						if (nodeName.equals("n:ID")) {
							UserInfo.setUserID(response.nextText());
						} else if (nodeName.equals("n:LoginName")) {
							UserInfo.setLoginName(response.nextText());
						} else if (nodeName.equals("n:DeviceType")) {
							UserInfo.setDeviceType(response.nextText());
						} else if (nodeName.equals("n:NoteName")) {
							UserInfo.setNoteName(response.nextText());
						} else if (nodeName.equals("n:Sex")) {
							UserInfo.setSex(response.nextText());
						} else if (nodeName.equals("n:Birthday")) {
							UserInfo.setBirthday(response.nextText());
						} else if (nodeName.equals("n:Nickname")) {
							UserInfo.setNickName(response.nextText());
							Log.e("n:Nickname", UserInfo.getNickName());
						} else if (nodeName.equals("n:Icon")) {
							// UserInfo.setIcon(response.nextText());
							// byte[] bytes = Base64.decode(response.nextText(),
							// Base64.DEFAULT);
							String icon = response.nextText();
							if (icon != null) {
								UserInfo.setIcon(icon);
							}
						} else if (nodeName.equals("n:City")) {
							UserInfo.setCity(response.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Users") && UserInfo != null) {
						String[] pinyin = PinYinManager.toPinYin(UserInfo.getNoteName());
						UserInfo.setFirstChar(pinyin[0]);
						UserInfoes.add(UserInfo);

						if (SysConfig.uid.equals(UserInfo.getLoginName())) {
							DolphinApp.getInstance().setMyUserInfo(UserInfo);
						}
						UserInfo = null;
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
		return UserInfoes;
	}

	public static HashMap<String, UserInfo> ParseXMLMsgUserInfo(XmlPullParser response) {
		UserInfo UserInfo = null;
		HashMap<String, UserInfo> map = new HashMap<String, UserInfo>();

		try {
			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
				// familyUserInfoes = new ArrayList<FamilyUserInfo>();
				// break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetUserInfoResponse")) {
						// UserInfoes = new ArrayList<UserInfo>();
					} else if (nodeName.equals("n:Code")) {
						GetUserInfoCode = response.nextText();
					} else if (nodeName.equals("n:Users")) {
						UserInfo = new UserInfo();
						// loginResponse.setToken(response.nextText());
					} else if (UserInfo != null) {
						if (nodeName.equals("n:ID")) {
							UserInfo.setUserID(response.nextText());
						} else if (nodeName.equals("n:LoginName")) {
							UserInfo.setLoginName(response.nextText());
						} else if (nodeName.equals("n:DeviceType")) {
							UserInfo.setDeviceType(response.nextText());
						} else if (nodeName.equals("n:NoteName")) {
							UserInfo.setNoteName(response.nextText());
						} else if (nodeName.equals("n:Sex")) {
							UserInfo.setSex(response.nextText());
						} else if (nodeName.equals("n:Birthday")) {
							UserInfo.setBirthday(response.nextText());
						} else if (nodeName.equals("n:Nickname")) {
							UserInfo.setNickName(response.nextText());
							Log.e("n:Nickname", UserInfo.getNickName());
						} else if (nodeName.equals("n:Icon")) {
							// UserInfo.setIcon(response.nextText());
							// byte[] bytes = Base64.decode(response.nextText(),
							// Base64.DEFAULT);
							String icon = response.nextText();
							if (icon != null) {
								UserInfo.setIcon(icon);
							}
						} else if (nodeName.equals("n:City")) {
							UserInfo.setCity(response.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Users") && UserInfo != null) {
						String[] pinyin = PinYinManager.toPinYin(UserInfo.getNoteName());
						UserInfo.setFirstChar(pinyin[0]);
						map.put(UserInfo.getUserID(), UserInfo);

						UserInfo = null;
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
		return map;
	}

	public static AdminInfo ParseXMLAdmin(XmlPullParser response) {
		AdminInfo adminInfo = new AdminInfo();
		try {
			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetDeviceAdminResponse")) {
					} else if (nodeName.equals("n:Code")) {
					} else if (nodeName.equals("n:FamilyID")) {
						adminInfo.setFamilyId(response.nextText());
					} else if (nodeName.equals("n:AdminUserID")) {
						adminInfo.setAdminUserId(response.nextText());
					} else if (nodeName.equals("n:AdminUserName")) {
						adminInfo.setAdminUserName(response.nextText());
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

		return adminInfo;
	}

	public static List<MessageInfo> ParseXMLMessage(XmlPullParser response) {
		List<MessageInfo> msgs = new ArrayList<MessageInfo>();
		MessageInfo info = null;
		try {
			int eventType = response.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetDeviceAdminResponse")) {
					} else if (nodeName.equals("n:Code")) {
						GetMessageBoxCode = response.nextText();
					} else if (nodeName.equals("n:Messages")) {
						info = new MessageInfo();
					} else if (info != null) {

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
					if (response.getName().equalsIgnoreCase("n:Messages") && info != null) {
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

	public static MyStatistcsInfo ParseXMLStatistcs(XmlPullParser response) {
		MyStatistcsInfo myStatistcsInfo = new MyStatistcsInfo();
		MyTime myTime = null;
		Rank myRank = null;
		List<MyTime> myTimes = new ArrayList<MyTime>();
		List<Rank> myRanks = new ArrayList<Rank>();
		try {
			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:StatPhoneRecordResponse")) {
					} else if (nodeName.equals("n:Code")) {
						GetStatPhoneRecordCode = response.nextText();
					} else if (nodeName.equals("n:MyTimes")) {
						myTime = new MyTime();
					} else if (myTime != null) {
						if (nodeName.equals("n:Day")) {
							myTime.setDay(response.nextText());
						} else if (nodeName.equals("n:Times")) {
							myTime.setTimes(response.nextText());
						}
					}
					if (nodeName.equals("n:Ranks")) {
						myRank = new Rank();
					} else if (myRank != null) {
						if (nodeName.equals("n:RankID")) {
							myRank.setRankID(response.nextText());
						} else if (nodeName.equals("n:UserID")) {
							myRank.setUserID(response.nextText());
						} else if (nodeName.equals("n:UserName")) {
							myRank.setUserName(response.nextText());
						} else if (nodeName.equals("n:Times")) {
							myRank.setTimes(response.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:MyTimes") && myTime != null) {
						myTimes.add(myTime);
						myTime = null;
						myStatistcsInfo.setMyTime(myTimes);
					}
					if (response.getName().equalsIgnoreCase("n:Ranks") && myRank != null) {
						myRanks.add(myRank);
						myRank = null;
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

	private static Context ctx;

	public XMLParser(Context cxt) {
		this.ctx = ctx;
	}

	// 保存数据
	public static DolphinConfigInfo ParseXMLConfig(XmlPullParser response) {
		DolphinConfigInfo dolphinConfigInfo = null;
		try {
			int eventType = response.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetDolphinConfigResponse")) {
						dolphinConfigInfo = new DolphinConfigInfo();
					} else if (nodeName.equals("n:NetworkName")) {
						dolphinConfigInfo.setNetWorkName(response.nextText());
					} else if (nodeName.equals("n:SoftwareVersion")) {
						String value = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setSoftwareVersion(value);
						}
					} else if (nodeName.equals("n:HardwareVersion")) {
						String value = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setHardwareVersion(value);
						}
					} else if (nodeName.equals("n:SerialNumber")) {
						String value = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setSerialNumber(value);
						}
					} else if (nodeName.equals("n:VoiceSize")) {
						dolphinConfigInfo.setVoiceSize(Integer.parseInt(response.nextText()));
					} else if (nodeName.equals("n:BrightnessSize")) {
						dolphinConfigInfo.setBrightnessSizer(Integer.parseInt(response.nextText()));
					} else if (nodeName.equals("n:VoiceLanguage")) {
						String voice = response.nextText();
						if(!voice.equals("NULL")){
							dolphinConfigInfo.setVoiceLanguage(voice);
						}
					} else if (nodeName.equals("n:ISDisplayPhoto")) {
						dolphinConfigInfo.setISDisplayPhoto(Integer.parseInt(response.nextText()));

					} else if (nodeName.equals("n:WaitMode")) {
						String mode  = response.nextText();
						if(!mode.equals("NULL")){
							dolphinConfigInfo.setWaitMode(mode);
						}
					} else if (nodeName.equals("n:StartTime")) {
						String value  = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setStartTime(value);
						}
					} else if (nodeName.equals("n:IntervalTime")) {
						String value  = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setIntervalTime(value);
						}
					} else if (nodeName.equals("n:ContinueTime")) {
						String value  = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setContinueTime(value);
						}
					} else if (nodeName.equals("n:PlayPhotoType")) {
						String value  = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setPlayPhotoType(value);
						}
					} else if (nodeName.equals("n:ISBroadcastOne")) {
						dolphinConfigInfo.setISBroadcastOne(Integer.parseInt(response.nextText()));

					} else if (nodeName.equals("n:BroadcastOneTime")) {
						String value  = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setBroadcastOneTime(value);
						}
					} else if (nodeName.equals("n:ISBroadcastTwo")) {
						dolphinConfigInfo.setISBroadcastTwo(Integer.parseInt(response.nextText()));
					} else if (nodeName.equals("n:BroadcastTwoTime")) {
						String value  = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setBroadcastTwoTime(value);
						}
					} else if (nodeName.equals("n:AgentContacts")) {
						String value  = response.nextText();
						if(!value.equals("NULL")){
							dolphinConfigInfo.setAgentContact(value);
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
		return dolphinConfigInfo;
	}
}
