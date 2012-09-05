package com.ds.birth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BirthEditActivity extends Activity implements OnClickListener {
	EditText nameEdit;
	Button topLeftBtn;
	final String[] mItems = { "item0", "item1", "itme2", "item3", "itme4",
			"item5", "item6" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_add);

		initViews();

	}

	private void initViews() {
		topLeftBtn = (Button) findViewById(R.id.top_left_btton);
		nameEdit = (EditText) findViewById(R.id.name);

		topLeftBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_left_btton:
			finish();
			break;
		}
	}

	private void createDialog() {
		final int mSingleChoiceID = -1;

		AlertDialog.Builder builder = new AlertDialog.Builder(
				BirthEditActivity.this);

		builder.setIcon(R.drawable.ic_launcher);

		builder.setTitle("单项选择");

		builder.setSingleChoiceItems(mItems, 0,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

//						mSingleChoiceID = whichButton;

//						showDialog("你选择的id为" + whichButton + " , "
//								+ mItems[whichButton]);

					}

				});

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

				if (mSingleChoiceID > 0) {

//					showDialog("你选择的是" + mSingleChoiceID);

				}

			}

		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

			}

		});

		builder.create().show();
	}

}
