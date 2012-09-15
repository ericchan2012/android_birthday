package com.ds.birth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ds.db.DbHelper;

/**
 * 如果登录了，则显示个人资料信息，如果没有，则不显示，只显示登录框信息。
 * 
 * @author chenxiang
 * 
 */
public class MineActivity extends Activity implements OnClickListener {
	DbHelper dbHelper;
	private static final String LOGIN = "login";
	private static final String PASSWORD = "passwd";
	private static final String NAME = "name";
	// private static final String ISAUTOLOGIN="isautologin";
	private static final String ISREPASS = "isrepass";
	private static final String STATE = "state";
	private SharedPreferences loginPre;
	private int mState = 0;// 是否为登录状态。
	private boolean repass = false;
	private String mName;
	private String mPasswd;

	private EditText mNameEdit;
	private EditText mPassEdit;
	private CheckBox mRePass;
	private Button loginBtn;
	private Button regBtn;
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_mine);
		dbHelper = DbHelper.getInstance(this);
		dbHelper.open(this);
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loginPre = getSharedPreferences(LOGIN, 0);
		mState = loginPre.getInt(STATE, 0);
		repass = loginPre.getBoolean(ISREPASS, false);
		mName = loginPre.getString(NAME, "");
		mPasswd = loginPre.getString(PASSWORD, "");
		updateViews();
	}

	private void updateViews() {
		mNameEdit.setText(mName);
		mRePass.setChecked(repass);
		if (repass) {
			mPassEdit.setText(mPasswd);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		dbHelper.close();
		loginPre = getSharedPreferences(LOGIN, 0);
		Editor editor = loginPre.edit();
		editor.putInt(STATE, mState).putBoolean(ISREPASS, mRePass.isChecked())
				.putString(NAME, mNameEdit.getText().toString());
		if (mRePass.isChecked()) {
			editor.putString(PASSWORD, mPassEdit.getText().toString());
		}
		editor.commit();
	}

	private void initViews() {

		mNameEdit = (EditText) findViewById(R.id.account);
		mPassEdit = (EditText) findViewById(R.id.password);
		mRePass = (CheckBox) findViewById(R.id.rememberPassword);

		loginBtn = (Button) findViewById(R.id.loginBtn);
		regBtn = (Button) findViewById(R.id.regBtn);
		loginBtn.setOnClickListener(this);
		regBtn.setOnClickListener(this);
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

			break;
		}
	}

	private void showToast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	private void checkLogin(String name, String passwd) {
		boolean isSuccess = false;
		pDialog = ProgressDialog.show(this,
				getResources().getString(R.string.login_title), getResources()
						.getString(R.string.login_message));
		pDialog.show();
		// connect the server to check the account,
		if (isSuccess) {
			mHandler.sendEmptyMessage(SUCCESS);
		} else {
			mHandler.sendEmptyMessage(FAILURE);
		}
	}

	private static final int SUCCESS = 1001;
	private static final int FAILURE = 1002;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case SUCCESS:
				// startActivity
				pDialog.dismiss();
				break;
			case FAILURE:
				pDialog.dismiss();
				showErrorDialog();
				break;
			}
		}
	};

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
}
