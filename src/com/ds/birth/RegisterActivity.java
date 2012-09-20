package com.ds.birth;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {

	private EditText passwdEt;
	private EditText phoneEt;
	private EditText validateEt;
	private EditText nicknameEt;

	private Button backBtn;
	private Button rightBtn;
	private TextView title;
	private ProgressDialog pDialog;
	private int serverId;
	public static final String SERVER_ID = "server_id";
	public static final String NAME = "name";
	private String mName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_validation);
		initViews();
	}

	private void initViews() {
		passwdEt = (EditText) findViewById(R.id.password);
		phoneEt = (EditText) findViewById(R.id.phone_number);
		validateEt = (EditText) findViewById(R.id.validation_code);
		nicknameEt = (EditText) findViewById(R.id.nickname);

		title = (TextView) findViewById(R.id.title);
		backBtn = (Button) findViewById(R.id.backBtn);
		rightBtn = (Button) findViewById(R.id.rightBtn);
		backBtn.setVisibility(View.VISIBLE);
		rightBtn.setVisibility(View.VISIBLE);
		title.setText(R.string.register);
		rightBtn.setText(R.string.submit);
		backBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.rightBtn:
			register();
			break;
		}
	}

	private void register() {
		String phone = phoneEt.getText().toString();
		String valCode = validateEt.getText().toString();
		String passwd = passwdEt.getText().toString();
		String nickname = nicknameEt.getText().toString();

		if (phone.length() == 0) {
			showToast(R.string.correct_phone);
		} else if (valCode.length() == 0) {
			showToast(R.string.val_code);
		} else if (passwd.length() < 6 || passwd.length() > 16) {
			showToast(R.string.password_hint);
		} else {
			mName = nickname;
			checkRegister(phone, nickname, passwd);
		}
	}

	private void checkRegister(String phone, String name, String passwd) {
		int id = -1;
		pDialog = ProgressDialog.show(this,
				getResources().getString(R.string.login_title), getResources()
						.getString(R.string.login_message));
		pDialog.show();
		// connect the server to check the account,
		String url = "http://10.1.1.121:80/birthday/register.php?name=" + name
				+ "&passwd=" + passwd + "&number=" + phone;
		id = serverCheckRegister(url);
		if (id > 0) {
			pDialog.dismiss();
			Intent data = new Intent();
			Bundle extras = new Bundle();
			extras.putString(NAME, mName);
			extras.putInt(SERVER_ID, serverId);
			data.putExtras(extras);
			setResult(Activity.RESULT_OK, data);
			showToast(R.string.register_success);
			finish();
		} else if (id == 0) {
			showErrorDialog(R.string.error_title,
					R.string.error_already_register);
		} else {
			showErrorDialog(R.string.error_title,
					R.string.error_register_message);
		}
	}

	private void showErrorDialog(int title, int message) {
		Dialog dialog = new AlertDialog.Builder(RegisterActivity.this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	public int serverCheckRegister(String url) {
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

				if (Integer.parseInt(str.toString()) > 0) {
					serverId = Integer.parseInt(str.toString());
					result = serverId;
				} else if (Integer.parseInt(str.toString()) == 0) {
					result = 0;
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

	private void showToast(int strId) {
		Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();
	}
}
