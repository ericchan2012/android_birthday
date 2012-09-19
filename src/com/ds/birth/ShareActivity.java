package com.ds.birth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ShareActivity extends Activity {
	Button backBtn;
	Button shareWeibo;
	Button shareSms;
	EditText message;
	TextView titleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
		initViews();
	}

	private void initViews() {
		message = (EditText) findViewById(R.id.share_message_content_et);
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText(R.string.invite);
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		shareWeibo = (Button) findViewById(R.id.share_weibo);
		shareSms = (Button) findViewById(R.id.share_sms);
		shareSms.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri smsToUri = Uri.parse("smsto:");
				Intent intent = new Intent(
						android.content.Intent.ACTION_SENDTO, smsToUri);
				intent.putExtra("sms_body", message.getText().toString());
				startActivity(intent);
			}
		});
	}
}
