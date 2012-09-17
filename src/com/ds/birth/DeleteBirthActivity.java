package com.ds.birth;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ds.birth.AsyncImageLoader.ImageCallback;
import com.ds.db.DatabaseHelper;
import com.ds.db.DbHelper;

public class DeleteBirthActivity extends Activity {
	Cursor mCursor;
	ProgressDialog mProgressDialog;
	private static final int SUCCESS = 1001;
	private static final int EMPTY = 1002;
	private TextView mEmptyView;
	private ListView mListView;
	private DbHelper mDbHelper;
	private static Map<Integer, Boolean> isSelected;
	DeleteCursorAdapter adapter;
	TextView title;
	Button selectAll;
	Button backBtn;
	int checkNum;
	Button delConfirm;
	boolean isSelectAll = false;
	int mCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_birth);
		mDbHelper = DbHelper.getInstance(this);
		mDbHelper.open(this);
		initViews();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mDbHelper.close();
	}

	private void initViews() {
		mEmptyView = (TextView) findViewById(R.id.emptyview);
		mListView = (ListView) findViewById(R.id.list);
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.delete_title);
		selectAll = (Button) findViewById(R.id.rightBtn);
		selectAll.setVisibility(View.VISIBLE);
		selectAll.setText(R.string.select_all);
		delConfirm = (Button) findViewById(R.id.delconfirm);
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		selectAll.setText(R.string.select_all);
		selectAll.setVisibility(View.VISIBLE);
		selectAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!isSelectAll) {
//					for (int i = 0; i < mCursor.getCount(); i++) {
//						isSelected.put(mCursor.getInt(DatabaserHelper.), true);
//					}
//					if(mCursor!=null && mCursor.getCount() > 0){
//						mCursor.moveToFirst();
//						do{
//							isSelected.put(mCursor.getInt(DatabaseHelper.ID_INDEX), true);
//						}while(cursor.moveToNext());
//					}
//					checkNum = mCount;
//					dataChanged();
//					isSelectAll = true;
//					selectAll.setText(R.string.deselect_all);
				} else {
					for (int i = 0; i < mCount; i++) {
						if (isSelected.get(i)) {
							isSelected.put(i, false);
							checkNum--;// 数量减1
						}
					}
					dataChanged();
					isSelectAll = false;
					selectAll.setText(R.string.select_all);
				}
			}
		});
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) view.findViewById(R.id.check);
				cb.toggle();
				isSelected.put(position, cb.isChecked());
				if (cb.isChecked() == true) {
					checkNum++;
				} else {
					checkNum--;
				}
				delConfirm.setText("删除(" + checkNum + ")项");
			}
		});
	}

	private void dataChanged() {
		adapter.notifyDataSetChanged();
		delConfirm.setText("删除（" + checkNum + ")项");
	}

	@Override
	protected void onResume() {
		super.onResume();
		startQuery();
	}

	private void startQuery() {
		mProgressDialog = ProgressDialog.show(DeleteBirthActivity.this,
				"Loading...", "Please wait...", true, false);
		Thread thread = new MyThread();
		thread.start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				mProgressDialog.dismiss();
				adapter = new DeleteCursorAdapter(DeleteBirthActivity.this,
						mCursor);
				mListView.setAdapter(adapter);
				break;

			case EMPTY:
				mProgressDialog.dismiss();
				adapter = null;
				mListView.setAdapter(adapter);
				mListView.setEmptyView(mEmptyView);
				mListView.invalidate();
				break;
			}
		}
	};

	class MyThread extends Thread {

		public MyThread() {
		}

		public void run() {
			mCursor = mDbHelper.queryAll();
			if (mCursor != null && mCursor.getCount() > 0) {
				mCount = mCursor.getCount();
				mHandler.sendEmptyMessage(SUCCESS);
			} else {
				mHandler.sendEmptyMessage(EMPTY);
			}
		}
	}

	class DeleteCursorAdapter extends CursorAdapter {
		Context context = null;
		private AsyncImageLoader asyncImageLoader = new AsyncImageLoader(); // 异步获取图片
		TextView userName;
		ImageView userImage;
		CheckBox userCheck;
		Cursor cursor;
		LayoutInflater mInflater;

		public DeleteCursorAdapter(Context context, Cursor cursor) {
			super(context, cursor);
			this.context = context;
			isSelected = new HashMap<Integer, Boolean>();
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.cursor = cursor;
			init();
		}

		private void init() {
			if(cursor!=null && cursor.getCount() > 0){
				cursor.moveToFirst();
				do{
					isSelected.put(cursor.getInt(DatabaseHelper.ID_INDEX), false);
				}while(cursor.moveToNext());
			}
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.renren_friend_item, parent,
					false);
			return view;
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {
			userName = (TextView) convertView.findViewById(R.id.username);
			userImage = (ImageView) convertView.findViewById(R.id.userimage);
			userCheck = (CheckBox) convertView.findViewById(R.id.check);
			if (cursor != null) {
				convertView.setTag(cursor.getInt(DatabaseHelper.ID_INDEX));
				userImage.setTag(cursor.getString(DatabaseHelper.AVATAR_INDEX));
				userName.setText(cursor.getString(DatabaseHelper.NAME_INDEX));
				// 异步加载图片并显示
				Drawable cachedImage = asyncImageLoader.loadDrawable(
						cursor.getInt(DatabaseHelper.ID_INDEX),
						cursor.getString(DatabaseHelper.AVATAR_INDEX),
						new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl) {
								ImageView imageView = (ImageView) mListView
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
				userCheck.setChecked(isSelected.get(cursor.getInt(DatabaseHelper.ID_INDEX)));
			}
		}
	}

}
