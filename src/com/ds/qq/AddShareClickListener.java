package com.ds.qq;

import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ds.birth.QQLoginActivity;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.TDebug;

/**
 * @author email:csshuai2009@gmail.com qq:65112183
 * @version 创建时间：2011-9-16 上午11:15:55
 * 类说明
 */
public class AddShareClickListener implements OnClickListener {
	private QQLoginActivity mActivity;

	public AddShareClickListener(QQLoginActivity activity) {
		mActivity = activity;
	}

	
	public void onClick(View v) {
		if (!mActivity.satisfyConditions()) {
			TDebug.msg("请先获取access token和open id", mActivity);
			return;
		}
		mActivity.showDialog(QQLoginActivity.PROGRESS);
		Bundle bundle = null;
		bundle = new Bundle();
		bundle.putString("title", "QQ登陆SDK：Add_Share测试");//必须。feeds的标题，最长36个中文字，超出部分会被截断。
		bundle.putString("url", "http://www.qq.com" + "#" + System.currentTimeMillis());//必须。分享所在网页资源的链接，点击后跳转至第三方网页， 请以http://开头。
		bundle.putString("comment", ("QQ登陆SDK：测试comment" + new Date()));//用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
		bundle.putString("summary", "QQ登陆SDK：测试summary");//所分享的网页资源的摘要内容，或者是网页的概要描述。 最长80个中文字，超出部分会被截断。
		bundle.putString("images", "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");//所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
		bundle.putString("type", "5");//分享内容的类型。
		bundle.putString("playurl", "http://player.youku.com/player.php/Type/Folder/Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");//长度限制为256字节。仅在type=5的时候有效。
		TencentOpenAPI.addShare(mActivity.mAccessToken, mActivity.mAppid, mActivity.mOpenId, bundle, new Callback() {
			
			
			public void onSuccess(final Object obj) {
				mActivity.runOnUiThread(new Runnable() {
					
					
					public void run() {
						mActivity.dismissDialog(QQLoginActivity.PROGRESS);
//						mActivity.showMessage("分享到QQ空间返回数据", "share_id: " + obj.toString());
					}
				});
			}
			
			
			public void onFail(final int ret, final String msg) {
				mActivity.runOnUiThread(new Runnable() {
					
					
					public void run() {
						mActivity.dismissDialog(QQLoginActivity.PROGRESS);
						TDebug.msg(ret + ": " + msg, mActivity);
					}
				});
			}
		});
	}

}
