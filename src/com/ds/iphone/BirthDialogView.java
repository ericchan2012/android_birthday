package com.ds.iphone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ds.birth.R;
public class BirthDialogView extends RelativeLayout {

	private TextView itemMessage, itemTitle;
	private Button bt_cancel, bt_yes, bt_no;

	public BirthDialogView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BirthDialogView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		itemTitle = (TextView) findViewById(R.id.dialog_title);
		itemMessage = (TextView) findViewById(R.id.dialog_message);

		bt_yes = (Button) findViewById(R.id.dialog_yes);
		bt_no = (Button) findViewById(R.id.dialog_no);
		bt_cancel = (Button) findViewById(R.id.dialog_cancel);

		super.onFinishInflate();
	}

	public void setTitle(int TitleId) {
		this.itemTitle.setText(TitleId);
	}

	public void setTitle(CharSequence title) {
		this.itemTitle.setText(title);
	}

	public void setMessage(int MessageId) {
		this.itemMessage.setText(MessageId);
	}

	public void setMessage(CharSequence message) {
		this.itemMessage.setText(message);
	}

	public void HiddenButton(int ButtonId) {
		if (ButtonId == R.id.dialog_yes) {
			this.bt_yes.setVisibility(GONE);
		} else if (ButtonId == R.id.dialog_no) {
			this.bt_no.setVisibility(GONE);
		} else if (ButtonId == R.id.dialog_cancel) {
			this.bt_cancel.setVisibility(GONE);
		}
	}
}
