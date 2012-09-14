package com.ds.birth;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ds.qq.AddShareClickListener;
import com.ds.qq.AddTopicClickListener;
import com.ds.qq.GetUserInfoClickListener;
import com.ds.qq.GetUserProfileClickListener;
import com.ds.qq.ListAlbumClickListener;
import com.ds.qq.UploadPicClickListener;
import com.tencent.tauth.TAuthView;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.TencentOpenRes;
import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.TDebug;

public class QQLoginActivity extends Activity {
	private static final String TAG = "QQLoginActivity";
	public static final int REQUEST_PICK_PICTURE = 1001;
	private static final String CALLBACK = "auth://tauth.qq.com/";

	public String mAppid = "100305035";// 申请时分配的appid
	private String scope = "get_user_info,get_user_profile,add_share,add_topic,list_album,upload_pic,add_album";// 授权范围
	private AuthReceiver receiver;

	public String mAccessToken, mOpenId;

	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renren_friend);
		auth(mAppid, "_self");
		registerIntentReceivers();
	}

	/**
	 * 打开登录认证与授权页面
	 * 
	 * @param String
	 *            clientId 申请时分配的appid
	 * @param String
	 *            target 打开登录页面的方式：“_slef”以webview方式打开; "_blank"以内置安装的浏览器方式打开
	 * @author John.Meng<arzen1013@gmail> QQ:3440895
	 * @date 2011-9-5
	 */
	private void auth(String clientId, String target) {
		Intent intent = new Intent(QQLoginActivity.this,
				com.tencent.tauth.TAuthView.class);

		intent.putExtra(TAuthView.CLIENT_ID, clientId);
		intent.putExtra(TAuthView.SCOPE, scope);
		intent.putExtra(TAuthView.TARGET, target);
		intent.putExtra(TAuthView.CALLBACK, CALLBACK);

		startActivity(intent);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!satisfyConditions()) {
			TDebug.msg("请先获取access token和open id", this);
			return;
		}
		getUserInfo();
	}

	private void getUserInfo() {
		showDialog(QQLoginActivity.PROGRESS);
		TencentOpenAPI.userInfo(mAccessToken, mAppid,
				mOpenId, new Callback() {

					public void onSuccess(final Object obj) {
						runOnUiThread(new Runnable() {

							public void run() {
								dismissDialog(QQLoginActivity.PROGRESS);
								// mActivity.showMessage("用户信息",
								// obj.toString());
							}
						});
					}

					public void onFail(final int ret, final String msg) {
						runOnUiThread(new Runnable() {

							public void run() {
								dismissDialog(QQLoginActivity.PROGRESS);
								TDebug.msg(ret + ": " + msg, QQLoginActivity.this);
							}
						});
					}
				});
	}

	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterIntentReceivers();
		}
	}

	private void registerIntentReceivers() {
		receiver = new AuthReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TAuthView.AUTH_BROADCAST);
		registerReceiver(receiver, filter);
	}

	private void unregisterIntentReceivers() {
		unregisterReceiver(receiver);
	}

	public void setOpenIdText(String txt) {
		((TextView) findViewById(R.id.openid)).setText(txt);
		mOpenId = txt;
	}

	/**
	 * 广播的侦听，授权完成后的回调是以广播的形式将结果返回
	 * 
	 * @author John.Meng<arzen1013@gmail> QQ:3440895
	 * @date 2011-9-5
	 */
	public class AuthReceiver extends BroadcastReceiver {

		private static final String TAG = "AuthReceiver";

		public void onReceive(Context context, Intent intent) {
			Bundle exts = intent.getExtras();
			String raw = exts.getString("raw");
			String access_token = exts.getString(TAuthView.ACCESS_TOKEN);
			String expires_in = exts.getString(TAuthView.EXPIRES_IN);
			String error_ret = exts.getString(TAuthView.ERROR_RET);
			String error_des = exts.getString(TAuthView.ERROR_DES);
			Log.i(TAG, String.format("raw: %s, access_token:%s, expires_in:%s",
					raw, access_token, expires_in));

			if (access_token != null) {
				mAccessToken = access_token;
				// TDebug.msg("正在获取OpenID...", getApplicationContext());
				if (!isFinishing()) {
					showDialog(PROGRESS);
				}
				// 用access token 来获取open id
				TencentOpenAPI.openid(access_token, new Callback() {

					public void onSuccess(final Object obj) {
						runOnUiThread(new Runnable() {

							public void run() {
								dismissDialog(PROGRESS);
								setOpenIdText(((OpenId) obj).getOpenId());
							}
						});
					}

					public void onFail(int ret, final String msg) {
						runOnUiThread(new Runnable() {

							public void run() {
								dismissDialog(PROGRESS);
								TDebug.msg(msg, getApplicationContext());
							}
						});
					}
				});
			}
			if (error_ret != null) {
				((TextView) findViewById(R.id.access_token))
						.setText("获取access token失败" + "\n错误码: " + error_ret
								+ "\n错误信息: " + error_des);
			}
		}

	}

	public boolean satisfyConditions() {
		return mAccessToken != null && mAppid != null && mOpenId != null
				&& !mAccessToken.equals("") && !mAppid.equals("")
				&& !mOpenId.equals("");
	}

	public static final int PROGRESS = 0;

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case PROGRESS:
			dialog = new ProgressDialog(this);
			((ProgressDialog) dialog).setMessage("请求中,请稍等...");
			break;
		}

		return dialog;
	}

}
