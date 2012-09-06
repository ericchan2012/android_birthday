package com.ds.birth;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class RankingActivity extends Activity {

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

}
