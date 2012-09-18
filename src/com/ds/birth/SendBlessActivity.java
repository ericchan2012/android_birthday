package com.ds.birth;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.widget.MyEditText;

public class SendBlessActivity extends Activity implements OnClickListener {
	MyEditText myEditText;
	Button sendBtn;
	Button backBtn;
	EditText msgEditText;
	Button selectContactBtn;
	private static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	private static final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	private static final int GET_CONTACT = 0;
	private static final String TAG = "SendBlessActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_bless);
		initViews();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(sendMessage);
		unregisterReceiver(receiver);
	}

	protected void onResume() {
		super.onResume();
		registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));
		registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));
	}

	private void initViews() {
		myEditText = (MyEditText) findViewById(R.id.phone);
		myEditText.setText(R.string.phone);
		sendBtn = (Button) findViewById(R.id.rightBtn);
		sendBtn.setVisibility(View.VISIBLE);
		sendBtn.setText(R.string.send);
		sendBtn.setOnClickListener(this);
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setVisibility(View.VISIBLE);
		TextView title = (TextView)findViewById(R.id.title);
		title.setText(R.string.bless);
		msgEditText = (EditText) findViewById(R.id.message);
		selectContactBtn = (Button) findViewById(R.id.selectcontact);
		selectContactBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.rightBtn:
			// send sms
			String phoneNumber = myEditText.getEditText();
			String message = msgEditText.getText().toString();
			if (myEditText.getEditText().equals("")) {
				Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
			} else {
				sendSMS(phoneNumber, message);
			}
			break;
		case R.id.selectcontact:
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);

			startActivityForResult(intent, GET_CONTACT);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case GET_CONTACT: {
			if (resultCode == Activity.RESULT_OK) {

				Uri contactData = data.getData();

				Cursor c = managedQuery(contactData, null, null, null, null);
				c.moveToFirst();
				String phoneNum = this.getContactPhone(c);
				Log.i(TAG, "phoneNum:" + phoneNum);
				myEditText.setEditText(phoneNum);
			}
			break;

		}

		}
	}

	private String getContactPhone(Cursor cursor) {
		int phoneColumn = cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		int phoneNum = cursor.getInt(phoneColumn);
		String phoneResult = "";
		// System.out.print(phoneNum);
		if (phoneNum > 0) {
			// 获得联系人的ID号
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
			// 获得联系人的电话号码的cursor;
			Cursor phones = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			// int phoneCount = phones.getCount();
			// allPhoneNum = new ArrayList<String>(phoneCount);
			if (phones.moveToFirst()) {
				// 遍历所有的电话号码
				for (; !phones.isAfterLast(); phones.moveToNext()) {
					int index = phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					int typeindex = phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
					int phone_type = phones.getInt(typeindex);
					String phoneNumber = phones.getString(index);
					Log.i(TAG, "phoneNumber:" + phoneNumber);
					Log.i(TAG, "phone_type:" + phone_type);
					// switch (phone_type) {
					// case 2:
					if (phones.getCount() == 1) {
						phoneResult = phoneNumber;
					} else {
						phoneResult += phoneNumber + ",";
					}

					// break;
					// }
					// allPhoneNum.add(phoneNumber);
				}
				if (!phones.isClosed()) {
					phones.close();
				}
			}
		}
		return phoneResult;
	}

	private void sendSMS(String phoneNumber, String message) {
		// ---sends an SMS message to another device---
		String[] numberTmp = phoneNumber.split(",");
		SmsManager sms = SmsManager.getDefault();

		// create the sentIntent parameter
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,
				0);

		// create the deilverIntent parameter
		Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,
				deliverIntent, 0);

		// 如果短信内容超过70个字符 将这条短信拆成多条短信发送出去
		if (message.length() > 70) {
			ArrayList<String> msgs = sms.divideMessage(message);
			for (String msg : msgs) {
				for (String number : numberTmp) {
					sms.sendTextMessage(number, null, msg, sentPI, deliverPI);
				}
			}
		} else {
			for (String number : numberTmp) {
				sms.sendTextMessage(number, null, message, sentPI, deliverPI);
			}
		}
	}

	private BroadcastReceiver sendMessage = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 判断短信是否发送成功
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 表示对方成功收到短信
			// Toast.makeText(context, "对方接收成功", Toast.LENGTH_LONG).show();
		}
	};

}
