package com.ds.birth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.birth.AsyncImageLoader.ImageCallback;
import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;
import com.ds.kaixin.BaseActivity;
import com.ds.kaixin.Constant;
import com.ds.kaixin.FriendInfo;
import com.ds.kaixin.GetFriendListTask;
import com.ds.kaixin.KaixinError;

public class KaixinFriendsActivity extends BaseActivity {
	private static final String TAG = "KaixinFriendsActivity";
	String strResult;
	ProgressDialog pDialog;
	ListView friendsListView;
	FriendsAdapater adapter = null;
	Button backBtn;
	Button selectAll;
	TextView title;
	private static Map<Integer, Boolean> isSelected;
	private static Map<String, Boolean> isIdSelected;
	List<BasicNameValuePair> paramList;
	Button importBtn;
	int checkNum;
	boolean isSelectAll = false;
	TextView empty;
	ProgressDialog importDialog;
	boolean isImporting = true;
	private static final int STATE_INFO = 1;
	HttpClient httpClient;

	private static final int IMPORT_SUCCESS = 1002;
	private static final int IMPORT_FAILURE = 1003;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			Log.i(TAG, "message.what:" + message.what);
			Log.i(TAG, "taskResultContainer:" + renrenList.size());
			switch (message.what) {
			case Constant.RESULT_FAILED_NETWORK_ERR:
				break;
			case Constant.RESULT_FAILED_JSON_PARSE_ERR:
				break;
			case Constant.RESULT_FAILED_ARG_ERR:
				break;
			case Constant.RESULT_FAILED_MALFORMEDURL_ERR:
				break;
			case Constant.RESULT_FAILED_ENCODER_ERR:
				break;
			case Constant.RESULT_FAILED:
				break;
			case Constant.RESULT_FAILED_REQUEST_ERR:
				KaixinError kaixinError = (KaixinError) message.obj;
				Toast.makeText(getApplicationContext(), kaixinError.toString(),
						Toast.LENGTH_LONG).show();
				break;
			case Constant.RESULT_GET_FRIENDS_OK:
				friendList = (ArrayList<FriendInfo>) renrenList.clone();
				adapter = new FriendsAdapater();
				friendsListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case Constant.RESULT_GET_FRIENDS_ZERO:
				empty.setVisibility(View.VISIBLE);
				adapter = null;
				friendsListView.setAdapter(adapter);
				friendsListView.setEmptyView(empty);
				break;
			case IMPORT_SUCCESS:
				importDialog.dismiss();
				Toast.makeText(KaixinFriendsActivity.this,
						R.string.import_complete, Toast.LENGTH_SHORT).show();
				break;
			case IMPORT_FAILURE:
				importDialog.dismiss();
				Toast.makeText(KaixinFriendsActivity.this,
						R.string.import_failure, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	private DbHelper dbHelper;
	ArrayList<Object> renrenList = new ArrayList<Object>();
	ArrayList<FriendInfo> friendList = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renren_friend);
		friendsListView = (ListView) findViewById(R.id.friendsList);
		friendsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		dbHelper = DbHelper.getInstance(this);
		dbHelper.open(this);
		initViews();
		GetFriendListTask getDataTask = new GetFriendListTask();
		getDataTask.execute("0", "500", kaixin, mHandler, renrenList,
				getApplicationContext());
	}

	@Override
	protected void onStop() {
		super.onStop();
		dbHelper.close();
	}

	private void dataChanged() {
		adapter.notifyDataSetChanged();
		importBtn.setText("导入（" + checkNum + ")项");
	}

