package com.ds.birth;

import com.ds.widget.HeaderLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MineActivity extends Activity {
	HeaderLayout mineLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_mine);
		initViews();
	}

	private void initViews() {
		mineLayout = (HeaderLayout) findViewById(R.id.myLayout);
		mineLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// add my reference or view it
			}
		});
	}

}
