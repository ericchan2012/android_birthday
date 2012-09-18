package com.ds.birth;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener {
	Button backBtn;
	TextView clientVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		initViews();
	}

	private void initViews() {
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		clientVersion = (TextView) findViewById(R.id.clientVersion);
		try {
			clientVersion.setText(getVersionName());
		} catch (Exception e) {
			Log.i("About", "error happens");
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		}
	}

	private String getVersionName() throws Exception {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		String version = packInfo.versionName;
		int versionCode = packInfo.versionCode;
		return version + versionCode;
	}
}
