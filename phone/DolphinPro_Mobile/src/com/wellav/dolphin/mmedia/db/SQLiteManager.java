package com.wellav.dolphin.mmedia.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.InviteMessage;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.entity.TGroupInfo;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;


public class SQLiteManager extends AbstractSQLManager {

    private String LOGTAG = "SQLiteManager";
    public static SQLiteManager getInstance() {
        return DolphinApp.getInstance().getSqlManager();
    }

    /**
     * 获取某一群组的成员信息名字
     * @param groupId
     * @return
     */
    public List<String> getFamilyMembersById(String FamilyId) {
        List<String> list = new ArrayList<String>();
        String sql = "select " + FamilyUserInfo._FAMILY_USER_NAME + " from " + TABLE_FAMILY_USER_INFO+ " where " + FamilyUserInfo._FAMILY_ID + " ='" + FamilyId + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            while(cursor!=null && cursor.moveToNext()){
            	list.add(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NAME)));
               
            }            
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }


    
    /**
     * 根据UserID获取家庭成员loginname
     * @param userid
     * @return
     */
    
    public String getLoginNameByUserId(String userid){
    	String sql = "select * from " + TABLE_FAMILY_USER_INFO + " where " + FamilyUserInfo._FAMILY_USER_ID + " ='" + userid + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        String LoginName = null;
        try{
            if(cursor!=null && cursor.moveToNext()){
            	LoginName = cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NAME));
            }    
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        
        return LoginName;
    }
    
    /**
     * 根据DeviceUserID获取紧急联系人ID
     * @param deviceuserid
     * @return userid
     */
    
    public String getAgentContactIDByDeviceUserID(String deviceuserid){
    	String sql = "select * from " + TABLE_FAMILY_INFO + " where " + FamilyInfo._FAMILY_DEVICE_USER_ID + " ='" + deviceuserid + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        String contactName = null;
        try{
            if(cursor!=null && cursor.moveToNext()){
            	contactName = cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AGENTCONTACT_ID));
            }    
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        
        return contactName;
    }
    
    /**
     * 根据DeviceID获取家庭成员familyid
     * @param DeviceID
     * @return
     */
    
    public String getFamilyIdByDeviceName(String DeviceName){
    	String sql = "select * from " + TABLE_FAMILY_INFO + " where " + FamilyInfo._FAMILY_DEVICE_ID + " ='" + DeviceName + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        String deviceName = null;
        try{
            if(cursor!=null && cursor.moveToNext()){
            	deviceName = cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ID));
            }    
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        
        return deviceName;
    }
    
    /**
     * 根据ID获取家庭所有familyid
     * @param userid
     * @return
     */
    
    public List<String> getFamilyIds(){
    	String sql = "select * from " + TABLE_FAMILY_INFO;
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        List<String> familyids = new ArrayList<String>();
        try{
        	 while(cursor!=null && cursor.moveToNext()){
            	String id = cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ID));
            	familyids.add(id);
            }    
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        
        return familyids;
    }
    
    /**
     * 根据FamilyID获取userid
     * @param FamilyID
     * @return userid
     */
    
    public ArrayList<String> getUseridByFamilyId(String familyid){
    	String sql = "select * from " + TABLE_FAMILY_USER_INFO + " where " + FamilyUserInfo._FAMILY_ID + " ='" + familyid + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        ArrayList<String> ids = new ArrayList<>();
        try{
            while(cursor!=null && cursor.moveToNext()){
            	ids.add(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_ID)));
            }    
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        
        return ids;
    }
    
    /**
     * 根据userid获取FamilyID
     * @param userid
     * @return
     */
    
    public ArrayList<String> getFamilyIDByUserId(String userid){
    	String sql = "select * from " + TABLE_FAMILY_USER_INFO + " where " + FamilyUserInfo._FAMILY_USER_ID + " ='" + userid + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        ArrayList<String> Familyids = new ArrayList<>();
        try{
            while(cursor!=null && cursor.moveToNext()){
            	Familyids.add(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_ID)));
            }    
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        
        return Familyids;
    }
    
    /**
     * 获取观看权限
     * @param userid
     * @return
     */
    public boolean getFamilyUsersAuthorByUserId(String familyid, String userid){
    	String sql = "select * from " + TABLE_FAMILY_USER_INFO + " where " + FamilyUserInfo._FAMILY_USER_ID + " ='" + userid + "'"+" and "+FamilyUserInfo._FAMILY_ID + " ='" + familyid + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        String Author ="4";
        try{
            if(cursor!=null &&  cursor.moveToFirst()){
            	Author = cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_AUTHORITY));
            }    
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        
        return DolphinUtil.getPrivacy(Author);
    }
       
    /**
     * 根据家庭组ID获取家庭成员
     * TODO 需要更改
     * @param famillyId
     * @return
     */
    public List<FamilyUserInfo> geFamilyUserInfoFamilyId(String id) {
    	List<FamilyUserInfo> infos = new ArrayList<FamilyUserInfo>();
    	FamilyUserInfo myinfo = null ;
        String sql = "select * from " + TABLE_FAMILY_USER_INFO + " where " + FamilyUserInfo._FAMILY_ID + " ='" + id + "'"+ "order by " + FamilyUserInfo._FAMILY_USER_FIRST_KEY + " asc";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
        	while(cursor!=null && cursor.moveToNext()){
            	 FamilyUserInfo info = new FamilyUserInfo();
            	 info.setFamilyID(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_ID)));
            	 String userid = cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_ID));
                 info.setUserID(userid);
                 String author = cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_AUTHORITY));
                 info.setAuthority(author);
                 info.setLoginName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NAME)));
                 info.setNickName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NICK_NAME)));
                 info.setNoteName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NOTE_NAME)));
                 info.setDeviceType(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_DEVICE_TYPE)));
                 info.setBirthday(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_BIRTHDAY)));
                 info.setSex(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_SEX)));
                 info.setCity(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_CITY)));
                 info.setIcon(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_PHOTO_ID)));
                 info.setFirstChar(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_FIRST_KEY)));
                 
                 
                 if(DolphinUtil.getCenter(author)==1){
                	 infos.add(0, info);
     			}else if(SysConfig.userid.equals(userid)){
     				myinfo = info;
     			}else{
     				infos.add(info);
     			}
            } 
        	infos.add(1, myinfo);
        }
        finally {
            if(cursor!=null)
            {
                cursor.close(); 
                cursor = null;
            }
        }
        return infos;
    }
    
    /**
     * 根据家庭组ID获取家庭成员
     * TODO 需要更改
     * @param famillyId
     * @return
     */
    public List<FamilyUserInfo> getFamilyUserInfoByFamilyIDNotDevice(String id) {
    	List<FamilyUserInfo> infos = new ArrayList<FamilyUserInfo>();
    	// String sql = "select * from table_family_user_info where _family_id=? and _family_user_id=?";
         //Cursor cursor = sqliteDB().rawQuery(sql, new String[]{info.getFamilyID(),info.getUserID()}); 
    	
        String sql = "select * from " + TABLE_FAMILY_USER_INFO + " where " + FamilyUserInfo._FAMILY_ID + " =?" + " and "+FamilyUserInfo._FAMILY_USER_DEVICE_TYPE+" =?"
    	+" order by " + FamilyUserInfo._FAMILY_USER_FIRST_KEY + " asc";
        Cursor cursor = sqliteDB().rawQuery(sql, new String[]{id,MsgKey.PHONE});
        try{
        	while(cursor!=null && cursor.moveToNext()){
            	 FamilyUserInfo info = new FamilyUserInfo();
            	 info.setFamilyID(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_ID)));
            	 String userid = cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_ID));
                 info.setUserID(userid);
                 String author = cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_AUTHORITY));
                 info.setAuthority(author);
                 info.setLoginName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NAME)));
                 info.setNickName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NICK_NAME)));
                 info.setNoteName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NOTE_NAME)));
                 info.setDeviceType(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_DEVICE_TYPE)));
                 info.setBirthday(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_BIRTHDAY)));
                 info.setSex(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_SEX)));
                 info.setCity(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_CITY)));
                 info.setIcon(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_PHOTO_ID)));
                 info.setFirstChar(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_FIRST_KEY)));
                 
                 
     			infos.add(info);
            } 
        }
        finally {
            if(cursor!=null)
            {
                cursor.close(); 
                cursor = null;
            }
        }
        return infos;
    }
    /**
     * 根据联系人ID获取家庭组
     * TODO 需要更改
     * @param uesrId
     * @return
     */
    public FamilyUserInfo geFamilyUserInfoLoginName(String name) {
   	  FamilyUserInfo info = new FamilyUserInfo();
        String sql = "select * from " + TABLE_FAMILY_USER_INFO + " where " + FamilyUserInfo._FAMILY_USER_NAME + " ='" + name + "'";
   	    // +" and"+ " where " + FamilyUserInfo._FAMILY_ID + " ='" + id + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
        	while(cursor!=null && cursor.moveToNext()){
            	 info.setFamilyID(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_ID)));
                 info.setUserID(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_ID)));
                 info.setAuthority(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_AUTHORITY)));
                 info.setLoginName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NAME)));
                 info.setNickName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NICK_NAME)));
                 info.setNoteName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NOTE_NAME)));
                 info.setDeviceType(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_DEVICE_TYPE)));
                 info.setBirthday(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_BIRTHDAY)));
                 info.setSex(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_SEX)));
                 info.setCity(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_CITY)));
                 info.setIcon(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_PHOTO_ID)));
                 info.setFirstChar(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_FIRST_KEY)));
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
     * 根据联系人ID获取家庭组
     * TODO 需要更改
     * @param uesrId
     * @return
     */
    public FamilyUserInfo geFamilyUserInfoUserId(String userid) {
   	  FamilyUserInfo info = new FamilyUserInfo();
        String sql = "select * from " + TABLE_FAMILY_USER_INFO + " where " + FamilyUserInfo._FAMILY_USER_ID + " ='" + userid + "'";
   	    // +" and"+ " where " + FamilyUserInfo._FAMILY_ID + " ='" + id + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
        	while(cursor!=null && cursor.moveToNext()){
            	 info.setFamilyID(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_ID)));
                 info.setUserID(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_ID)));
                 info.setAuthority(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_AUTHORITY)));
                 info.setLoginName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NAME)));
                 info.setNickName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NICK_NAME)));
                 info.setNoteName(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_NOTE_NAME)));
                 info.setDeviceType(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_DEVICE_TYPE)));
                 info.setBirthday(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_BIRTHDAY)));
                 info.setSex(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_SEX)));
                 info.setCity(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_CITY)));
                 info.setIcon(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_PHOTO_ID)));
                 info.setFirstChar(cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_FIRST_KEY)));
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
     * 添加个家庭组成员
     * @param list
     */
    public void saveFamilyUserInfoList(final List<FamilyUserInfo> list, final boolean dbFlag) {
        HBaseApp.post2WorkRunnable(new Runnable (){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (int i = 0; i < list.size(); i++) {
                    saveFamilyUserInfo(list.get(i), false);
                } 
                if(dbFlag){
                	dbChanged(new Integer(1)); //全部保存后刷新
                }
                
            }

        });

    }
    
    /**
     * 添加个家庭组成员
     * @param list
     */
    public void saveFamilyInfoList(final List<FamilyInfo> list, final boolean dbFlag) {
        HBaseApp.post2WorkRunnable(new Runnable (){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (int i = 0; i < list.size(); i++) {
                    saveFamilyInfo(list.get(i), false);
                } 
               //
                if(dbFlag){
                	dbChanged(new Integer(0)); //全部保存后刷新
                }
            }
        });
    }


    /**
     * 添加一条联系人信息
     * @param info
     */
    public void saveMyUserInfo(UserInfo info){
        //  CommFunc.PrintLog(5, LOGTAG, "name:" + info.getName());
    	 if(info.getLoginName() == null)
             return;
    	 PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_NAME, info.getLoginName());
    	 PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_NICK_NAME, info.getNickName());
    	 PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_ID, info.getUserID());
    	 PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_NOTE_NAME, info.getNoteName());
    	 PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_BIRTHDAY, info.getBirthday());
    	 PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_SEX, info.getSex());
    	 PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_CITY, info.getCity());
    	 PreferenceUtils.getInstance().saveSharePreferences(UserInfo._USER_PHOTO_ID, info.getIcon());
       
    }
    
    public UserInfo getMyUserInfo(){
    	UserInfo info = new UserInfo();
    	 info.setLoginName(PreferenceUtils.getInstance().getStringSharePreferences(UserInfo._USER_NAME));
    	 info.setNickName(PreferenceUtils.getInstance().getStringSharePreferences(UserInfo._USER_NICK_NAME));
    	 info.setUserID(PreferenceUtils.getInstance().getStringSharePreferences(UserInfo._USER_ID));
    	 info.setNoteName(PreferenceUtils.getInstance().getStringSharePreferences(UserInfo._USER_NOTE_NAME));
    	 info.setBirthday(PreferenceUtils.getInstance().getStringSharePreferences(UserInfo._USER_BIRTHDAY));
    	 info.setSex(PreferenceUtils.getInstance().getStringSharePreferences(UserInfo._USER_SEX));
    	 info.setCity(PreferenceUtils.getInstance().getStringSharePreferences(UserInfo._USER_CITY));
    	 info.setIcon(PreferenceUtils.getInstance().getStringSharePreferences(UserInfo._USER_PHOTO_ID));
    	return info;
    }
    /**
     * 添加一条家庭组成员信息
     * @param info
     */
    public void saveFamilyUserInfo(FamilyUserInfo info, boolean dbFlag){
        //  CommFunc.PrintLog(5, LOGTAG, "name:" + info.getName());
    	long code = 0;
        if(info.getFamilyID().length()==0 || info.getUserID() == null)
            return;
        String sql = "select * from table_family_user_info where _family_id=? and _family_user_id=?";
        Cursor cursor = sqliteDB().rawQuery(sql, new String[]{info.getFamilyID(),info.getUserID()}); 
        
        ContentValues values = new ContentValues();
        values.put(FamilyUserInfo._FAMILY_USER_ID, info.getUserID());
        values.put(FamilyUserInfo._FAMILY_ID, info.getFamilyID());
        values.put(FamilyUserInfo._FAMILY_USER_AUTHORITY, info.getAuthority());
        values.put(FamilyUserInfo._FAMILY_USER_NAME, info.getLoginName());
        values.put(FamilyUserInfo._FAMILY_USER_NICK_NAME, info.getNickName());
        values.put(FamilyUserInfo._FAMILY_USER_NOTE_NAME, info.getNoteName());
        values.put(FamilyUserInfo._FAMILY_USER_DEVICE_TYPE, info.getDeviceType());
        values.put(FamilyUserInfo._FAMILY_USER_BIRTHDAY, info.getBirthday());
        values.put(FamilyUserInfo._FAMILY_USER_SEX, info.getSex());
        values.put(FamilyUserInfo._FAMILY_USER_CITY, info.getCity());
        values.put(FamilyUserInfo._FAMILY_USER_PHOTO_ID, info.getIcon());
        values.put(FamilyUserInfo._FAMILY_USER_FIRST_KEY, info.getFirstChar());
        
        if(cursor.getCount() == 0)
        {
           
            //CommFunc.PrintLog(5, LOGTAG, "saveContactInfo name:"+info.getName()+" number:"+info.getPhoneNum());
            code =  sqliteDB().replaceOrThrow(TABLE_FAMILY_USER_INFO, null, values);
           

        }else{
        	String whereClause = "_family_id=? and _family_user_id=?";//修改条件
        	String[] whereArgs = {info.getFamilyID(),info.getUserID()};//修改条件的参数
        	
        	 sqliteDB().update(TABLE_FAMILY_USER_INFO, values,whereClause, whereArgs);
       }
        
        if(dbFlag){
        	dbChanged(new Integer(1)); //全部保存后刷新
        }
        
    }
    
    /**
     * 添加一条家庭组
     * @param info
     */
    public void saveFamilyInfo(FamilyInfo info, boolean dbFlag){
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
            values.put(FamilyInfo._FAMILY_ICON, info.getDeviceIcon());
            values.put(FamilyInfo._FAMILY_AGENTCONTACT_ID, info.getAgentContact());
            //CommFunc.PrintLog(5, LOGTAG, "saveContactInfo name:"+info.getName()+" number:"+info.getPhoneNum());
            sqliteDB().replaceOrThrow(TABLE_FAMILY_INFO, null, values);
            //   CommFunc.PrintLog(5, LOGTAG, "getContactId:"+info.getContactId() +" name:"+info.getName()+"  number:"+info.getPhoneNum());

        }
        if(dbFlag){
        	dbChanged(new Integer(5)); //全部保存后刷新
        }
        
    }
    
    public List<FamilyInfo>  getFamilyInfo(){
    	List<FamilyInfo> list = new ArrayList<FamilyInfo>();
        String sql = "select * from " + TABLE_FAMILY_INFO ;
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            while(cursor!=null && cursor.moveToNext()){
            	FamilyInfo info = new FamilyInfo();
                info.setFamilyID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ID)));
                info.setDeviceID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_DEVICE_ID)));
                info.setMangerID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_MANGER_ID)));
                info.setDeviceUserID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_DEVICE_USER_ID)));
                info.setAuthority(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AUTHORITY)));
                info.setNickName(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_NICKNAME)));
                info.setNote(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_NOTE)));
                info.setCity(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_CITY)));
                info.setDeviceIcon(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ICON)));
                info.setAgentContact(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AGENTCONTACT_ID)));
                list.add(info);
            }    
        }
            

        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }
    
    public String  getFamilyInfoManagerByFamilyId(String familyID){
    	String manager = null  ;
        String sql = "select * from " + TABLE_FAMILY_INFO  + " where " + FamilyInfo._FAMILY_ID + " ='" + familyID + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
        	  if(cursor!=null && cursor.moveToNext()){
        		  manager = cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_MANGER_ID));
              } 
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return manager;
    }
    public FamilyInfo  getFamilyInfoFamilyID(String FamilyID){
    	FamilyInfo info  = new FamilyInfo();
        String sql = "select * from " + TABLE_FAMILY_INFO  + " where " + FamilyInfo._FAMILY_ID + " ='" + FamilyID + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
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
                info.setDeviceIcon(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ICON)));
                info.setAgentContact(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AGENTCONTACT_ID)));
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
    
    public FamilyInfo  getFamilyInfoDeviceID(String DeviceID){
    	FamilyInfo info  = new FamilyInfo();
        String sql = "select * from " + TABLE_FAMILY_INFO  + " where " + FamilyInfo._FAMILY_DEVICE_ID + " ='" + DeviceID + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
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
                info.setDeviceIcon(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ICON)));
                info.setAgentContact(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AGENTCONTACT_ID)));
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
     * 更新联系人信息的某一项 value为字符串类型
     * @param id
     * @param key
     * @param value
     * @param dbFlag
     */
    public void updateData(){
    	dbChanged(new Integer(0));
    }
    public void updateOnlineMsg(){
    	dbChanged(new Integer(0));
}
    /**
     * 更新联系人信息的某一项 value为字符串类型
     * @param id
     * @param key
     * @param value
     * @param dbFlag
     */
    public void updateFamilyInfo(String userid, String key, String value, boolean dbFlag){
        ContentValues values = new ContentValues();
        values.put(key, value);
        sqliteDB().update(TABLE_FAMILY_INFO, values, FamilyInfo._FAMILY_DEVICE_USER_ID + " = '" + userid  + "'", null);
        if (dbFlag) {
            dbChanged(new Integer(0));
        }
    }
    
    public List<FamilyInfo>  getFamilyInfoByMangerID(String userid){
    	List<FamilyInfo> list = new ArrayList<FamilyInfo>();
        String sql = "select * from " + TABLE_FAMILY_INFO  + " where " + FamilyInfo._FAMILY_MANGER_ID + " ='" + userid + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            while(cursor!=null && cursor.moveToNext()){
            	FamilyInfo info = new FamilyInfo();
                info.setFamilyID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ID)));
                info.setDeviceID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_DEVICE_ID)));
                info.setMangerID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_MANGER_ID)));
                info.setDeviceUserID(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_DEVICE_USER_ID)));
                info.setAuthority(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AUTHORITY)));
                info.setNickName(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_NICKNAME)));
                info.setNote(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_NOTE)));
                info.setCity(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_CITY)));
                info.setDeviceIcon(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_ICON)));
                info.setAgentContact(cursor.getString(cursor.getColumnIndex(FamilyInfo._FAMILY_AGENTCONTACT_ID)));
                list.add(info);
            }    
        }


        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }
    

    /**
     * 更新某一群组信息的某一项
     * @param groupId
     * @param key
     * @param value
     * @param dbFlag
     */
    public void updateFamilyUserInfo(String userId, String key, String value, boolean dbFlag) {

        ContentValues values = new ContentValues();
        values.put(key, value);
        {
            CommFunc.PrintLog(5, LOGTAG, "updateGroupInfo key:"+key+" value:"+value);
        }
        sqliteDB().update(TABLE_FAMILY_USER_INFO, values, FamilyUserInfo._FAMILY_USER_ID+ " = '" + userId + "'", null);
        if (dbFlag) {
            dbChanged(new Integer(1));
        }
    }
    

    /**
     * 更新某一群组信息的某一项
     * @param groupId
     * @param key
     * @param value
     * @param dbFlag
     */
    public void updateFamilyUserInfoAuthor(String familyid,String userId, String key, String value, boolean dbFlag) {

    	String whereClause = "_family_id=? and _family_user_id=?";//修改条件
    	String[] whereArgs = {familyid,userId};//修改条件的参数
    	
        ContentValues values = new ContentValues();
        values.put(key, value);
        {
            CommFunc.PrintLog(5, LOGTAG, "updateGroupInfo key:"+key+" value:"+value);
        }
       sqliteDB().update(TABLE_FAMILY_USER_INFO, values, whereClause, whereArgs);
        if (dbFlag) {
        	   dbChanged(new Integer(1));
        }
    }
    /**
     * 删除某一群组某个人
     * @param groupId
     * @param dbFlag
     */
    public void deleteFamilyUserInfoById(String FamilyId,String userid, boolean dbFlag){
    	String whereClause = "_family_id=? and _family_user_id=?";//修改条件
    	String[] whereArgs = {FamilyId,userid};//修改条件的参数
    	
        sqliteDB().delete(TABLE_FAMILY_USER_INFO,whereClause, whereArgs);
        if (dbFlag) {
        	   dbChanged(new Integer(1));
        }
    }
    /**
     * 删除某一群组
     * @param groupId
     * @param dbFlag
     */
    public void deleteFamilyInfoById(String FamilyId, boolean dbFlag){
        sqliteDB().delete(TABLE_FAMILY_USER_INFO, FamilyUserInfo._FAMILY_ID + " = '" + FamilyId + "'", null);
        sqliteDB().delete(TABLE_FAMILY_INFO, FamilyInfo._FAMILY_ID + " = '" + FamilyId + "'", null);
        deleteBoxMessageByFamilyid(FamilyId,false);
        CommFunc.PrintLog(5, LOGTAG, "deleteFamilyInfoById");
        if (dbFlag) {
        	   dbChanged(new Integer(2));
        }
    }
    
    /**
     * 管理员更新
     */
    public void updateFamilyAdmin(String Familyid,String oldId,String newId,boolean isUpdate){
    	String whereClause = "_family_id=? and _family_user_id=?";//修改条件
    	String[] oldwhereArgs = {Familyid,oldId};//修改条件的参数
        String sql = "select * from table_family_user_info where _family_id=? and _family_user_id=?";
        Cursor cursor = sqliteDB().rawQuery(sql, new String[]{Familyid,oldId}); 
        Log.e(LOGTAG, cursor.getCount()+"");
        if(cursor!=null && cursor.moveToNext()){
    	    ContentValues values = new ContentValues();
    		String  Authority = cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_AUTHORITY));
    		int manage = Integer.parseInt(Authority) & 6;//110
    		 
    	    values.put(FamilyUserInfo._FAMILY_USER_AUTHORITY, manage+"");
    	    sqliteDB().update(TABLE_FAMILY_USER_INFO, values,whereClause, oldwhereArgs);
    	    
    	    String whereClause2 = "_family_id=? and _family_manger_id=?";//修改条件
    	    ContentValues values2 = new ContentValues();
    	    values2.put(FamilyInfo._FAMILY_MANGER_ID, newId);
    	    sqliteDB().update(TABLE_FAMILY_INFO, values2,whereClause2, oldwhereArgs);
    	}
        
    	cursor = sqliteDB().rawQuery(sql, new String[]{Familyid,newId}); 
    	String[] newwhereArgs = {Familyid,newId};//修改条件的参数
    	  if(cursor!=null && cursor.moveToNext()){
    	    ContentValues values = new ContentValues();
    		String  Authority = cursor.getString(cursor.getColumnIndex(FamilyUserInfo._FAMILY_USER_AUTHORITY));
    		int manage = Integer.parseInt(Authority) | 1;//1
    		 
    	    values.put(FamilyUserInfo._FAMILY_USER_AUTHORITY, manage+"");
    	    sqliteDB().update(TABLE_FAMILY_USER_INFO, values,whereClause, newwhereArgs);
    	    
    	}
    	  if(cursor!=null){
    		  cursor.close();
    		  cursor = null;
    	  }
    	  if(isUpdate){
    		   dbChanged(new Integer(2));
    	  }
    	
    }


    /**
     * 关闭SQLiteDatabase
     */
    public void clearInstance() {
        if (sqliteDB != null)
            sqliteDB.close();
    }

    public void dbChanged(final Object object) {
        HBaseApp.post2UIRunnable(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                setChanged();
                notifyObservers(object); 
            }

        });

    }
    
    /**
	 * 保存message
	 * @param message
	 * @return  返回这条messaged在db中的id
	 */
	public synchronized void saveBoxMessageList(List<MessageInform> message){
		
		 for (int i = 0; i < message.size(); i++) {

			 saveBoxMessage(message.get(i));
	        }
		   dbChanged(new Integer(4));
	}
    /**
	 * 保存message
	 * @param message
	 * @return  返回这条messaged在db中的id
	 */
	public synchronized long saveBoxMessage(MessageInform info){
		
		ContentValues values = new ContentValues();
		
		values.put(MessageInform._NAME, info.getName());
		values.put(MessageInform._DOLPHINNAME, info.getDolphinName());
		values.put(MessageInform._DEVICE_ID, info.getDeviceID());
		values.put(MessageInform._FAMILY_ID, info.getFamilyId());
		values.put(MessageInform._EVENTTYPE, info.getEventType());
		values.put(MessageInform._BEENANSWERED, info.getBeenAnswered());
		values.put(MessageInform._NUM, info.getNum());
		values.put(MessageInform._TALKINGID, info.getTalkingID());
		values.put(MessageInform._MEETTINGID, info.getMeettingID());
		values.put(MessageInform._TIME, info.getTime());
		values.put(MessageInform._USER_ID, info.getUserID());
		 long id= sqliteDB().replaceOrThrow(TABLE_BOX_MESSAGE, null, values);
	       
		return id;
	}
	/**
	 * 更新message
	 * @param msgId
	 * @param values
	 */
	public void updateBoxMessage(int msgId,ContentValues values){
		if(sqliteDB().isOpen()){
			sqliteDB().update(TABLE_BOX_MESSAGE, values, MessageInform._ID + " = ?", new String[]{String.valueOf(msgId)});
		}
	}
	
	/**
	 * 获取所有消息盒数据
	 * @return
	 */
	public MessageInform getLastBoxMessageInfo() {
		
		MessageInform info = new MessageInform();
		String sql = "select * from " + TABLE_BOX_MESSAGE + " order by " + MessageInform._TIME
				+ " desc";
		Cursor cursor = sqliteDB().rawQuery(sql, null);
		cursor.moveToLast();
		try {
			if (cursor != null) {
				info.setTime(cursor.getString(cursor
						.getColumnIndex(MessageInform._TIME)));
				info.setName(cursor.getString(cursor
						.getColumnIndex(MessageInform._NAME)));
				info.setDolphinName(cursor.getString(cursor
						.getColumnIndex(MessageInform._DOLPHINNAME)));
				info.setFamilyId(cursor.getString(cursor
						.getColumnIndex(MessageInform._FAMILY_ID)));
				info.setDeviceID(cursor.getString(cursor
						.getColumnIndex(MessageInform._DEVICE_ID)));
				info.setEventType(cursor.getString(cursor
						.getColumnIndex(MessageInform._EVENTTYPE)));
				info.setBeenAnswered(cursor.getString(cursor
						.getColumnIndex(MessageInform._BEENANSWERED)));
				info.setNum(cursor.getString(cursor
						.getColumnIndex(MessageInform._NUM)));
				info.setUserID(cursor.getString(cursor
						.getColumnIndex(MessageInform._USER_ID)));
				info.setTalkingID(cursor.getString(cursor
						.getColumnIndex(MessageInform._TALKINGID)));
				info.setMeettingID(cursor.getString(cursor
						.getColumnIndex(MessageInform._MEETTINGID)));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return info;
	}
	
	/**
	 * 获取所有消息盒数据
	 * @return
	 */
	public List<MessageInform> getAllBoxMessageInfo() {
		
		List<MessageInform> list = new ArrayList<MessageInform>();
		String sql = "select * from " + TABLE_BOX_MESSAGE + " order by " + MessageInform._TIME
				+ " desc";
		Cursor cursor = sqliteDB().rawQuery(sql, null);
		try {
			while (cursor != null && cursor.moveToNext()) {
				MessageInform info = new MessageInform();
				info.setID(cursor.getInt(cursor
						.getColumnIndex(MessageInform._ID)));
				info.setTime(cursor.getString(cursor
						.getColumnIndex(MessageInform._TIME)));
				info.setName(cursor.getString(cursor
						.getColumnIndex(MessageInform._NAME)));
				info.setDolphinName(cursor.getString(cursor
						.getColumnIndex(MessageInform._DOLPHINNAME)));
				info.setFamilyId(cursor.getString(cursor
						.getColumnIndex(MessageInform._FAMILY_ID)));
				info.setDeviceID(cursor.getString(cursor
						.getColumnIndex(MessageInform._DEVICE_ID)));
				info.setEventType(cursor.getString(cursor
						.getColumnIndex(MessageInform._EVENTTYPE)));
				info.setBeenAnswered(cursor.getString(cursor
						.getColumnIndex(MessageInform._BEENANSWERED)));
				info.setNum(cursor.getString(cursor
						.getColumnIndex(MessageInform._NUM)));
				info.setUserID(cursor.getString(cursor
						.getColumnIndex(MessageInform._USER_ID)));
				info.setTalkingID(cursor.getString(cursor
						.getColumnIndex(MessageInform._TALKINGID)));
				info.setMeettingID(cursor.getString(cursor
						.getColumnIndex(MessageInform._MEETTINGID)));
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
    
	public void deleteBoxMessageById(int id,boolean flag){
		if(sqliteDB().isOpen()){
			sqliteDB().delete(TABLE_BOX_MESSAGE, MessageInform._ID + " = ?", new String[]{String.valueOf(id)});
		}
		
		if(flag){
			   dbChanged(new Integer(4));
		}
	}
	
	public void deleteBoxMessageByFamilyid(String family,boolean flag){
		if(sqliteDB().isOpen()){
			sqliteDB().delete(TABLE_BOX_MESSAGE, MessageInform._FAMILY_ID+ " = ?", new String[]{family});
		}
		
		if(flag){
			   dbChanged(new Integer(4));
		}
	}
	
	
	public void deleteALLBoxMessage(){
        if(sqliteDB().isOpen()){
        	sqliteDB().execSQL("DELETE FROM "+TABLE_BOX_MESSAGE);
        }
        if(sqliteDB()!=null){
        	sqliteDB().close();
        }
        dbChanged(new Integer(4));
    }
	
    /**
	 * 保存message
	 * @param message
	 * @return  返回这条messaged在db中的id
	 */
	public synchronized void saveInviteMessageList(List<InviteMessage> message){
		
		 for (int i = 0; i < message.size(); i++) {

			 saveInviteMessage(message.get(i),false);
	        }
		   dbChanged(new Integer(5));
	}
	
    
    /**
	 * 保存message
	 * @param message
	 * @return  返回这条messaged在db中的id
	 */
	public synchronized long saveInviteMessage(InviteMessage message,boolean isChange){
		
			ContentValues values = new ContentValues();
			
			values.put(InviteMessage.COLUMN_MSG_MANAGER_ID, message.getManagerId());
			values.put(InviteMessage.COLUMN_MSG_MANAGER_NAME, message.getManagerName());
			values.put(InviteMessage.COLUMN_MSG_MANAGER_NICKNAME, message.getManagerNickname());
			values.put(InviteMessage.COLUMN_MSG_USER_NICKNAME, message.getUserNickname());
			values.put(InviteMessage.COLUMN_MSG_USER_NAME, message.getUserName());
			values.put(InviteMessage.COLUMN_MSG_USER_ID, message.getUserId());
			values.put(InviteMessage.COLUMN_MSG_USER_FAMILY_ID, message.getUserFamilyId());
			values.put(InviteMessage.COLUMN_MSG_FAMILY_ID, message.getDeviceFamilyId());
			values.put(InviteMessage.COLUMN_MSG_DEVICE_USER_ID, message.getDeviceUserID());
			values.put(InviteMessage.COLUMN_MSG_DEVICE_NAME, message.getDeviceName());
			values.put(InviteMessage.COLUMN_MSG_DEVICE_ID, message.getDeviceId());
			values.put(InviteMessage.COLUMN_MSG_CONTENT, message.getContent());
			values.put(InviteMessage.COLUMN_MSG_TIME, message.getTime());
			values.put(InviteMessage.COLUMN_MSG_ACTION, message.getAction());
			values.put(InviteMessage.COLUMN_MSG_STATUS, message.getStatus());
			values.put(InviteMessage.COLUMN_MSG_TYPE, message.getType());
		    long id= sqliteDB().replaceOrThrow(TABLE_INVITE_MSG, null, values);
	       if(isChange){
	    	   dbChanged(new Integer(5));
	       }
		    
		return id;
	}
	/**
	 * 更新message
	 * @param msgId
	 * @param values
	 */
	public void updateInviteMessage(int msgId,ContentValues values){
		if(sqliteDB().isOpen()){
			sqliteDB().update(TABLE_INVITE_MSG, values, InviteMessage.COLUMN_MSG_ID + " = ?", new String[]{String.valueOf(msgId)});
		}
	}
	
	/**
	 * 获取messges
	 * @return
	 */
	public List<InviteMessage> getInviteMessagesList(){
		List<InviteMessage> msgs = new ArrayList<InviteMessage>();
		if(sqliteDB().isOpen()){
			 String sql = "select * from " + TABLE_INVITE_MSG + " order by " + InviteMessage.COLUMN_MSG_ID + " desc";
			Cursor cursor = sqliteDB().rawQuery(sql,null);
			while(cursor.moveToNext()){
				InviteMessage msg = new InviteMessage();
				int id = cursor.getInt(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_ID));
				String usernickname = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_USER_NICKNAME));
				String username = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_USER_NAME));
				String userid = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_USER_ID));
				String userfamilyid = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_USER_FAMILY_ID));
				String devicename = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_DEVICE_NAME));
				String deviceid = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_DEVICE_ID));
				String deviceuserid = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_DEVICE_USER_ID));
				String familyid = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_FAMILY_ID));
				String time = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_TIME));
				String content = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_CONTENT));
				String type = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_TYPE)); 
				String manager = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_MANAGER_ID)); 
				String managername = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_MANAGER_NAME)); 
				String managernickname = cursor.getString(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_MANAGER_NICKNAME)); 
				int status = cursor.getInt(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_STATUS));
				int action = cursor.getInt(cursor.getColumnIndex(InviteMessage.COLUMN_MSG_ACTION));
				msg.setId(id);
				msg.setUserNickname(usernickname);
				msg.setUserName(username);
				msg.setUserId(userid);
				msg.setUserFamilyId(userfamilyid);
				msg.setDeviceFamilyId(familyid);
				msg.setDeviceName(devicename);
				msg.setDeviceUserID(deviceuserid);
				msg.setDeviceId(deviceid);
				msg.setTime(time);
			//	msg.setType(type);
				msg.setContent(content);
				msg.setStatus(status);
				msg.setAction(action);
				msg.setManagerId(manager);
				msg.setManagerName(managername);
				msg.setManagerNickname(managernickname);
				msgs.add(msg);
			}
			cursor.close();
		}
		return msgs;
	}
	
	public void deleteInviteMessage(String name){
		if(sqliteDB().isOpen()){
			sqliteDB().delete(TABLE_INVITE_MSG, InviteMessage.COLUMN_MSG_USER_NAME + " = ?", new String[]{name});
		}
	}
	public void deleteALLInviteMessage(){
        if(sqliteDB().isOpen()){
        	sqliteDB().execSQL("DELETE FROM "+TABLE_INVITE_MSG);
        }
        if(sqliteDB()!=null){
        	sqliteDB().close();
        }
    }
}
