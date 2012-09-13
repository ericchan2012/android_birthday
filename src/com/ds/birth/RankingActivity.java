package com.ds.birth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RankingActivity extends Activity implements OnItemClickListener {

	private static String[] datas = null;

	ListView mineListView;
	TextView title;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_rank);
		initViews();
	}

	private void initViews() {
		datas = getResources().getStringArray(R.array.rankData);
		mineListView = (ListView) findViewById(R.id.phoneMyMainListView);
		mineListView.setAdapter(mAdapter);
		title = (TextView) findViewById(R.id.module_title_text_view);
		title.setText(R.string.rank_title);
		mineListView.setOnItemClickListener(this);
	}

	private BaseAdapter mAdapter = new BaseAdapter() {

		public int getCount() {
			return datas.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.phone_adapter_my_main, null);
			TextView title = (TextView) retval
					.findViewById(R.id.phoneMyMainText);
			title.setText(datas[position]);

			return retval;
		}

	};

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch(arg2){
		case 0:
			intent.setClass(this, RenrenLoginActivity.class);
			startActivity(intent);
			break;
		}
	}

}
