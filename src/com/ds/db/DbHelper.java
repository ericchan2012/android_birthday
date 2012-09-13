package com.ds.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbHelper {
	private static final String TAG = "DbHelper";
	Context mContext;
	private static final String BIRTH_TABLE = "birth";

	private static DbHelper dbHelper = new DbHelper();
	private DatabaseHelper databaseHelper;

	private DbHelper() {

	}

	public static DbHelper getInstance(Context context) {
		return dbHelper;
	}

	public long insert(ContentValues values) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		return db.insert(BIRTH_TABLE, null, values);
	}

	public int update(ContentValues values, String where) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		return db.update(BIRTH_TABLE, values, where, null);
	}

	public Cursor queryAll() {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		Cursor cur = db.query(BIRTH_TABLE, DatabaseHelper.QUERY_ALL_PROJECTION,
				null, null, null, null, null);

		return cur;
	}

	public Cursor queryAlarm(String where) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		Cursor cur = db.query(BIRTH_TABLE, DatabaseHelper.QUERY_ALL_PROJECTION,
				where, null, null, null, null);

		return cur;
	}

	public Cursor queryStar() {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		Cursor cur = db.query(BIRTH_TABLE, DatabaseHelper.QUERY_ALL_PROJECTION,
				" isstar = 1 ", null, null, null, null);

		return cur;
	}

	public Cursor queryId(int id) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		Cursor cur = db.query(BIRTH_TABLE, DatabaseHelper.QUERY_ALL_PROJECTION,
				" _id = " + id, null, null, null, null);

		return cur;
	}

	public void open(Context context) {
		databaseHelper = new DatabaseHelper(context);
	}

	public void close() {
		databaseHelper.close();
	}

}
