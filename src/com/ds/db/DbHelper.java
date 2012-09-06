package com.ds.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbHelper {
	private static final String TAG = "DbHelper";
	private static final String DataBaseName = "birthday";
	SQLiteDatabase db;
	Context mContext;
	private static final String BIRTH_TABLE = "birth";

	private static final String NAME = "name";
	private static final String SEX = "sex";
	private static final String BIRTHDAY = "birthday";
	private static final String RINGTYPE = "ringtype";
	private static final String RINGDAY = "ringday";
	private static final String PHONE_NUMBER = "phone_number";
	private static final String NOTE = "note";
	private String[] QUERY_ALL_PROJECTION = { NAME, SEX, BIRTHDAY, RINGTYPE,
			RINGDAY, PHONE_NUMBER, NOTE };

	public DbHelper(Context context) throws Exception {
		open(context);
	}

	private void createTables() throws Exception {
		String sql = "CREATE TABLE IF NOT EXISTS " + BIRTH_TABLE
				+ " (ID INTEGER PRIMARY KEY autoincrement, " + NAME
				+ " TEXT NOT NULL,  " + SEX + " INTEGER DEFAULT 0, " + BIRTHDAY
				+ " TEXT, " + RINGTYPE + " INTEGER DEFAULT 0," + RINGDAY
				+ " TEXT, " + PHONE_NUMBER + " TEXT, " + NOTE + " TEXT" + " )";
		db.execSQL(sql);
		Log.v(TAG, "Create Table BIRTH_TABLE ok");
	}

	public boolean save(String name, String sex, Integer ages) {
		String sql = "insert into TestUser values(null,'" + name + "','" + sex
				+ "'," + ages + ")";
		try {
			db.execSQL(sql);
			Log.v(TAG, "insert  Table TestUser 1 record ok");
			return true;
		} catch (Exception e) {

			Log.v(TAG, "insert  Table TestUser 1 record fail");
			return false;
		} finally {
			// db.close();
			Log.v(TAG, "insert  Table TestUser ");
		}
	}

	public long insert(ContentValues values) {
		return db.insert(BIRTH_TABLE, null, values);
	}
	
	public int update(ContentValues values,String where){
		return db.update(BIRTH_TABLE, values, where, null);
	}

	public Cursor queryAll() {
		Cursor cur = db.query(BIRTH_TABLE, QUERY_ALL_PROJECTION, null, null,
				null, null, null);

		return cur;
	}

	public void open(Context context) throws Exception {
		if (null == db || !db.isOpen()) {
			mContext = context;
			db = context.openOrCreateDatabase(DataBaseName,
					Context.MODE_PRIVATE, null);
			createTables();
			Log.v(TAG, "create  or Open DataBase。。。");
		}
	}

	public void close() {
		db.close();
	}

}
