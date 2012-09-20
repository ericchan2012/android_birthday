package com.ds.birth;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.BirthConstants;
import com.ds.utility.ChineseCalendar;
import com.ds.utility.Lunar;
import com.ds.utility.Utility;

public class AlarmActivity extends Activity {

	DbHelper dbHelper;
	ListView list;
	AlarmCursorAdapter adapter;
	Button okBtn;
	TextView titleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_activity);
		dbHelper = DbHelper.getInstance(this);
		dbHelper.open(this);
		initViews();
	}

	private void initViews() {
		list = (ListView) findViewById(R.id.list);
		titleView = (TextView) findViewById(R.id.title);
		okBtn = (Button) findViewById(R.id.btn_ok);
		okBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlarmActivity.this.finish();
			}
		});
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
				+ (solarMonth + 1) + " solarDay:" + solarDay);
		Log.i("AlarmDialog", "lunar:" + lunar);

		String solarwhere = " (month = " + (solarMonth + 1) + " and day = "
				+ solarDay + " and islunar = 0)";
		String lunarwhere = " (month = " + lunarStr[1] + " and day = "
				+ lunarStr[2] + " and islunar = 1)";
		titleView.setText("" + solarYear + "-" + (solarMonth + 1) + "-"
				+ solarDay + "(" + Lunar.getChineseMonth(Integer.parseInt(lunarStr[1]))
				+ getResources().getString(R.string.month)
				+ Lunar.getChinaDayString(Integer.parseInt(lunarStr[2]))+")");
		Cursor mCursor = dbHelper.queryAlarm((solarwhere) + " or "
				+ (lunarwhere));
		adapter = new AlarmCursorAdapter(this, R.layout.alarm_item, mCursor);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Cursor cursor = (Cursor) adapter.getItemAtPosition(position);
				int birthId = cursor.getInt(DatabaseHelper.ID_INDEX);
				Intent intent = new Intent(BirthConstants.ACTION_VIEW_BIRTH);
				Bundle extras = new Bundle();
				extras.putInt(BirthConstants.ID, birthId);
				intent.putExtras(extras);
				startActivity(intent);

			}
		});
	}

	class AlarmCursorAdapter extends CursorAdapter {
		Context context = null;
		int viewResId;
		LayoutInflater mInflater;

		public AlarmCursorAdapter(Context context, int resource, Cursor cursor) {
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
			if (view != null) {
				ViewHolder viewHolder = new ViewHolder();
				View birthView = view;
				viewHolder.avater = (ImageView) birthView
						.findViewById(R.id.avatar);
				viewHolder.name = (TextView) birthView.findViewById(R.id.name);

				viewHolder.name.setText(cursor
						.getString(DatabaseHelper.NAME_INDEX));
				// avatar
				viewHolder.avater
						.setBackgroundResource(R.drawable.common_head_withbg);
			}
		}
	}

	private class ViewHolder {
		ImageView avater;
		TextView name;
	}

}
