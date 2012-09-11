package com.ds.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ds.birth.R;

public class MyEditText extends LinearLayout {

	private EditText et;
	private TextView tv;
	private LayoutInflater mInflater;

	public MyEditText(Context context) {
		this(context, null);
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.myedittext, this, true);
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		et = (EditText) findViewById(R.id.et);
		tv = (TextView) findViewById(R.id.tv);
	}

	public void setText(int resId) {
		tv.setText(resId);
	}

}
