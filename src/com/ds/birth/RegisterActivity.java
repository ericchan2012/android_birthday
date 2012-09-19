package com.ds.birth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity implements OnClickListener {

	private EditText passwdEt;
	private EditText phoneEt;
	private EditText validateEt;

	private Button backBtn;
	private Button rightBtn;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_validation);
		initViews();
	}

	private void initViews() {
		passwdEt = (EditText) findViewById(R.id.password);
		phoneEt = (EditText) findViewById(R.id.phone_number);
		validateEt = (EditText) findViewById(R.id.validation_code);

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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.rightBtn:
			break;
		}
	}
}
