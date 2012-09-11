package com.ds.birth;

import com.ds.widget.MyEditText;

import android.app.Activity;
import android.os.Bundle;

public class SendBlessActivity extends Activity{
	MyEditText myEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_bless);
	}
	private void initViews(){
		myEditText = (MyEditText)findViewById(R.id.myedittext);
		myEditText.setText(R.string.phone);
	}
	
}
