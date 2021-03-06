package com.ds.birth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ds.birth.AsyncImageLoader.ImageCallback;
import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.BirthConstants;
import com.ds.utility.ChineseCalendar;
import com.ds.utility.Lunar;
import com.ds.utility.Person;
import com.ds.utility.Utility;

public class BirthDetailActivity extends Activity implements OnClickListener {
	private static final String TAG = "BirthDetailActivity";
	private static final int EDIT_BIRTHDAY = 0;
	private int birthId = -1;
	private DbHelper dbHelper;
	private Cursor mCursor;

	private Button backBtn;
	private Button editBtn;
	private TextView nameTextView;
	private TextView solarAlarmTextView;
	private TextView lunarAlarmTextView;
	private ImageView avatarView;
	private ImageView genderView;
	private ImageView solarView;
	private ImageView lunarView;
	private TextView mainTextView;
	private TextView secondaryTextView;
	private TextView ageView;
	private TextView dayView;
	private String mName;
	// private TextView constants;
	private EditText noteTextView;
	private int mIsLunar;
	private int mIsStar;
	private int ringtype;
	private String ringdays;
	private String birthday;
	private int gender;
	private String note;
	private String phoneNumber;
	private LinearLayout blessImgBtn;
	private LinearLayout luckyImgBtn;
	private LinearLayout wikiImgBtn;
	private TextView titleView;
	private LinearLayout callLayout;
	private TextView phoneNumView;

	int year;
	int month;
	int day;
	SharedPreferences lunarShare;
	private static final String LUNAR_SHARE = "lunar_share";
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String LUNAR_YEAR = "lunar_year";
	private static final String LUNAR_MONTH = "lunar_month";
	private static final String LUNAR_DAY = "lunar_day";

	private TextView solarAge;
	private TextView lunarAge;
	private ImageView starView;
	private boolean isStar = false;

