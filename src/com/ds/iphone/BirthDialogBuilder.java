package com.ds.iphone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class BirthDialogBuilder extends AlertDialog.Builder {

	private BirthDialog md;
	private Context context;

	public BirthDialogBuilder(Context context) {
		super(context);
		md = new BirthDialog(context);
		this.context = context;
	}

	public BirthDialogBuilder setMessage(int messageId) {
		md.setMessage(context.getResources().getString(messageId));
		return this;
	}

	public BirthDialogBuilder setMessage(CharSequence message) {
		md.setMessage(message);
		return this;
	}

	public BirthDialogBuilder setTitle(int titleId) {
		md.setTitle(context.getResources().getString(titleId));
		return this;
	}

	public BirthDialogBuilder setTitle(CharSequence title) {
		md.setTitle(title);
		return this;
	}

	// 认同按钮
	public BirthDialogBuilder setPositiveButton(int textId,
			OnClickListener listener) {
		md.setButton(context.getResources().getString(textId), listener);
		return this;
	}

	// 认同按钮
	public BirthDialogBuilder setPositiveButton(CharSequence text,
			OnClickListener listener) {
		md.setButton(text, listener);
		return this;
	}

	// 中立按钮
	public BirthDialogBuilder setNeutralButton(int textId,
			OnClickListener listener) {
		md.setButton2(context.getResources().getString(textId), listener);
		return this;
	}

	// 中立按钮
	public BirthDialogBuilder setNeutralButton(CharSequence text,
			OnClickListener listener) {
		md.setButton2(text, listener);
		return this;
	}

	// 否定按钮
	public BirthDialogBuilder setNegativeButton(int textId,
			OnClickListener listener) {
		md.setButton3(context.getResources().getString(textId), listener);
		return this;
	}

	// 否定按钮
	public BirthDialogBuilder setNegativeButton(CharSequence text,
			OnClickListener listener) {
		md.setButton3(text, listener);
		return this;
	}

	@Override
	public BirthDialog create() {
		return md;
	}
}