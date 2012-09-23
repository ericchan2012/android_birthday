package com.ds.feedback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.birth.R;
import com.ds.utility.Utility;

public class FeedbackRecordActivity extends ListActivity {

	private ListView mListView = null;

	private ListAdapter mAdapter = null;

	private Button mRightBtn = null;

	private TextView mTitle = null;
	List<Feedback> list;
	private static final String TAG="FeedbackRecordActivity";
	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_feedback);
		initTitle();
		initAdapter();
		MyFeedbackTask task = new MyFeedbackTask(FeedbackRecordActivity.this);
		task.execute(Utility.FEEDBACK_QUERY_URL);
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
		tv = (TextView)findViewById(android.R.id.empty);
	}

	private Handler mHanlder = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 1000:
				if (list == null) {
					mAdapter = null;
					initListView();
					tv.setText(R.string.no_feedback);
					mListView.setEmptyView(tv);
				} else {
					Log.i(TAG,"list.size====" + list.size());
					for (int i = 0; i < list.size(); i++) {
						addListItem(list.get(i));
					}
					mAdapter.notifyDataSetChanged();
					initListView();
				}
				break;
			case 1001:
				break;
			}
		}
	};

	private void addListItem(Feedback feedback) {
		ListItem item = new ListItem();
		item.mContent = feedback.getContent();
		item.mContact= feedback.getContact();
		mAdapter.getItems().add(item);
	}

	private void initListView() {
		mListView = getListView();
		mListView.setAdapter(mAdapter);
	}

	private void initAdapter() {
		mAdapter = new ListAdapter(this);
	}

	public boolean getServerJsonData(String url) {
		int res = 0;
		boolean result = false;
		HttpClient client = new DefaultHttpClient();
		StringBuilder str = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpRes = client.execute(httpGet);
			httpRes = client.execute(httpGet);
			res = httpRes.getStatusLine().getStatusCode();
			if (res == 200) {
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(httpRes.getEntity().getContent()));
				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					str.append(s);
				}
				Log.i(TAG,"str:"+str.toString());
				if (str.toString().equals("2")) {
					result = true;
					list = null;
				} else {
					Log.i(TAG,"--parse json---");
					list = parseFeedbackFromJson(str.toString());
					result = true;
				}
			} else {
				result = false;
			}
		} catch (Exception e) {
		}
		return result;
	}

	public List<Feedback> parseFeedbackFromJson(String str) {
		Log.i(TAG,"parse feedback start");
		List<Feedback> list = new ArrayList<Feedback>();
		try {
//			JSONObject obj = new JSONObject(str);
			Log.i(TAG,"json before");
			JSONArray jsonArray = new JSONArray(str);
			Log.i(TAG,"json after");
			int length = jsonArray.length();
			Log.i(TAG,"lenght:" + length);
			for (int i = 0; i < length; ++i) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Feedback renren = new Feedback();
				renren.setContact(jsonObject.getString("contact"));
				renren.setContent(jsonObject.getString("content"));
				Log.i(TAG,"content:" + jsonObject.getString("content"));
				list.add(renren);
			}
			return list;
		} catch (JSONException e) {
		}
		return null;
	}

	private class MyFeedbackTask extends AsyncTask<Object, Object, Object> {

		private ProgressDialog mProgDialog = null;
		private Context mContext;

		public MyFeedbackTask(Context context) {
			mContext = context;
		}

		@Override
		protected Object doInBackground(Object... param) {
			Log.i("Feedback", "---doInBackground----");
			String res = "0";
			String url = (String) param[0];
//			try {
//				String str = Utility.getJSONData(url);
//				Log.i(TAG,"json str:" +str);
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			boolean isSuccess = getServerJsonData(url);
			if (isSuccess) {
				res = "1";
			} else {
				res = "0";
			}
			return res;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (mProgDialog != null) {
				mProgDialog.dismiss();
			}
			int resultCode = Integer.parseInt((String) result);
			if (resultCode == 1) {
				mHanlder.sendEmptyMessage(1000);
			} else {
				showToast(R.string.feedback_failed);
				mHanlder.sendEmptyMessage(1001);
			}
			return;
		}

		@Override
		protected void onPreExecute() {
			mProgDialog = new ProgressDialog(FeedbackRecordActivity.this);
			mProgDialog.setMessage(FeedbackRecordActivity.this
					.getString(R.string.waiting));
			mProgDialog.setCancelable(false);
			mProgDialog.show();
		}

	}

	private void showToast(int resid) {
		Toast.makeText(FeedbackRecordActivity.this, resid, Toast.LENGTH_SHORT)
				.show();
	}

	private class Feedback {
		String contact;
		String content;

		public String getContact() {
			return contact;
		}

		public void setContact(String contact) {
			this.contact = contact;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}
}
