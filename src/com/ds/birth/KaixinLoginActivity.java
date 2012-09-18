package com.ds.birth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.ds.kaixin.Kaixin;
import com.ds.kaixin.KaixinAuthError;
import com.ds.kaixin.KaixinAuthListener;

public class KaixinLoginActivity extends Activity {
	public final static String TAG = "KaixinLoginActivity";
	private WebView renrenLoginWebView; // WebView 控件，用于显示从人人网请求得到html授权页面
	Kaixin kaixin;

	KaixinAuthListener authListener = new KaixinAuthListener() {

		public void onAuthCancel(Bundle values) {
			Log.i(TAG, "onAuthCancel");
		}

		public void onAuthCancelLogin() {
			Log.i(TAG, "onAuthCancelLogin");
		}

		public void onAuthComplete(Bundle values) {
			// Kaixin kaixin = Kaixin.getInstance();
			// try {
			// String response = kaixin.refreshAccessToken(AndroidExample.this,
			// null);
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (MalformedURLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			Intent intent = new Intent(KaixinLoginActivity.this,
					KaixinFriendsActivity.class);
			startActivity(intent);
			finish();
		}

		public void onAuthError(KaixinAuthError kaixinAuthError) {
			Log.i(TAG, "onAuthError");
			Message msg = Message.obtain();
			msg.obj = kaixinAuthError.getErrorDescription();
			// msg.what = LOGINERROR;
			// mHandler.sendMessage(msg);
			Toast.makeText(KaixinLoginActivity.this, (String) msg.obj,
					Toast.LENGTH_SHORT).show();

		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renren_login);
		// renrenLoginWebView = (WebView) findViewById(R.id.renren); // 得到
		// kaixin = Kaixin.getInstance();
		// WebSettings webSettings = renrenLoginWebView.getSettings();
		// webSettings.setJavaScriptEnabled(true);
		// webSettings.setBuiltInZoomControls(true);
		// webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//
		// // 根据client_id取得到人人服务器人人对你的应用授权，如果成功则返回人人网登陆页面的html文件，并在WebView控件上显示
		// // 此时用户需要输入自己人人账号的用户名、密码并点击登陆
		// renrenLoginWebView
		// .loadUrl("http://api.kaixin001.com/oauth2/authorize?"
		// + "client_id=94220751574408a324c4445bd6762841&response_type=token"
		// +
		// "&display=touch&redirect_uri=http://api.kaixin001.com/oauth2/oauth_redirect");
		//
		// renrenLoginWebView.setWebViewClient(new WebViewClient() {
		//
		// // 击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
		//
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// view.loadUrl(url);
		// return true;
		// }
		//
		//
		// public void onReceivedSslError(WebView view,
		// SslErrorHandler handler, SslError error) {
		// handler.proceed();// 让webview处理https请求
		// }
		//
		//
		// public void onPageFinished(WebView view, String url) {
		// String url0 = renrenLoginWebView.getUrl();
		// String access_token = "";
		// String expires_in = "";
		// Log.i(TAG, "URL = " + url0);
		// if (url0 != null) {
		// if (url0.contains("access_token=")) { // 从URL中解析得到
		// // access_token
		// access_token = url0.substring(
		// url0.indexOf("access_token=") + 13,
		// url0.length() - 19);
		// try {
		// access_token = URLDecoder.decode(access_token,
		// "utf-8"); // 制定为utf-8编码
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// Log.i(TAG, "access_token = " + access_token);
		// }
		// if (url0.contains("expires_in=")) { // 从URL中解析得到 expires_in
		// expires_in = url0.substring(
		// url0.indexOf("expires_in=") + 11, url0.length());
		// Log.i(TAG, "expires_in = " + expires_in);
		// }
		// KaixinUtil.access_token = access_token; // 将解析得到的
		// // access_token 保存起来
		// KaixinUtil.expires_in = expires_in; // 将解析得到的 expires_in
		// // 保存起来
		// // kaixin.setAccessToken(access_token);
		// // kaixin.setAccessExpiresIn(expires_in);
		//
		// // 输入用户名、密码登陆成功，进行页面跳转
		// if (KaixinUtil.access_token.length() != 0) {
		// Intent intent = new Intent(KaixinLoginActivity.this,
		// KaixinFriendsActivity.class);
		// startActivity(intent);
		// finish();
		// }
		// }
		// super.onPageFinished(view, url);
		// }
		// });
		login();
	}

	private void login() {
		kaixin = Kaixin.getInstance();
		String[] permissions = { "basic", "create_records" };
		kaixin.authorize(KaixinLoginActivity.this, permissions, authListener);
	}
}
