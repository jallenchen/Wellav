package com.wellav.dolphin.mmedia.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.wellav.dolphin.mmedia.commands.SysConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// �û����ݿ��ļ��İ汾
	private static final int DB_VERSION = 3;
	// ���ݿ��ļ�Ŀ����·��ΪϵͳĬ��λ�ã�cn.arthur.examples ����İ���
	private static String DB_PATH = "/data/data/"+SysConfig.PackageName+"/databases/";
	/*
	 * //�����������ݿ��ļ������SD���Ļ� private static String DB_PATH =
	 * android.os.Environment.getExternalStorageDirectory().getAbsolutePath() +
	 * "/arthurcn/drivertest/packfiles/";
	 */
	private static String DB_NAME = "dolphin_cities.db";
	private static String ASSETS_NAME = "dolphin_cities.db";
	private SQLiteDatabase myDataBase = null;
	private final Context myContext;

	/**
	 * ������ݿ��ļ��ϴ�ʹ��FileSplit�ָ�ΪС��1M��С�ļ� �����зָ�Ϊ hello.db.101 hello.db.102
	 * hello.db.103
	 */
	private static final int ASSETS_SUFFIX_BEGIN = 101;
	private static final int ASSETS_SUFFIX_END = 103;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, null, version);
		this.myContext = context;
	}

	public DBHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DBHelper(Context context, String name) {
		this(context, name, DB_VERSION);
	}

	public DBHelper(Context context) {
		this(context, DB_PATH + DB_NAME);
	}

	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
		} else {
			try {
				File dir = new File(DB_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File dbf = new File(DB_PATH + DB_NAME);
				if (dbf.exists()) {
					dbf.delete();
				}
				SQLiteDatabase.openOrCreateDatabase(dbf, null);
				copyDataBase();
			} catch (IOException e) {
				throw new Error(e.toString());
			}
		}
	}
	
	private boolean checkDataBase(){
	     File dbFile = new File( DB_PATH, DB_NAME );
	     return dbFile.exists();
	}
//	
//	private boolean checkDataBase() {
//		SQLiteDatabase checkDB = null;
//		String myPath = DB_PATH + DB_NAME;
//		try {
//			checkDB = SQLiteDatabase.openDatabase(myPath, null,
//					SQLiteDatabase.OPEN_READONLY);
//		} catch (SQLiteException e) {
//			// database does't exist yet.
//			e.printStackTrace();
//		}
//		if (checkDB != null) {
//			checkDB.close();
//		}
//		return checkDB != null ? true : false;
//	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(ASSETS_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	private void copyBigDataBase() throws IOException {
		InputStream myInput;
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		for (int i = ASSETS_SUFFIX_BEGIN; i < ASSETS_SUFFIX_END + 1; i++) {
			myInput = myContext.getAssets().open(ASSETS_NAME + "." + i);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myInput.close();
		}
		myOutput.close();
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}