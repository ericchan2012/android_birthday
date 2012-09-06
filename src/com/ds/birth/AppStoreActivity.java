package com.ds.birth;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppStoreActivity extends Activity {
	/** Called when the activity is first created. */
	private RelativeLayout layout;

	private RelativeLayout layout1;
	private RelativeLayout layout2;
	private RelativeLayout layout3;
	private TextView tab1;
	private TextView tab2;
	private TextView tab3;
	private TextView first;
	private int current = 1;

	private LinearLayout page;

	private boolean isAdd = false;
	private int select_width;
	private int select_height;
	private int firstLeft;
	private int startLeft;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_store);

		init();
	}

	private void init() {
		layout = (RelativeLayout) findViewById(R.id.root);

		layout1 = (RelativeLayout) findViewById(R.id.layout1);
		layout2 = (RelativeLayout) findViewById(R.id.layout2);
		layout3 = (RelativeLayout) findViewById(R.id.layout3);

		page = (LinearLayout) this.findViewById(R.id.page);

		tab1 = (TextView) findViewById(R.id.tab1);
		tab1.setOnClickListener(onClickListener);
		tab2 = (TextView) findViewById(R.id.tab2);
		tab2.setOnClickListener(onClickListener);
		tab3 = (TextView) findViewById(R.id.tab3);
		tab3.setOnClickListener(onClickListener);

		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		first = new TextView(this);
		first.setTag("first");
		first.setGravity(Gravity.CENTER);
		first.setBackgroundResource(R.drawable.slidebar);
		first.setText(tab1.getText());
		View view1 = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.page1, null);
		page.addView(view1);

		switch (current) {
		case 1:
			layout1.addView(first, rl);
			current = R.id.tab1;
			break;
		case 2:
			layout2.addView(first, rl);
			current = R.id.tab2;
			break;
		case 3:
			layout3.addView(first, rl);
			current = R.id.tab3;
			break;
		default:
			break;
		}

	}

	private void replace() {
		switch (current) {
		case R.id.tab1:
			changeTop(layout1);
			break;
		case R.id.tab2:
			changeTop(layout2);
			break;
		case R.id.tab3:
			changeTop(layout3);
			break;
		default:
			break;
		}
	}

	private void changeTop(RelativeLayout relativeLayout) {
		TextView old = (TextView) relativeLayout.findViewWithTag("first");
		select_width = old.getWidth();
		select_height = old.getHeight();

		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
				select_width, select_height);
		rl.leftMargin = old.getLeft()
				+ ((RelativeLayout) old.getParent()).getLeft();
		rl.topMargin = old.getTop()
				+ ((RelativeLayout) old.getParent()).getTop();

		firstLeft = old.getLeft()
				+ ((RelativeLayout) old.getParent()).getLeft();

		TextView tv = new TextView(this);
		tv.setTag("move");
		tv.setBackgroundResource(R.drawable.slidebar);

		layout.addView(tv, rl);
		relativeLayout.removeView(old);
	}

	private OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (!isAdd) {
				replace();
				isAdd = true;
			}

			TextView top_select = (TextView) layout.findViewWithTag("move");
			top_select.setGravity(Gravity.CENTER);
			top_select.setText(tab1.getText());
			int tabLeft;
			int endLeft = 0;

			boolean run = false;

			switch (v.getId()) {
			case R.id.tab1:
				if (current != R.id.tab1) {
					page.removeAllViews();
					tabLeft = ((RelativeLayout) tab1.getParent()).getLeft()
							+ tab1.getLeft() + tab1.getWidth() / 2;
					endLeft = tabLeft - select_width / 2;
					current = R.id.tab1;
					top_select.setText(tab1.getText());
					run = true;
					View view1 = LayoutInflater.from(getApplicationContext())
							.inflate(R.layout.page1, null);
					page.addView(view1);
				}
				break;
			case R.id.tab2:
				if (current != R.id.tab2) {
					page.removeAllViews();
					tabLeft = ((RelativeLayout) tab2.getParent()).getLeft()
							+ tab2.getLeft() + tab2.getWidth() / 2;
					endLeft = tabLeft - select_width / 2;
					current = R.id.tab2;
					top_select.setText(tab2.getText());
					run = true;
					View view2 = LayoutInflater.from(getApplicationContext())
							.inflate(R.layout.page2, null);
					page.addView(view2);
				}
				break;
			case R.id.tab3:
				if (current != R.id.tab3) {
					page.removeAllViews();
					tabLeft = ((RelativeLayout) tab3.getParent()).getLeft()
							+ tab3.getLeft() + tab3.getWidth() / 2;
					endLeft = tabLeft - select_width / 2;
					current = R.id.tab3;
					top_select.setText(tab3.getText());
					run = true;
					View view3 = LayoutInflater.from(getApplicationContext())
							.inflate(R.layout.page3, null);
					page.addView(view3);
				}
				break;
			default:
				break;
			}

			if (run) {
				TranslateAnimation animation = new TranslateAnimation(
						startLeft, endLeft - firstLeft, 0, 0);
				startLeft = endLeft - firstLeft;
				animation.setDuration(100);
				animation.setFillAfter(true);
				top_select.bringToFront();
				top_select.startAnimation(animation);

			}

		}

	};
}