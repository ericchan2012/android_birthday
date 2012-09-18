package com.ds.feedback;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ds.birth.R;

public class FeedbackRecordActivity extends ListActivity {

	private ListView mListView = null;

	private ListAdapter mAdapter = null;

	private Button mRightBtn = null;
	
	private TextView mTitle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_feedback);
		initTitle();
		initAdapter();
		initListView();
		addListItem();
	}

	private void initTitle() {
		mRightBtn = (Button) findViewById(R.id.rightBtn);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setBackgroundResource(R.drawable.icon_feedback);
		mRightBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				FeedbackRecordActivity.this.finish();
			}
		});
		
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.feedback_record);
	}

	private void addListItem() {
		ListItem item = new ListItem();
		item.mContent = getResources().getString(R.string.mock_record_first);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		item.mDateTime = sdf.format(new Date());
		mAdapter.getItems().add(item);
		
		ListItem item2 = new ListItem();
		item2.mContent = getResources().getString(R.string.mock_record_second);
		item2.mDateTime = sdf.format(new Date());
		mAdapter.getItems().add(item2);
		mAdapter.notifyDataSetChanged();
	}

	private void initListView() {
		mListView = getListView();
		mListView.setAdapter(mAdapter);
	}

	private void initAdapter() {
		mAdapter = new ListAdapter(this);
	}

}
