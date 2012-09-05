package com.ds.birth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class SplashActivity extends Activity {
	ImageView splashImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		splashImage = (ImageView) findViewById(R.id.splash_open_up);

		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 1);
		alphaAnimation.setDuration(3000);
		alphaAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				// splashImage.setVisibility(View.GONE);
				startActivity(new Intent(SplashActivity.this,
						NavigationActivity.class));
				SplashActivity.this.finish();
			}
		});

		splashImage.setAnimation(alphaAnimation);
		splashImage.setVisibility(View.VISIBLE);
	}
}
