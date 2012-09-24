package com.ds.update;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class HttpConnect {
	/**
	 * 访问的Web服务器地址
	 */
	private String url;
	private HttpPost httpRequest;
	private HttpResponse httpResponse;
	private HttpParams httpParams;
	private HttpClient httpClient;

	public HttpConnect(String url, Context context) {
		this.url = url;
		httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
		HttpConnectionParams.setSoTimeout(httpParams, 15000);
		httpClient = new DefaultHttpClient(httpParams);
	}

	/**
	 * Role:通过Http请求来获取返回值(InputStream) <BR>
	 * Date:2012-2-10 <BR>
	 * 
	 * @author CODYY)peijiangping
	 */
	public InputStream getDataAsInputStream(List<NameValuePair> params) {
		InputStream result = null;
		try {
			httpRequest = new HttpPost(url);
			if (params != null) {
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
			}
			httpResponse = httpClient.execute(httpRequest);
			if (200 == httpResponse.getStatusLine().getStatusCode()) {
				result = httpResponse.getEntity().getContent();
			}
		} catch (IOException e) {
			Log.e("nimeimei", e.getMessage(), e);
			return result;
		}
		return result;
	}

	/**
	 * Role:通过Http请求来获取返回值(String),异常返回Error<BR>
	 * Date:2012-2-10 <BR>
	 * 
	 * @author CODYY)peijiangping
	 */
	public String getDataAsString(List<NameValuePair> params) {
		String result = null;
		try {
			httpRequest = new HttpPost(url);
			if (params != null) {
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
			}
			httpResponse = httpClient.execute(httpRequest);
			if (200 == httpResponse.getStatusLine().getStatusCode()) {
				result = EntityUtils.toString(httpResponse.getEntity());
				System.out.println("取得返回值" + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Error";
		}
		return result;
	}

	/**
	 * Role:通过Http请求来获取返回值(int),失败返回-1，成功返回数据 <BR>
	 * Date:2012-2-10 <BR>
	 * 
	 * @author CODYY)peijiangping
	 */
	public int getDataAsInt(List<NameValuePair> params) {
		int result = -1;
		try {
			httpRequest = new HttpPost(url);
			if (params != null) {
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
			}
			httpResponse = httpClient.execute(httpRequest);
			if (200 == httpResponse.getStatusLine().getStatusCode()) {
				result = Integer.parseInt(EntityUtils.toString(httpResponse
						.getEntity()));
			}
		} catch (IOException e) {
			return result;
		}
		return result;
	}

	/**
	 * Role:http请求服务器，不取得返回数据，成功返回1<BR>
	 * Date:2012-2-10 <BR>
	 * 
	 * @author CODYY)peijiangping
	 */
	public int sendToService(List<NameValuePair> params) {
		try {
			httpRequest = new HttpPost(url);
			if (params != null) {
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
			}
			httpResponse = httpClient.execute(httpRequest);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
}
