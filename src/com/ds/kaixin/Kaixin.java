/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ds.kaixin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;

public class Kaixin {

	/**
	 * �������ʱ��õ�api key���ڵ��ýӿ�ʱ����������Ψһ��ݡ�
	 */
	public static final String API_KEY = "94220751574408a324c4445bd6762841"; // �滻Ϊ����ʱ��õ�api
																				// key

	/**
	 * �������ʱ��õ�secret key
	 */
	public static final String SECRET_KEY = "58fb999b82ca813ed858541d714c25c6"; // �滻Ϊ����ʱ��õ�secret
																				// key

	/**
	 * �������ʱ��д����վ��ַ
	 */
	private static String KX_AUTHORIZE_CALLBACK_URL = "http://localhost/"; // �滻Ϊ�������ʱ��д����վ��ַ

	/**
	 * Kaixin��Ȩ��ַ
	 */
	private static final String KX_AUTHORIZE_URL = "http://api.kaixin001.com/oauth2/authorize";

	/**
	 * ˢ�����Ƶ�ַ
	 */
	private static String KX_REFRESHTOKEN_URL = "http://api.kaixin001.com/oauth2/access_token";

	/**
	 * ˢ�����Ƶ�ַ
	 */
	private static String KX_REFRESHTOKEN_URL_S = "https://api.kaixin001.com/oauth2/access_token";

	/**
	 * Kaixin��¼��ַ
	 */
	private static String KX_LOGIN_URL = "http://www.kaixin001.com/login/connect.php";

	/**
	 * oauth �汾
	 */
	private static final String OAUTH_VERSION = "2.0";

	/**
	 * rest api�ӿڵ�ַ
	 */
	private static String KX_REST_URL = "https://api.kaixin001.com";

	/**
	 * �����ַ�
	 */
	private static final String SESSION_KEY = "session_key";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String EXPIRES_IN = "expires_in";
	private static final String ACCESS_DENIED = "access_denied";
	private static final String LOGIN_DENIED = "login_denied";

	/**
	 * ��Ȩ���������صĲ���ֵ
	 */
	private String mAccessToken = null;
	private String mRefreshToken = null;
	private long mAccessExpires = 0;

	/**
	 * ���ػ����ֶ�
	 */
	private static final String KAIXIN_SDK_STORAGE = "kaixin_sdk_storage";
	private static final String KAIXIN_SDK_STORAGE_ACCESS_TOKEN = "kaixin_sdk_storage_access_token";
	private static final String KAIXIN_SDK_STORAGE_REFRESH_TOKEN = "kaixin_sdk_storage_refresh_token";
	private static final String KAIXIN_SDK_STORAGE_EXPIRES = "kaixin_sdk_storage_expires";
	private static final long ONE_HOUR = 1000 * 60 * 60;

	/**
	 * Kaixin��ʵ��
	 */
	private static Kaixin instance = null;

	public static synchronized Kaixin getInstance() {
		if (null == instance)
			instance = new Kaixin();
		return instance;
	}

	private X509TrustManager xtm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	private HostnameVerifier hnv = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	private Kaixin() {
		System.setProperty("http.keepAlive", "false");

		SSLContext sslContext = null;

		try {
			sslContext = SSLContext.getInstance("TLS");
			X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
			sslContext.init(null, xtmArray, new java.security.SecureRandom());
		} catch (GeneralSecurityException gse) {
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
		}

		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}

	/**
	 * ��ɵ�¼����ȡaccess_token(User-Agent Flow��ʽ)
	 * 
	 * @param context
	 * @param listener
	 */
	public void authorize(final Context context,
			final KaixinAuthListener listener) {
		this.authorize(context, null, listener);
	}

	/**
	 * ��ɵ�¼����ȡaccess_token(User-Agent Flow��ʽ)
	 * 
	 * @param context
	 * @param permissions
	 * @param listener
	 */
	public void authorize(final Context context, String[] permissions,
			final KaixinAuthListener listener) {
		if (this.isSessionValid()) {
			listener.onAuthComplete(new Bundle());
			return;
		}
		this.authorize(context, permissions, listener,
				KX_AUTHORIZE_CALLBACK_URL, "token");
	}

