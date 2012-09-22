package com.ds.birth;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.BirthConstants;
import com.ds.utility.ChineseCalendar;
import com.ds.utility.Lunar;
import com.ds.utility.Person;
import com.ds.utility.Utility;
import com.ds.widget.ArrayWheelAdapter;
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
	// TextView genderText;
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

	public static final String TYPE = "type";
	public static final String NAME = "name";
	private String meName = "";

	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");

	private File mCurrentPhotoFile;
	private static final int GET_PHOTO_WITH_GALLARY = 102;
	private static final int GET_PHOTO_WITH_CAMERA = 101;
	String mAvatarUri;
	RadioButton maleRadio;
	RadioButton femaleRadio;

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
			mType = intent.getIntExtra(TYPE, 0);
			mMode = MODE_ADD;
			if (mType == 1) {
				meName = intent.getStringExtra(NAME);
			}
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
						mType = mCursor.getInt(DatabaseHelper.TYPE_INDEX);
						person.setType(mType);
						mAvatarUri = mCursor
								.getString(DatabaseHelper.AVATAR_INDEX);
						person.setAvater(mAvatarUri);
						updateEdit(person);
					} while (mCursor.moveToNext());
				}
			}
		}
	}

	private void updateEdit(Person p) {
		String avater = p.getAvater();
		if (avater == null || avater.length() == 0) {
			Drawable drawable = getResources().getDrawable(
					R.drawable.common_head_withbg);
			headerImage.setBackgroundDrawable(drawable);
		} else {
			if (avater.startsWith("http")) {
				Drawable cachedImage = AsyncImageLoader
						.loadImageFromUrl(avater);
				headerImage.setBackgroundDrawable(cachedImage);
			} else {
				String url = avater;
				Uri uri = Uri.parse(url);
				Bitmap avatar = Utility.getBitmapFromUri(uri,
						BirthEditActivity.this);
				if (avatar != null) {
					BitmapDrawable bd = new BitmapDrawable(avatar);
					headerImage.setBackgroundDrawable(bd);
				}
			}
		}

		nameEdit.setText(p.getName());
		if (p.getType() == 1) {
			nameEdit.setClickable(false);
			nameEdit.setFocusable(false);
		}
		if (p.getIsStar() == 1) {
			mStar.setChecked(true);
		}
		if (p.getGender() == 1) {
			// genderText.setText(R.string.female);
			maleRadio.setChecked(true);
		} else {
			// genderText.setText(R.string.male);
			femaleRadio.setChecked(true);
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
		topLeftBtn = (Button) findViewById(R.id.backBtn);
		topLeftBtn.setVisibility(View.VISIBLE);
		nameEdit = (EditText) findViewById(R.id.name);
		if (mType == 1) {
			nameEdit.setText(meName);
			nameEdit.setFocusable(false);
			nameEdit.setClickable(false);
		}

		maleRadio = (RadioButton) findViewById(R.id.male);
		femaleRadio = (RadioButton) findViewById(R.id.female);
		// genderText = (TextView) findViewById(R.id.tv_gender);
		ringDaysText = (TextView) findViewById(R.id.tv_ringdays);
		ringTypeText = (TextView) findViewById(R.id.tv_ringtype);
		headerImage = (ImageView) findViewById(R.id.head);
		mBirthdayTextView = (TextView) findViewById(R.id.tv_birthday);
		mBirthAttach = (TextView) findViewById(R.id.birthattach);
		mStar = (CheckBox) findViewById(R.id.star);
		noteEdit = (EditText) findViewById(R.id.tv_note);
		phoneNumEdit = (EditText) findViewById(R.id.tv_number);
		saveBtn = (Button) findViewById(R.id.rightBtn);
		saveBtn.setVisibility(View.VISIBLE);
		saveBtn.setText(R.string.save);
		topLeftBtn.setOnClickListener(this);
		// genderText.setOnClickListener(this);
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
		case R.id.backBtn:
			if (BirthEditActivity.this.getCurrentFocus() != null) {
				hideSoftKeypad();
			}
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		// case R.id.tv_gender:
		// if (genderText.getText().equals(mGenderItems[0])) {
		// defaultSexSelect = 0;
		// } else {
		// defaultSexSelect = 1;
		// }
		// if(maleRadio.isChecked()){
		// defaultSexSelect = 1;
		// }else if(femaleRadio.isChecked()){
		// defaultSexSelect = 0;
		// }
		// createOptionDialog(R.string.gender_title, mGenderItems,
		// R.id.tv_gender, defaultSexSelect);
		// break;
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
		case R.id.head:
			loadMenu();
			break;
		case R.id.rightBtn:
			if (BirthEditActivity.this.getCurrentFocus() != null) {
				hideSoftKeypad();
			}
			updateData(mMode);
			break;
		case R.id.tv_birthday:
			if (mMode == MODE_ADD) {
				defaultRingType = 0;
				ringTypeText.setText(mRingTypeItem[0]);
			} else {
				switch (ringtype) {
				case 0:
					defaultRingType = 0;
					ringTypeText.setText(mRingTypeItem[0]);
					break;
				case 1:
					defaultRingType = 1;
					ringTypeText.setText(mRingTypeItem[1]);
					break;
				case 2:
					defaultRingType = 2;
					ringTypeText.setText(mRingTypeItem[2]);
					break;
				}
			}
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
			defaultRingType = 1;
			ringTypeText.setText(mRingTypeItem[1]);
			showLunar = true;
			initLunar(mMode);
			break;
		case R.id.solar:
			defaultRingType = 0;
			ringTypeText.setText(mRingTypeItem[0]);
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
						String lunarYear = lunar.getLunarYear();
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
		// dialog.setButton2(mRes.getString(R.string.cancel),
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });
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
		if (name == null || name.equals("") || name.length() == 0) {
			// show dialog
			Toast.makeText(this, R.string.pls_input_name, Toast.LENGTH_SHORT)
					.show();
			return;
		} else {
			if (mBirthdayTextView.getText().toString().equals("")
					|| mBirthdayTextView.getText().toString().length() == 0) {
				Toast.makeText(this, R.string.save_birthday_hint,
						Toast.LENGTH_SHORT).show();
				return;
			}
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
		gender = maleRadio.isChecked() ? 1 : 0;
		isStar = mStar.isChecked() ? 1 : 0;
		birthday = mBirthdayTextView.getText().toString();
		ringtype = defaultRingType;
		ringdays = ringDaysText.getText().toString();
		isLunar = showLunar ? 1 : 0;
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
		if (mAvatarUri == null) {
			mAvatarUri = "";
		}
		contentValues.put(DatabaseHelper.AVATAR, mAvatarUri);
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
							// genderText.setText(items[defaultSexSelect]);
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
		// private Button defaultBtn;
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
			// defaultBtn = (Button) findViewById(R.id.default_button);
			cancelBtn = (Button) findViewById(R.id.cancel_button);
			takePicBtn
					.setOnClickListener(new android.view.View.OnClickListener() {

						public void onClick(View v) {
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {
								// doTakePhoto();
								takePhoto();
							} else {
								showToast("没有SD卡");
							}
							MyDialog.this.dismiss();
						}
					});
			gallaryBtn
					.setOnClickListener(new android.view.View.OnClickListener() {

						public void onClick(View v) {
							Intent intent = getPhotoPickIntent();
							startActivityForResult(intent,
									GET_PHOTO_WITH_GALLARY);
							MyDialog.this.dismiss();
						}
					});
			// defaultBtn
			// .setOnClickListener(new android.view.View.OnClickListener() {
			//
			// public void onClick(View v) {
			//
			// }
			// });
			cancelBtn
					.setOnClickListener(new android.view.View.OnClickListener() {

						public void onClick(View v) {
							MyDialog.this.dismiss();
						}
					});

		}
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	Uri imageUri;

	public void takePhoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File photo = new File(Environment.getExternalStorageDirectory(),
				getPhotoFileName());
		Log.i(TAG, "getPhotoFileName():" + getPhotoFileName());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		imageUri = Uri.fromFile(photo);
		startActivityForResult(intent, GET_PHOTO_WITH_CAMERA);
	}

	protected void doTakePhoto() {
		try {
			PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, GET_PHOTO_WITH_CAMERA);
		} catch (ActivityNotFoundException e) {
		}
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-dd_HH-mm-ss");
		return dateFormat.format(date) + ".jpg";
	}

	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		return intent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == GET_PHOTO_WITH_CAMERA) {
				// doCropPhoto(mCurrentPhotoFile);
				Uri selectedImage = imageUri;
				Log.i(TAG, "uri:" + imageUri.toString());
				getContentResolver().notifyChange(selectedImage, null);
				ContentResolver cr = getContentResolver();
				Bitmap bitmap;
				try {
					bitmap = android.provider.MediaStore.Images.Media
							.getBitmap(cr, selectedImage);
					BitmapDrawable bd = new BitmapDrawable(bitmap);
					headerImage.setBackgroundDrawable(bd);
				} catch (Exception e) {
					Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
							.show();
				}
				mAvatarUri = selectedImage.toString();
			}
			if (requestCode == GET_PHOTO_WITH_GALLARY) {
				if (data != null) {
					Uri uri = data.getData();
					// Bitmap photo = data.getParcelableExtra("data");
					Bitmap avatar = Utility.getBitmapFromUri(uri, this);
					if (avatar != null) {
						BitmapDrawable bd = new BitmapDrawable(avatar);
						headerImage.setBackgroundDrawable(bd);
					}
					mAvatarUri = uri.toString();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void doCropPhoto(File f) {
		try {
			// 启动gallery去剪辑这个照片
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, GET_PHOTO_WITH_GALLARY);
		} catch (Exception e) {
		}
	}

	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}

}
