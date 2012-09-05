package com.ds.birth;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class NavigationActivity extends TabActivity implements OnClickListener {
	private static final int BIRTH = 0;
	private static final int RANKING = 1;
	private static final int MINE = 2;
	private static final int MORE = 3;

	RadioButton mBirthBtn;
	RadioButton mRankingBtn;
	RadioButton mMoreBtn;
	RadioButton mMineBtn;
	TabHost mTabHost;

	Intent mBirthIntent;
	Intent mRankingIntent;
	Intent mMineIntent;
	Intent mMoreIntent;

	private static final String TAB_SPEC_BRAND = "brand";
	private static final String TAB_SPEC_RANKING = "ranking";
	private static final String TAB_SPEC_MINE = "mine";
	private static final String TAB_SPEC_FAVORITE = "favorite";

	private TabSpec mBirthTabSpec;
	private TabSpec mRankingTabSpec;
	private TabSpec mMineTabSpec;
	private TabSpec mMoreTabSpec;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_layout);
		initIntents();
		initTabHost();

	}

	private void initTabHost() {
		// TODO Auto-generated method stub
		mTabHost.addTab(mBirthTabSpec);
		mTabHost.addTab(mRankingTabSpec);
		mTabHost.addTab(mMineTabSpec);
		mTabHost.addTab(mMoreTabSpec);

		mTabHost.setCurrentTab(0);
	}

	private void initIntents() {
		mTabHost = getTabHost();
		mBirthIntent = new Intent(this, BirthActivity.class);
		mRankingIntent = new Intent(this, RankingActivity.class);
		mMineIntent = new Intent(this, MineActivity.class);
		mMoreIntent = new Intent(this, MoreActivity.class);

		mBirthTabSpec = mTabHost.newTabSpec(TAB_SPEC_BRAND)
				.setIndicator("tab1").setContent(mBirthIntent);
		mRankingTabSpec = mTabHost.newTabSpec(TAB_SPEC_RANKING)
				.setIndicator("tab2").setContent(mRankingIntent);
		mMineTabSpec = mTabHost.newTabSpec(TAB_SPEC_MINE).setIndicator("tab3")
				.setContent(mMineIntent);
		mMoreTabSpec = mTabHost.newTabSpec(TAB_SPEC_FAVORITE)
				.setIndicator("tab4").setContent(mMoreIntent);

		mBirthBtn = (RadioButton) findViewById(R.id.tab_brand);
		mRankingBtn = (RadioButton) findViewById(R.id.tab_rank);
		mMineBtn = (RadioButton) findViewById(R.id.tab_mine);
		mMoreBtn = (RadioButton) findViewById(R.id.tab_more);
		mBirthBtn.setOnClickListener(this);
		mRankingBtn.setOnClickListener(this);
		mMoreBtn.setOnClickListener(this);
		mMineBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_brand:
			mTabHost.setCurrentTab(BIRTH);
			break;
		case R.id.tab_rank:
			mTabHost.setCurrentTab(RANKING);
			break;
		case R.id.tab_mine:
			mTabHost.setCurrentTab(MINE);
			break;
		case R.id.tab_more:
			mTabHost.setCurrentTab(MORE);
			break;
		}
	}
}
