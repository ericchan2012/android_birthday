package com.ds.birth;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.BirthConstants;
import com.ds.utility.Person;
import com.ds.widget.NumericWheelAdapter;
import com.ds.widget.OnWheelChangedListener;
import com.ds.widget.OnWheelScrollListener;
import com.ds.widget.WheelView;

public class BirthEditActivity extends Activity implements OnClickListener {
	private static final String TAG = "BirthEditActivity";

	Button topLeftBtn;
	String[] mGenderItems = null;
	String[] mRingTypeItem = null;
	private static final int MODE_EDIT = 1;
	private static final int MODE_ADD = 0;
	int mMode = 0;
	ArrayList<String> mRingList = new ArrayList<String>();
	String[] ringItems = null;
	Resources mRes;
	boolean[] flags = new boolean[] { false, false, false, false };
	LayoutInflater mInflater;
	DbHelper mDbHelper;
	Button saveBtn;

	ImageView headerImage;
	EditText nameEdit;
	TextView genderText;
	TextView ringDaysText;
	TextView ringTypeText;
	CheckBox mStar;
	ImageView mImportContactName;
	int defaultSexSelect = 0;
	int defaultRingType = 0;
	TextView mBirthdayTextView;
	EditText noteEdit;
	EditText phoneNumEdit;

	String name;
	int gender;
	int isStar;
	String birthday;
	int ringtype;// 0,1,2
	String ringdays;
	String note;
	String phoneNum;
	ContentValues contentValues;

