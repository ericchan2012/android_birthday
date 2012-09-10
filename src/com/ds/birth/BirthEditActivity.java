package com.ds.birth;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.ds.utility.ChineseCalendar;
import com.ds.utility.Lunar;
import com.ds.utility.Person;
import com.ds.widget.ArrayWheelAdapter;
import com.ds.widget.NumericWheelAdapter;
import com.ds.widget.OnWheelChangedListener;
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
	boolean[] flags = new boolean[] { true, false, false, false };
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
	int defaultRingType = -1;
	TextView mBirthdayTextView;
	TextView mBirthAttach;
	EditText noteEdit;
	EditText phoneNumEdit;

	String name;
	int gender;
	int isStar;
	String birthday;
	String birthdayYear;
	String birthdayMonth;
	String birthdayDay;
	int ringtype;// 0,1,2
	String ringdays;
	String note;
	String phoneNum;
	ContentValues contentValues;

	int mId = -1;
	private Cursor mCursor;
	private TextView titleTextView;
	private LinearLayout datePickerLayout;
	ImageView pickerSelectYear;
	ImageView pickerLunar;
	ImageView pickerSolar;
	int isLunar = 0;
	int mType = 0;// 1 is me ,0 is not me
	boolean isShowYear = true;
	WheelView year;
	WheelView month;
	WheelView day;
	int currentYear;
	int currentMonth;
	int currentDay;
	int settingYear;
	int settingMonth;
	int settingDay;
	ArrayWheelAdapter<String> yearAdapter;
	ArrayWheelAdapter<String> monthAdapter;
	ArrayWheelAdapter<String> dayAdapter;
	String years[] = null;
	String months[] = null;
	String days[] = null;
	boolean showLunar = false;
	private static final int START_YEAR = 1901;
	private static final int END_YEAR = 2050;

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

	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mDbHelper.close();
	}

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
						isLunar = mCursor.getInt(DatabaseHelper.ISLUNAR_INDEX);
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
		mBirthAttach = (TextView) findViewById(R.id.birthattach);
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
			if (BirthEditActivity.this.getCurrentFocus() != null) {
				hideSoftKeypad();
			}
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
			mRingList.clear();
			for (int i = 0; i < flags.length; i++) {
				if (flags[i]) {
					mRingList.add(ringItems[i]);
				}
			}
			createCheckedDialog(R.string.ring_title, flags);
			break;
		case R.id.tv_ringtype:
			if (!mBirthdayTextView.getText().equals("")) {
				createOptionDialog(R.string.ringtype, mRingTypeItem,
						R.id.tv_ringtype, defaultRingType);
			}
			break;
		case R.id.img_icon:
			loadMenu();
			break;
		case R.id.save:
			if (BirthEditActivity.this.getCurrentFocus() != null) {
				hideSoftKeypad();
			}
			updateData(mMode);
			break;
		case R.id.tv_birthday:
			initYear();
			initDatePickerView(mMode);
			showPickViewDialog();
			break;
		case R.id.selectyear:
			if (isShowYear) {
				datePickerLayout.findViewById(R.id.year).setVisibility(
						View.GONE);
				pickerSelectYear.setImageResource(R.drawable.displayyear);
				isShowYear = false;
			} else {
				isShowYear = true;
				datePickerLayout.findViewById(R.id.year).setVisibility(
						View.VISIBLE);
				pickerSelectYear.setImageResource(R.drawable.ignoreyear);
			}
			break;
		case R.id.lunar:
			showLunar = true;
			initLunar(mMode);
			break;
		case R.id.solar:
			showLunar = false;
			initSolar(mMode);
			break;
		}
	}

	private void showPickViewDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		// dialog.setTitle("选择分类");
		dialog.setButton(mRes.getString(R.string.confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Log.i(TAG, "cur year:" + year.getCurrentItem());
						Log.i(TAG, "cur month:" + month.getCurrentItem());
						Log.i(TAG, "cur day:" + day.getCurrentItem());
						Calendar cal = Calendar.getInstance();
						int solarYear = START_YEAR + year.getCurrentItem();
						cal.set(Calendar.YEAR, solarYear);
						cal.set(Calendar.MONTH, month.getCurrentItem());
						cal.set(Calendar.DAY_OF_MONTH,
								(day.getCurrentItem() + 1));
						Lunar lunar = new Lunar(cal);
						String lunarYear = lunar.getLunarYear()
								+ mRes.getString(R.string.year);
						String monthstr[] = mRes
								.getStringArray(R.array.lunarMonths);
						String daystr[] = mRes
								.getStringArray(R.array.lunarDaysLong);
						if (showLunar) {
							mBirthdayTextView.setText((isShowYear ? lunarYear
									: "")
									+ monthstr[month.getCurrentItem()]
									+ daystr[day.getCurrentItem()]);
							if (isShowYear) {
								mBirthAttach.setText(ChineseCalendar
												.sCalendarLundarToSolar(
														solarYear,
														month.getCurrentItem()+1,
														day.getCurrentItem()+1));
							} else {
								mBirthAttach.setText("");
							}
						} else {
							String lunarMonth = lunar.get_month();
							String lunarDay = lunar.get_date();
							mBirthdayTextView
									.setText((isShowYear ? (solarYear + "-")
											: "")
											+ (month.getCurrentItem() + 1)
											+ "-" + (day.getCurrentItem() + 1));
							if (isShowYear) {
								mBirthAttach.setText(lunarYear + lunarMonth
										+ mRes.getString(R.string.month)
										+ lunarDay);
							} else {
								mBirthAttach.setText("");
							}
						}
						if (isShowYear) {
							settingYear = solarYear;
						} else {
							settingYear = 0;
						}
						settingMonth = month.getCurrentItem() + 1;
						settingDay = day.getCurrentItem() + 1;
					}

				});
		dialog.setButton2(mRes.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.setView(datePickerLayout);
		dialog.show();
	}

	private void initYear() {
		datePickerLayout = (LinearLayout) mInflater.inflate(
				R.layout.date_picker, null);
		pickerSelectYear = (ImageView) datePickerLayout
				.findViewById(R.id.selectyear);
		pickerLunar = (ImageView) datePickerLayout.findViewById(R.id.lunar);
		pickerSolar = (ImageView) datePickerLayout.findViewById(R.id.solar);
		pickerSelectYear.setOnClickListener(this);
		pickerLunar.setOnClickListener(this);
		pickerSolar.setOnClickListener(this);
		isShowYear = true;
		showLunar = false;
		pickerSelectYear.setImageResource(R.drawable.ignoreyear);
		year = (WheelView) datePickerLayout.findViewById(R.id.year);
		years = new String[END_YEAR - START_YEAR + 1];
		for (int i = 0; i < (END_YEAR - START_YEAR + 1); i++) {
			years[i] = String.valueOf(START_YEAR + i) + "   "
					+ mRes.getString(R.string.year);
		}
		yearAdapter = new ArrayWheelAdapter<String>(years);
		year.setAdapter(yearAdapter);
		month = (WheelView) datePickerLayout.findViewById(R.id.month);
		day = (WheelView) datePickerLayout.findViewById(R.id.day);
	}

	private void initDatePickerView(int mode) {
		Log.i(TAG, "year:" + currentYear + "  month:" + currentMonth + " day:"
				+ currentDay);
		switch (mode) {
		case MODE_ADD:
			initSolar(mode);
			break;
		case MODE_EDIT:
			if (isLunar == 0) {
				initSolar(mode);
			} else {
				initLunar(mode);
			}
			break;
		}
	}

	private void initLunar(int mode) {
		pickerLunar.setImageResource(R.drawable.nong_blue);
		pickerSolar.setImageResource(R.drawable.gong_grey);
		Calendar calendar = Calendar.getInstance();
		Lunar lunar = new Lunar(calendar);
		final String lunarStr[] = (lunar.toString()).split("-");
		String temp[] = mRes.getStringArray(R.array.lunarMonths);
		for (int i = 0; i < temp.length; i++) {
			if (lunarStr[0].equals(temp[i])) {
				currentMonth = i;
			}
			months[i] = temp[i] + "   ";
		}
		monthAdapter = new ArrayWheelAdapter<String>(months);
		month.setAdapter(monthAdapter);

		year.setCurrentItem(currentYear - START_YEAR);
		Log.i(TAG, "after lunarStr[0]:" + lunarStr[0]);
		Log.i(TAG, "currentMonth:" + currentMonth);
		month.setCurrentItem(currentMonth);
		final int daycnt = Lunar.monthDays(currentYear, currentMonth);
		String tmp[] = null;
		if (daycnt == 29) {
			tmp = mRes.getStringArray(R.array.lunarDays);
		} else if (daycnt == 30) {
			tmp = mRes.getStringArray(R.array.lunarDaysLong);
		}
		for (int i = 0; i < tmp.length; i++) {
			if (lunarStr[1].equals(tmp[i])) {
				currentDay = i;
			}
			days[i] = tmp[i] + "   ";
		}
		dayAdapter = new ArrayWheelAdapter<String>(days);
		day.setAdapter(dayAdapter);
		day.setCurrentItem(currentDay);

		year.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.i(TAG, "year oldValue:" + oldValue);
				Log.i(TAG, "year newValue:" + newValue);
			}
		});
		month.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.i(TAG, "oldValue:" + oldValue);
				Log.i(TAG, "newValue:" + newValue);
				String tmp2[] = null;
				if (daycnt == 29) {
					tmp2 = mRes.getStringArray(R.array.lunarDays);
				} else if (daycnt == 30) {
					tmp2 = mRes.getStringArray(R.array.lunarDaysLong);
				}
				for (int i = 0; i < tmp2.length; i++) {
					if (lunarStr[1].equals(tmp2[i])) {
						currentDay = i;
					}
					days[i] = tmp2[i] + "   ";
				}
				dayAdapter = new ArrayWheelAdapter<String>(days);
				day.setAdapter(dayAdapter);
			}
		});
	}

	private void initSolar(int mode) {
		pickerLunar.setImageResource(R.drawable.nong_grey);
		pickerSolar.setImageResource(R.drawable.gong_blue);
		if (mode == MODE_ADD) {
			Calendar calendar = Calendar.getInstance();
			currentYear = calendar.get(Calendar.YEAR);
			currentMonth = calendar.get(Calendar.MONTH);
			currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		}
		months = new String[12];
		for (int i = 0; i < 12; i++) {
			months[i] = String.valueOf(i + 1) + "   "
					+ mRes.getString(R.string.month);
		}
		monthAdapter = new ArrayWheelAdapter<String>(months);
		month.setAdapter(monthAdapter);

		year.setCurrentItem(currentYear - START_YEAR);
		month.setCurrentItem(currentMonth);
		if (currentMonth == 1) {
			days = new String[28];

		} else {
			days = new String[31];

		}
		for (int i = 0; i < days.length; i++) {
			days[i] = String.valueOf(i + 1) + "   "
					+ mRes.getString(R.string.day);
		}
		dayAdapter = new ArrayWheelAdapter<String>(days);
		day.setAdapter(dayAdapter);
		day.setCurrentItem(currentDay - 1);

		year.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
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
		birthday = mBirthdayTextView.getText().toString();
		ringtype = defaultRingType;
		ringdays = ringDaysText.getText().toString();
		isLunar = showLunar ? 1 : 0;
		mType = 0;
		note = noteEdit.getText().toString();
		phoneNum = phoneNumEdit.getText().toString();
		contentValues.clear();
		contentValues.put(DatabaseHelper.NAME, name);
		contentValues.put(DatabaseHelper.SEX, gender);
		contentValues.put(DatabaseHelper.ISSTAR, isStar);
		contentValues.put(DatabaseHelper.BIRTHDAY, birthday);
		contentValues.put(DatabaseHelper.RINGTYPE, ringtype);
		contentValues.put(DatabaseHelper.RINGDAY, ringdays);
		contentValues.put(DatabaseHelper.ISLUNAR, isLunar);
		contentValues.put(DatabaseHelper.YEAR, settingYear);
		contentValues.put(DatabaseHelper.MONTH, settingMonth);
		contentValues.put(DatabaseHelper.DAY, settingDay);
		contentValues.put(DatabaseHelper.TYPE, mType);
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

		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
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

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
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
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (mRingList.size() == 0) {
							ringDaysText.setText(R.string.no_remind);
						} else {
							ringDaysText.setText(mRingList.toString());
						}
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

						public void onClick(View v) {

						}
					});
			gallaryBtn
					.setOnClickListener(new android.view.View.OnClickListener() {

						public void onClick(View v) {

						}
					});
			defaultBtn
					.setOnClickListener(new android.view.View.OnClickListener() {

						public void onClick(View v) {

						}
					});
			cancelBtn
					.setOnClickListener(new android.view.View.OnClickListener() {

						public void onClick(View v) {
							MyDialog.this.dismiss();
						}
					});

		}
	}
}
