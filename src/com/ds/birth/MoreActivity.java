package com.ds.birth;

import com.ds.iphone.BirthDialogBuilder;

import android.app.Activity;
import android.content.DialogInterface;
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

public class MoreActivity extends Activity implements OnItemClickListener {
	private String[] datas = null;

	ListView mineListView;
	TextView title;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_more);
		datas = getResources().getStringArray(R.array.set);
		initViews();
	}

	private void initViews() {
		mineListView = (ListView) findViewById(R.id.phoneMyMainListView);
		mineListView.setAdapter(mAdapter);
		title = (TextView) findViewById(R.id.module_title_text_view);
		title.setText(R.string.more);
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
					R.layout.more_adapter, null);
			TextView title = (TextView) retval
					.findViewById(R.id.phoneMyMainText);
			title.setText(datas[position]);
			if(position == 0){
				retval.setBackgroundResource(R.drawable.moreitem_bg_top);
			}else if(position == (datas.length-1)){
				retval.setBackgroundResource(R.drawable.moreitem_bg_bottom);
				
			}else {
				retval.setBackgroundResource(R.drawable.moreitem_bg_middle);
			}
			return retval;
		}

	};

	
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		switch (position) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			Intent intent = new Intent(this, AppStoreActivity.class);
			startActivity(intent);
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			showAboutDialog();
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
