package com.ds.birth;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ds.iphone.BirthDialogBuilder;

public class MoreActivity extends Activity implements OnClickListener {

	TextView title;
	LinearLayout setting;
	LinearLayout feedback;
	LinearLayout appstore;
	LinearLayout update;
	LinearLayout invite;
	LinearLayout about;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_more);
		initViews();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.more);
		setting = (LinearLayout) findViewById(R.id.setting);
		feedback = (LinearLayout) findViewById(R.id.feedback);
		appstore = (LinearLayout) findViewById(R.id.appstore);
		update = (LinearLayout) findViewById(R.id.update);
		invite = (LinearLayout) findViewById(R.id.invite);
		about = (LinearLayout) findViewById(R.id.about);
		setting.setOnClickListener(this);
		feedback.setOnClickListener(this);
		appstore.setOnClickListener(this);
		update.setOnClickListener(this);
		invite.setOnClickListener(this);
		about.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.setting:
			break;
		case R.id.feedback:
			break;
		case R.id.appstore:
			intent.setClass(this, AppStoreActivity.class);
			startActivity(intent);
			break;
		case R.id.update:
			break;
		case R.id.invite:
			break;
		case R.id.about:
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
			break;
		}
	}

	private void showAboutDialog() {
		BirthDialogBuilder idb = new BirthDialogBuilder(this);
		idb.setTitle(R.string.about);
		idb.setMessage("测试内容");
		idb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		idb.show();
	}

}
