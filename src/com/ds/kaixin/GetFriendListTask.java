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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetFriendListTask extends AsyncTask<Object, Void, Integer> {
	private static final String TAG = "GetFriendListTask";
	private static final String RESTAPI_INTERFACE_GETFRIENDLIST = "/friends/me.json";
	private ArrayList<FriendInfo> resultContainer;
	private Handler handler;

	@SuppressWarnings("unchecked")
	protected Integer doInBackground(Object... params) {
		if (params == null || params.length == 0 || params.length != 6) {
			handler.sendEmptyMessage(Constant.RESULT_FAILED_ARG_ERR);
			return 0;
		}

		String start = (String) params[0];
		String number = (String) params[1];
		Kaixin kaixin = (Kaixin) params[2];
		handler = (Handler) params[3];
		resultContainer = (ArrayList<FriendInfo>) params[4];
		Context context = (Context) params[5];

		Bundle bundle = new Bundle();
		bundle.putString("start", start);
		bundle.putString("num", number);
		bundle.putString("fields", "uid,name,gender,logo50,birthday");

		try {
			String jsonResult = kaixin.request(context,
					RESTAPI_INTERFACE_GETFRIENDLIST, bundle, "GET");
			if (jsonResult == null) {
				handler.sendEmptyMessage(Constant.RESULT_FAILED_NETWORK_ERR);
			} else {
				KaixinError kaixinError = Util.parseRequestError(jsonResult);
				if (kaixinError != null) {
					Message msg = Message.obtain();
					msg.what = Constant.RESULT_FAILED_REQUEST_ERR;
					msg.obj = kaixinError;
					handler.sendMessage(msg);
				} else {
					ArrayList<FriendInfo> friendList = getFriendInfos(jsonResult);
					if (null != friendList) {
						resultContainer.addAll(friendList);
					}
					if (resultContainer.size() > 0) {
						handler.sendEmptyMessage(Constant.RESULT_GET_FRIENDS_OK);
					}else{
						handler.sendEmptyMessage(Constant.RESULT_GET_FRIENDS_ZERO);
					}
				}
			}
		} catch (MalformedURLException e1) {
			Log.e(TAG, "", e1);
			handler.sendEmptyMessage(Constant.RESULT_FAILED_MALFORMEDURL_ERR);
		} catch (JSONException e1) {
			Log.e(TAG, "", e1);
			handler.sendEmptyMessage(Constant.RESULT_FAILED_JSON_PARSE_ERR);
		} catch (IOException e1) {
			Log.e(TAG, "", e1);
			handler.sendEmptyMessage(Constant.RESULT_FAILED_NETWORK_ERR);
		} catch (Exception e1) {
			Log.e(TAG, "", e1);
			handler.sendEmptyMessage(Constant.RESULT_FAILED);
		}

		return 1;
	}

	private ArrayList<FriendInfo> getFriendInfos(String jsonResult)
			throws JSONException {
		if (jsonResult == null || jsonResult.length() == 0) {
			return null;
		}
		JSONObject friendInfos = new JSONObject(jsonResult);
		if (friendInfos == null) {
			return null;
		}

		JSONArray jsonArray = friendInfos.getJSONArray("users");
		int len = jsonArray == null ? 0 : jsonArray.length();
		if (len == 0) {
			return null;
		}

		ArrayList<FriendInfo> friendList = new ArrayList<FriendInfo>(len);
		JSONObject jsonObj = null;
		for (int i = 0; i < len; i++) {
			jsonObj = jsonArray.getJSONObject(i);

			String uid = jsonObj.getString("uid");
			String name = jsonObj.getString("name");
			String gender = jsonObj.getString("gender");
			String logo = jsonObj.getString("logo50");
			String birthday = jsonObj.getString("birthday");
			Log.i(TAG, String.format(
					"uid:%s name:%s gender:%s logo50:%s birthday:%s", uid,
					name, gender, logo, birthday));
			friendList.add(new FriendInfo(uid, name, gender, logo, birthday));
		}
		return friendList;
	}

	protected void onPostExecute(Integer result) {
	}
}