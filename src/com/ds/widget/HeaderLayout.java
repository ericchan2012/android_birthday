package com.ds.widget;


import com.ds.birth.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class HeaderLayout extends LinearLayout{
	LayoutInflater mInflater;
	public HeaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.person_mine, this);
		initViews();
	}

	
	private void initViews(){
		
	}
}
