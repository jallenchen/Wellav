package com.wellav.dolphin.interfaces;

import android.net.Uri;
import android.provider.BaseColumns;

public interface IProviderContactMetaData {

	 // 定义外部访问的Authority
    public static final String AUTHORITY = "com.wellav.contactprovider";
    // 数据库名称
    public static final String DB_NAME = "dolphin_contact.db";
    // 数据库版本
    public static final int VERSION = 1;
    
    
    public interface ContactTableMetaData extends BaseColumns {
        // 表名
        public static final String TABLE_NAME = "contact";
        // 外部程序访问本表的uri地址
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + TABLE_NAME);
        // Contact表列名
    	public static final String _ID = "_id";
    	public static final String _FAMILY_ID = "_family_id";
    	public static final String _FAMILY_USER_ID = "_family_user_id";
    	public static final String _FAMILY_USER_AUTHORITY = "_family_user_authority";
    	public static final String _FAMILY_USER_NAME = "_family_user_name";
    	public static final String _FAMILY_USER_NICK_NAME = "_family_user_nick_name";
    	public static final String _FAMILY_USER_NOTE_NAME = "_family_user_note_name";
    	public static final String _FAMILY_USER_DEVICE_TYPE = "_family_user_device_type";
    	public static final String _FAMILY_USER_BIRTHDAY = "_family_user_birthday";
    	public static final String _FAMILY_USER_SEX = "_family_user_sex";
    	public static final String _FAMILY_USER_CITY= "_family_user_city";
    	public static final String _FAMILY_USER_PHOTO_ID = "_family_user_photo_id";
    	public static final String _FAMILY_USER_FIRST_KEY = "_family_user_first_key";
        //默认排序
        public static final String SORT_ORDER="_id desc";
        //得到book表中的所有记录
        public static final String CONTENT_LIST="vnd.android.cursor.dir/vnd.contactprovider.contact";
        //得到一个表信息
        public static final String CONTENT_ITEM="vnd.android.cursor.item/vnd.contactprovider.contact";
    }
    
    public interface MessageInformData extends BaseColumns {
        // 表名
    	public static String TABLE_MESSAGE_INFO = "table_message_info"; // 消息盒
        
        // MessageInform表列名
        public static String _ID = "_id";
    	public static String _YEARANDMONTH = "_yearAndMonth";
    	public static String _MONTHANDDAY = "_monthAndDay";
    	public static String _NAME = "_name";
    	public static String _DOLPHINNAME = "_dolphinName";
    	public static String _EVENTTYPE = "_eventType";
    	public static String _BEENANSWERED = "_beenAnswered"; 
    	public static String _NUM = "_num"; 
    	public static String _TALKINGID = "_talkingID";
    	public static String _MEETTINGID = "_meettingID";
    	
     }
    
    public interface MessageInformSafeData extends BaseColumns {
        // 表名
    	public static String TABLE_MESSAGE_INFO_SAFE = "table_message_info_safe"; // 消息盒安全类
        
        // MessageInformSafe表列名
        public static String _ID = "_id";
    	public static String _YEARANDMONTH = "_yearAndMonth";
    	public static String _MONTHANDDAY = "_monthAndDay";
    	public static String _NAME = "_name";
    	public static String _EVENTTYPE = "_eventType";
    	public static String _NUM = "_num"; 
    	
    }
}
