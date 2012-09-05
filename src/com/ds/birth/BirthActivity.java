package com.ds.birth;

import com.ds.utility.BirthConstants;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BirthActivity extends Activity {
	private static final String TAG = "BirthActivity";
	RadioButton leftRadioBtn;
	RadioButton rightRadioBtn;
	Button topRightBtn;
	ListView mListView;
	TextView mEmptyView;

	BackgroundQueryHandler mQueryHandler;
	CursorAdapter mBirthAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_list_layout);
		initViews();
	}

	private void initViews() {

		leftRadioBtn = (RadioButton) findViewById(R.id.top_left_radio);
		rightRadioBtn = (RadioButton) findViewById(R.id.top_right_radio);
		leftRadioBtn.setText(R.string.star_birth);
		rightRadioBtn.setText(R.string.all_birth);

		mListView = (ListView) findViewById(R.id.listview);
		mEmptyView = (TextView) findViewById(R.id.emptyview);
		topRightBtn = (Button) findViewById(R.id.top_right_button);
		topRightBtn.setVisibility(View.VISIBLE);
		topRightBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// add birthday
				Intent intent = new Intent(BirthConstants.ACTION_ADD_BIRTH);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		startBackgroundQuery();
	}

	private void startBackgroundQuery() {
		// mQueryHandler.startQuery(token, cookie, uri, projection, selection,
		// selectionArgs, sortBy);
	}

	private class BackgroundQueryHandler extends AsyncQueryHandler {

		public BackgroundQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (null == cursor && cursor.getCount() == 0) {
				mEmptyView.setText(R.string.empty);
			} else {
				mBirthAdapter = new BirthCursorAdapter(BirthActivity.this,
						R.layout.birth_list_item, cursor);
				mListView.setAdapter(mBirthAdapter);
			}

		}
	};

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
				viewHolder.avater = (ImageView)birthView.findViewById(R.id.avatar);
			}
		}
	}
	
	private class ViewHolder{
		ImageView avater;
		TextView name;
	}
}
