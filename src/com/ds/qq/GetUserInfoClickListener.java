package com.ds.qq;

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
public class GetUserInfoClickListener implements OnClickListener {
	private QQLoginActivity mActivity;

	public GetUserInfoClickListener(QQLoginActivity activity) {
		mActivity = activity;
	}

	
	public void onClick(View v) {
		if (!mActivity.satisfyConditions()) {
			TDebug.msg("请先获取access token和open id", mActivity);
			return;
		}
		mActivity.showDialog(QQLoginActivity.PROGRESS);
		TencentOpenAPI.userInfo(mActivity.mAccessToken, mActivity.mAppid, mActivity.mOpenId, new Callback() {
			
			
			public void onSuccess(final Object obj) {
				mActivity.runOnUiThread(new Runnable() {
					
					
					public void run() {
						mActivity.dismissDialog(QQLoginActivity.PROGRESS);
//						mActivity.showMessage("用户信息", obj.toString());
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
