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

/**
 * KaixinDialog�¼�������
 */
public interface KaixinDialogListener {

	/**
	 * δ����
	 */
	public final static int UNPROCCESS = 0;

	/**
	 * �Ѵ���
	 */
	public final static int PROCCESSED = 1;

	/**
	 * ��Dialog����
	 */
	public final static int DIALOG_PROCCESS = 2;

	/**
	 * ҳ�����֮ǰ���á�
	 * 
	 * @param url
	 * @return 0:δ���?1:�Ѿ����?2:��Dialog����
	 */
	public int onPageBegin(String url);

	/**
	 * ҳ�濪ʼ����ʱ����
	 * 
	 * @param url
	 * @return
	 */
	public boolean onPageStart(String url);

	/**
	 * ҳ����ؽ�������
	 * 
	 * @param url
	 */
	public void onPageFinished(String url);

	/**
	 * ���ִ������
	 * 
	 * @param errorCode
	 * @param description
	 * @param failingUrl
	 */
	public void onReceivedError(int errorCode, String description,
			String failingUrl);
}
