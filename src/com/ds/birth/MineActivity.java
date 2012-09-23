package com.ds.birth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.utility.BirthConstants;
import com.ds.utility.FileUtils;
import com.ds.utility.Person;
import com.ds.utility.Utility;

/**
 * 如果登录了，则显示个人资料信息，如果没有，则不显示，只显示登录框信息。
 * 
 * @author chenxiang
 * 
 */
public class MineActivity extends Activity implements OnClickListener {
	private static final String Tag = "MineActivity";
	DbHelper dbHelper;
	public static final String LOGIN = "login.xml";
	private static final String PASSWORD = "passwd";
	private static final String NAME = "name";
	// public static final String ISAUTOLOGIN = "isautologin";
	private static final String ISREPASS = "isrepass";
	private static final String STATE = "state";
	private SharedPreferences loginPre;
	private int mState = 0;// 是否为登录状态。
	private boolean repass = false;
	// private boolean autoLogin = false;
	private String mName;
	private String mPasswd;

	private EditText mNameEdit;
	private EditText mPassEdit;
	private CheckBox mRePass;
	// private CheckBox mAutoLogin;
	private Button loginBtn;
	private Button regBtn;
	private ProgressDialog pDialog;
	private TextView titleView;
	private int isPersonal = 0;
	private static final int REQUEST_REGISTER = 1000;

	private TextView pNameView;
	private Button pEditButton;
	private Button rightBtn;
	private static final String TAG = "MineActivity";
	private LinearLayout backup;
	private LinearLayout recovery;

	private static final String SERVER_URL = "";
	private int mServerId = -1;
	URL url;

