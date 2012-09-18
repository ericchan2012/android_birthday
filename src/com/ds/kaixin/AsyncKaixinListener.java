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
 * �첽����������������
 */
public interface AsyncKaixinListener {
	/**
	 * �������
	 * 
	 * @param response
	 *            ���������ص�JSON��
	 * @param obj
	 *            �����첽����ʱ���õĹ��������
	 * @see AsyncKaixin
	 */
	public void onRequestComplete(String response, Object obj);

	/**
	 * ���������ش�����Ϣ
	 * 
	 * @param kaixinError
	 *            ��װ�������صĴ�����Ϣ
	 * @param obj
	 *            �����첽����ʱ���õĹ��������
	 * @see AsyncKaixin
	 */
	public void onRequestError(KaixinError kaixinError, Object obj);

	/**
	 * �������з����˴���
	 * 
	 * @param fault
	 *            ���������׳����쳣
	 * @param obj
	 *            �����첽����ʱ���õĹ��������
	 * @see AsyncKaixin
	 */
	public void onRequestNetError(Throwable fault, Object obj);
}
