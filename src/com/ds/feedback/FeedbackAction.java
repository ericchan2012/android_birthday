package com.ds.feedback;

import android.content.Context;

public class FeedbackAction {
	
	private Context mContext = null;
	private static final int SUCCESS = 0;
	private static final int FAILURE = 1;
	
	public FeedbackAction(Context context) {
		mContext = context;
	}

	/**
	 * �˴����ڽ�������Ϣ���͸������
	 * @param content
	 * @param contact
	 * @return
	 */
	public int sendFeedbackMessage(String content, String contact) {
		return SUCCESS;
	}

}
