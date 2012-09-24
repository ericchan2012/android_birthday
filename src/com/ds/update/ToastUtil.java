package com.ds.update;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ToastUtil {
	private Context context;

	public ToastUtil(Context context) {
		this.context = context;
	}

	/**
	 * @param title
	 * @param icon
	 * @return返回一个带图片的toast
	 */
	public Toast showPicToast(String title, Drawable icon) {
		Toast toast = Toast.makeText(context, title, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		ImageView imageCodeProject = new ImageView(context);
		imageCodeProject.setBackgroundDrawable(icon);
		toastView.addView(imageCodeProject, 0);
		return toast;
	}

	/**
	 * @param title
	 * @return返回一个默认的toast
	 */
	public Toast showDefultToast(String title) {
		Toast toast = Toast.makeText(context, title, Toast.LENGTH_SHORT);
		return toast;
	}

	/**
	 * @param layoutid
	 * @return返回一个自定义的toast
	 */
	public Toast showDiyToast(int layoutid) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View myView = inflater.inflate(layoutid, null);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.RIGHT | Gravity.TOP, 12, 40);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(myView);
		return toast;
	}
}
