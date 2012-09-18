package com.ds.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ds.birth.R;

public class ImageBtn extends LinearLayout {

	private ImageView iv;
	private TextView tv;
	private LayoutInflater mInflater;

	public ImageBtn(Context context) {
		this(context, null);
	}

	public ImageBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.birth_cloud_item, this, true);
//		setBackgroundResource(android.R.drawable.btn_default);
		setOrientation(LinearLayout.VERTICAL);
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		iv = (ImageView) findViewById(R.id.iv);
		tv = (TextView) findViewById(R.id.tv);
	}

	public void setImageResource(int resId) {
		iv.setImageResource(resId);
	}

	public void setText(int resId) {
		tv.setText(resId);
	}

	// public ImageBtn(Context context, AttributeSet attrs) {
	// super(context, attrs);
	// }
	//
	// public ImageBtn(Context context, int imageResId, int textResId) {
	//
	// super(context);
	//
	// mButtonImage = new ImageView(context);
	//
	// mButtonText = new TextView(context);
	//
	// setImageResource(imageResId);
	//
	// mButtonImage.setPadding(0, 0, 0, 0);
	//
	// setText(textResId);
	// setTextColor(0xFF000000);
	//
	// mButtonText.setPadding(0, 0, 0, 0);
	//
	// // 设置本布局的属性
	// setClickable(true); // 可点击
	// setFocusable(true); // 可聚焦
	// setBackgroundResource(android.R.drawable.btn_default); // 布局才用普通按钮的背景
	// setOrientation(LinearLayout.VERTICAL); // 垂直布局
	//
	// // 首先添加Image，然后才添加Text
	//
	// // 添加顺序将会影响布局效果
	// addView(mButtonImage);
	// addView(mButtonText);
	// }
	//
	// // ----------------public method-----------------------------
	//
	// /*
	// * setImageResource方法
	// */
	//
	// public void setImageResource(int resId) {
	//
	// mButtonImage.setImageResource(resId);
	// }
	//
	// /*
	// * setText方法
	// */
	//
	// public void setText(int resId) {
	//
	// mButtonText.setText(resId);
	// }
	//
	// public void setText(CharSequence buttonText) {
	//
	// mButtonText.setText(buttonText);
	// }
	//
	// /*
	// * setTextColor方法
	// */
	//
	// public void setTextColor(int color) {
	//
	// mButtonText.setTextColor(color);
	// }
	//
	// // ----------------private attribute-----------------------------
	//
	// private ImageView mButtonImage = null;
	//
	// private TextView mButtonText = null;
}