	private void initImportDialog() {
		importDialog = new ProgressDialog(this);
		importDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		importDialog.setTitle("导入开心网好友");
		importDialog.setIcon(R.drawable.ic_launcher);
		importDialog.setButton(getResources().getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						isImporting = false;
						dialog.dismiss();
					}
				});
		importDialog.setIndeterminate(false);
		importDialog.setCancelable(false);
		importDialog.show();
	}

	private void initViews() {
		empty = (TextView) findViewById(R.id.emptyview);
		importBtn = (Button) findViewById(R.id.renrenimport);
		importBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				initImportDialog();
				FriendsTask taskInfo = new FriendsTask(STATE_INFO);
				taskInfo.execute();
			}

		});
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		selectAll = (Button) findViewById(R.id.rightBtn);
		backBtn.setVisibility(View.VISIBLE);
		selectAll.setVisibility(View.VISIBLE);
		title.setText(R.string.importKaxin);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		selectAll.setText(R.string.select_all);
		selectAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!isSelectAll) {
					for (int i = 0; i < renrenList.size(); i++) {
						isSelected.put(i, true);
						isIdSelected.put(
								((FriendInfo) renrenList.get(i)).getUid(), true);
					}
					checkNum = renrenList.size();
					dataChanged();
					isSelectAll = true;
					selectAll.setText(R.string.deselect_all);
				} else {
					for (int i = 0; i < renrenList.size(); i++) {
						if (isSelected.get(i)) {
							isSelected.put(i, false);
							isIdSelected.put(
									((FriendInfo) renrenList.get(i)).getUid(),
									false);
							checkNum--;// 数量减1
						}
					}
					dataChanged();
					isSelectAll = false;
					selectAll.setText(R.string.select_all);
				}
			}
		});
		friendsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Log.i(TAG, "postion:" + position);
				CheckBox cb = (CheckBox) view.findViewById(R.id.check);
				cb.toggle();
				isSelected.put(position, cb.isChecked());
				isIdSelected.put((String) view.getTag(), cb.isChecked());
				if (cb.isChecked() == true) {
					checkNum++;
				} else {
					checkNum--;
				}
				if(checkNum == renrenList.size()){
					isSelectAll = true;
					selectAll.setText(R.string.deselect_all);
				}else{
					isSelectAll = false;
					selectAll.setText(R.string.select_all);
				}
				importBtn.setText("导入(" + checkNum + ")项");
			}

		});
	}

	class FriendsAdapater extends BaseAdapter {
		private AsyncImageLoader asyncImageLoader = new AsyncImageLoader(); // 异步获取图片
		TextView userName;
		ImageView userImage;
		CheckBox userCheck;

		public FriendsAdapater() {
			isSelected = new HashMap<Integer, Boolean>();
			isIdSelected = new HashMap<String, Boolean>();
			init();
		}

		private void init() {
			for (int i = 0; i < getCount(); i++) {
				isSelected.put(i, false);
			}
			for (int i = 0; i < getCount(); i++) {
				isIdSelected.put(((FriendInfo) renrenList.get(i)).getUid(),
						false);
			}
		}

		public int getCount() {
			return renrenList == null ? 0 : renrenList.size();
		}

		public Object getItem(int position) {
			return renrenList == null ? null : renrenList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.renren_friend_item, null);
			userName = (TextView) convertView.findViewById(R.id.username);
			userImage = (ImageView) convertView.findViewById(R.id.userimage);
			userCheck = (CheckBox) convertView.findViewById(R.id.check);
			FriendInfo renren = (FriendInfo) renrenList.get(position);
			if (renren != null) {
				convertView.setTag(renren.getUid());
				userImage.setTag(renren.getLogoUrl());
				userName.setText(renren.getName());

				// 异步加载图片并显示
				Drawable cachedImage = asyncImageLoader.loadDrawable(renren,
						new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl) {
								ImageView imageView = (ImageView) friendsListView
										.findViewWithTag(imageUrl);
								if (imageView != null) {
									imageView.setImageDrawable(imageDrawable);
								}
							}
						});

				if (cachedImage != null) {
					userImage.setImageDrawable(cachedImage);
				} else {// 如果没有图片，就以一个载入的图片代替显示
					userImage.setImageResource(R.drawable.avatar_default);
				}
				userCheck.setChecked(isSelected.get(position));
			}
			return convertView;
		}
	}

	class FriendsTask extends AsyncTask<String, Integer, String> {
		int mState;

		public FriendsTask(int state) {
			mState = state;
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("FriendsTask", "----doInBackground----");
			importDialog.setMax(renrenList.size());
			ContentValues contentValues = new ContentValues();
			for (int i = 0; i < renrenList.size(); i++) {
				// insert into db
				if (isImporting) {
					FriendInfo renren = (FriendInfo) renrenList.get(i);
					contentValues.clear();
					contentValues.put(DatabaseHelper.NAME, renren.getName());
					contentValues.put(DatabaseHelper.SEX, renren.getGender());
					contentValues.put(DatabaseHelper.AVATAR,
							renren.getLogoUrl());
					contentValues.put(DatabaseHelper.ISSTAR, 0);
					contentValues.put(DatabaseHelper.BIRTHDAY,
							renren.getBirthday());
					contentValues.put(DatabaseHelper.RINGTYPE, 0);
					contentValues.put(DatabaseHelper.RINGDAY, "");
					contentValues.put(DatabaseHelper.ISLUNAR, 0);
					String birthday = renren.getBirthday();
					String year = "";
					String month = "";
					String day = "";
					Log.i(TAG, "birthday:" + birthday);
					Calendar cal = Calendar.getInstance();
					if (birthday != null && birthday != ""
							&& birthday.length() != 0) {
						if (birthday.contains(getResources().getString(
								R.string.after))) {
							int index1 = birthday.indexOf(getResources()
									.getString(R.string.after));
							int index2 = birthday.indexOf(getResources()
									.getString(R.string.month));
							int index3 = birthday.indexOf(getResources()
									.getString(R.string.day));
							year = "19" + birthday.substring(0, index1);
							month = birthday.substring(index1 + 1, index2);
							day = birthday.substring(index2 + 1, index3);
						} else if (birthday.contains(getResources().getString(
								R.string.year))) {
							int index1 = birthday.indexOf(getResources()
									.getString(R.string.year));
							int index2 = birthday.indexOf(getResources()
									.getString(R.string.month));
							int index3 = birthday.indexOf(getResources()
									.getString(R.string.day));
							year = birthday.substring(0, index1);
							month = birthday.substring(index1 + 1, index2);
							day = birthday.substring(index2 + 1, index3);
						} else {
							int index2 = birthday.indexOf(getResources()
									.getString(R.string.month));
							int index3 = birthday.indexOf(getResources()
									.getString(R.string.day));
							year = String.valueOf(cal.get(Calendar.YEAR));
							month = birthday.substring(0, index2);
							day = birthday.substring(index2 + 1, index3);
						}
						Log.i(TAG, "year:" + year + " month:" + month + " day:"
								+ day);
					} else {
						year = String.valueOf(cal.get(Calendar.YEAR));
						month = String.valueOf((cal.get(Calendar.MONTH) + 1));
						day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
					}
					contentValues.put(DatabaseHelper.YEAR, year);
					contentValues.put(DatabaseHelper.MONTH, month);
					contentValues.put(DatabaseHelper.DAY, day);
					contentValues.put(DatabaseHelper.TYPE, 0);
					contentValues.put(DatabaseHelper.NOTE, "");
					contentValues.put(DatabaseHelper.PHONE_NUMBER, "");
					contentValues.put(DatabaseHelper.ISSTAR, "1");
					long id = dbHelper.insert(contentValues);
					if (id > 0) {
						publishProgress(i + 1);
					}
				}
			}
			return "1";

		}

		@Override
		protected void onCancelled() {
			Log.i("FriendsTask", "----onCancelled----");
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("FriendsTask", "----onPostExecute----");
			Log.i("FriendsTask", "result:" + result);
			if (result.equals("1")) {
				mHandler.sendEmptyMessage(IMPORT_SUCCESS);
			} else {
				mHandler.sendEmptyMessage(IMPORT_FAILURE);
			}
		}

		@Override
		protected void onPreExecute() {
			Log.i("FriendsTask", "----onPreExecute----");
			importDialog.setProgress(0);
			// importDialog.setMessage("0/0");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.i("FriendsTask", "---onProgressUpdate----");
			if (mState == STATE_INFO) {
				importDialog.setProgress(values[0]);
				// importDialog.setMessage("" + values[0] + "/"
				// + renrenInfoList.size());
			}
		}
	}
}
