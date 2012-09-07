package com.ds.birth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.BirthConstants;

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
	}

	private void initViews() {
		settings = getSharedPreferences(BIRTH_PREFERENCE, 0);
		defaultRadioBtn = settings.getInt(RADIO_BTN, 0);
		leftRadioBtn = (RadioButton) findViewById(R.id.top_left_radio);
		rightRadioBtn = (RadioButton) findViewById(R.id.top_right_radio);
		leftRadioBtn.setText(R.string.star_birth);
		rightRadioBtn.setText(R.string.all_birth);
		leftRadioBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.i(TAG, "left ischecked:" + isChecked);
				if (isChecked) {
					defaultRadioBtn = 0;
					settings.edit().putInt(RADIO_BTN, defaultRadioBtn).commit();
					update(defaultRadioBtn);
				}
			}
		});
		rightRadioBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.i(TAG, "right ischecked:" + isChecked);
				if (isChecked) {
					defaultRadioBtn = 1;
					settings.edit().putInt(RADIO_BTN, defaultRadioBtn).commit();
					update(defaultRadioBtn);
				}
			}
		});
		if (defaultRadioBtn == 0) {
			leftRadioBtn.setChecked(true);
			queryStarData();
		} else {
			rightRadioBtn.setChecked(true);
			queryAllData();
		}
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
				mProgressDialog = ProgressDialog.show(BirthActivity.this,
						"Loading...", "Please wait...", true, false);
				Thread thread = new MyThread(UPDATE);
				thread.start();
				break;
			}
		}
	}

	private void updateListView() {
		if (mBirthAdapter != null) {
			mBirthAdapter.changeCursor(mCursor);
			mListView.setAdapter(mBirthAdapter);
			mListView.invalidate();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mCursor.close();
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
			int radiobtn = settings.getInt(RADIO_BTN, 0);
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
				mProgressDialog.dismiss();
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
					mEmptyView.setText(R.string.empty);
				}
				mListView.setAdapter(null);
				mListView.setEmptyView(mEmptyView);
				mListView.invalidate();
				break;
			case QUERY_UPDATE:
				mProgressDialog.dismiss();
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
				if (mState == 0) {
					mHandler.sendEmptyMessage(QUERY_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(QUERY_UPDATE);
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
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(viewResId, parent, false);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if (view != null) {
				ViewHolder viewHolder = new ViewHolder();
				View birthView = (RelativeLayout) view;
				viewHolder.avater = (ImageView) birthView
						.findViewById(R.id.avatar);
				viewHolder.name = (TextView) birthView.findViewById(R.id.name);

				viewHolder.name.setText(cursor
						.getString(DatabaseHelper.NAME_INDEX));
			}
		}
	}

	private class ViewHolder {
		ImageView avater;
		TextView name;
	}
}