	private static final int BACKUP = 0;
	private static final int RECOVERY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_mine);
		dbHelper = DbHelper.getInstance(this);
		dbHelper.open(this);
		initViews();
	}

	private void initPersonalViews() {
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText(R.string.personal);
		pNameView = (TextView) findViewById(R.id.name);
		pEditButton = (Button) findViewById(R.id.editProfile);
		pEditButton.setOnClickListener(this);
		rightBtn = (Button) findViewById(R.id.rightBtn);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setText(R.string.quit_login);
		rightBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// getSharedPreferences(LOGIN, 0).edit()
				// .putBoolean(ISAUTOLOGIN, false).commit();
				setContentView(R.layout.birth_mine);
				isPersonal = 0;
				initViews();
				onResume();
			}
		});

		backup = (LinearLayout) findViewById(R.id.backup);
		recovery = (LinearLayout) findViewById(R.id.recovery);
		backup.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Utility.isHasSdcard()) {
					BackupRecoverTask task = new BackupRecoverTask();
					task.execute(BACKUP);
				} else {
					Toast.makeText(MineActivity.this, "No sdcard",
							Toast.LENGTH_SHORT).show();
				}
			}

		});
		recovery.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (Utility.isHasSdcard()) {
					File file = new File("/sdcard/birthday/birthday.db");
					if (!file.exists()) {
						Toast.makeText(MineActivity.this, R.string.no_db_file,
								Toast.LENGTH_SHORT).show();
						return;
					}
					BackupRecoverTask task = new BackupRecoverTask();
					task.execute(RECOVERY);
				} else {
					Toast.makeText(MineActivity.this, "No sdcard",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void download() {
		Log.i(TAG, "download mServerId:" + mServerId);
		String url = Utility.DOWNLOAD_URI + mServerId;
		int result = 0;
		// int result = downFile(url, "/sdcard/download", "birthday.db_"
		// + mServerId);
		Log.i(TAG, "result:" + result);
		SQLiteDatabase sqlitedatabase = openDatabase("/sdcard/birthday/birthday.db");
		Cursor cursor = DbHelper.queryExternal(sqlitedatabase);
		cursor.moveToFirst();
		ContentValues contentValues = new ContentValues();
		contentValues.clear();
		do {
			Person p = new Person();
			int gender = cursor.getInt(DatabaseHelper.SEX_INDEX);
			p.setGender(gender);
			int isStar = cursor.getInt(DatabaseHelper.ISSTAR_INDEX);
			p.setIsStar(isStar);
			String birthday = cursor.getString(DatabaseHelper.BIRTHDAY_INDEX);
			p.setBirthDay(birthday);
			String name = cursor.getString(DatabaseHelper.NAME_INDEX);
			p.setName(name);
			int ringtype = cursor.getInt(DatabaseHelper.RINGTYPE_INDEX);
			p.setRingtype(ringtype);
			int isLunar = cursor.getInt(DatabaseHelper.ISLUNAR_INDEX);
			p.setIsLunar(isLunar);
			String phonenumber = cursor
					.getString(DatabaseHelper.PHONE_NUMBER_INDEX);
			p.setPhoneNumber(phonenumber);
			int year = cursor.getInt(DatabaseHelper.YEAR_INDEX);
			p.setYear(year);
			int month = cursor.getInt(DatabaseHelper.MONTH_INDEX);
			p.setMonth(month);
			int day = cursor.getInt(DatabaseHelper.DAY_INDEX);
			p.setDay(day);
			generateData(contentValues, p);
		} while (cursor.moveToNext());
		// return result;
	}

	private void generateData(ContentValues contentValues, Person person) {
		contentValues.clear();
		contentValues.put(DatabaseHelper.NAME, person.getName());
		contentValues.put(DatabaseHelper.SEX, person.getGender());
		contentValues.put(DatabaseHelper.ISSTAR, person.getIsStar());
		contentValues.put(DatabaseHelper.BIRTHDAY, person.getBirthDay());
		contentValues.put(DatabaseHelper.RINGTYPE, person.getRingtype());
		contentValues.put(DatabaseHelper.ISLUNAR, person.getIsLunar());
		contentValues.put(DatabaseHelper.YEAR, person.getYear());
		contentValues.put(DatabaseHelper.MONTH, person.getMonth());
		contentValues.put(DatabaseHelper.DAY, person.getDay());
		contentValues.put(DatabaseHelper.TYPE, 0);
		contentValues.put(DatabaseHelper.PHONE_NUMBER, person.getPhoneNumber());
		contentValues.put(DatabaseHelper.AVATAR, "");
		dbHelper.insert(contentValues);
	}

	private SQLiteDatabase openDatabase(String path) {
		SQLiteDatabase database = null;
		database = SQLiteDatabase.openOrCreateDatabase(path, null);
		return database;
	}

	public int downFile(String urlStr, String path, String fileName) {
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils();

			if (fileUtils.isFileExist(path + fileName)) {
				return 1;
			} else {
				inputStream = getInputStreamFromURL(urlStr);
				File resultFile = fileUtils.write2SDFromInput(path, fileName,
						inputStream);
				if (resultFile == null) {
					return -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public InputStream getInputStreamFromURL(String urlStr) {
		HttpURLConnection urlConn = null;
		InputStream inputStream = null;
		try {
			url = new URL(urlStr);
			urlConn = (HttpURLConnection) url.openConnection();
			inputStream = urlConn.getInputStream();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return inputStream;
	}

	public void requestDownload(String url) {
		int res = 0;
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
				Log.i(Tag, "requestDownload:" + str.toString());
			} else {
				Log.i(Tag, "HttpGet Error");
			}
		} catch (Exception e) {
			Log.i(Tag, "Exception");
		}
	}

	private void backup() {
		String url = Utility.BACKUP_URI;
		String filePath = "/data/data/com.ds.birth/databases/birthday.db";
		File file = new File(filePath);
		// uploadFile(url, filePaht);
		File dir = new File("/sdcard/birthday/");
		if (!dir.exists()) {
			dir.mkdir();
		}
		File targetFile = new File("/sdcard/birthday/", "birthday.db");
		try {
			copyFile(file, targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	public void saveToSDCard(String filename, String content) throws Exception {
		// 通过getExternalStorageDirectory方法获取SDCard的文件路径
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);
		// 获取输出流
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(content.getBytes());
		outStream.close();
	}

	private void uploadFile(String uploadUrl, String srcPath) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
					+ srcPath.substring(srcPath.lastIndexOf("/") + 1)
					+ "_"
					+ mServerId + "\"" + end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(srcPath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			// 读取文件
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();

			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			dos.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
			setTitle(e.getMessage());
		}
	}

	public static void uploadFile(String murl, File file) {
		try {
			URL url = new URL(murl + "&file="
					+ URLEncoder.encode(file.getName()));
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url
					.openConnection();
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setRequestMethod("POST");
			OutputStream os = httpUrlConnection.getOutputStream();
			Thread.sleep(100);
			BufferedInputStream fis = new BufferedInputStream(
					new FileInputStream(file));

			int bufSize = 0;
			byte[] buffer = new byte[1024];
			while ((bufSize = fis.read(buffer)) != -1) {
				os.write(buffer, 0, bufSize);
			}
			fis.close();

			String responMsg = httpUrlConnection.getResponseMessage();
			Log.d(TAG, "responMsg =====" + responMsg);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isPersonal == 0) {
			loginPre = getSharedPreferences(LOGIN, 0);
			mState = loginPre.getInt(STATE, 0);
			repass = loginPre.getBoolean(ISREPASS, false);
			mName = loginPre.getString(NAME, "");
			mPasswd = loginPre.getString(PASSWORD, "");
			// autoLogin = loginPre.getBoolean(ISAUTOLOGIN, false);
			updateViews();
		}
	}

	private void updateViews() {
		// Log.i(TAG, "autoLogin:" + autoLogin);
		mNameEdit.setText(mName);
		mRePass.setChecked(repass);
		// mAutoLogin.setChecked(autoLogin);
		if (repass) {
			mNameEdit.setText(mName);
		} else {
			mNameEdit.setText("");
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		dbHelper.close();
		if (isPersonal == 0) {
			Log.i(TAG, "----onstop----");
			loginPre = getSharedPreferences(LOGIN, 0);
			Editor editor = loginPre.edit();
			editor.putInt(STATE, mState).putBoolean(ISREPASS,
					mRePass.isChecked());
			// .putBoolean(ISAUTOLOGIN, mAutoLogin.isChecked());
			if (mRePass.isChecked()) {
				editor.putString(NAME, mNameEdit.getText().toString());
			}
			// if (mAutoLogin.isChecked()) {
			// editor.putString(PASSWORD, mPassEdit.getText().toString());
			// }
			editor.commit();
		}
	}

	private void initViews() {

		mNameEdit = (EditText) findViewById(R.id.account);
		mPassEdit = (EditText) findViewById(R.id.password);
		mRePass = (CheckBox) findViewById(R.id.rememberPassword);
		mRePass.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				getSharedPreferences(LOGIN, 0).edit()
						.putBoolean(ISREPASS, isChecked).commit();
				if (!isChecked) {
					mNameEdit.setText("");
					mPassEdit.setText("");
					getSharedPreferences(LOGIN, 0).edit().putString(NAME, "")
							.commit();
				}
			}
		});
		// mAutoLogin = (CheckBox) findViewById(R.id.autologin);

		loginBtn = (Button) findViewById(R.id.loginBtn);
		regBtn = (Button) findViewById(R.id.regBtn);
		loginBtn.setOnClickListener(this);
		regBtn.setOnClickListener(this);
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText(R.string.mine_title);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			mName = mNameEdit.getText().toString();
			mPasswd = mPassEdit.getText().toString();
			if (TextUtils.isEmpty(mName)) {
				showToast(R.string.name_empty_hint);
			} else if (TextUtils.isEmpty(mPasswd)) {
				showToast(R.string.passwd_empty_hint);
			} else {
				checkLogin(mName, mPasswd);
			}
			break;
		case R.id.regBtn:
			Intent intent = new Intent(MineActivity.this,
					RegisterActivity.class);
			startActivityForResult(intent, REQUEST_REGISTER);
			break;
		case R.id.editProfile:
			Cursor c = dbHelper.queryMe();
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				int id = c.getInt(DatabaseHelper.ID_INDEX);
				Intent dintent = new Intent(BirthConstants.ACTION_VIEW_BIRTH);
				Bundle extras = new Bundle();
				extras.putInt(BirthConstants.ID, id);
				dintent.putExtras(extras);
				startActivity(dintent);
			} else {
				Intent addintent = new Intent(BirthConstants.ACTION_ADD_BIRTH);
				addintent.putExtra(BirthEditActivity.TYPE, 1);
				addintent.putExtra(BirthEditActivity.NAME, pNameView.getText()
						.toString());
				startActivity(addintent);
			}
			dbHelper.close();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_REGISTER) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				int serverId = extras.getInt(RegisterActivity.SERVER_ID);
				mServerId = serverId;
				String name = extras.getString(RegisterActivity.NAME);
				setContentView(R.layout.personal);
				isPersonal = 1;
				initPersonalViews();
				pNameView.setText(name);
			}
		}
	}

	private void showToast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	private void checkLogin(String name, String passwd) {
		// connect the server to check the account,
		String url = Utility.LOGIN_URI + "number=" + name + "&passwd=" + passwd;
		CheckLoginTask task = new CheckLoginTask();
		task.execute(url);
	}

	public boolean serverCheckLogin(String url) {
		int res = 0;
		boolean result = false;
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
				Log.i(Tag, str.toString());
				if (str.equals(1)) {
					result = true;
				} else {
					result = false;
				}
			} else {
				Log.i(Tag, "HttpGet Error");
			}
		} catch (Exception e) {
			Log.i(Tag, "Exception");
		}
		return result;
	}

	public boolean getServerJsonDataWithNoType(String url) {
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
				// String out =
				// EntityUtils.toString(httpRes.getEntity().getContent(),
				// "UTF-8");
				// StringBuilder sb = new StringBuilder()
				Log.i(Tag, "str:" + str.toString());
				if (str.toString().equals("0")) {
					result = false;
				} else {
					try {

						// JSONObject json = new
						// JSONObject(str.toString()).getJSONObject("content");
						JSONObject json = new JSONObject(str.toString().trim());
						int id = json.getInt("id");
						String name = json.getString("name");
						String passwd = json.getString("passwd");
						int gender = json.getInt("gender");
						String number = json.getString("phone");
						Log.i(Tag, "name:" + name);
						mName = name;
						mServerId = id;
					} catch (JSONException e) {
						Log.i(Tag, e.getLocalizedMessage());
						// buffer.close();
						e.printStackTrace();
					}
					result = true;
				}
			} else {
				Log.i(Tag, "HttpGet Error");
				result = false;
			}
		} catch (Exception e) {
			Log.i(Tag, "Exception");
		}
		return result;
	}

	private static final int SUCCESS = 1001;
	private static final int FAILURE = 1002;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case SUCCESS:
				// startActivity
				pDialog.dismiss();
				hideSoftKeypad();
				saveState();
				updateContentView();
				break;
			case FAILURE:
				pDialog.dismiss();
				showErrorDialog();
				break;
			}
		}
	};

	private void hideSoftKeypad() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(MineActivity.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void saveState() {
		if (isPersonal == 0) {
			Log.i(TAG, "----saveState----");
			loginPre = getSharedPreferences(LOGIN, 0);
			Editor editor = loginPre.edit();
			editor.putInt(STATE, mState).putBoolean(ISREPASS,
					mRePass.isChecked());
			// .putBoolean(ISAUTOLOGIN, mAutoLogin.isChecked());
			if (mRePass.isChecked()) {
				editor.putString(NAME, mNameEdit.getText().toString());
			} else {
				editor.putString(NAME, "");
			}
			// if (mAutoLogin.isChecked()) {
			// editor.putString(PASSWORD, mPassEdit.getText().toString());
			// }
			editor.commit();
		}
	}

	private void updateContentView() {
		// MineActivity.this.setContentView(layoutResID);
		isPersonal = 1;
		setContentView(R.layout.personal);
		initPersonalViews();
		pNameView.setText(mName);
	}

	private void showErrorDialog() {
		Dialog dialog = new AlertDialog.Builder(MineActivity.this)
				.setTitle(R.string.error_title)
				.setMessage(R.string.error_message)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	class CheckLoginTask extends AsyncTask<String, Integer, String> {
		boolean isSuccess = false;
		String result = "0";

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = ProgressDialog.show(MineActivity.this, getResources()
					.getString(R.string.login_title),
					getResources().getString(R.string.login_message));
			pDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("1")) {
				mHandler.sendEmptyMessage(SUCCESS);
			} else {
				mHandler.sendEmptyMessage(FAILURE);
			}
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			Log.i(TAG, "---doInBackground---");
			Log.i(TAG, "url:" + url);
			isSuccess = getServerJsonDataWithNoType(url);
			if (isSuccess) {
				result = "1";
			} else {
				result = "0";
			}
			return result;
		}

	}

	class BackupRecoverTask extends AsyncTask<Integer, Integer, Integer> {
		boolean isSuccess = false;
		int result;
		int state;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MineActivity.this);
			pDialog.setMessage(MineActivity.this.getString(R.string.waiting));
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (pDialog != null) {
				pDialog.dismiss();
			}
			if (state == BACKUP) {
				Toast.makeText(MineActivity.this, R.string.success,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MineActivity.this, R.string.recover_success,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			state = params[0];
			if (state == BACKUP) {
				backup();
				result = 1;
			} else {
				download();
				result = 1;
			}
			return result;
		}

	}
}