	// private static final String NOTE = "note";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_detail);
		dbHelper = DbHelper.getInstance(this);
		dbHelper.open(this);
		getBirthId();
		initViews();
	}

	private void initViews() {
		lunarShare = getSharedPreferences(LUNAR_SHARE, 0);
		backBtn = (Button) findViewById(R.id.backBtn);
		editBtn = (Button) findViewById(R.id.rightBtn);
		backBtn.setVisibility(View.VISIBLE);
		editBtn.setVisibility(View.VISIBLE);
		editBtn.setText(R.string.edit);
		backBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		starView = (ImageView) findViewById(R.id.favor);
		starView.setOnClickListener(this);

		callLayout = (LinearLayout) findViewById(R.id.call);
		callLayout.setOnClickListener(this);
		phoneNumView = (TextView) findViewById(R.id.phoneNum);

		solarAge = (TextView) findViewById(R.id.solarage);
		lunarAge = (TextView) findViewById(R.id.lunarage);

		titleView = (TextView) findViewById(R.id.title);
		titleView.setText(R.string.detail_title);
		nameTextView = (TextView) findViewById(R.id.name);
		avatarView = (ImageView) findViewById(R.id.avatar);
		genderView = (ImageView) findViewById(R.id.gender);
		solarView = (ImageView) findViewById(R.id.solar);
		lunarView = (ImageView) findViewById(R.id.lunar);
		mainTextView = (TextView) findViewById(R.id.birth_date);
		secondaryTextView = (TextView) findViewById(R.id.birth_secondary_date);
		// lunarView.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// year = lunarShare.getInt(LUNAR_YEAR, -1);
		// month = lunarShare.getInt(LUNAR_MONTH, -1);
		// day = lunarShare.getInt(LUNAR_DAY, -1);
		// // getDays(true);
		// solarView.setBackgroundResource(R.drawable.gong_grey);
		// lunarView.setBackgroundResource(R.drawable.nong_blue);
		// }
		//
		// });
		// solarView.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// year = lunarShare.getInt(YEAR, -1);
		// month = lunarShare.getInt(MONTH, -1);
		// day = lunarShare.getInt(DAY, -1);
		// // getDays(false);
		// solarView.setBackgroundResource(R.drawable.gong_blue);
		// lunarView.setBackgroundResource(R.drawable.nong_grey);
		// }
		//
		// });
		// getDays(R.id.solarage);
		// getDays(R.id.lunarage);

		ageView = (TextView) findViewById(R.id.agecnt);
		dayView = (TextView) findViewById(R.id.leftday);
		noteTextView = (EditText) findViewById(R.id.note_et);
		solarAlarmTextView = (TextView) findViewById(R.id.alarmsolar);
		lunarAlarmTextView = (TextView) findViewById(R.id.alarmlunar);
		blessImgBtn = (LinearLayout) findViewById(R.id.blessing);
		luckyImgBtn = (LinearLayout) findViewById(R.id.lucky);
		wikiImgBtn = (LinearLayout) findViewById(R.id.wiki);
		blessImgBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(BirthDetailActivity.this,
						SendBlessActivity.class);
				Bundle extras = new Bundle();
				extras.putString("number", phoneNumView.getText().toString());
				intent.putExtras(extras);
				startActivity(intent);
			}

		});
		luckyImgBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}

		});
		wikiImgBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}

		});
	}

	private void getBirthId() {
		Intent intent = getIntent();
		if (null != intent
				&& BirthConstants.ACTION_VIEW_BIRTH.equals(intent.getAction())) {
			birthId = intent.getExtras().getInt(BirthConstants.ID);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "birthId:" + birthId);
		startQuery();
	}

	private void startQuery() {
		if (birthId != -1) {
			mCursor = dbHelper.queryId(birthId);
			if (mCursor != null && mCursor.getCount() > 0) {
				Person person = new Person();
				if (mCursor.moveToFirst()) {
					do {
						person.setName(mCursor
								.getString(DatabaseHelper.NAME_INDEX));
						Log.i(TAG, "start query year:" + year);
						mIsLunar = mCursor.getInt(DatabaseHelper.ISLUNAR_INDEX);
						mIsStar = mCursor.getInt(DatabaseHelper.ISSTAR_INDEX);
						person.setIsLunar(mIsLunar);
						person.setIsStar(mIsStar);
						birthday = mCursor
								.getString(DatabaseHelper.BIRTHDAY_INDEX);
						person.setBirthDay(birthday);
						gender = mCursor.getInt(DatabaseHelper.SEX_INDEX);
						person.setGender(gender);
						year = mCursor.getInt(DatabaseHelper.YEAR_INDEX);
						month = mCursor.getInt(DatabaseHelper.MONTH_INDEX);
						day = mCursor.getInt(DatabaseHelper.DAY_INDEX);
						Log.i(TAG, "year:" + year + " month:" + month + " day:"
								+ day);
						if (mIsLunar == 0) {
							String lunar = ChineseCalendar
									.sCalendarSolarToLundar(year, month, day);
							String[] lunarStr = lunar.split("-");
							int lunarYear = Integer.parseInt(lunarStr[0]);
							int lunarMonth = Integer.parseInt(lunarStr[1]);
							int lunarDay = Integer.parseInt(lunarStr[2]);
							Log.i(TAG, "lunarYear: " + lunarYear
									+ " lunarMonth: " + lunarMonth
									+ " lunarDay: " + lunarDay);
							getSharedPreferences(LUNAR_SHARE, 0).edit()
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
							getSharedPreferences(LUNAR_SHARE, 0).edit()
									.putInt(YEAR, solarYear)
									.putInt(MONTH, solarMonth)
									.putInt(DAY, solarDay)
									.putInt(LUNAR_YEAR, year)
									.putInt(LUNAR_MONTH, month)
									.putInt(LUNAR_DAY, day).commit();
						}
						note = mCursor.getString(DatabaseHelper.NOTE_INDEX);
						phoneNumber = mCursor
								.getString(DatabaseHelper.PHONE_NUMBER_INDEX);
						// String tmp = lunarShare.getString(NOTE, "");
						// lunarShare.edit().putString(NOTE, note +
						// tmp).commit();
						person.setPhoneNumber(phoneNumber);
						person.setNote(note);
						ringtype = mCursor
								.getInt(DatabaseHelper.RINGTYPE_INDEX);
						ringdays = mCursor
								.getString(DatabaseHelper.RINGDAY_INDEX);
						person.setRingtype(ringtype);
						person.setRingDays(ringdays);
						String avater = mCursor
								.getString(DatabaseHelper.AVATAR_INDEX);
						person.setAvater(avater);
						updateDetailData(person);
					} while (mCursor.moveToNext());
				}
			}
		}
	}

	private void updateDetailData(Person p) {
		Log.i(TAG, "birthdate:" + p.getBirthDay());
		String avater = p.getAvater();
		if (avater == null || avater.length() == 0) {
			Drawable drawable = getResources().getDrawable(
					R.drawable.common_head_withbg);
			avatarView.setBackgroundDrawable(drawable);
		} else {
			if (avater.startsWith("http")) {
				Drawable cachedImage = AsyncImageLoader
						.loadImageFromUrl(avater);
				avatarView.setBackgroundDrawable(cachedImage);
			} else {
				String url = avater;
				Uri uri = Uri.parse(url);
				Bitmap avatar = Utility.getBitmapFromUri(uri,
						BirthDetailActivity.this);
				if (avatar != null) {
					BitmapDrawable bd = new BitmapDrawable(avatar);
					avatarView.setBackgroundDrawable(bd);
				}
			}
		}
		if (p.getIsStar() == 1) {
			starView.setBackgroundResource(R.drawable.contact_detial_rb_fever_c);
			isStar = true;
		}
		if (p.getPhoneNumber().length() != 0) {
			phoneNumView.setText(p.getPhoneNumber());
		} else {
			phoneNumView.setText(R.string.no_phone_number);
		}
		titleView.setText(p.getName()
				+ getResources().getString(R.string.detail_title));
		nameTextView.setText(p.getName());
		mainTextView.setText(p.getBirthDay());
		noteTextView.setText(p.getNote());
		if (p.getGender() == 0) {
			genderView.setBackgroundResource(R.drawable.sex_female);
		} else {
			genderView.setBackgroundResource(R.drawable.sex_male);
		}
		if (p.getIsLunar() == 1) {
			lunarView.setBackgroundResource(R.drawable.nong_blue);
			solarView.setBackgroundResource(R.drawable.gong_grey);
			secondaryTextView.setText(String.valueOf(lunarShare
					.getInt(YEAR, -1))
					+ "-"
					+ lunarShare.getInt(MONTH, -1)
					+ "-" + lunarShare.getInt(DAY, -1));
		} else {
			solarView.setBackgroundResource(R.drawable.gong_blue);
			lunarView.setBackgroundResource(R.drawable.nong_grey);
			int tmpYear = lunarShare.getInt(LUNAR_YEAR, -1);
			int tmpMonth = lunarShare.getInt(LUNAR_MONTH, -1);
			int tmpDay = lunarShare.getInt(LUNAR_DAY, -1);
			String[] monthsTmp = getResources().getStringArray(
					R.array.lunarMonths);
			String[] daysTmp = getResources().getStringArray(
					R.array.lunarDaysLong);
			secondaryTextView.setText(Lunar.getYear(tmpYear)
					+ monthsTmp[tmpMonth - 1] + daysTmp[tmpDay - 1]);
		}
		// if (p.getIsLunar() == 1) {
		// getDays(R.id.lunarage);
		// } else {
		// getDays(R.id.solarage);
		// }
		getSolarDays(p.getIsLunar());
		getLunarDays(p.getIsLunar());
		String solartmp = getResources().getString(R.string.solaralarm);
		String lunartmp = getResources().getString(R.string.lunaralarm);
		String noalarm = getResources().getString(R.string.noalarm);
		if (p.getRingtype() == 0) {
			solarAlarmTextView.setText(String.format(solartmp, getResources()
					.getString(R.string.open)));
			lunarAlarmTextView.setText(String.format(lunartmp, getResources()
					.getString(R.string.close)));
		} else if (p.getRingtype() == 1) {
			lunarAlarmTextView.setText(String.format(lunartmp, getResources()
					.getString(R.string.open)));
			solarAlarmTextView.setText(String.format(solartmp, getResources()
					.getString(R.string.close)));
		} else if (p.getRingtype() == 2) {
			lunarAlarmTextView.setText(String.format(solartmp, getResources()
					.getString(R.string.open)));
			solarAlarmTextView.setText(String.format(lunartmp, getResources()
					.getString(R.string.open)));
		} else {
			lunarAlarmTextView.setText(String.format(lunartmp, getResources()
					.getString(R.string.close)));
			solarAlarmTextView.setText(String.format(solartmp, getResources()
					.getString(R.string.close)));
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		String tmp = noteTextView.getText().toString();
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.NOTE, tmp);

		dbHelper.update(values, " _id = " + birthId);
		// getSharedPreferences(LUNAR_SHARE, 0).edit().putString(NOTE, tmp +
		// note)
		// .commit();
		dbHelper.close();
	}

	private void getLunarDays(int islunar) {
		Calendar cal = Calendar.getInstance();
		int nowDay = cal.get(Calendar.DAY_OF_MONTH);
		int nowMonth = cal.get(Calendar.MONTH) + 1;
		int nowYear = cal.get(Calendar.YEAR);
		String nowLunar = ChineseCalendar.sCalendarSolarToLundar(nowYear,
				nowMonth, nowDay);
		String[] tmp = nowLunar.split("-");
		nowYear = Integer.parseInt(tmp[0]);
		nowMonth = Integer.parseInt(tmp[1]);
		nowDay = Integer.parseInt(tmp[2]);
		Log.i(TAG, "Lunar nowYear:" + nowYear + "Lunar nowMonth:" + nowMonth
				+ "Lunar nowDay:" + nowDay);

		SharedPreferences shared = getSharedPreferences(LUNAR_SHARE, 0);
		int lunarYear = shared.getInt(LUNAR_YEAR, -1);
		int lunarMonth = shared.getInt(LUNAR_MONTH, -1);
		int lunarDay = shared.getInt(LUNAR_DAY, -1);

		Log.i(TAG, "lunarYear:" + lunarYear + " lunarMonth:" + lunarMonth
				+ " lunarDay:" + lunarDay);
		int age = 1;
		if (nowYear == lunarYear) {
			if (nowMonth > lunarMonth) {
				nowYear = nowYear + 1;
				age = age + 1;
			} else if (nowMonth == lunarMonth) {
				if (nowDay > lunarDay) {
					nowYear = nowYear + 1;
					age = age + 1;
				}
			} else {
				
			}
		} else if (nowYear > lunarYear) {
			age = nowYear - lunarYear;
			if (nowMonth > lunarMonth) {
				nowYear = nowYear + 1;
				age = age + 1;
			} else if (nowMonth == lunarMonth) {
				if (nowDay > lunarDay) {
					nowYear = nowYear + 1;
					age = age + 1;
				}
			} else {
				
			}
		} else {

		}
		String begin = String.valueOf(nowYear + "-" + nowMonth + "-" + nowDay);
		String end = String.valueOf(lunarYear + "-" + lunarMonth + "-"
				+ lunarDay);
		long daycnt = Utility.getDays(begin, end);
		String data = getResources().getString(R.string.age_show);
		data = String.format(data, age, daycnt);
		lunarAge.setText(data);
	}

	private void getSolarDays(int isLunar) {
		Calendar cal = Calendar.getInstance();
		int nowDay = cal.get(Calendar.DAY_OF_MONTH);
		int nowMonth = cal.get(Calendar.MONTH) + 1;
		int nowYear = cal.get(Calendar.YEAR);
		Log.i(TAG, "solar nowYear:" + nowYear + "solar nowMonth:" + nowMonth
				+ "solar nowDay:" + nowDay);

		SharedPreferences shared = getSharedPreferences(LUNAR_SHARE, 0);
		int solarYear = shared.getInt(YEAR, -1);
		int solarMonth = shared.getInt(MONTH, -1);
		int solarDay = shared.getInt(DAY, -1);

		Log.i(TAG, "solarYear:" + solarYear + " solarMonth:" + solarMonth
				+ " solarDay:" + solarDay);
		int age = 1;
		if (nowYear == solarYear) {
			if (nowMonth > solarMonth) {
				nowYear = nowYear + 1;
				age = age + 1;
			} else if (nowMonth == solarMonth) {
				if (nowDay > solarDay) {
					nowYear = nowYear + 1;
					age = age + 1;
				}
			} else {
				
			}
		} else if (nowYear > solarYear) {
			age = nowYear - solarYear;
			if (nowMonth > solarMonth) {
				nowYear = nowYear + 1;
				age = age + 1;
			} else if (nowMonth == solarMonth) {
				if (nowDay > solarDay) {
					nowYear = nowYear + 1;
					age = age + 1;
				}
			} else {
			}
		} else {

		}
		String begin = String.valueOf(nowYear + "-" + nowMonth + "-" + nowDay);
		String end = String.valueOf(solarYear + "-" + solarMonth + "-"
				+ solarDay);
		long daycnt = Utility.getDays(begin, end);
		String data = getResources().getString(R.string.age_show);
		data = String.format(data, age, daycnt);
		solarAge.setText(data);
	}

	private void getDays(int viewId) {
		boolean isLunar = false;
		if (viewId == R.id.lunarage) {
			isLunar = true;
		}
		Calendar cal = Calendar.getInstance();
		int nowDay = cal.get(Calendar.DAY_OF_MONTH);
		int nowMonth = cal.get(Calendar.MONTH) + 1;
		int nowYear = cal.get(Calendar.YEAR);
		Log.i(TAG, "isLunar:" + isLunar);
		if (isLunar) {
			String nowLunar = ChineseCalendar.sCalendarSolarToLundar(nowYear,
					nowMonth, nowDay);
			String[] tmp = nowLunar.split("-");
			nowYear = Integer.parseInt(tmp[0]);
			nowMonth = Integer.parseInt(tmp[1]);
			nowDay = Integer.parseInt(tmp[2]);
		}
		Log.i(TAG, "nowYear:" + nowYear + " nowMonth:" + nowMonth + " nowDay:"
				+ nowDay);

		Log.i(TAG, "year:" + year + " month:" + month + " day:" + day);
		String time = String.valueOf(year + "-" + month + "-" + day);
		String data = "";
		int age = -1;
		try {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			Date date = f.parse(time);
			age = Utility.getAge(date);
			if (nowMonth > month) {
				age = age + 1;
			} else if (nowMonth == month) {
				if (nowDay >= day) {
					age = age + 1;
				}
			}
			// if (isLunar) {
			// // data = getResources().getString(R.string.ageShowlunar);
			// constants.setText(R.string.ageShowlunar);
			// } else {
			// // data = getResources().getString(R.string.ageShowsolar);
			// constants.setText(R.string.ageShowsolar);
			// }
		} catch (Exception e) {

		}

		String begin = String.valueOf(nowYear + "-" + month + "-" + day);

		String end = String.valueOf(nowYear + "-" + nowMonth + "-" + nowDay);
		Log.i(TAG, "nowMonth:" + nowMonth + " month:" + month + " nowDay:"
				+ nowDay + " day:" + day);
		if (nowMonth > month) {
			end = String.valueOf((nowYear + 1) + "-" + month + "-" + day);
			begin = String.valueOf(nowYear + "-" + nowMonth + "-" + nowDay);
		} else if (nowMonth == month) {
			Log.i(TAG, "nowMonth==month");
			if (nowDay > day) {
				Log.i(TAG, "nowDay>day");
				end = String.valueOf((nowYear + 1) + "-" + month + "-" + day);
				begin = String.valueOf(nowYear + "-" + nowMonth + "-" + nowDay);
			}
		}
		data = getResources().getString(R.string.age_show);
		long daycnt = Utility.getDays(begin, end);
		data = String.format(data, age, daycnt);
		if (isLunar) {
			// lunarLeftDay.setText(String.valueOf(daycnt));
			// data = String.valueOf(age);
			lunarAge.setText(data);
		} else {
			// solarLeftDay.setText(String.valueOf(daycnt));
			// data = String.valueOf(age);
			solarAge.setText(data);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.call:
			if (phoneNumber != null && phoneNumber.length() != 0) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ phoneNumber));
				startActivity(intent);
			}
			break;
		case R.id.backBtn:
			finish();
			break;
		case R.id.rightBtn:
			// edit birthday
			Intent intent = new Intent(BirthConstants.ACTION_EDIT_BIRTH);
			Bundle extras = new Bundle();
			extras.putInt(BirthConstants.ID, birthId);
			intent.putExtras(extras);
			startActivityForResult(intent, EDIT_BIRTHDAY);
			break;
		case R.id.favor:
			String where = "_id = " + birthId;
			ContentValues values = new ContentValues();
			if (isStar) {
				starView.setBackgroundResource(R.drawable.contact_detial_rb_fever_no);
				values.put(DatabaseHelper.ISSTAR, 0);
				isStar = false;
			} else {
				starView.setBackgroundResource(R.drawable.contact_detial_rb_fever_c);
				values.put(DatabaseHelper.ISSTAR, 1);
				isStar = true;
			}
			updateStar(values, where);
			break;
		}
	}

	private void updateStar(ContentValues values, String where) {
		dbHelper.update(values, where);
		dbHelper.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case EDIT_BIRTHDAY:

				break;
			}
		}
	}

}
