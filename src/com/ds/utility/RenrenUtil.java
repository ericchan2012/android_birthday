package com.ds.utility;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RenrenUtil {
	public static String access_token;
	public static String expires_in;

	/**
	 * 解析JSON数据为人人对象
	 * 
	 * @param renrenJsonData
	 * @return
	 */
	public static List<Renren> parseRenrenFromJson(String renrenJsonData) {
		List<Renren> renrenList = new ArrayList<Renren>();
		try {
			JSONArray jsonArray = new JSONArray(renrenJsonData);
			int length = jsonArray.length();
			for (int i = 0; i < length; ++i) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Renren renren = new Renren();
				renren.setId(jsonObject.getString("id"));
				renren.setName(jsonObject.getString("name"));
				renren.setHeadurl(jsonObject.getString("headurl"));
				renrenList.add(renren);
			}
			return renrenList;
		} catch (JSONException e) {
		}
		return null;
	}
	

	public static List<Renren> parseRenrenInfoFromJson(String renrenJsonData) {
		List<Renren> renrenList = new ArrayList<Renren>();
		try {
			JSONArray jsonArray = new JSONArray(renrenJsonData);
			int length = jsonArray.length();
			for (int i = 0; i < length; ++i) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Renren renren = new Renren();
				renren.setId(jsonObject.getString("uid"));
				renren.setName(jsonObject.getString("name"));
				renren.setHeadurl(jsonObject.getString("headurl"));
				renren.setBirthday(jsonObject.getString("birthday"));
				renren.setSex(jsonObject.getString("sex"));
				renrenList.add(renren);
				Log.i("RenrenUtil", "id:" + renren.getId());
				Log.i("RenrenUtil", "name:" + renren.getName());
				Log.i("RenrenUtil", "birthday:" + renren.getBirthday());
				Log.i("RenrenUtil", "sex:" + renren.getSex());
				Log.i("RenrenUtil", "headurl:" + renren.getHeadurl());
			}
			return renrenList;
		} catch (JSONException e) {
		}
		return null;
	}

	public static List<Renren> parseKaixinFromJson(String renrenJsonData) {
		List<Renren> renrenList = new ArrayList<Renren>();
		try {
			JSONArray jsonArray = new JSONArray(renrenJsonData);
			int length = jsonArray.length();
			for (int i = 0; i < length; ++i) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Renren renren = new Renren();
				renren.setId(jsonObject.getString("id"));
				renren.setName(jsonObject.getString("name"));
				renren.setSex(jsonObject.getString("gender"));
				renren.setHeadurl(jsonObject.getString("logo50"));
				renren.setBirthday(jsonObject.getString("birthday"));
				renrenList.add(renren);
				Log.i("RenrenUtil", "id:" + renren.getId());
				Log.i("RenrenUtil", "name:" + renren.getName());
				Log.i("RenrenUtil", "birthday:" + renren.getBirthday());
				Log.i("RenrenUtil", "sex:" + renren.getSex());
				Log.i("RenrenUtil", "headurl:" + renren.getHeadurl());
			}
			return renrenList;
		} catch (JSONException e) {
		}
		return null;
	}
}
