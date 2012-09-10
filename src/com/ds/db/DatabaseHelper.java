package com.ds.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DBNAME = "birthday.db";
	private static final int VERSION = 1;
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String SEX = "sex";
	public static final String BIRTHDAY = "birthday";
	public static final String RINGTYPE = "ringtype";
	public static final String RINGDAY = "ringday";
	public static final String PHONE_NUMBER = "phone_number";
	public static final String NOTE = "note";
	public static final String ISSTAR = "isstar";
	public static final String ISLUNAR = "islunar";// 0 is solar,1 is lunar
	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String DAY = "day";
	public static final String TYPE = "type";// 1 is me,0 is not me
	public static String[] QUERY_ALL_PROJECTION = { ID, NAME, SEX, BIRTHDAY,
			RINGTYPE, RINGDAY, PHONE_NUMBER, NOTE, ISSTAR, ISLUNAR, YEAR,
			MONTH, DAY, TYPE };
	public static final int ID_INDEX = 0;
	public static final int NAME_INDEX = 1;
	public static final int SEX_INDEX = 2;
	public static final int BIRTHDAY_INDEX = 3;
	public static final int RINGTYPE_INDEX = 4;
	public static final int RINGDAY_INDEX = 5;
	public static final int PHONE_NUMBER_INDEX = 6;
	public static final int NOTE_INDEX = 7;
	public static final int ISSTAR_INDEX = 8;
	public static final int ISLUNAR_INDEX = 9;
	public static final int YEAR_INDEX = 10;
	public static final int MONTH_INDEX = 11;
	public static final int DAY_INDEX = 12;
	public static final int TYPE_INDEX = 13;

	private static final String BIRTH_TABLE = "birth";

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DatabaseHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE IF NOT EXISTS " + BIRTH_TABLE
				+ " (_id INTEGER PRIMARY KEY autoincrement, " + NAME
				+ " TEXT NOT NULL,  " + SEX + " INTEGER DEFAULT 0, " + BIRTHDAY
				+ " TEXT, " + RINGTYPE + " INTEGER DEFAULT 0," + RINGDAY
				+ " TEXT, " + PHONE_NUMBER + " TEXT, " + NOTE + " TEXT,"
				+ ISSTAR + " INTEGER DEFAULT 1," + ISLUNAR
				+ " INTEGER DEFAULT 0, " + YEAR + " INTEGER," + MONTH + " INTEGER,"
				+ DAY + " INTEGER," + TYPE + " INTEGER DEFAULT 0" + " )";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists person");
		onCreate(db);
	}

	/**
	 * 根据版本号进行更新
	 * 
	 * @param db
	 * @param mNewVersion
	 */
	public void checkVersionCreate(SQLiteDatabase db, int mNewVersion) {
		int version = db.getVersion();
		if (version != mNewVersion) {
			db.beginTransaction();
			try {
				if (version == 0) {
					onCreate(db);
				} else {
					onUpgrade(db, version, mNewVersion);
				}
				db.setVersion(mNewVersion); // 设置为新的版本号
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
	}
	public SQLiteDatabase getDatabase(SQLiteDatabase mDatabase,
			Context mContext, String mName, CursorFactory mFactory) {
		if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
			return mDatabase; // The database is already open for business
		}
		if (mName == null) {
			mDatabase = SQLiteDatabase.create(null);
		} else {
			mDatabase = mContext.openOrCreateDatabase(mName, 0, mFactory);
		}
		return mDatabase;
	}
}
