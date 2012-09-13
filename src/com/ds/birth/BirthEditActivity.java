package com.ds.birth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
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
	ArrayList<Integer> mRingCountList = new ArrayList<Integer>();
	int[] ringcounts = new int[] { 0, 1, 3, 7 };
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
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String LUNAR_YEAR = "lunar_year";
	private static final String LUNAR_MONTH = "lunar_month";
	private static final String LUNAR_DAY = "lunar_day";
	private static final String SHARE_BIRTH_EDIT = "edit.xml";

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
						int year = mCursor.getInt(DatabaseHelper.YEAR_INDEX);
						int month = mCursor.getInt(DatabaseHelper.MONTH_INDEX);
						int day = mCursor.getInt(DatabaseHelper.DAY_INDEX);
						person.setName(mCursor
								.getString(DatabaseHelper.NAME_INDEX));
						isLunar = mCursor.getInt(DatabaseHelper.ISLUNAR_INDEX);
						person.setIsLunar(isLunar);
						isStar = mCursor.getInt(DatabaseHelper.ISSTAR_INDEX);
						gender = mCursor.getInt(DatabaseHelper.SEX_INDEX);
						person.setIsStar(isStar);
						person.setGender(gender);
						if (isLunar == 0) {
							String lunar = ChineseCalendar
									.sCalendarSolarToLundar(year, month, day);
							String[] lunarStr = lunar.split("-");
							int lunarYear = Integer.parseInt(lunarStr[0]);
							int lunarMonth = Integer.parseInt(lunarStr[1]);
							int lunarDay = Integer.parseInt(lunarStr[2]);
							Log.i(TAG, "lunarYear: " + lunarYear
									+ " lunarMonth: " + lunarMonth
									+ " lunarDay: " + lunarDay);
							getSharedPreferences(SHARE_BIRTH_EDIT, 0).edit()
									.putInt(YEAR, year).putInt(MONTH, month)
									.putInt(DAY, day)
									.putInt(LUNAR_YEAR, lunarYear)
									.putInt(LUNAR_MONTH, lunarMonth)
									.putInt(LUNAR_DAY, lunarDay).commit();
						} else {
							String solar = ChineseCalendar
									.sCalendarLundarToSolar(year, month, day);
							String[] solarStr = solar.split("-");
							int solarYear = Integer.parseInt(solarStr[0]);
							int solarMonth = Integer.parseInt(solarStr[1]);
							int solarDay = Integer.parseInt(solarStr[2]);
							Log.i(TAG, "solarYear: " + solarYear
									+ " solarMonth: " + solarMonth
									+ " solarDay: " + solarDay);
							getSharedPreferences(SHARE_BIRTH_EDIT, 0).edit()
									.putInt(YEAR, solarYear)
									.putInt(MONTH, solarMonth)
									.putInt(DAY, solarDay)
									.putInt(LUNAR_YEAR, year)
									.putInt(LUNAR_MONTH, month)
									.putInt(LUNAR_DAY, day).commit();
						}
						ringtype = mCursor
								.getInt(DatabaseHelper.RINGTYPE_INDEX);
						ringdays = mCursor
								.getString(DatabaseHelper.RINGDAY_INDEX);
						note = mCursor.getString(DatabaseHelper.NOTE_INDEX);
						phoneNum = mCursor
								.getString(DatabaseHelper.PHONE_NUMBER_INDEX);
						String birth = mCursor
								.getString(DatabaseHelper.BIRTHDAY_INDEX);
						person.setBirthDay(birth);
						person.setRingDays(ringdays);
						person.setRingtype(ringtype);
						updateEdit(person);
					} while (mCursor.moveToNext());
				}
			}
		}
	}

	private void updateEdit(Person p) {
		nameEdit.setText(p.getName());
		if (p.getIsStar() == 1) {
			mStar.setChecked(true);
		}
		if (p.getGender() == 1) {
			genderText.setText(R.string.female);
		} else {
			genderText.setText(R.string.male);
		}
		mBirthdayTextView.setText(p.getBirthDay());
		if (p.getIsLunar() == 1) {
			int year = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(YEAR,
					-1);
			int month = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(MONTH,
					-1);
			int day = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(DAY, -1);
			settingYear = year;
			settingMonth = month;
			settingDay = day;
			mBirthAttach
					.setText(String.valueOf(year + "-" + month + "-" + day));
			showLunar = true;
		} else {
			int tmpYear = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					LUNAR_YEAR, -1);
			int tmpMonth = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					LUNAR_MONTH, -1);
			int tmpDay = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					LUNAR_DAY, -1);
			String[] monthsTmp = getResources().getStringArray(
					R.array.lunarMonths);
			String[] daysTmp = getResources().getStringArray(
					R.array.lunarDaysLong);
			mBirthAttach.setText(Lunar.getYear(tmpYear)
					+ monthsTmp[tmpMonth - 1] + daysTmp[tmpDay - 1]);
			settingYear = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					YEAR, -1);
			settingMonth = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					MONTH, -1);
			settingDay = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(DAY,
					-1);
			showLunar = false;
		}
		if (p.getRingtype() != -1) {
			String ringtypeStr[] = getResources().getStringArray(
					R.array.ringtypes);
			ringTypeText.setText(ringtypeStr[p.getRingtype()]);
		} else {
			ringTypeText.setText(R.string.no_remind);
		}
		ringDaysText.setText(p.getRingDays());
		noteEdit.setText(note);
		phoneNumEdit.setText(phoneNum);
		defaultRingType = p.getRingtype();
		defaultSexSelect = p.getGender();

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
			// createCheckedDialog(R.string.ring_title);
			break;
		case R.id.tv_ringtype:
			if (!mBirthdayTextView.getText().equals("")) {
				if (mMode == MODE_EDIT) {
					defaultRingType = ringtype;
				}
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
			if (isLunar == 1) {
				showLunar = true;
			} else {
				showLunar = false;
			}
			initYear();
			initDatePickerView(mMode);
			showPickViewDialog();
			break;
		case R.id.selectyear:
			// if (isShowYear) {
			// datePickerLayout.findViewById(R.id.year).setVisibility(
			// View.GONE);
			// pickerSelectYear.setImageResource(R.drawable.displayyear);
			// isShowYear = false;
			// } else {
			// isShowYear = true;
			// datePickerLayout.findViewById(R.id.year).setVisibility(
			// View.VISIBLE);
			// pickerSelectYear.setImageResource(R.drawable.ignoreyear);
			// }
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
										.sCalendarLundarToSolar(solarYear,
												month.getCurrentItem() + 1,
												day.getCurrentItem() + 1));
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
							settingYear = cal.get(Calendar.YEAR);
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
			if (!showLunar) {
				initSolar(mode);
			} else {
				initLunar(mode);
			}
			break;
		}
	}

	private void initLunar(final int mode) {
		pickerLunar.setImageResource(R.drawable.nong_blue);
		pickerSolar.setImageResource(R.drawable.gong_grey);
		if (mode == MODE_EDIT) {
			currentYear = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					LUNAR_YEAR, -1);
			currentMonth = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					LUNAR_MONTH, -1) - 1;
			currentDay = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					LUNAR_DAY, -1) - 1;
		}
		Calendar calendar = Calendar.getInstance();
		Lunar lunar = new Lunar(calendar);
		final String lunarStr[] = (lunar.toString()).split("-");
		String temp[] = mRes.getStringArray(R.array.lunarMonths);
		months = new String[12];
		for (int i = 0; i < temp.length; i++) {
			if (mode == MODE_ADD) {
				if (lunarStr[0].equals(temp[i])) {
					currentMonth = i;
				}
			}
			months[i] = temp[i] + "   ";
		}
		monthAdapter = new ArrayWheelAdapter<String>(months);
		month.setAdapter(monthAdapter);

		year.setCurrentItem(currentYear - START_YEAR);
		Log.i(TAG, "currentMonth:" + currentMonth);
		month.setCurrentItem(currentMonth);
		int daycnt = Lunar.monthDays(currentYear, currentMonth);
		String tmp[] = mRes.getStringArray(R.array.lunarDaysLong);
		days = new String[daycnt];
		for (int i = 0; i < daycnt; i++) {
			if (mode == MODE_ADD) {
				if (lunarStr[1].equals(tmp[i])) {
					currentDay = i;
				}
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
				String tmp2[] = mRes.getStringArray(R.array.lunarDaysLong);
				int daycnt = Lunar.monthDays(currentYear, newValue);
				Log.i(TAG, "dayCnt:" + daycnt);
				days = new String[daycnt];
				for (int i = 0; i < daycnt; i++) {
					Log.i(TAG, "tmp2[" + i + "]:" + tmp2[i]);
					if (mode == MODE_ADD) {
						if (lunarStr[1].equals(tmp2[i])) {
							currentDay = i;
						}
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
		} else {
			currentYear = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					YEAR, -1);
			currentMonth = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(
					MONTH, -1) - 1;
			currentDay = getSharedPreferences(SHARE_BIRTH_EDIT, 0).getInt(DAY,
					-1);
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
		confirmDay(currentMonth);
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
				confirmDay(newValue);
				dayAdapter = new ArrayWheelAdapter<String>(days);
				day.setAdapter(dayAdapter);
			}
		});
	}

	private void confirmDay(int m) {
		switch (m) {
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			days = new String[31];
			break;
		case 3:
		case 5:
		case 8:
		case 10:
			days = new String[30];
			break;
		default:
			if (isLeapYear(currentYear)) {
				days = new String[29];
			} else {
				days = new String[28];
			}
		}
		for (int i = 0; i < days.length; i++) {
			days[i] = String.valueOf(i + 1) + "   "
					+ mRes.getString(R.string.day);
		}
	}

	private boolean isLeapYear(int year) {
		if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
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
			setAlarm((int) id);
		} else if (mode == MODE_EDIT) {
			String where = "_id = " + mId;
			returnUpdateId = mDbHelper.update(contentValues, where);
			setAlarm(mId);
		}

		Log.i(TAG, "id:" + id);
		if (id > 0 || returnUpdateId > 0) {
			setResult(Activity.RESULT_OK);
			BirthEditActivity.this.finish();
		}
	}

	private void setAlarm(int id) {
		if (isLunar == 1) {
			Log.i(TAG, ChineseCalendar.sCalendarLundarToSolar(settingYear,
					settingMonth, settingDay));
			String lunarCal = ChineseCalendar.sCalendarLundarToSolar(
					settingYear, settingMonth, settingDay);
			String[] lunarCalStr = lunarCal.split("-");
			int solarYear = Integer.parseInt(lunarCalStr[0]);
			int solarMonth = Integer.parseInt(lunarCalStr[1]);
			int solarDay = Integer.parseInt(lunarCalStr[2]);
			switch (ringtype) {
			case 0:// 仅提醒公历生日
				setSolarAlarm(solarMonth, solarDay, id);
				break;
			case 1:// 仅提醒农历生日
				setLunarAlarm(settingMonth, settingDay, id);
				break;
			case 2:// 两个都提醒
				setSolarAlarm(solarMonth, solarDay, id);
				setLunarAlarm(settingMonth, settingDay, id);
				break;
			default:
				break;
			}
		} else {
			Log.i(TAG, ChineseCalendar.sCalendarLundarToSolar(settingYear,
					settingMonth, settingDay));
			String solarCal = ChineseCalendar.sCalendarSolarToLundar(
					settingYear, settingMonth, settingDay);
			String[] solarCalStr = solarCal.split("-");
			int lunarYear = Integer.parseInt(solarCalStr[0]);
			int lunarMonth = Integer.parseInt(solarCalStr[1]);
			int lunarDay = Integer.parseInt(solarCalStr[2]);
			switch (ringtype) {
			case 0:// 仅提醒公历生日
				setSolarAlarm(settingMonth, settingDay, id);
				break;
			case 1:// 仅提醒农历生日
				setLunarAlarm(lunarMonth, lunarDay, id);
				break;
			case 2:// 两个都提醒
				setSolarAlarm(settingMonth, settingDay, id);
				setLunarAlarm(lunarMonth, lunarDay, id);
				break;
			default:
				break;
			}
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
		for (int i = 0; i < flags.length; i++) {
			Log.i(TAG, "flags[" + i + "]==" + flags[i]);
		}
		if (flags[0]) {
			contentValues.put(DatabaseHelper.TODAY, ringcounts[0]);
		} else {
			contentValues.put(DatabaseHelper.TODAY, -1);
		}
		if (flags[1]) {
			contentValues.put(DatabaseHelper.AHEAD_ONE_DAY, ringcounts[1]);
		} else {
			contentValues.put(DatabaseHelper.AHEAD_ONE_DAY, -1);
		}
		if (flags[2]) {
			contentValues.put(DatabaseHelper.AHEAD_THREE_DAY, ringcounts[2]);
		} else {
			contentValues.put(DatabaseHelper.AHEAD_THREE_DAY, -1);
		}
		if (flags[3]) {
			contentValues.put(DatabaseHelper.AHEAD_SEVEN_DAY, ringcounts[3]);
		} else {
			contentValues.put(DatabaseHelper.AHEAD_SEVEN_DAY, -1);
		}
	}

	private void setLunarAlarm(int month, int day, int id) {
		Calendar c = Calendar.getInstance();
		if (month < c.get(Calendar.MONTH)) {
			c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
		} else if (month == c.get(Calendar.MONTH)) {
			if (day < c.get(Calendar.DAY_OF_MONTH)) {
				c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
			}
		}
		c.set(Calendar.MONTH, month - 1);// 也可以填数字，0-11,一月为0
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, 06);
		c.set(Calendar.MINUTE, 00);
		c.set(Calendar.SECOND, 00);
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, id, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
	}

	private void setSolarAlarm(int month, int day, int id) {
		Calendar c = Calendar.getInstance();
		if (month < c.get(Calendar.MONTH)) {
			c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
		} else if (month == c.get(Calendar.MONTH)) {
			if (day < c.get(Calendar.DAY_OF_MONTH)) {
				c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
			}
		}
		c.set(Calendar.MONTH, month - 1);// 也可以填数字，0-11,一月为0
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, 06);
		c.set(Calendar.MINUTE, 00);
		c.set(Calendar.SECOND, 00);
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pi2 = PendingIntent.getBroadcast(this, id, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi2);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi2);
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

	private void createCheckedDialog(int title) {
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
