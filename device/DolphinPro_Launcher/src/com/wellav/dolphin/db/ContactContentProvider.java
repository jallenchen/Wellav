package com.wellav.dolphin.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.wellav.dolphin.interfaces.IProviderContactMetaData;
import com.wellav.dolphin.interfaces.IProviderContactMetaData.ContactTableMetaData;

public class ContactContentProvider extends ContentProvider {

	private static final String TAG = "ContactContentProvider";
	private static UriMatcher uriMatcher = null;
	private static final int CONTACTS = 1;
	private static final int CONTACT = 2;
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	/**
	 * 这部分就相当于为外部程序准备好一个所有地址匹配集合
	 */
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(IProviderContactMetaData.AUTHORITY,
				IProviderContactMetaData.ContactTableMetaData.TABLE_NAME,
				CONTACTS);
		uriMatcher
				.addURI(IProviderContactMetaData.AUTHORITY,
						IProviderContactMetaData.ContactTableMetaData.TABLE_NAME
								+ "/#", CONTACT);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		db = dbHelper.getReadableDatabase();

		switch (uriMatcher.match(uri)) {
		case CONTACTS:

			return db
					.query(IProviderContactMetaData.ContactTableMetaData.TABLE_NAME,
							projection, selection, selectionArgs, null, null,
							sortOrder);
		case CONTACT:
			// long id = ContentUris.parseId(uri);
			String where = selection;
			if (selection != null && !"".equals(selection)) {
				// String where = selection + " and " + where;
			}
			return db.query(
					IProviderContactMetaData.ContactTableMetaData.TABLE_NAME,
					projection, where, selectionArgs, null, null, sortOrder);
		default:
			throw new IllegalArgumentException("This is a unKnow Uri"
					+ uri.toString());
		}
	}

	// 取得数据的类型,此方法会在系统进行URI的MIME过滤时被调用。
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case CONTACTS:
			return IProviderContactMetaData.ContactTableMetaData.CONTENT_LIST;
		case CONTACT:
			return IProviderContactMetaData.ContactTableMetaData.CONTENT_ITEM;
		default:
			throw new IllegalArgumentException("This is a unKnow Uri"
					+ uri.toString());
		}
	}

	// 增加
	@SuppressWarnings("static-access")
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (uriMatcher.match(uri)) {
		case CONTACTS:
			db = dbHelper.getWritableDatabase();// 取得数据库操作实例
			long rowId = db.insertWithOnConflict(
					IProviderContactMetaData.ContactTableMetaData.TABLE_NAME,
					IProviderContactMetaData.ContactTableMetaData._ID, values,
					db.CONFLICT_REPLACE);
			Uri insertUri = Uri.withAppendedPath(uri, "/" + rowId);
			Log.e(TAG, "insertUri:" + insertUri.toString());
			// 发出数据变化通知(book表的数据发生变化)
			this.getContext().getContentResolver()
					.notifyChange(ContactTableMetaData.CONTENT_URI, null);
			return insertUri;
		default:
			// 不能识别uri
			throw new IllegalArgumentException("This is a unKnow Uri"
					+ uri.toString());
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case CONTACTS:
			this.getContext().getContentResolver()
					.notifyChange(ContactTableMetaData.CONTENT_URI, null);
			return db.delete(
					IProviderContactMetaData.ContactTableMetaData.TABLE_NAME,
					selection, selectionArgs);
		case CONTACT:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			this.getContext().getContentResolver()
					.notifyChange(ContactTableMetaData.CONTENT_URI, null);
			return db.delete(
					IProviderContactMetaData.ContactTableMetaData.TABLE_NAME,
					selection, selectionArgs);
		default:
			throw new IllegalArgumentException("This is a unKnow Uri"
					+ uri.toString());
		}

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();

		Log.e(TAG, selection);
		switch (uriMatcher.match(uri)) {
		case CONTACTS:
			Log.e(TAG, "CONTACTS");
			this.getContext().getContentResolver()
					.notifyChange(ContactTableMetaData.CONTENT_URI, null);
			return db.update(
					IProviderContactMetaData.ContactTableMetaData.TABLE_NAME,
					values, null, null);
		case CONTACT:
			Log.e(TAG, "CONTACT");
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			this.getContext().getContentResolver()
					.notifyChange(ContactTableMetaData.CONTENT_URI, null);
			return db.update(
					IProviderContactMetaData.ContactTableMetaData.TABLE_NAME,
					values, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("This is a unKnow Uri"
					+ uri.toString());
		}
	}

}
