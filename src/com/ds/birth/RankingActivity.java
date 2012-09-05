package com.ds.birth;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class RankingActivity extends Activity {

	RadioButton leftRadioBtn;
	RadioButton rightRadioBtn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.rank_inc_top);
//		initViews();
	}

	private void initViews() {

		leftRadioBtn = (RadioButton) findViewById(R.id.top_left_radio);
		rightRadioBtn = (RadioButton) findViewById(R.id.top_right_radio);
		// leftRadioBtn.setText(R.string.brand);
		// rightRadioBtn.setText(R.string.shopping_mall);

	}

}
