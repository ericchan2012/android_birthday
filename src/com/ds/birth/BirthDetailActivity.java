package com.ds.birth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.BirthConstants;
import com.ds.utility.Person;

public class BirthDetailActivity extends Activity implements OnClickListener {
	private static final String TAG = "BirthDetailActivity";
	private static final int EDIT_BIRTHDAY = 0;
	private int birthId = -1;
	private DbHelper dbHelper;
	private Cursor mCursor;

	private Button backBtn;
	private Button editBtn;
	private TextView nameTextView;
	private String mName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_detail);
		dbHelper = DbHelper.getInstance(this);
		dbHelper.open(this);
		getBirthId();
		initViews();
	}

	private void initViews() {
		backBtn = (Button) findViewById(R.id.back);
		editBtn = (Button) findViewById(R.id.edit);
		backBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);

		nameTextView = (TextView) findViewById(R.id.name);
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
		Log.i(TAG,"birthId:" + birthId);
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
						updateDetailData(person);
					} while (mCursor.moveToNext());
				}
			}
		}
	}

	private void updateDetailData(Person p) {
		nameTextView.setText(p.getName());
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.edit:
			// edit birthday
			Intent intent = new Intent(BirthConstants.ACTION_EDIT_BIRTH);
			Bundle extras = new Bundle();
			extras.putInt(BirthConstants.ID, birthId);
			intent.putExtras(extras);
			startActivityForResult(intent, EDIT_BIRTHDAY);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch(requestCode){
			case EDIT_BIRTHDAY:
				
				break;
			}
		}
	}
	
	

}
