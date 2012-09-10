package com.ds.birth;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.BirthConstants;
import com.ds.utility.Utility;

public class BirthActivity extends Activity {
	private static final String TAG = "BirthActivity";
	RadioButton leftRadioBtn;
	RadioButton rightRadioBtn;
	Button topRightBtn;
	ListView mListView;
	TextView mEmptyView;

	CursorAdapter mBirthAdapter;

	private static final int ADD_BIRTHDAY = 0;
	private static final int QUERY_SUCCESS = 0;
	private static final int QUERY_EMPTY = 1;
	private static final int QUERY_UPDATE = 2;
	private static final int QUERY = 0;
	private static final int UPDATE = 1;
	private static final int QUERY_STAR = 2;
	DbHelper mDbHelper;
	Cursor mCursor;
	private ProgressDialog mProgressDialog;
	SharedPreferences settings;
	int defaultRadioBtn = 0;// 0 left,1 right
	private static final String BIRTH_PREFERENCE = "birth_setting";
	private static final String RADIO_BTN = "radio_btn";
	int radiobtn;
	boolean leftClick = false;
	boolean rightClick = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_list_layout);
		mDbHelper = DbHelper.getInstance(this);
		mDbHelper.open(this);
		initViews();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mCursor.close();
		mDbHelper.close();
		mEmptyView.setVisibility(View.GONE);
	}

	private void initViews() {
		settings = getSharedPreferences(BIRTH_PREFERENCE, 0);
		leftRadioBtn = (RadioButton) findViewById(R.id.top_left_radio);
		rightRadioBtn = (RadioButton) findViewById(R.id.top_right_radio);
		leftRadioBtn.setText(R.string.star_birth);
		rightRadioBtn.setText(R.string.all_birth);
		leftRadioBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				defaultRadioBtn = 0;
				settings.edit().putInt(RADIO_BTN, defaultRadioBtn).commit();
				update(defaultRadioBtn);
				leftClick = false;
				rightClick = true;
				updateRadioClick();
			}
		});
		rightRadioBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				defaultRadioBtn = 1;
				settings.edit().putInt(RADIO_BTN, defaultRadioBtn).commit();
				update(defaultRadioBtn);
				leftClick = true;
				rightClick = false;
				updateRadioClick();
			}
		});

		mListView = (ListView) findViewById(R.id.listview);
		mEmptyView = (TextView) findViewById(R.id.emptyview);
		mEmptyView.setVisibility(View.GONE);
		topRightBtn = (Button) findViewById(R.id.top_right_button);
		topRightBtn.setVisibility(View.VISIBLE);
		topRightBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// add birthday
				Intent intent = new Intent(BirthConstants.ACTION_ADD_BIRTH);
				startActivityForResult(intent, ADD_BIRTHDAY);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				Cursor cursor = (Cursor) adapter.getItemAtPosition(position);
				int id = cursor.getInt(DatabaseHelper.ID_INDEX);
				Intent intent = new Intent(BirthConstants.ACTION_VIEW_BIRTH);
				Bundle extras = new Bundle();
				extras.putInt(BirthConstants.ID, id);
				intent.putExtras(extras);
				startActivity(intent);
			}
		});

	}

	private void updateRadioClick() {
		leftRadioBtn.setClickable(leftClick);
		rightRadioBtn.setClickable(rightClick);
	}

	private void update(int radioBtn) {
		if (radioBtn == 0) {
			queryStarData();
		} else {
			queryAllData();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case ADD_BIRTHDAY:
				// mProgressDialog = ProgressDialog.show(BirthActivity.this,
				// "Loading...", "Please wait...", true, false);
				// Thread thread = new MyThread(UPDATE);
				// thread.start();
				break;
			}
		}
	}

	private void updateListView() {
		if (mBirthAdapter != null) {
			mBirthAdapter.changeCursor(mCursor);
		}
		mListView.setAdapter(mBirthAdapter);
		mListView.invalidate();
	}

	@Override
	protected void onResume() {
		super.onResume();
		defaultRadioBtn = settings.getInt(RADIO_BTN, 0);
		Log.i(TAG, "defaultRadioBtn==" + defaultRadioBtn);
		if (defaultRadioBtn == 0) {
			leftRadioBtn.setChecked(true);
			leftClick = false;
			rightClick = true;
			queryStarData();
		} else {
			rightRadioBtn.setChecked(true);
			leftClick = true;
			rightClick = false;
			queryAllData();
		}
		updateRadioClick();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mCursor.close();
		mDbHelper.close();
	}

	private void queryAllData() {
		mProgressDialog = ProgressDialog.show(BirthActivity.this, "Loading...",
				"Please wait...", true, false);
		Thread thread = new MyThread(QUERY);
		thread.start();
	}

	private void queryStarData() {
		mProgressDialog = ProgressDialog.show(BirthActivity.this, "Loading...",
				"Please wait...", true, false);
		Thread thread = new MyThread(QUERY_STAR);
		thread.start();
	}

	private Cursor startBackgroundQuery(int state) {
		switch (state) {
		case QUERY_STAR:
			return mDbHelper.queryStar();
		case QUERY:
			return mDbHelper.queryAll();
		case UPDATE:
			Log.i(TAG, "---update---");
			radiobtn = settings.getInt(RADIO_BTN, 0);
			Log.i(TAG, "---radiobtn===" + radiobtn);
			if (radiobtn == 0) {
				leftRadioBtn.setChecked(true);
				return mDbHelper.queryStar();
			} else {
				rightRadioBtn.setChecked(true);
				return mDbHelper.queryAll();
			}
		default:
			return null;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case QUERY_SUCCESS:
				Log.i(TAG, "---handler success----");
				mProgressDialog.dismiss();
				mEmptyView.setVisibility(View.GONE);
				mBirthAdapter = new BirthCursorAdapter(BirthActivity.this,
						R.layout.birth_list_item, mCursor);
				mListView.setAdapter(mBirthAdapter);
				break;

			case QUERY_EMPTY:
				Log.i(TAG, "--query empty---");
				mProgressDialog.dismiss();
				int arg1 = msg.arg1;
				Log.i(TAG, "--arg1---" + arg1);
				mEmptyView.setVisibility(View.VISIBLE);
				if (arg1 == QUERY_STAR) {
					mEmptyView.setText(R.string.empty_star);
				} else {
					radiobtn = settings.getInt(RADIO_BTN, 0);
					if (radiobtn == 0) {
						mEmptyView.setText(R.string.empty_star);
					} else {
						mEmptyView.setText(R.string.empty);
					}
				}
				mBirthAdapter = null;
				mListView.setAdapter(mBirthAdapter);
				mListView.setEmptyView(mEmptyView);
				mListView.invalidate();
				break;
			case QUERY_UPDATE:
				Log.i(TAG, "---handler update----");
				mProgressDialog.dismiss();
				mEmptyView.setVisibility(View.GONE);
				updateListView();
				break;
			}
		}
	};

	class MyThread extends Thread {
		private int mState;

		public MyThread(int state) {
			mState = state;
		}

		public void run() {
			mCursor = startBackgroundQuery(mState);

			if (mCursor != null && mCursor.getCount() > 0) {
				Log.i(TAG, "--cursor not null---");
				switch (mState) {
				case UPDATE:
					mHandler.sendEmptyMessage(QUERY_UPDATE);
					break;
				case QUERY_STAR:
					mHandler.sendEmptyMessage(QUERY_SUCCESS);
					break;
				case QUERY:
					mHandler.sendEmptyMessage(QUERY_SUCCESS);
					break;
				}
			} else {
				Message msg = new Message();
				msg.what = QUERY_EMPTY;
				msg.arg1 = mState;
				mHandler.sendMessage(msg);
			}
		}
	}

	class BirthCursorAdapter extends CursorAdapter {
		Context context = null;
		int viewResId;
		LayoutInflater mInflater;

		public BirthCursorAdapter(Context context, int resource, Cursor cursor) {
			super(context, cursor);
			viewResId = resource;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(viewResId, parent, false);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Calendar cal = Calendar.getInstance();
			if (view != null) {
				ViewHolder viewHolder = new ViewHolder();
				View birthView = (RelativeLayout) view;
				viewHolder.avater = (ImageView) birthView
						.findViewById(R.id.avatar);
				viewHolder.name = (TextView) birthView.findViewById(R.id.name);
				viewHolder.date = (TextView) birthView.findViewById(R.id.date);
				viewHolder.candle = (ImageView) birthView
						.findViewById(R.id.candle);
				viewHolder.yearcnt = (TextView) birthView
						.findViewById(R.id.yearcnt);
				viewHolder.daycnt = (TextView) birthView
						.findViewById(R.id.daycnt);

				viewHolder.name.setText(cursor
						.getString(DatabaseHelper.NAME_INDEX));
				viewHolder.date.setText(cursor
						.getString(DatabaseHelper.BIRTHDAY_INDEX));
				if (cursor.getInt(DatabaseHelper.ISLUNAR_INDEX) == 0) {
					viewHolder.candle.setImageResource(R.drawable.birth_solar);
				} else {
					viewHolder.candle.setImageResource(R.drawable.birth_lunar);
				}

				Drawable drawable = context.getResources().getDrawable(
						R.drawable.avatar_default);
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				Bitmap bitmap = bitmapDrawable.getBitmap();

				BitmapDrawable bbb = new BitmapDrawable(Utility.toRoundCorner(
						bitmap, 30));
				viewHolder.avater.setBackgroundDrawable(bbb);
				
				viewHolder.yearcnt.setText(String.valueOf(cal.get(Calendar.YEAR)-cursor.getInt(DatabaseHelper.YEAR_INDEX) + 1));
				Log.i(TAG,"year:" + cal.get(Calendar.YEAR));
				Log.i(TAG,"day of month:" + cal.get(Calendar.DAY_OF_MONTH));
				Log.i(TAG,"day of year:" + cal.get(Calendar.DAY_OF_YEAR));
//				viewHolder.daycnt.setText(text);

			}
		}
	}

	private class ViewHolder {
		ImageView avater;
		TextView name;
		TextView date;
		ImageView candle;
		TextView yearcnt;
		TextView daycnt;

	}

}
