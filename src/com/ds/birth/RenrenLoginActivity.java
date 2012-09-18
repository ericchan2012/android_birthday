package com.ds.birth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ds.utility.RenrenUtil;

public class RenrenLoginActivity extends Activity {
	public final static String TAG = "RenrenLoginActivity";
	private WebView renrenLoginWebView; // WebView 控件，用于显示从人人网请求得到html授权页面

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renren_login);
		renrenLoginWebView = (WebView) findViewById(R.id.renren); // 得到
																	// WebView
																	// 控件

		// 对WebView进行设置（对JS的支持，对缩放的支持，对缓存模式的支持）
		WebSettings webSettings = renrenLoginWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		// 根据client_id取得到人人服务器人人对你的应用授权，如果成功则返回人人网登陆页面的html文件，并在WebView控件上显示
		// 此时用户需要输入自己人人账号的用户名、密码并点击登陆
		renrenLoginWebView
				.loadUrl("https://graph.renren.com/oauth/authorize?"
						+ "client_id=bfb4a6b9a8bc4a4c997cca20df11d117&response_type=token"
						+ "&display=touch&redirect_uri=http://graph.renren.com/oauth/login_success.html");

		renrenLoginWebView.setWebViewClient(new WebViewClient() {

			// 击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();// 让webview处理https请求
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				String url0 = renrenLoginWebView.getUrl();
				String access_token = "";
				String expires_in = "";
				Log.i(TAG, "URL = " + url0);
				if (url0 != null) {
					if (url0.contains("access_token=")) { // 从URL中解析得到
															// access_token
						access_token = url0.substring(
								url0.indexOf("access_token=") + 13,
								url0.length() - 19);
						try {
							access_token = URLDecoder.decode(access_token,
									"utf-8"); // 制定为utf-8编码
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						Log.i(TAG, "access_token = " + access_token);
					}
					if (url0.contains("expires_in=")) { // 从URL中解析得到 expires_in
						expires_in = url0.substring(
								url0.indexOf("expires_in=") + 11, url0.length());
						Log.i(TAG, "expires_in = " + expires_in);
					}
					RenrenUtil.access_token = access_token; // 将解析得到的
														// access_token 保存起来
					RenrenUtil.expires_in = expires_in; // 将解析得到的 expires_in
													// 保存起来

					// 输入用户名、密码登陆成功，进行页面跳转
					if (RenrenUtil.access_token.length() != 0) {
						Intent intent = new Intent(RenrenLoginActivity.this,
								RenrenFriendsActivity.class);
						startActivity(intent);
						finish();
					}
				}
				super.onPageFinished(view, url);
			}
		});
	}
}
