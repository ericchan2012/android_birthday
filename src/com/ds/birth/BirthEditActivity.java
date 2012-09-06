package com.ds.birth;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ds.iphone.BirthDialogBuilder;
import com.ds.utility.BirthConstants;

public class BirthEditActivity extends Activity implements OnClickListener {
	private static final String TAG = "BirthEditActivity";
	EditText nameEdit;
	Button topLeftBtn;
	TextView genderText;
	TextView ringText;
	String[] mGenderItems = null;
	int mSingleChoiceID = -1;
	private static final int MODE_EDIT = 1;
	private static final int MODE_ADD = 0;
	int mMode = 0;
	ArrayList<String> mRingList = new ArrayList<String>();
	String[] ringItems = null;
	Resources mRes;
	ImageView headerImage;
	boolean[] flags = new boolean[] { false, false, false, false };
	LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_add);
		mRes = getResources();
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ringItems = mRes.getStringArray(R.array.selectDays);
		mGenderItems = mRes.getStringArray(R.array.UMgenderList);
		initMode();
		initViews();

	}

	private void initMode() {
		Intent intent = getIntent();
		if (intent.getAction().equals(BirthConstants.ACTION_ADD_BIRTH)) {
			mMode = MODE_ADD;
		} else {
			mMode = MODE_EDIT;
		}
	}

	private void initViews() {
		topLeftBtn = (Button) findViewById(R.id.top_left_btton);
		nameEdit = (EditText) findViewById(R.id.name);
		genderText = (TextView) findViewById(R.id.tv_gender);
		ringText = (TextView) findViewById(R.id.tv_ringtype);
		headerImage = (ImageView) findViewById(R.id.img_icon);
		topLeftBtn.setOnClickListener(this);
		genderText.setOnClickListener(this);
		ringText.setOnClickListener(this);
		headerImage.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_left_btton:
			finish();
			break;
		case R.id.tv_gender:
			int defaultSelect = 0;
			if (genderText.getText().equals(mGenderItems[0])) {
				defaultSelect = 0;
			} else {
				defaultSelect = 1;
			}
			createOptionDialog(R.string.gender_title, mGenderItems,
					R.id.tv_gender, defaultSelect);
			break;
		case R.id.tv_ringtype:
			createCheckedDialog(R.string.ring_title, flags);
			break;
		case R.id.img_icon:
			loadMenu();
			break;
		}
	}

	private void loadMenu() {
		Dialog dialog = new MyDialog(BirthEditActivity.this, R.style.MyDialog);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		}
		return true;
	}

	private void createOptionDialog(int title, final String[] items,
			final int id, int defaultSelect) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				BirthEditActivity.this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setSingleChoiceItems(items, defaultSelect,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mSingleChoiceID = whichButton;
					}
				});

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Log.i(TAG, "---mSingleChoiceID====" + mSingleChoiceID);
				switch (id) {
				case R.id.tv_gender:
					genderText.setText(items[mSingleChoiceID]);
					break;
				}
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void createCheckedDialog(int title, final boolean[] flags) {
		Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setMultiChoiceItems(R.array.selectDays, flags,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						flags[which] = isChecked;
						if (flags[which]) {
							mRingList.add(ringItems[which]);
						} else {
							mRingList.remove(ringItems[which]);
						}
					}
				});

		// 添加一个确定按钮
		builder.setPositiveButton(" 确 定 ",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ringText.setText(mRingList.toString());
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	class MyDialog extends Dialog {

		private Context mCon;
		private Button takePicBtn;
		private Button gallaryBtn;
		private Button defaultBtn;
		private Button cancelBtn;

		public MyDialog(Context context) {
			super(context);
			this.mCon = context;
		}

		public MyDialog(Context context, int theme) {
			super(context, theme);
			this.mCon = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.birth_menu);
			initDialog();
		}

		private void initDialog() {
			takePicBtn = (Button) findViewById(R.id.takepic_button);
			gallaryBtn = (Button) findViewById(R.id.gallery_button);
			defaultBtn = (Button) findViewById(R.id.default_button);
			cancelBtn = (Button) findViewById(R.id.cancel_button);
			takePicBtn
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			gallaryBtn
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			defaultBtn
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			cancelBtn
					.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MyDialog.this.dismiss();
						}
					});

		}
	}
}
