package com.ds.birth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RankingActivity extends Activity implements OnClickListener {

	private TextView title;

	private LinearLayout renren;
	private LinearLayout kaixin;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_import);
		initViews();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.import_title);
		renren = (LinearLayout) findViewById(R.id.renren);
		kaixin = (LinearLayout) findViewById(R.id.kaixin);
		renren.setOnClickListener(this);
		kaixin.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.renren:
			intent.setClass(this, RenrenLoginActivity.class);
			break;
		case R.id.kaixin:
			intent.setClass(this, KaixinLoginActivity.class);
			break;
		}
		startActivity(intent);
	}

}
