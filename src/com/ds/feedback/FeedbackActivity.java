package com.ds.feedback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.birth.R;

public class FeedbackActivity extends Activity {

	private EditText mContactEdit = null;
	private EditText mContentEdit = null;
	private Button mLeftBtn = null;
	private Button mRightBtn = null;
	private Button mSubmitBtn = null;
	private TextView titleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		initView();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void initView() {
		mContactEdit = (EditText) findViewById(R.id.feedback_contact_edit);
		mContentEdit = (EditText) findViewById(R.id.feedback_content_edit);
		titleView = (TextView)findViewById(R.id.title);
		mLeftBtn = (Button) findViewById(R.id.backBtn);
		mRightBtn = (Button) findViewById(R.id.rightBtn);
		mRightBtn.setVisibility(View.VISIBLE);
		mLeftBtn.setVisibility(View.VISIBLE);
		mRightBtn.setBackgroundResource(R.drawable.icon_rm_list);
		mContentEdit.requestFocus();
		titleView.setText(R.string.feedback);
		mLeftBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		mRightBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(FeedbackActivity.this,
						FeedbackRecordActivity.class);
				startActivity(intent);
			}
		});

		mSubmitBtn = (Button) findViewById(R.id.submit_button);
		mSubmitBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String content = mContentEdit.getText().toString().trim();
				String contact = mContactEdit.getText().toString().trim();
				if (content.equals("")) {
					Toast.makeText(FeedbackActivity.this,
							R.string.request_content, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				SendFeedbackTask task = new SendFeedbackTask(
						FeedbackActivity.this, content, contact);
				task.execute("");

			}
		});
	}

	private class SendFeedbackTask extends AsyncTask<Object, Object, Object> {

		private Context mContext = null;
		private String mContact = "";
		private String mContent = "";
		private ProgressDialog mProgDialog = null;

		public SendFeedbackTask(Context context, String content, String contact) {
			mContext = context;
			mContent = content;
			mContact = contact;
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			return Integer.valueOf(new FeedbackAction(mContext)
					.sendFeedbackMessage(mContent, mContact));
		}

		@Override
		protected void onPostExecute(Object result) {
			if (mProgDialog != null) {
				mProgDialog.dismiss();
			}
			int resultCode = ((Integer) result).intValue();
			if (resultCode == 0) {
				Toast.makeText(FeedbackActivity.this,
						R.string.feedback_success, Toast.LENGTH_SHORT);
				// FeedbackActivity.this.finish();
			} else {
				Toast.makeText(FeedbackActivity.this, R.string.feedback_failed,
						Toast.LENGTH_SHORT);
			}
			return;
		}

		@Override
		protected void onPreExecute() {
			mProgDialog = new ProgressDialog(FeedbackActivity.this);
			mProgDialog.setMessage(FeedbackActivity.this
					.getString(R.string.waiting));
			mProgDialog.setCancelable(false);
			mProgDialog.show();
		}

	}
}
