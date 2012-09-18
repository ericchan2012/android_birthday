package com.ds.birth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.birth.AsyncImageLoader.ImageCallback;
import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.Renren;
import com.ds.utility.RenrenUtil;

public class RenrenFriendsActivity extends Activity {
	private static final String TAG = "RenrenFriendsActivity";
	String strResult;
	List<Renren> renrenList;
	List<Renren> renrenInfoList;
	HttpClient httpClient;
	ProgressDialog pDialog;
	ListView friendsListView;
	FriendsAdapater adapter = null;
	Button backBtn;
	Button selectAll;
	TextView title;
	private static Map<Integer, Boolean> isSelected;
	private static Map<String, Boolean> isIdSelected;
	List<BasicNameValuePair> paramList;
	Button importBtn;
	int checkNum;
	boolean isSelectAll = false;
	TextView empty;
	ProgressDialog importDialog;
	boolean isImporting = true;
	private static final int STATE_FRIENDS = 0;
	private static final int STATE_INFO = 1;

	private static final int GET_FRIENDS_SUCCESS = 0;
	private static final int GET_FRIENDS_FAILURE = 1;
	private static final int IMPORT_SUCCESS = 2;
	private static final int IMPORT_FAILURE = 3;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case GET_FRIENDS_SUCCESS:
				adapter = new FriendsAdapater();
				if (adapter.getCount() == 0) {
					adapter = null;
					friendsListView.setAdapter(adapter);
					empty.setVisibility(View.VISIBLE);
					friendsListView.setEmptyView(empty);
				} else {
					friendsListView.setAdapter(adapter);
				}
				break;
			case GET_FRIENDS_FAILURE:
				Toast.makeText(RenrenFriendsActivity.this, "error",
						Toast.LENGTH_SHORT).show();
				friendsListView.setAdapter(null);
				break;
			case IMPORT_SUCCESS:
				importDialog.dismiss();
				Toast.makeText(RenrenFriendsActivity.this,
						R.string.import_complete, Toast.LENGTH_SHORT).show();
				break;
			case IMPORT_FAILURE:
				importDialog.dismiss();
				Toast.makeText(RenrenFriendsActivity.this,
						R.string.import_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private DbHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renren_friend);
		friendsListView = (ListView) findViewById(R.id.friendsList);
		friendsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		dbHelper = DbHelper.getInstance(this);
		dbHelper.open(this);
		initViews();
		FriendsTask task = new FriendsTask(STATE_FRIENDS);
		task.execute();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		dbHelper.close();
	}

	private void dataChanged() {
		adapter.notifyDataSetChanged();
		importBtn.setText("导入（" + checkNum + ")项");
	}

	private void initImportDialog() {
		importDialog = new ProgressDialog(this);
		importDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		importDialog.setTitle("导入人人网好友");
		importDialog.setIcon(R.drawable.ic_launcher);
		importDialog.setButton(getResources().getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						isImporting = false;
						dialog.dismiss();
					}
				});
		importDialog.setIndeterminate(false);
		importDialog.setCancelable(false);
		importDialog.show();
	}

	private void initViews() {
		empty = (TextView) findViewById(R.id.emptyview);
		importBtn = (Button) findViewById(R.id.renrenimport);
		importBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				initImportDialog();
				FriendsTask taskInfo = new FriendsTask(STATE_INFO);
				taskInfo.execute();
			}

		});
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title);
		selectAll = (Button) findViewById(R.id.rightBtn);
		title.setText(R.string.importRenren);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		selectAll.setText(R.string.select_all);
		selectAll.setVisibility(View.VISIBLE);
		selectAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!isSelectAll) {
					for (int i = 0; i < renrenList.size(); i++) {
						isSelected.put(i, true);
						isIdSelected.put(renrenList.get(i).getId(), true);
					}
					checkNum = renrenList.size();
					dataChanged();
					isSelectAll = true;
					selectAll.setText(R.string.deselect_all);
				} else {
					for (int i = 0; i < renrenList.size(); i++) {
						if (isSelected.get(i)) {
							isSelected.put(i, false);
							isIdSelected.put(renrenList.get(i).getId(), false);
							checkNum--;// 数量减1
						}
					}
					dataChanged();
					isSelectAll = false;
					selectAll.setText(R.string.select_all);
				}
			}
		});
		friendsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Log.i(TAG, "postion:" + position);
				CheckBox cb = (CheckBox) view.findViewById(R.id.check);
				cb.toggle();
				isSelected.put(position, cb.isChecked());
				isIdSelected.put((String) view.getTag(), cb.isChecked());
				if (cb.isChecked() == true) {
					checkNum++;
				} else {
					checkNum--;
				}
				if (checkNum == renrenList.size()) {
					isSelectAll = true;
					selectAll.setText(R.string.deselect_all);
				} else {
					isSelectAll = false;
					selectAll.setText(R.string.select_all);
				}
				importBtn.setText("导入(" + checkNum + ")项");
			}

		});
	}

	private String sendRequest(int state) {
		String returnValue = "0";
		httpClient = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost(
					"http://api.renren.com/restserver.do");
			httpPost.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));// 添加请求参数到请求对象

			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) { // 为200表示执行成功
				strResult = EntityUtils.toString(httpResponse.getEntity()); // 得到返回数据（为JSON数据）
				Log.i("RenrenFriendsActivity", "strResult:" + strResult);
				if (!strResult.contains("error_code")) {
					if (state == STATE_FRIENDS) {
						renrenList = RenrenUtil.parseRenrenFromJson(strResult); // 解析JSON数据为相应对象
					} else {
						renrenInfoList = RenrenUtil
								.parseRenrenInfoFromJson(strResult);

					}
					returnValue = "1"; // 定义返回标志
				}
			}
		} catch (ClientProtocolException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		}
		return returnValue;
	}

	/**
	 * 组装参数
	 */
	private void getParams(int state) {
		List<String> param = new ArrayList<String>();
		String method = "";
		StringBuilder sb = new StringBuilder();
		switch (state) {
		case STATE_INFO:
			method = "users.getInfo";
			for (int i = 0; i < renrenList.size(); i++) {
				if (isIdSelected.get(renrenList.get(i).getId())) {
					sb.append(renrenList.get(i).getId() + ",");
				}
			}
			Log.i(TAG, "before param :" + sb.toString());
			break;
		case STATE_FRIENDS:
			method = "friends.getFriends";
			break;
		}

		// int count = 30; // 得到用户数30个

		param.add("method=" + method);
		param.add("v=1.0"); // 版本固定参数
		param.add("access_token=" + RenrenUtil.access_token); // RenrenUtil.access_token//
																// 是在LoginActivity中已经保存的数据
		param.add("format=JSON"); // 返回JSON数据
		if (state == STATE_INFO) {
			param.add("uids=" + sb.toString());
			param.add("fields="
					+ "uid,name,sex,star,zidou,vip,birthday,tinyurl,headurl,mainurl,hometown_location,work_history,university_history");
		}

		String signature = getSignature(param,
				"149820d495794bac974d6e54606f9a89"); // 第二个参数为 Secret Key
		paramList = new ArrayList<BasicNameValuePair>();
		paramList.add(new BasicNameValuePair("sig", signature)); // 签名
		paramList.add(new BasicNameValuePair("method", method));
		paramList.add(new BasicNameValuePair("v", "1.0"));
		paramList.add(new BasicNameValuePair("access_token",
				RenrenUtil.access_token));
		paramList.add(new BasicNameValuePair("format", "JSON"));
		if (state == STATE_INFO) {
			paramList.add(new BasicNameValuePair("uids", sb.toString()));
			paramList
					.add(new BasicNameValuePair(
							"fields",
							"uid,name,sex,star,zidou,vip,birthday,tinyurl,headurl,mainurl,hometown_location,work_history,university_history"));
		}
	}

	/**
	 * 得到MD5签名
	 * 
	 * @param paramList
	 * @param secret
	 * @return
	 */
	public String getSignature(List<String> paramList, String secret) {
		Collections.sort(paramList);
		StringBuffer buffer = new StringBuffer();
		for (String param : paramList) {
			buffer.append(param); // 将参数键值对，以字典序升序排列后，拼接在一起
		}
		buffer.append(secret); // 符串末尾追加上应用的Secret Key
		try {// 下面是将拼好的字符串转成MD5值，然后返回
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			StringBuffer result = new StringBuffer();
			try {
				for (byte b : md.digest(buffer.toString().getBytes("utf-8"))) {
					result.append(Integer.toHexString((b & 0xf0) >>> 4));
					result.append(Integer.toHexString(b & 0x0f));
				}
			} catch (UnsupportedEncodingException e) {
				for (byte b : md.digest(buffer.toString().getBytes())) {
					result.append(Integer.toHexString((b & 0xf0) >>> 4));
					result.append(Integer.toHexString(b & 0x0f));
				}
			}
			return result.toString();
		} catch (java.security.NoSuchAlgorithmException ex) {
		}
		return null;
	}

	class FriendsAdapater extends BaseAdapter {
		private AsyncImageLoader asyncImageLoader = new AsyncImageLoader(); // 异步获取图片
		TextView userName;
		ImageView userImage;
		CheckBox userCheck;

		public FriendsAdapater() {
			isSelected = new HashMap<Integer, Boolean>();
			isIdSelected = new HashMap<String, Boolean>();
			init();
		}

		private void init() {
			for (int i = 0; i < getCount(); i++) {
				isSelected.put(i, false);
			}
			for (int i = 0; i < getCount(); i++) {
				isIdSelected.put(renrenList.get(i).getId(), false);
			}
		}

		public int getCount() {
			return renrenList == null ? 0 : renrenList.size();
		}

		public Object getItem(int position) {
			return renrenList == null ? null : renrenList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.renren_friend_item, null);
			userName = (TextView) convertView.findViewById(R.id.username);
			userImage = (ImageView) convertView.findViewById(R.id.userimage);
			userCheck = (CheckBox) convertView.findViewById(R.id.check);
			Renren renren = renrenList.get(position);
			if (renren != null) {
				convertView.setTag(renren.getId());
				userImage.setTag(renren.getHeadurl());
				userName.setText(renren.getName());

				// 异步加载图片并显示
				Drawable cachedImage = asyncImageLoader.loadDrawable(renren,
						new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl) {
								ImageView imageView = (ImageView) friendsListView
										.findViewWithTag(imageUrl);
								if (imageView != null) {
									imageView.setImageDrawable(imageDrawable);
								}
							}
						});

				if (cachedImage != null) {
					userImage.setImageDrawable(cachedImage);
				} else {// 如果没有图片，就以一个载入的图片代替显示
					userImage.setImageResource(R.drawable.avatar_default);
				}
				userCheck.setChecked(isSelected.get(position));
			}
			return convertView;
		}
	}

	class FriendsTask extends AsyncTask<String, Integer, String> {
		int mState;

		public FriendsTask(int state) {
			mState = state;
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("FriendsTask", "----doInBackground----");
			if (mState == STATE_FRIENDS) {
				getParams(STATE_FRIENDS);
				return sendRequest(STATE_FRIENDS);
			} else {
				getParams(STATE_INFO);
				String result = sendRequest(STATE_INFO);
				importDialog.setMax(renrenInfoList.size());
				ContentValues contentValues = new ContentValues();
				if (result == "1") {
					for (int i = 0; i < renrenInfoList.size(); i++) {
						// insert into db
						if (isImporting) {
							Renren renren = renrenInfoList.get(i);
							contentValues.clear();
							contentValues.put(DatabaseHelper.NAME,
									renren.getName());
							contentValues.put(DatabaseHelper.SEX,
									renren.getSex());
							contentValues.put(DatabaseHelper.AVATAR,
									renren.getHeadurl());
							contentValues.put(DatabaseHelper.ISSTAR, 0);
							contentValues.put(DatabaseHelper.BIRTHDAY,
									renren.getBirthday());
							contentValues.put(DatabaseHelper.RINGTYPE, 0);
							contentValues.put(DatabaseHelper.RINGDAY, "");
							contentValues.put(DatabaseHelper.ISLUNAR, 0);
							String[] date = renren.getBirthday().split("-");
							String year = date[0];
							String month = date[1];
							String day = date[2];
							Calendar cal = Calendar.getInstance();
							Log.i(TAG, "year:" + year + " month:" + month
									+ " day:" + day);
							if (year.equals("0000")) {
								year = String.valueOf(cal.get(Calendar.YEAR));
							}
							if (month.equals("00")) {
								month = String
										.valueOf((cal.get(Calendar.MONTH) + 1));
							}
							if (day.equals("00")) {
								day = String.valueOf(cal
										.get(Calendar.DAY_OF_MONTH));
							}
							contentValues.put(DatabaseHelper.YEAR, year);
							contentValues.put(DatabaseHelper.MONTH, month);
							contentValues.put(DatabaseHelper.DAY, day);
							contentValues.put(DatabaseHelper.TYPE, 0);
							contentValues.put(DatabaseHelper.NOTE, "");
							contentValues.put(DatabaseHelper.PHONE_NUMBER, "");
							long id = dbHelper.insert(contentValues);
							if (id > 0) {
								publishProgress(i + 1);
							}
						}
					}
				}
				return result;
			}

		}

		@Override
		protected void onCancelled() {
			Log.i("FriendsTask", "----onCancelled----");
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("FriendsTask", "----onPostExecute----");
			Log.i("FriendsTask", "result:" + result);
			if (mState == STATE_FRIENDS) {
				pDialog.dismiss();
				if (result.equals("1")) {
					mHandler.sendEmptyMessage(GET_FRIENDS_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(GET_FRIENDS_FAILURE);
				}
			} else {
				if (result.equals("1")) {
					mHandler.sendEmptyMessage(IMPORT_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(IMPORT_FAILURE);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			Log.i("FriendsTask", "----onPreExecute----");
			if (mState == STATE_FRIENDS) {
				pDialog = ProgressDialog.show(RenrenFriendsActivity.this,
						"Importing", "importing...");
			} else {
				importDialog.setProgress(0);
				// importDialog.setMessage("0/0");
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.i("FriendsTask", "---onProgressUpdate----");
			if (mState == STATE_INFO) {
				importDialog.setProgress(values[0]);
				// importDialog.setMessage("" + values[0] + "/"
				// + renrenInfoList.size());
			}
		}
	}
}
