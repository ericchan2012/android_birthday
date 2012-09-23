package com.ds.feedback;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.birth.R;
import com.ds.utility.Utility;

public class FeedbackActivity extends Activity {

	private EditText mContactEdit = null;
	private EditText mContentEdit = null;
	private Button mLeftBtn = null;
	private Button mRightBtn = null;
	private Button mSubmitBtn = null;
	private TextView titleView;
	private static final String TAG = "FeedbackActivity";

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
		titleView = (TextView) findViewById(R.id.title);
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
					showToast(R.string.request_content);
				} else if (contact.equals("")) {
					showToast(R.string.request_contact);
				} else {
					SendFeedbackTask task = new SendFeedbackTask(
							FeedbackActivity.this);
					task.execute(contact, content);
				}
			}
		});
	}
	public int insertFeedback(String url) {
		int res = 0;
		int result = -1;
		HttpClient client = new DefaultHttpClient();
		StringBuilder str = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpRes = client.execute(httpGet);
			res = httpRes.getStatusLine().getStatusCode();
			if (res == 200) {
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(httpRes.getEntity().getContent()));
				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					str.append(s);
				}
				Log.i(TAG,"insertId:" + Integer.parseInt(str.toString().trim()));
				if (Integer.parseInt(str.toString().trim()) > 0) {
					result = Integer.parseInt(str.toString().trim());
				} else {
					result = -1;
				}
			} else {
				result = -1;
			}
		} catch (Exception e) {
		}
		return result;
	}

	private class SendFeedbackTask extends AsyncTask<Object, Object, Object> {

		private ProgressDialog mProgDialog = null;
		private Context mContext;

		public SendFeedbackTask(Context context) {
			mContext = context;
		}

		@Override
		protected Object doInBackground(Object... param) {
			Log.i("Feedback", "---doInBackground----");
			String contact = (String) param[0];
			String content = (String) param[1];
			String url = Utility.FEEDBACK_URL + "&contact=" + contact
					+ "&content=" + content;
			int isSuccess = insertFeedback(url);
			Log.i("Feedback", "isSuccess:" + isSuccess);
			return isSuccess;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (mProgDialog != null) {
				mProgDialog.dismiss();
			}
			int resultCode = (Integer)result;
			Log.i("Feedback", "resultCode:" + resultCode);
			if (resultCode > 0) {
				showToast(R.string.feedback_success);
				FeedbackActivity.this.finish();
			} else {
				showToast(R.string.feedback_failed);
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

	private void showToast(int resid) {
		Toast.makeText(FeedbackActivity.this, resid, Toast.LENGTH_SHORT).show();
	}
}
