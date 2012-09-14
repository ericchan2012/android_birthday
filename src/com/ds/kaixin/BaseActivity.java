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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ds.birth.R;

/**
 * ����Ϊ���Դ�����Ȩ�Ļ��࣬������Լ̳����Լ�ҳ���߼�
 */
public class BaseActivity extends Activity {
	private static final String TAG = "BaseActivity";

	protected Kaixin kaixin = Kaixin.getInstance();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (kaixin.isSessionValid()) {
			doOnResume();
		}
	}

	protected Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dealWithMessage(msg);
		}
	};

	/**
	 * ��Kaixinʵ���ʼ����ɣ��ſ�ʼ����ҳ���߼�
	 */
	protected void doOnResume() {

	}

	/**
	 * ��Kaixinʵ���ʼ����ɣ��ſ�ʼ����ҳ���߼�
	 */
	protected void doOnPause() {

	}

	/**
	 * ����Message��������Ե��ô˷��������쳣����
	 * 
	 * @param msg
	 *            Message
	 */
	protected void dealWithMessage(Message msg) {
		switch (msg.what) {
		case Constant.RESULT_FAILED_NETWORK_ERR:
			break;
		case Constant.RESULT_FAILED_JSON_PARSE_ERR:
			break;
		case Constant.RESULT_FAILED_ARG_ERR:
			break;
		case Constant.RESULT_FAILED_MALFORMEDURL_ERR:
			break;
		case Constant.RESULT_FAILED_ENCODER_ERR:
			break;
		case Constant.RESULT_FAILED:
			break;
		case Constant.RESULT_FAILED_REQUEST_ERR:
			KaixinError kaixinError = (KaixinError) msg.obj;
			Toast.makeText(getApplicationContext(), kaixinError.toString(),
					Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (kaixin.isSessionValid()) {
			doOnPause();
		}
	}
}