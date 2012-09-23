package com.ds.birth;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SendBlessActivity extends Activity implements OnClickListener {
	EditText myEditText;
	Button sendBtn;
	Button backBtn;
	EditText msgEditText;
	Button selectContactBtn;
	private static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	private static final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	private static final int GET_CONTACT = 0;
	private static final String TAG = "SendBlessActivity";

	private android.support.v4.view.ViewPager mPager;
	private List<View> listViews;
	private ImageView cursor;
	private TextView t1, t2, t3;
	private int offset = 0;
	private int bmpW;
	private int currIndex = 0;
	private ListView mMsgListView;
	private ListAdapter listAdapter;

	private Button msg_fumu;
	private Button msg_laopo;
	private Button msg_laogong;
	private Button msg_jiaren;
	private Button msg_pengyou;
	private Button msg_zhiyou;
	private Button msg_lingdao;
	private Button msg_tongshi;
	private Button msg_tongxue;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_bless);
		initViews();
		// InitImageView();
		// InitTextView();
		// InitViewPager();
	}

	
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
		myEditText = (EditText) findViewById(R.id.phone);
		sendBtn = (Button) findViewById(R.id.rightBtn);
		sendBtn.setVisibility(View.VISIBLE);
		sendBtn.setText(R.string.send);
		sendBtn.setOnClickListener(this);
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setVisibility(View.VISIBLE);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.bless);
		msgEditText = (EditText) findViewById(R.id.message);
		selectContactBtn = (Button) findViewById(R.id.selectcontact);
		selectContactBtn.setOnClickListener(this);

		mMsgListView = (ListView) findViewById(R.id.msglistview);
//		listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_single_choice,"");
		msg_fumu = (Button) findViewById(R.id.msg_fumu);
		msg_fumu.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}

		});
		msg_laopo = (Button) findViewById(R.id.msg_laopo);
		msg_laopo.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
		msg_laogong = (Button) findViewById(R.id.msg_laogong);
		msg_laogong.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
		msg_jiaren = (Button) findViewById(R.id.msg_jiaren);
		msg_jiaren.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
		msg_pengyou = (Button) findViewById(R.id.msg_pengyou);
		msg_pengyou.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
		msg_zhiyou = (Button) findViewById(R.id.msg_zhiyou);
		msg_zhiyou.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
		msg_lingdao = (Button) findViewById(R.id.msg_lingdao);
		msg_lingdao.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
		msg_tongshi = (Button) findViewById(R.id.msg_tongshi);
		msg_tongshi.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
		msg_tongxue = (Button) findViewById(R.id.msg_tongxue);
		msg_tongxue.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.rightBtn:
			// send sms
			String phoneNumber = myEditText.getText().toString();
			String message = msgEditText.getText().toString();
			if (myEditText.getText().toString().equals("")) {
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
				myEditText.setText(phoneNum);
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

		
		public void onReceive(Context context, Intent intent) {
			// 表示对方成功收到短信
			// Toast.makeText(context, "对方接收成功", Toast.LENGTH_LONG).show();
		}
	};

	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.page1, null));
		listViews.add(mInflater.inflate(R.layout.page2, null));
		listViews.add(mInflater.inflate(R.layout.page3, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
				.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		
		public void finishUpdate(View arg0) {
		}

		
		public int getCount() {
			return mListViews.size();
		}

		
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		
		public Parcelable saveState() {
			return null;
		}

		
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;
		int two = one * 2;

		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}

}
