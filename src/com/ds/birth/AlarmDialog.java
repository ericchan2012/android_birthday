package com.ds.birth;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ds.db.DbHelper;
import com.ds.utility.ChineseCalendar;

public class AlarmDialog extends Activity {
	Button okBtn;
	Button delayBtn;
	TextView content;
	DbHelper dbHelper;
	int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.alarm_dialog);
		dbHelper = DbHelper.getInstance(this);
		dbHelper.open(this);
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Calendar cal = Calendar.getInstance();
		int solarYear = cal.get(Calendar.YEAR);
		int solarMonth = cal.get(Calendar.MONTH);
		int solarDay = cal.get(Calendar.DAY_OF_MONTH);
		String lunar = ChineseCalendar.sCalendarSolarToLundar(solarYear,
				(solarMonth + 1), solarDay);
		String[] lunarStr = lunar.split("-");
		Log.i("AlarmDialog", "solarYear:" + solarYear + " solarMonth:"
				+ (solarMonth+1) + " solarDay:" + solarDay);
		Log.i("AlarmDialog", "lunar:" + lunar);
		String solarwhere = " month = " + solarMonth +" and day = " + solarDay + " and islunar = 0";
		String lunarwhere = " month = " + lunarStr[1] +" and day = " + lunarStr[2] + " and islunar = 1";
		Cursor mCursor = dbHelper.queryAlarm(solarwhere+" or " + lunarwhere);
		count = mCursor.getCount();
		mCursor.close();
		content.setText(String.valueOf(count));
	}

	@Override
	protected void onStop() {
		super.onStop();
		dbHelper.close();
	}

	private void initView() {
		okBtn = (Button) findViewById(R.id.btn_ok);
		delayBtn = (Button) findViewById(R.id.btn_delay);
		content = (TextView) findViewById(R.id.content);
		okBtn.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				Intent it = new Intent(AlarmDialog.this, AlarmActivity.class);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				AlarmDialog.this.startActivity(it);
				AlarmDialog.this.finish();
			}
		});
		delayBtn.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				AlarmDialog.this.finish();
			}
		});
	}

}
