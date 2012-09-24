package com.ds.birth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ds.feedback.FeedbackActivity;
import com.ds.iphone.BirthDialogBuilder;
import com.ds.update.Config;
import com.ds.update.HttpConnect;
import com.ds.update.HttpDownload;
import com.ds.update.NetworkTool;
import com.ds.update.ToastUtil;
import com.ds.utility.Utility;

public class MoreActivity extends Activity implements OnClickListener {

	TextView title;
	LinearLayout setting;
	LinearLayout feedback;
	LinearLayout appstore;
	LinearLayout update;
	LinearLayout invite;
	LinearLayout about;
	ProgressDialog pDialog;
	private int newVerCode = 0;
	private String newVerName = "";
	public ProgressDialog pBar;
	private static final int DO_UPDATE = 1001;
	private static final int CANCEL_UPDATE = 1002;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){

			if (msg.what == 1) {
				ToastUtil toastUtil = new ToastUtil(MoreActivity.this);
				toastUtil.showDefultToast("下载成功！！").show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File("/sdcard/update/", "birthday.apk")),
						"application/vnd.android.package-archive");
				startActivity(intent);
			} else {
				ToastUtil toastUtil = new ToastUtil(MoreActivity.this);
				toastUtil.showDefultToast("下载失败！！").show();
			}
			pBar.cancel();
		}
	};
	private String packageName;
	String nowVersion;
	String newVersion;

	// ToggleButton autoLogin;
	private static final String TAG = "MoreActivity";
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DO_UPDATE:
				if (pDialog != null) {
					pDialog.dismiss();
				}
				// doNewVersionUpdate();
				if (Double.valueOf(newVersion) > Double.valueOf(nowVersion)) {
					doNewVersionUpdate(nowVersion, newVersion);
				}
				break;
			case CANCEL_UPDATE:
				if (pDialog != null) {
					pDialog.dismiss();
				}
				notNewVersionShow();
				break;
			}
		}
	};

	private void doNewVersionUpdate(String nowVersion, String newVersion) {
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(nowVersion);
		sb.append(", 发现新版本:");
		sb.append(newVersion);
		sb.append(", 是否更新?");
		Dialog dialog = new AlertDialog.Builder(MoreActivity.this)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								pBar = new ProgressDialog(MoreActivity.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								// pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								pBar.show();
								goToDownloadApk();
							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								dialog.dismiss();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	private void goToDownloadApk() {
		new Thread(new DownloadApkThread(handler)).start();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birth_more);
		initViews();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.more);
		setting = (LinearLayout) findViewById(R.id.setting);
		// setting.setClickable(false);
		feedback = (LinearLayout) findViewById(R.id.feedback);
		appstore = (LinearLayout) findViewById(R.id.appstore);
		update = (LinearLayout) findViewById(R.id.update);
		invite = (LinearLayout) findViewById(R.id.invite);
		about = (LinearLayout) findViewById(R.id.about);
		// setting.setOnClickListener(this);
		// autoLogin = (ToggleButton) findViewById(R.id.auto_login_toggle);
		// boolean isAuto = getSharedPreferences(MineActivity.LOGIN, 0)
		// .getBoolean(MineActivity.ISAUTOLOGIN, false);
		// autoLogin.setChecked(isAuto);
		// autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// Log.i(TAG, "isChecked:" + isChecked);
		// getSharedPreferences(MineActivity.LOGIN, 0).edit()
		// .putBoolean(MineActivity.ISAUTOLOGIN, isChecked)
		// .commit();
		// }
		// });
		feedback.setOnClickListener(this);
		appstore.setOnClickListener(this);
		update.setOnClickListener(this);
		invite.setOnClickListener(this);
		about.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		// case R.id.setting:
		// break;
		case R.id.feedback:
			intent.setClass(this, FeedbackActivity.class);
			break;
		case R.id.appstore:
			intent.setClass(this, AppStoreActivity.class);
			break;
		case R.id.update:
			pDialog = ProgressDialog.show(this, "",
					getResources().getString(R.string.update_hint));
//			checkUpdate.start();
			if (yesOrNoUpdataApk() == 0) {
				mHandler.sendEmptyMessage(CANCEL_UPDATE);
			} else {
				mHandler.sendEmptyMessage(DO_UPDATE);
			}
			break;
		case R.id.invite:
			intent.setClass(this, ShareActivity.class);
			break;
		case R.id.about:
			intent.setClass(this, AboutActivity.class);
			break;
		}
		if (v.getId() != R.id.update) {
			startActivity(intent);
		}
	}

	private Thread checkUpdate = new Thread() {
		public void run() {
			// if (getServerVerCode()) {
			// int vercode = Config.getVerCode(MoreActivity.this);
			// if (newVerCode > vercode) {
			// mHandler.sendEmptyMessage(DO_UPDATE);
			// } else {
			// mHandler.sendEmptyMessage(CANCEL_UPDATE);
			// }
			// } else {
			// mHandler.sendEmptyMessage(CANCEL_UPDATE);
			// }
			if (yesOrNoUpdataApk() == 0) {
				mHandler.sendEmptyMessage(CANCEL_UPDATE);
			} else {
				mHandler.sendEmptyMessage(DO_UPDATE);
			}
		}
	};

	private int yesOrNoUpdataApk() {
		packageName = this.getPackageName();
		nowVersion = getVerCode(MoreActivity.this);
		newVersion = goToCheckNewVersion();
//		if (newVersion.equals("Error")) {
//			return 0;
//		}
		Log.i(TAG,"newVersion:"+newVersion);
		if(nowVersion.equals(newVersion)){
			return 0;
		}
		return 1;
	}

	private String getVerCode(Context context) {
		int verCode = 0;
		String verName = null;
		try {
			verCode = context.getPackageManager()
					.getPackageInfo(packageName, 0).versionCode;
			verName = context.getPackageManager()
					.getPackageInfo(packageName, 0).versionName;
		} catch (Exception e) {
			System.out.println("no");
		}
		System.out.println("verCode" + verCode + "===" + "verName" + verName);
		return verName;
	}

	private String goToCheckNewVersion() {
		System.out.println("goToCheckNewVersion");
		String newVersion = null;
		final String url = Utility.GET_VERSION_URI;
		HttpConnect hc = new HttpConnect(url, this);
		newVersion = hc.getDataAsString(null);
		if (newVersion.equals("Error")) {
			return "Error";
		}
		return newVersion;
	}

	private boolean getServerVerCode() {
		try {
			String verjson = NetworkTool.getContent(Config.UPDATE_SERVER
					+ Config.UPDATE_VERJSON);
			JSONArray array = new JSONArray(verjson);
			if (array.length() > 0) {
				JSONObject obj = array.getJSONObject(0);
				try {
					newVerCode = Integer.parseInt(obj.getString("verCode"));
					newVerName = obj.getString("verName");
				} catch (Exception e) {
					newVerCode = -1;
					newVerName = "";
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void showAboutDialog() {
		BirthDialogBuilder idb = new BirthDialogBuilder(this);
		idb.setTitle(R.string.about);
		idb.setMessage("测试内容");
		idb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		idb.show();
	}

	private void notNewVersionShow() {
		int verCode = Config.getVerCode(this);
		String verName = Config.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(",\n已是最新版,无需更新!");
		Dialog dialog = new AlertDialog.Builder(MoreActivity.this)
				.setTitle("软件更新").setMessage(sb.toString())// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}

						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	private void doNewVersionUpdate() {
		int verCode = Config.getVerCode(this);
		String verName = Config.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(", 发现新版本:");
		sb.append(newVerName);
		sb.append(" Code:");
		sb.append(newVerCode);
		sb.append(", 是否更新?");
		Dialog dialog = new AlertDialog.Builder(MoreActivity.this)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								pBar = new ProgressDialog(MoreActivity.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								downFile(Config.UPDATE_SERVER
										+ Config.UPDATE_APKNAME);
							}

						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								dialog.dismiss();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	void downFile(final String url) {
		pBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {

						File file = new File(
								Environment.getExternalStorageDirectory(),
								Config.UPDATE_SAVENAME);
						fileOutputStream = new FileOutputStream(file);

						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {
							}
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	void down() {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				update();
			}
		});

	}

	void update() {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), Config.UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	class DownloadApkThread implements Runnable {
		private Handler handler;
		private static final String url = Utility.UPDATE_URI;
		private static final String fileName = "birthday.apk";
		private static final String path = "/sdcard/update";

		public DownloadApkThread(Handler handler) {
			this.handler = handler;
		}

		public void run() {
			System.out.println("下载线程开启");
			Message message = new Message();
			message.what = HttpDownload.downLoadFile(url, fileName, path);
			handler.sendMessage(message);
		}
	}

}
