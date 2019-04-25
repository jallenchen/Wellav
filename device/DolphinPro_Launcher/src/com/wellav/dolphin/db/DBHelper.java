package com.wellav.dolphin.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyInfo;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.bean.MessageInform;
import com.wellav.dolphin.bean.MessageInformSafe;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.interfaces.IProviderContactMetaData;
import com.wellav.dolphin.interfaces.IProviderContactMetaData.ContactTableMetaData;
import com.wellav.dolphin.utils.Util;

public class DBHelper extends SQLiteOpenHelper implements IProviderContactMetaData{
	public String TABLE_FAMILY_INFO = "family_info"; //家庭组

	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTableForFamilyUserInfo(db);
		createTableForMessageInfo(db);
		createTableForMessageInfoSafe(db);
		createTableForFamilyInfo(db);
	}
	
	/**
	 * 创建消息盒表
	 * @param db
	 */
	private void createTableForMessageInfo(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS "
				+ MessageInformData.TABLE_MESSAGE_INFO
				+ " ("
				+ MessageInformData._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 自增
				+ MessageInformData._NAME + " VARCHAR,"
				+ MessageInformData._DOLPHINNAME + " VARCHAR,"
				+ MessageInformData._YEARANDMONTH + " VARCHAR,"
				+ MessageInformData._MONTHANDDAY + " VARCHAR,"
				+ MessageInformData._EVENTTYPE + " VARCHAR,"
				+ MessageInformData._NUM + " VARCHAR,"
				
				+ MessageInformData._TALKINGID + " VARCHAR,"
				+ MessageInformData._MEETTINGID + " VARCHAR,"
				
				+ MessageInformData._BEENANSWERED + " VARCHAR" + ")";
		db.execSQL(sql);
	}
	
	/**
	 * 创建消息盒表安全
	 * @param db
	 */
	private void createTableForMessageInfoSafe(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS "
				+ MessageInformSafeData.TABLE_MESSAGE_INFO_SAFE
				+ " ("
				+ MessageInformSafeData._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 自增
				+ MessageInformSafeData._NAME + " VARCHAR,"
				+ MessageInformSafeData._YEARANDMONTH + " VARCHAR,"
				+ MessageInformSafeData._MONTHANDDAY + " VARCHAR,"
				+ MessageInformSafeData._EVENTTYPE + " VARCHAR,"
				+ MessageInformSafeData._NUM + " VARCHAR" + ")";
		db.execSQL(sql);
	}
	
	 /**
     * 创建家庭组
     * @param db
     */
    private void createTableForFamilyInfo(SQLiteDatabase db) {
    	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_FAMILY_INFO + " ("
        + FamilyInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 自增
        + FamilyInfo._FAMILY_ID + " VARCHAR UNIQUE,"//UNIQUE
        + FamilyInfo._FAMILY_DEVICE_ID + " VARCHAR,"
        + FamilyInfo._FAMILY_MANGER_ID + " VARCHAR,"
        + FamilyInfo._FAMILY_DEVICE_USER_ID + " VARCHAR,"
        + FamilyInfo._FAMILY_NICKNAME + " VARCHAR,"
        + FamilyInfo._FAMILY_NOTE + " VARCHAR,"
        + FamilyInfo._FAMILY_CITY + " VARCHAR,"
        + FamilyInfo._FAMILY_AUTHORITY + " VARCHAR,"
        + FamilyInfo._FAMILY_AGENT_ID+ " VARCHAR,"
        + FamilyInfo._FAMILY_ICON + " VARCHAR"
        + ")";
    	db.execSQL(sql);
    }
	
	
	/**
     * 创建家庭组
     * @param db
     */
    private void createTableForFamilyUserInfo(SQLiteDatabase db) {
    	String sql = "CREATE TABLE IF NOT EXISTS " + ContactTableMetaData.TABLE_NAME + " ("
        + ContactTableMetaData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 自增
        + ContactTableMetaData._FAMILY_USER_ID + " VARCHAR UNIQUE,"//UNIQUE
        + ContactTableMetaData._FAMILY_ID + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_NAME + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_NICK_NAME + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_NOTE_NAME + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_DEVICE_TYPE + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_BIRTHDAY + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_SEX + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_CITY + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_FIRST_KEY + " VARCHAR,"
        + ContactTableMetaData._FAMILY_USER_AUTHORITY + " VARCHAR,"
          + ContactTableMetaData._FAMILY_USER_PHOTO_ID + " VARCHAR"
        + ")";
    	db.execSQL(sql);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
	        onCreate(db);
	}
	
    /**
     * 更新联系人信息的某一项 value为字符串类型
     * @param id
     * @param key
     * @param value
     * @param dbFlag
     */
    public void updateFamilyInfo(String userid, String key, String value){
        ContentValues values = new ContentValues();
        values.put(key, value);
        LauncherApp.getInstance().getDBHelper().getWritableDatabase().update(TABLE_FAMILY_INFO, values, FamilyInfo._FAMILY_DEVICE_USER_ID + " = '" + userid  + "'", null);
        LauncherApp.getInstance().getContentResolver().notifyChange(ContactTableMetaData.CONTENT_URI, null);
    }
	
    /**
     * 添加一条家庭组
     * @param info
     */
    public void saveFamilyInfo(FamilyInfo info){
        //  CommFunc.PrintLog(5, LOGTAG, "name:" + info.getName());
        if(info.getFamilyID()==null)
            return;
        {
            ContentValues values = new ContentValues();
            values.put(FamilyInfo._FAMILY_ID, info.getFamilyID());
            values.put(FamilyInfo._FAMILY_DEVICE_ID, info.getDeviceID());
            values.put(FamilyInfo._FAMILY_MANGER_ID, info.getMangerID());
            values.put(FamilyInfo._FAMILY_DEVICE_USER_ID, info.getDeviceUserID());
            values.put(FamilyInfo._FAMILY_AUTHORITY, info.getAuthority());
            values.put(FamilyInfo._FAMILY_NICKNAME, info.getNickName());
            values.put(FamilyInfo._FAMILY_NOTE, info.getNote());
            values.put(FamilyInfo._FAMILY_CITY, info.getCity());
            values.put(FamilyInfo._FAMILY_AGENT_ID, info.getAgentContactID());
            values.put(FamilyInfo._FAMILY_ICON, info.getDeviceIcon());
            //CommFunc.PrintLog(5, LOGTAG, "saveContactInfo name:"+info.getName()+" number:"+info.getPhoneNum());
            
    		LauncherApp.getInstance().getDBHelper().getWritableDatabase().replaceOrThrow(TABLE_FAMILY_INFO, null, values);
            //   CommFunc.PrintLog(5, LOGTAG, "getContactId:"+info.getContactId() +" name:"+info.getName()+"  number:"+info.getPhoneNum());

        }
    }
    
    public FamilyInfo  getFamilyInfo(){
    	FamilyInfo info  = new FamilyInfo();
        String sql = "select * from " + TABLE_FAMILY_INFO ;
        Cursor cursor = LauncherApp.getInstance().getDBHelper().getWritableDatabase().rawQuery(sql, null);
        try{
            while(cursor!=null && cursor.moveToNext()){
                info.setFamilyID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ID)));
                info.setDeviceID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_DEVICE_ID)));
                info.setMangerID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_MANGER_ID)));
                info.setDeviceUserID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_DEVICE_USER_ID)));
                info.setAuthority(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AUTHORITY)));
                info.setNickName(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_NICKNAME)));
                info.setNote(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_NOTE)));
                info.setCity(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_CITY)));
                info.setAgentContactID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AGENT_ID)));
                info.setDeviceIcon(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ICON)));
            }    
        }


        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return info;
    }
	
	
	/**
	 * 获取用户名为 信信  
	 */
	public FamilyUserInfo getFamilyUser(Context ct,String name){
		FamilyUserInfo info = new FamilyUserInfo();
		  Cursor cursor = ct.getContentResolver().query(Uri.withAppendedPath(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, SysConfig.CONTACT),
				  null,  ContactTableMetaData._FAMILY_USER_NAME+" =?", new String[]{name}, null);
		   try{
		       if(cursor!=null && cursor.moveToFirst()){
	            	 info.setFamilyID(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_ID)));
	                 info.setUserID(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_ID)));
	                 String  authoity = cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_AUTHORITY));
	                 info.setAuthority(authoity);
	                 info.setLoginName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NAME)));
	                 info.setNickName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NICK_NAME)));
	                 info.setNoteName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NOTE_NAME)));
	                 info.setDeviceType(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_DEVICE_TYPE)));
	                 info.setBirthday(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_BIRTHDAY)));
	                 info.setSex(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_SEX)));
	                 info.setCity(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_CITY)));
	                 info.setIcon(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_PHOTO_ID)));
	                 info.setFirstChar(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_FIRST_KEY)));
	            }    
	        }
	        finally {
	            if(cursor!=null)
	            {
	                cursor.close();
	                cursor = null;
	            }
	        }
		
		return info;
	}
	
	/**
	 * 获取用户名为 信信  
	 */
	public boolean isFamilyUser(Context ct,String loginName){
		  Cursor cursor = ct.getContentResolver().query(Uri.withAppendedPath(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, SysConfig.CONTACT),
				  null,  ContactTableMetaData._FAMILY_USER_NAME+" =?", new String[]{loginName}, null);
		   try{
		       if(cursor!=null && cursor.moveToFirst()){
	            	
	            }    
	        }
	        finally {
	            if(cursor!=null)
	            {
	                cursor.close();
	                cursor = null;
	                return true;
	            }
	        }
		
		return false;
	}
	
	/**
	 * 获取用户名为 信信  
	 */
	public FamilyUserInfo getFamilyUserByUserid(Context ct,String Userid){
		FamilyUserInfo info = new FamilyUserInfo();
		  Cursor cursor = ct.getContentResolver().query(Uri.withAppendedPath(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, SysConfig.CONTACT),
				  null,  ContactTableMetaData._FAMILY_USER_ID+" =?", new String[]{Userid}, null);
		   try{
		       if(cursor!=null && cursor.moveToFirst()){
	            	 info.setFamilyID(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_ID)));
	                 info.setUserID(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_ID)));
	                 String  authoity = cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_AUTHORITY));
	                 info.setAuthority(authoity);
	                 info.setLoginName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NAME)));
	                 info.setNickName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NICK_NAME)));
	                 info.setNoteName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NOTE_NAME)));
	                 info.setDeviceType(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_DEVICE_TYPE)));
	                 info.setBirthday(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_BIRTHDAY)));
	                 info.setSex(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_SEX)));
	                 info.setCity(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_CITY)));
	                 info.setIcon(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_PHOTO_ID)));
	                 info.setFirstChar(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_FIRST_KEY)));
	            }    
	        }
	        finally {
	            if(cursor!=null)
	            {
	                cursor.close();
	                cursor = null;
	            }
	        }
		
		return info;
	}
	
	/**
	 * 获取家庭组成员信息
	 */
	
	public ArrayList<String> getAllFamilyUsersName(Context ct){
		ArrayList<String> names = new ArrayList<String>();
		 Cursor cursor = ct.getContentResolver().query(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, null, null,null, null);
			   try {
				while(cursor!=null && cursor.moveToNext()){
					   String  name = cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NAME));
					   names.add(name);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			   finally {
					if (cursor != null) {
						cursor.close();
						cursor = null;
					}
				}
			   
			   return names;
	}
	
	/**
	 * 获取家庭组成员信息
	 */
	
	public List<FamilyUserInfo> getAllFamilyUsers(Context ct){
		  List<FamilyUserInfo> list = new ArrayList<FamilyUserInfo>();
	    	List<FamilyUserInfo> centerManager = new ArrayList<FamilyUserInfo>();
	        Cursor cursor = ct.getContentResolver().query(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, null, null,null, ContactTableMetaData._FAMILY_USER_FIRST_KEY+" asc");
	        try{
	        	while(cursor!=null && cursor.moveToNext()){
	            	 FamilyUserInfo info = new FamilyUserInfo();
	            	 info.setFamilyID(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_ID)));
	                 info.setUserID(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_ID)));
	                 String  authoity = cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_AUTHORITY));
	                 info.setAuthority(authoity);
	                 info.setLoginName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NAME)));
	                 info.setNickName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NICK_NAME)));
	                 info.setNoteName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NOTE_NAME)));
	                 info.setDeviceType(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_DEVICE_TYPE)));
	                 info.setBirthday(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_BIRTHDAY)));
	                 info.setSex(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_SEX)));
	                 info.setCity(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_CITY)));
	                 info.setIcon(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_PHOTO_ID)));
	                 info.setFirstChar(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_FIRST_KEY)));
	                 if(Util.getCenter(authoity) == true){
	                	 centerManager.add(0,info);
	                 }else if(Util.getManger(authoity) == true){
	                	 centerManager.add(info);
	                 }else{
	                	 list.add(info); 
	                 }
	                 
	        }
			if (cursor != null && centerManager.size() > 1) {
				list.add(0, centerManager.get(0));
				list.add(1, centerManager.get(1));
				cursor.close();
			}
	    } finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
	
	public void updateFamilyUserByUserid(Context ct,String key,String value,String userid){
		ContentValues values = new ContentValues();
		values.put(key, value);
		ct.getContentResolver().update(Uri.withAppendedPath(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, SysConfig.CONTACT),
				values,FamilyUserInfo._FAMILY_USER_ID+" = ? ", new String[]{userid});
	}
	
	public void delFamilyUserByUserid(Context ct,String userid){
		ct.getContentResolver().delete(Uri.withAppendedPath(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, SysConfig.CONTACT),
				FamilyUserInfo._FAMILY_USER_ID+" = ? ", new String[]{userid});
	}
	public void delFamilyUserExUserid(Context ct,String userid){
		ct.getContentResolver().delete(Uri.withAppendedPath(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, SysConfig.CONTACTS),
				FamilyUserInfo._FAMILY_USER_ID+" not like ? ", new String[]{userid});
		deleteAllMessageInform();
		deleteAllMessageInformSafe();
	}
	
	public List<FamilyUserInfo> getAllFamilyUsersOderByType(Context ct){
		  List<FamilyUserInfo> list = new ArrayList<FamilyUserInfo>();
		  FamilyUserInfo mUserinfo = null;
	        Cursor cursor = ct.getContentResolver().query(IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, null, FamilyUserInfo._FAMILY_USER_NAME+" not like ? ", new String[]{SysConfig.uid}, ContactTableMetaData._FAMILY_USER_DEVICE_TYPE+" desc");
	        try{
	        	while(cursor!=null && cursor.moveToNext()){
	            	 FamilyUserInfo info = new FamilyUserInfo();
	            	 info.setFamilyID(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_ID)));
	                 info.setUserID(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_ID)));
	                 String  authoity = cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_AUTHORITY));
	                 info.setAuthority(authoity);
	                 info.setLoginName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NAME)));
	                 info.setNickName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NICK_NAME)));
	                 info.setNoteName(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_NOTE_NAME)));
	                 info.setDeviceType(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_DEVICE_TYPE)));
	                 info.setBirthday(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_BIRTHDAY)));
	                 info.setSex(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_SEX)));
	                 info.setCity(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_CITY)));
	                 info.setIcon(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_PHOTO_ID)));
	                 info.setFirstChar(cursor.getString(cursor.getColumnIndex(ContactTableMetaData._FAMILY_USER_FIRST_KEY)));
	                 
	                 list.add(info);  
	          }
	        }
	        finally {
	            if(cursor!=null){
	                cursor.close();
	                cursor = null;
	            }
	        }
	        return list;
	}
	
	
	   /**
     * 添加个家庭组成员
     * @param list
     */
    public void saveFamilyUserInfoList(final List<FamilyUserInfo> list, final Context ct) {
        HBaseApp.post2WorkRunnable(new Runnable (){

            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    saveFamilyUserInfo(list.get(i), ct);
                } 
            }
        });
    }
	
	 /**
     * 添加一条家庭组成员信息
     * @param info
     */
    public long saveFamilyUserInfo(FamilyUserInfo info, Context ct){
        //  CommFunc.PrintLog(5, LOGTAG, "name:" + info.getName());
        if(info.getFamilyID().length()==0 || info.getUserID() == null)
            return 0;
        
        ContentValues values = new ContentValues();
        values.put(ContactTableMetaData._FAMILY_USER_ID, info.getUserID());
        values.put(ContactTableMetaData._FAMILY_ID, info.getFamilyID());
        values.put(ContactTableMetaData._FAMILY_USER_AUTHORITY, info.getAuthority());
        values.put(ContactTableMetaData._FAMILY_USER_NAME, info.getLoginName());
        values.put(ContactTableMetaData._FAMILY_USER_NICK_NAME, info.getNickName());
        values.put(ContactTableMetaData._FAMILY_USER_NOTE_NAME, info.getNoteName());
        values.put(ContactTableMetaData._FAMILY_USER_DEVICE_TYPE, info.getDeviceType());
        values.put(ContactTableMetaData._FAMILY_USER_BIRTHDAY, info.getBirthday());
        values.put(ContactTableMetaData._FAMILY_USER_SEX, info.getSex());
        values.put(ContactTableMetaData._FAMILY_USER_CITY, info.getCity());
        values.put(ContactTableMetaData._FAMILY_USER_PHOTO_ID, info.getIcon());
        values.put(ContactTableMetaData._FAMILY_USER_FIRST_KEY, info.getFirstChar());
        
        Uri insertUri = ct.getContentResolver().insert(
                IProviderContactMetaData.ContactTableMetaData.CONTENT_URI, values);
        
        return ContentUris.parseId(insertUri);
    } 
    
    /**
	 * 保存一条消息盒数据
	 * @param info
	 * @param dbFlag
     * @return 
	 */
	public void saveMessageInfo(MessageInform info) {
		
		ContentValues values = new ContentValues();
		
		values.put(MessageInformData._YEARANDMONTH, info.getYearAndMonth());
		values.put(MessageInformData._MONTHANDDAY, info.getMonthAndDay());
		values.put(MessageInformData._NAME, info.getName());
		values.put(MessageInformData._DOLPHINNAME, info.getDolphinName());
		values.put(MessageInformData._EVENTTYPE, info.getEventType());
		values.put(MessageInformData._BEENANSWERED, info.getBeenAnswered());
		values.put(MessageInformData._NUM, info.getNum());
		
		values.put(MessageInformData._TALKINGID, info.getTalkingID());
		values.put(MessageInformData._MEETTINGID, info.getMeettingID());
		 
		LauncherApp.getInstance().getDBHelper().getWritableDatabase().replaceOrThrow(MessageInformData.TABLE_MESSAGE_INFO, null, values);
	}

	/**
	 * 获取所有消息盒数据
	 * @return
	 */
	public List<MessageInform> getAllMessageInfo() {
		
		List<MessageInform> list = new ArrayList<MessageInform>();
		String sql = "select * from " + MessageInformData.TABLE_MESSAGE_INFO + " order by "
				+ MessageInformData._ID
				+ " desc";
		Cursor cursor = LauncherApp.getInstance().getDBHelper().getWritableDatabase().rawQuery(sql, null);
		try {
			while (cursor != null && cursor.moveToNext()) {
				MessageInform info = new MessageInform();
				info.setYearAndMonth(cursor.getString(cursor
						.getColumnIndex(MessageInformData._YEARANDMONTH)));
				info.setMonthAndDay(cursor.getString(cursor
						.getColumnIndex(MessageInformData._MONTHANDDAY)));
				info.setName(cursor.getString(cursor
						.getColumnIndex(MessageInformData._NAME)));
				info.setDolphinName(cursor.getString(cursor
						.getColumnIndex(MessageInformData._DOLPHINNAME)));
				info.setEventType(cursor.getString(cursor
						.getColumnIndex(MessageInformData._EVENTTYPE)));
				info.setBeenAnswered(cursor.getString(cursor
						.getColumnIndex(MessageInformData._BEENANSWERED)));
				info.setNum(cursor.getString(cursor
						.getColumnIndex(MessageInformData._NUM)));
				
				info.setTalkingID(cursor.getString(cursor
						.getColumnIndex(MessageInformData._TALKINGID)));
				info.setMeettingID(cursor.getString(cursor
						.getColumnIndex(MessageInformData._MEETTINGID)));
				list.add(info);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
	
	/**
	 * 删除所有消息盒数据
	 * @param dbFlag
	 */
	public void deleteAllMessageInform() {
		LauncherApp.getInstance().getDBHelper().getWritableDatabase().delete(MessageInformData.TABLE_MESSAGE_INFO, null, null);
	}
	
	/**
	 * 保存一条消息盒数据安全类
	 * @param info
	 * @param dbFlag
	 */
	public void saveMessageInfoSafe(MessageInformSafe info) {
		
		ContentValues values = new ContentValues();
		
		values.put(MessageInformSafeData._YEARANDMONTH, info.getYearAndMonth());
		values.put(MessageInformSafeData._MONTHANDDAY, info.getMonthAndDay());
		values.put(MessageInformSafeData._NAME, info.getName());
		values.put(MessageInformSafeData._EVENTTYPE, info.getEventType());
		values.put(MessageInformSafeData._NUM, info.getNum());
		
		LauncherApp.getInstance().getDBHelper().getWritableDatabase().replaceOrThrow(MessageInformSafeData.TABLE_MESSAGE_INFO_SAFE, null, values);
	}
	/**
	 * 获取所有消息盒数据安全类
	 * @return
	 */
	public List<MessageInformSafe> getAllMessageInfoSafe() {
		
		List<MessageInformSafe> list = new ArrayList<MessageInformSafe>();
		String sql = "select * from " + MessageInformSafeData.TABLE_MESSAGE_INFO_SAFE + " order by "
				+ MessageInformSafeData._YEARANDMONTH + " desc, " + MessageInformSafeData._MONTHANDDAY
				+ " desc";
		Cursor cursor = LauncherApp.getInstance().getDBHelper().getWritableDatabase().rawQuery(sql, null);
		try {
			while (cursor != null && cursor.moveToNext()) {
				MessageInformSafe info = new MessageInformSafe();
				info.setYearAndMonth(cursor.getString(cursor
						.getColumnIndex(MessageInformSafeData._YEARANDMONTH)));
				info.setMonthAndDay(cursor.getString(cursor
						.getColumnIndex(MessageInformSafeData._MONTHANDDAY)));
				info.setName(cursor.getString(cursor
						.getColumnIndex(MessageInformSafeData._NAME)));
				info.setEventType(cursor.getString(cursor
						.getColumnIndex(MessageInformSafeData._EVENTTYPE)));
				info.setNum(cursor.getString(cursor
						.getColumnIndex(MessageInformSafeData._NUM)));
				
				list.add(info);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
	/**
	 * 删除所有消息盒数据安全类
	 * @param dbFlag
	 */
	public void deleteAllMessageInformSafe() {
		LauncherApp.getInstance().getDBHelper().getWritableDatabase().delete(MessageInformSafeData.TABLE_MESSAGE_INFO_SAFE, null, null);
	}
}