	int mId = -1;
	private Cursor mCursor;
	private TextView titleTextView;
	private LinearLayout datePickerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_add);
		mRes = getResources();
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ringItems = mRes.getStringArray(R.array.selectDays);
		mGenderItems = mRes.getStringArray(R.array.UMgenderList);
		mRingTypeItem = mRes.getStringArray(R.array.ringtypes);
		contentValues = new ContentValues();
		mDbHelper = DbHelper.getInstance(this);
		mDbHelper.open(this);
		initMode();
		initViews();

	}

	private void initMode() {
		Intent intent = getIntent();
		if (intent.getAction().equals(BirthConstants.ACTION_ADD_BIRTH)) {
			mMode = MODE_ADD;
		} else {
			mMode = MODE_EDIT;
			mId = intent.getExtras().getInt(BirthConstants.ID);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "mMode:" + mMode);
		if (mMode == MODE_EDIT) {
			Log.i(TAG, "mId:" + mId);
			initEdit();
		}
	}

	private void initEdit() {
		if (mId != -1) {
			mCursor = mDbHelper.queryId(mId);
			if (mCursor != null && mCursor.getCount() > 0) {
				Person person = new Person();
				if (mCursor.moveToFirst()) {
					do {
						person.setName(mCursor
								.getString(DatabaseHelper.NAME_INDEX));
						updateEdit(person);
					} while (mCursor.moveToNext());
				}
			}
		}
	}

	private void updateEdit(Person p) {
		nameEdit.setText(p.getName());
	}

	private void initViews() {
		topLeftBtn = (Button) findViewById(R.id.top_left_btton);
		nameEdit = (EditText) findViewById(R.id.name);
		genderText = (TextView) findViewById(R.id.tv_gender);
		ringDaysText = (TextView) findViewById(R.id.tv_ringdays);
		ringTypeText = (TextView) findViewById(R.id.tv_ringtype);
		headerImage = (ImageView) findViewById(R.id.img_icon);
		mBirthdayTextView = (TextView) findViewById(R.id.tv_birthday);
		mStar = (CheckBox) findViewById(R.id.star);
		noteEdit = (EditText) findViewById(R.id.tv_note);
		phoneNumEdit = (EditText) findViewById(R.id.tv_number);
		saveBtn = (Button) findViewById(R.id.save);
		topLeftBtn.setOnClickListener(this);
		genderText.setOnClickListener(this);
		ringDaysText.setOnClickListener(this);
		headerImage.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		ringTypeText.setOnClickListener(this);
		mBirthdayTextView.setOnClickListener(this);
		titleTextView = (TextView) findViewById(R.id.title);
		switch (mMode) {
		case MODE_ADD:
			titleTextView.setText(R.string.add_title);
			break;
		case MODE_EDIT:
			titleTextView.setText(R.string.edit_title);
			break;
		}
	}

	private void hideSoftKeypad() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(BirthEditActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_left_btton:
			hideSoftKeypad();
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.tv_gender:
			if (genderText.getText().equals(mGenderItems[0])) {
				defaultSexSelect = 0;
			} else {
				defaultSexSelect = 1;
			}
			createOptionDialog(R.string.gender_title, mGenderItems,
					R.id.tv_gender, defaultSexSelect);
			break;
		case R.id.tv_ringdays:
			createCheckedDialog(R.string.ring_title, flags);
			break;
		case R.id.tv_ringtype:
			createOptionDialog(R.id.tv_ringtype, mRingTypeItem,
					R.id.tv_ringtype, defaultRingType);
			break;
		case R.id.img_icon:
			loadMenu();
			break;
		case R.id.save:
			hideSoftKeypad();
			updateData(mMode);
			break;
		case R.id.tv_birthday:
			datePickerLayout = (LinearLayout) mInflater.inflate(
					R.layout.date_picker, null);
			initDatePickerView();
			showPickViewDialog();
			break;
		}
	}

	private void showPickViewDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		// dialog.setTitle("选择分类");
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 实现下ui的刷新
			}

		});
		dialog.setButton2("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setView(datePickerLayout);
		dialog.show();
	}

	private void initDatePickerView() {
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		Log.i(TAG, "year:" + currentYear + "  month:" + currentMonth + " day:"
				+ currentDay);
		WheelView year = (WheelView) datePickerLayout.findViewById(R.id.year);
		year.setAdapter(new NumericWheelAdapter(2000, 2112));
		year.setLabel("年");
		year.setCurrentItem(1902);
		WheelView month = (WheelView) datePickerLayout.findViewById(R.id.month);
		month.setAdapter(new NumericWheelAdapter(1, 12));
		month.setLabel("月");
		month.setCurrentItem(currentMonth);
		final WheelView day = (WheelView) datePickerLayout
				.findViewById(R.id.day);
		if (currentMonth == 1) {
			day.setAdapter(new NumericWheelAdapter(1, 28));
		} else {
			day.setAdapter(new NumericWheelAdapter(1, 31));
		}
		day.setLabel("日");
		day.setCurrentItem(currentDay - 1);
		year.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				Log.i(TAG, "year oldValue:" + oldValue);
				Log.i(TAG, "year newValue:" + newValue);
			}
		});
		month.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.i(TAG, "oldValue:" + oldValue);
				Log.i(TAG, "newValue:" + newValue);
				if (newValue == 1) {
					day.setAdapter(new NumericWheelAdapter(1, 28));
				} else {
					day.setAdapter(new NumericWheelAdapter(1, 31));
				}
			}
		});
	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * 
	 * @param wheel
	 *            the wheel
	 * @param label
	 *            the wheel label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
	}

	private void updateData(int mode) {
		long id = -1;
		int returnUpdateId = -1;
		name = nameEdit.getText().toString();
		if (name == null || name.equals("")) {
			// show dialog
			return;
		}
		generateData();
		if (mode == MODE_ADD) {
			id = mDbHelper.insert(contentValues);
		} else if (mode == MODE_EDIT) {
			String where = "_id = " + mId;
			returnUpdateId = mDbHelper.update(contentValues, where);
		}

		Log.i(TAG, "id:" + id);
		if (id > 0 || returnUpdateId > 0) {
			setResult(Activity.RESULT_OK);
			BirthEditActivity.this.finish();
		}
	}

	private void generateData() {
		gender = defaultSexSelect;
		isStar = mStar.isChecked() ? 1 : 0;
		birthday = "2009-09-09";
		ringtype = defaultRingType;
		ringdays = "hello";
		note = noteEdit.getText().toString();
		phoneNum = phoneNumEdit.getText().toString();
		contentValues.clear();
		contentValues.put(DatabaseHelper.NAME, name);
		contentValues.put(DatabaseHelper.SEX, gender);
		contentValues.put(DatabaseHelper.ISSTAR, isStar);
		contentValues.put(DatabaseHelper.BIRTHDAY, birthday);
		contentValues.put(DatabaseHelper.RINGTYPE, ringtype);
		contentValues.put(DatabaseHelper.RINGDAY, ringdays);
		if (null == note || note.equals("")) {
			note = "";
		}
		if (null == phoneNum || phoneNum.equals("")) {
			phoneNum = "";
		}
		contentValues.put(DatabaseHelper.NOTE, note);
		contentValues.put(DatabaseHelper.PHONE_NUMBER, phoneNum);
	}

	private void loadMenu() {
		Dialog dialog = new MyDialog(BirthEditActivity.this, R.style.MyDialog);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		}
		return true;
	}

	private void createOptionDialog(int title, final String[] items,
			final int id, int defaultSelect) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				BirthEditActivity.this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setSingleChoiceItems(items, defaultSelect,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						switch (id) {
						case R.id.tv_gender:
							defaultSexSelect = whichButton;
							break;
						case R.id.tv_ringtype:
							defaultRingType = whichButton;
							break;
						}

					}
				});

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				switch (id) {
				case R.id.tv_gender:
					genderText.setText(items[defaultSexSelect]);
					break;
				case R.id.tv_ringtype:
					ringTypeText.setText(items[defaultRingType]);
					break;
				}
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void createCheckedDialog(int title, final boolean[] flags) {
		Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setMultiChoiceItems(R.array.selectDays, flags,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						flags[which] = isChecked;
						if (flags[which]) {
							mRingList.add(ringItems[which]);
						} else {
							mRingList.remove(ringItems[which]);
						}
					}
				});

		// 添加一个确定按钮
		builder.setPositiveButton(" 确 定 ",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ringDaysText.setText(mRingList.toString());
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	class MyDialog extends Dialog {

		private Context mCon;
		private Button takePicBtn;
		private Button gallaryBtn;
		private Button defaultBtn;
		private Button cancelBtn;

		public MyDialog(Context context) {
			super(context);
			this.mCon = context;
		}

		public MyDialog(Context context, int theme) {
			super(context, theme);
			this.mCon = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.birth_menu);
			initDialog();
		}

		private void initDialog() {
			takePicBtn = (Button) findViewById(R.id.takepic_button);
			gallaryBtn = (Button) findViewById(R.id.gallery_button);
			defaultBtn = (Button) findViewById(R.id.default_button);
			cancelBtn = (Button) findViewById(R.id.cancel_button);
			takePicBtn
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			gallaryBtn
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			defaultBtn
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			cancelBtn
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MyDialog.this.dismiss();
						}
					});

		}
	}
}
