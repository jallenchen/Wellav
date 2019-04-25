package com.wellav.dolphin.mmedia.db;

import java.util.Observable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.InviteMessage;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.utils.CommFunc;



public abstract class AbstractSQLManager extends Observable {
    public static final int VERSION = 2;
    public static final String TAG = AbstractSQLManager.class.getName();
    private DatabaseHelper databaseHelper;
    public SQLiteDatabase sqliteDB;
    public String TABLE_FAMILY_USER_INFO = "table_family_user_info"; //联系人
    public String TABLE_FAMILY_INFO = "table_family_info"; //家庭组
    public String TABLE_INVITE_MSG = "table_invite_msgs"; //家庭组
    public static String TABLE_BOX_MESSAGE = "table_box_msgs"; // 消息盒
    
    private String[] tables = {TABLE_FAMILY_USER_INFO,TABLE_FAMILY_INFO,TABLE_INVITE_MSG,TABLE_BOX_MESSAGE}; // 数据库表数组

    public AbstractSQLManager() {
        openDatabase(DolphinApp.getInstance(),SysConfig.uid, VERSION);
    }

    private void openDatabase(Context context,String name, int databaseVersion) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context,name, databaseVersion);
        }
        if (sqliteDB == null) {
            sqliteDB = databaseHelper.getWritableDatabase();
        }
    }

    public void destroy() {
        try {
            if (databaseHelper != null) {
                databaseHelper.close();
            }
            if (sqliteDB != null) {
                sqliteDB.close();
            }
        } catch (Exception e) {
            
        }
    }

    private void open(boolean isReadonly) {
        if (sqliteDB == null) {
            if (isReadonly) {
                sqliteDB = databaseHelper.getReadableDatabase();
            } else {
                sqliteDB = databaseHelper.getWritableDatabase();
            }
        }
    }
    

    public final void reopen() {
        closeDB();
        open(false);
        
    }

    private void closeDB() {
        if (sqliteDB != null) {
            sqliteDB.close();
            sqliteDB = null;
        }
    }

    protected final SQLiteDatabase sqliteDB() {
        open(false);
        return sqliteDB;
    }
    private class DatabaseHelper extends SQLiteOpenHelper {
        
        public DatabaseHelper(Context context, String name,int version) {
            super(context, name+".db",null, version);
            // TODO Auto-generated constructor stub
            CommFunc.PrintLog(5, "DatabaseHelper--dbname", name+".db");
        }

        /**
         * 重写oncreate方法建表
         */

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }
        
        private void createTables(SQLiteDatabase db) {
            createTableForFamilyUserInfo(db);
            createTableForFamilyInfo(db);
            createTableForInviteMsg(db);
            createTableForMessageInfo(db);
        }

        /**
         * 消息
         * @param db
         */
        private void createTableForInviteMsg(SQLiteDatabase db) {
        	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_INVITE_MSG + " ("
        			+ InviteMessage.COLUMN_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        			+ InviteMessage.COLUMN_MSG_TYPE + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_DEVICE_NAME + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_DEVICE_ID + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_DEVICE_USER_ID + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_FAMILY_ID + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_MANAGER_ID + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_MANAGER_NAME + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_MANAGER_NICKNAME + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_USER_ID + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_USER_NAME + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_USER_NICKNAME + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_USER_FAMILY_ID + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_CONTENT + " TEXT,"
        			+ InviteMessage.COLUMN_MSG_STATUS + " INTEGER,"
        			+ InviteMessage.COLUMN_MSG_ACTION + " INTEGER,"
        			+ InviteMessage.COLUMN_MSG_TIME + " TEXT UNIQUE"
            + ")";
        	db.execSQL(sql);
        }
        
    	/**
    	 * 创建消息盒表
    	 * @param db
    	 */
    	private void createTableForMessageInfo(SQLiteDatabase db) {
    		String sql = "CREATE TABLE IF NOT EXISTS "
    				+ TABLE_BOX_MESSAGE
    				+ " ("
    				+ MessageInform._ID
    				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 自增
    				+ MessageInform._DEVICE_ID + " VARCHAR,"
    				+ MessageInform._DOLPHINNAME + " VARCHAR,"
    				+ MessageInform._FAMILY_ID + " VARCHAR,"
    				+ MessageInform._EVENTTYPE + " VARCHAR,"
    				+ MessageInform._USER_ID + " VARCHAR,"
    				+ MessageInform._NAME + " VARCHAR,"
    				+ MessageInform._TALKINGID + " VARCHAR,"
    				+ MessageInform._MEETTINGID + " VARCHAR,"
    				+ MessageInform._BEENANSWERED + " VARCHAR," 
    				+ MessageInform._NUM + " VARCHAR,"
    				+ MessageInform._TIME + " TEXT UNIQUE"
    				+ ")";
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
            + FamilyInfo._FAMILY_AGENTCONTACT_ID + " VARCHAR,"
            + FamilyInfo._FAMILY_DEVICE_USER_ID + " VARCHAR,"
            + FamilyInfo._FAMILY_NICKNAME + " VARCHAR,"
            + FamilyInfo._FAMILY_NOTE + " VARCHAR,"
            + FamilyInfo._FAMILY_CITY + " VARCHAR,"
            + FamilyInfo._FAMILY_AUTHORITY + " VARCHAR,"
            + FamilyInfo._FAMILY_ICON + " VARCHAR"
            + ")";
        	db.execSQL(sql);
        }
        
        
        /**
         * 创建家庭组
         * @param db
         */
        private void createTableForFamilyUserInfo(SQLiteDatabase db) {
        	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_FAMILY_USER_INFO + " ("
            + FamilyUserInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 自增
            + FamilyUserInfo._FAMILY_USER_ID + " VARCHAR,"//UNIQUE
            + FamilyUserInfo._FAMILY_ID + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_NAME + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_NICK_NAME + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_NOTE_NAME + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_DEVICE_TYPE + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_BIRTHDAY + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_SEX + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_CITY + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_FIRST_KEY + " VARCHAR,"
            + FamilyUserInfo._FAMILY_USER_AUTHORITY + " VARCHAR,"
              + FamilyUserInfo._FAMILY_USER_PHOTO_ID + " VARCHAR"
            + ")";
        	db.execSQL(sql);
        }

        /**
         * 重写onupgrade方法，当数据库版本号更新时执行
         */

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            CommFunc.PrintLog(5, TAG, "onUpgrade oldversion:"+oldVersion +"newVersion:"+newVersion);
            // TODO Auto-generated method stub
            dropTableByTableName(db,tables);
            createTables(db);
            //
//            MyApplication.getInstance().clearConfigInfo();
//            MyApplication.getInstance().cleanSnapConfigInfo();
//             MyApplication.getInstance().saveDataToSharedXml(new String[]{RegistInfo.VER_ADDR,RegistInfo.VER_FAX,FaxWeb.KEY_VERSION}
//             , new String[]{"0","0","0"});
        }
        /**
         * Drop表
         * 
         * @param db
         * @param TABLENAME
         */
        void dropTableByTableName(SQLiteDatabase db, String[] TABLENAME) {
            StringBuffer sql = new StringBuffer("DROP TABLE IF EXISTS ");
            int len = sql.length();
            for (String name : TABLENAME) {
                try {
                    sql.append(name);
                    db.execSQL(sql.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    sql.delete(len, sql.length());
                }
            }
        }

    }

}
