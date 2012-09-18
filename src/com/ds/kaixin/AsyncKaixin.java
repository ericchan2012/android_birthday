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


import android.content.Context;
import android.os.Bundle;


/**
 * ��Kaixin�е������װ���첽����
 * 
 * @see Kaixin
 * @see AsyncKaixinListener
 * 
 */

public class AsyncKaixin {
	private Kaixin mKaixin;

	public AsyncKaixin(Kaixin kaixin) {
		mKaixin = kaixin;
	}

	/**
	 * �첽����
	 * 
	 * @param context
	 *            Ӧ�û���
	 * @param restInterface
	 *            rest api�ӿ�
	 * @param params
	 *            key-value��ʽ�Ĳ���
	 * @param httpMethod
	 *            http����ķ�����GET �� POST
	 * @param obj
	 *            �첽�����������ݣ��������󣬻�ͨ��AsyncKaixinListener�Ľӿڴ��ݸ������
	 * @param listener
	 *            �첽���������
	 */
	public void request(final Context context, final String restInterface,
			final Bundle params, final String httpMethod,
			final AsyncKaixinListener listener, final Object obj) {
		new Thread() {
			@Override
			public void run() {
				try {
					String resp = mKaixin.request(context, restInterface, params,
							httpMethod);
					KaixinError error = Util.parseRequestError(resp);
					if (error != null) {
						listener.onRequestError(error, obj);
					} else {
						listener.onRequestComplete(resp, obj);
					}

				} catch (Throwable e) {
					listener.onRequestNetError(e, obj);
				}
			}
		}.start();
	}
	
	/**
	 * �첽����
	 * 
	 * @param context
	 *            Ӧ�û���
	 * @param restInterface
	 *            rest api�ӿ�
	 * @param params
	 *            key-value��ʽ�Ĳ���
	 * @param httpMethod
	 *            http����ķ�����GET �� POST
	 * @param obj
	 *            �첽�����������ݣ��������󣬻�ͨ��AsyncKaixinListener�Ľӿڴ��ݸ������
	 * @param listener
	 *            �첽���������
	 */
	public void login(final Context context, final Bundle params, final AsyncKaixinListener listener, final Object obj) {
		new Thread() {
			@Override
			public void run() {
				try {
					String sError = mKaixin.login( params, context);
					KaixinError error = new KaixinError(sError);
					if (sError != null) {
						listener.onRequestError(error, obj);
					} else {
						listener.onRequestComplete(null, obj);
					}

				} catch (Throwable e) {
					listener.onRequestNetError(e, obj);
				}
			}
		}.start();
	}

}