	/**
	 * ��ɵ�¼����ȡaccess_token(User-Agent Flow��ʽ)
	 * 
	 * @param context
	 * @param permissions
	 *            Ȩ���б?�μ�http://wiki.open.kaixin001.com/index.php?id=OAuth%E6%
	 *            96% 87%E6%A1%A3#REST%E6%
	 *            8E%A5%E5%8F%A3%E5%92%8COAuth%E6%9D%83%E9%99%90%E5%AF%B9%E7%85%
	 *            A 7%E8%A1%A8
	 * @param listener
	 * @param redirectUrl
	 * @param responseType
	 */
	private void authorize(final Context context, String[] permissions,
			final KaixinAuthListener listener, final String redirectUrl,
			String responseType) {

		CookieSyncManager.createInstance(context);

		Bundle params = new Bundle();
		params.putString("client_id", API_KEY);
		params.putString("response_type", responseType);
		params.putString("redirect_uri", redirectUrl);
		params.putString("state", "");
		params.putString("display", "page");
		params.putString("oauth_client", "1");

		if (permissions != null && permissions.length > 0) {
			String scope = TextUtils.join(" ", permissions);
			params.putString("scope", scope);
		}

		String url = KX_AUTHORIZE_URL + "?" + Util.encodeUrl(params);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Util.showAlert(context, "错误", "添加网络权限");
		} else {
			new KaixinDialog(context, url, new KaixinDialogListener() {

				public int onPageBegin(String url) {
					return KaixinDialogListener.DIALOG_PROCCESS;
				}

				public void onPageFinished(String url) {
				}

				public boolean onPageStart(String url) {
					return (KaixinDialogListener.PROCCESSED == parseUrl(url));
				}

				public void onReceivedError(int errorCode, String description,
						String failingUrl) {
					listener.onAuthError(new KaixinAuthError(String
							.valueOf(errorCode), description, failingUrl));
				}

				private int parseUrl(String url) {
					if (url.startsWith(KX_AUTHORIZE_CALLBACK_URL)) {
						Bundle values = Util.parseUrl(url);
						String error = values.getString("error");// ��Ȩ���������صĴ������
						if (error != null) {
							if (ACCESS_DENIED.equalsIgnoreCase(error)) {
								listener.onAuthCancel(values);
							} else if (LOGIN_DENIED.equalsIgnoreCase(error)) {
								listener.onAuthCancelLogin();
							} else {
								listener.onAuthError(new KaixinAuthError(error,
										error, url));
							}

							Util.clearCookies(context);

							setAccessToken(null);
							setRefreshToken(null);
							setAccessExpires(0L);

						} else {
							this.authComplete(values, url);
						}
						return KaixinDialogListener.PROCCESSED;
					}
					return KaixinDialogListener.UNPROCCESS;
				}

				private void authComplete(Bundle values, String url) {
					CookieSyncManager.getInstance().sync();
					String accessToken = values.getString(ACCESS_TOKEN);
					String refreshToken = values.getString(REFRESH_TOKEN);
					String expiresIn = values.getString(EXPIRES_IN);
					if (accessToken != null && refreshToken != null
							&& expiresIn != null) {
						try {
							setAccessToken(accessToken);
							setRefreshToken(refreshToken);
							setAccessExpiresIn(expiresIn);
							listener.onAuthComplete(values);
						} catch (Exception e) {
							listener.onAuthError(new KaixinAuthError(e
									.getClass().getName(), e.getMessage(), e
									.toString()));
						}
					} else {
						listener.onAuthError(new KaixinAuthError("����",
								"��Ȩ���������ص���Ϣ������", url));
					}
				}
			}).show();
		}
	}

	/**
	 * �жϻỰ�Ƿ���Ч
	 * 
	 * @return �Ự�Ƿ���Ч
	 */
	public boolean isSessionValid() {
		return (getAccessToken() != null)
				&& ((getAccessExpires() == 0) || (System.currentTimeMillis() < getAccessExpires()));
	}

	public void setAccessToken(String token) {
		mAccessToken = token;
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public void setRefreshToken(String token) {
		mRefreshToken = token;
	}

	public String getRefreshToken() {
		return mRefreshToken;
	}

	public void setAccessExpires(long time) {
		mAccessExpires = time;
	}

	public long getAccessExpires() {
		return mAccessExpires;
	}

	public void setAccessExpiresIn(String expiresIn) {
		if (expiresIn != null && !expiresIn.equals("0")) {
			setAccessExpires(System.currentTimeMillis()
					+ Long.parseLong(expiresIn) * 1000);
		}
	}

	/**
	 * ��accessToken����ʱ����ˢ�����ƻ�ȡ�µ�accessToken
	 * 
	 * @param context
	 * 
	 * @param permissions
	 *            Ȩ���б?�μ�http://wiki.open.kaixin001.com/index.php?id=OAuth%E6%
	 *            96% 87%E6%A1%A3#REST%E6%
	 *            8E%A5%E5%8F%A3%E5%92%8COAuth%E6%9D%83%E9%99%90%E5%AF%B9%E7%85%
	 *            A 7%E8%A1%A8
	 * 
	 * @return ���������ص�JSON��
	 * @throws FileNotFoundException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String refreshAccessToken(Context context, String[] permissions)
			throws FileNotFoundException, MalformedURLException, IOException {

		mAccessToken = null;
		if (mRefreshToken == null) {
			return null;
		}

		Bundle params = new Bundle();
		params.putString("grant_type", REFRESH_TOKEN);
		params.putString("refresh_token", mRefreshToken);
		params.putString("client_id", API_KEY);
		params.putString("client_secret", SECRET_KEY);
		if (permissions != null && permissions.length > 0) {
			String scope = TextUtils.join(" ", permissions);
			params.putString("scope", scope);
		}

		return Util.openUrl(context, KX_REFRESHTOKEN_URL, "GET", params, null);
	}

	/**
	 * �ϴ����ݽӿڣ�����multi-part post��ʽ�ϴ����
	 * 
	 * @param params
	 *            �����б�
	 * @param photos
	 *            key-value��ʽ��ͼ����ݼ��� keyΪfilename��
	 *            valueΪͼ����ݣ��������Ϳ�����InputStream��byte[]
	 *            ����������ΪInputStream������openUrl�����н������ر�
	 * @return ���������ص�JSON��
	 * @throws FileNotFoundException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String uploadContent(Context context, String restInterface,
			Bundle params, Map<String, Object> photos)
			throws FileNotFoundException, MalformedURLException, IOException {

		if (params == null) {
			params = new Bundle();
		}
		params.putString("access_token", getAccessToken());

		return Util.openUrl(context, KX_REST_URL + restInterface, "POST",
				params, photos);
	}
	public String request(Context context, String restInterface, Bundle params,
			String httpMethod) throws FileNotFoundException,
			MalformedURLException, IOException {

		if (params == null) {
			params = new Bundle();
		}
		params.putString("access_token", getAccessToken());
		return Util.openUrl(context, KX_REST_URL + restInterface, httpMethod,
				params, null);
	}

	public String login(Bundle params, Context ctx) {
		String url = KX_REFRESHTOKEN_URL_S;
		String method = "POST";
		String sError = null;
		try {
			String response = Util.openUrl(ctx, url, method, params, null);
			if (response != null) {
				sError = setOauth(response, ctx);
				if (sError != null) {
					return sError;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "错误";
		}
	}

	public String setOauth(String response, Context ctx) {
		try {
			JSONObject obj = new JSONObject(response);
			String error = obj.optString("error");
			if (error != null && error.length() > 0) {
				return error;
			} else {
				String accessToken = obj.optString(ACCESS_TOKEN);
				String refreshToken = obj.optString(REFRESH_TOKEN);
				String expiresIn = obj.optString(EXPIRES_IN);
				if (accessToken != null && refreshToken != null
						&& expiresIn != null) {
					try {
						setAccessToken(accessToken);
						setRefreshToken(refreshToken);
						setAccessExpiresIn(expiresIn);
						updateStorage(ctx);
						return null;
					} catch (Exception e) {
						return "错误";
					}
				} else {
					return "错误";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "错误";
		}
	}

	/**
	 * ��ȡ���ػ���
	 * 
	 * @param context
	 * @return ��ȡ���ػ����Ƿ�ɹ�
	 */
	public boolean loadStorage(Context context) {
		SharedPreferences sp = context.getSharedPreferences(KAIXIN_SDK_STORAGE,
				Context.MODE_PRIVATE);
		String accessToken = sp
				.getString(KAIXIN_SDK_STORAGE_ACCESS_TOKEN, null);
		if (accessToken == null) {
			return false;
		}

		String refreshToken = sp.getString(KAIXIN_SDK_STORAGE_REFRESH_TOKEN,
				null);
		if (refreshToken == null) {
			return false;
		}

		long expires = sp.getLong(KAIXIN_SDK_STORAGE_EXPIRES, 0);
		long currenct = System.currentTimeMillis();
		if (expires < (currenct - ONE_HOUR)) {
			clearStorage(context);
			return false;
		}

		mAccessToken = accessToken;
		mRefreshToken = refreshToken;
		mAccessExpires = expires;

		return true;
	}

	/**
	 * ���±��ػ���
	 * 
	 * @param context
	 * @return ���±��ػ����Ƿ�ɹ�
	 */
	public boolean updateStorage(Context context) {
		boolean bUpdate = false;
		Editor editor = context.getSharedPreferences(KAIXIN_SDK_STORAGE,
				Context.MODE_PRIVATE).edit();
		if (mAccessToken != null && mRefreshToken != null && mAccessExpires > 0) {
			editor.putString(KAIXIN_SDK_STORAGE_ACCESS_TOKEN, mAccessToken);
			editor.putString(KAIXIN_SDK_STORAGE_REFRESH_TOKEN, mRefreshToken);
			editor.putLong(KAIXIN_SDK_STORAGE_EXPIRES, mAccessExpires);
			bUpdate = true;
		} else {
			clearStorage(context);
			bUpdate = false;
		}
		editor.commit();
		return bUpdate;
	}

	/**
	 * ���ػ���
	 * 
	 * @param context
	 */
	public void clearStorage(Context context) {
		Editor editor = context.getSharedPreferences(KAIXIN_SDK_STORAGE,
				Context.MODE_PRIVATE).edit();
		editor.remove(KAIXIN_SDK_STORAGE_ACCESS_TOKEN);
		editor.remove(KAIXIN_SDK_STORAGE_REFRESH_TOKEN);
		editor.remove(KAIXIN_SDK_STORAGE_EXPIRES);
		editor.commit();
	}
}
