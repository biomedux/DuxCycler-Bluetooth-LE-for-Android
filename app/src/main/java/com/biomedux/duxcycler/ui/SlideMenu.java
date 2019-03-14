// ============================================================
// FileName		: SlideMenu.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler.ui;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SlideMenu {

	// ============================================================
	// Constants
	// ============================================================

	// ============================================================
	// Fields
	// ============================================================

	private LinearLayout menuLayout;
	private LinearLayout shadowLayout;
	private int screenWidth;
	private int scaleWidth;

	private boolean expanded;

	// ============================================================
	// Constructors
	// ============================================================

	public SlideMenu(Activity activity, int menuLayoutId, int shadowLayoutId, float width) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;

		scaleWidth = (int) (screenWidth * width);

		menuLayout = (LinearLayout) activity.findViewById(menuLayoutId);
		FrameLayout.LayoutParams menuParams = (FrameLayout.LayoutParams) menuLayout.getLayoutParams();
		menuParams.rightMargin = 0;
		menuParams.leftMargin = -scaleWidth;
		menuParams.width = scaleWidth;
		menuLayout.setLayoutParams(menuParams);

		shadowLayout = (LinearLayout) activity.findViewById(shadowLayoutId);
		FrameLayout.LayoutParams shadowParams = (FrameLayout.LayoutParams) shadowLayout.getLayoutParams();
		shadowParams.rightMargin = 0;
		shadowParams.leftMargin = -screenWidth;
		shadowParams.width = screenWidth;
		shadowLayout.setLayoutParams(shadowParams);
	}

	// ============================================================
	// Getter & Setter
	// ============================================================

	public boolean isExpanded() {
		return expanded;
	}

	// ============================================================
	// Methods for/from SuperClass/Interfaces
	// ============================================================

	// ============================================================
	// Methods
	// ============================================================

	public void menuToggle() {
		if (expanded) {
			menuLayout.startAnimation(translateAnimation(0.0f, -1.0f, 250, translateOFF));
			shadowLayout.startAnimation(alphaAnimation(0.6f, 0.0f, 250));
		} else {
			menuLayout.startAnimation(translateAnimation(-1.0f, 0.0f, 250, translateON));
			shadowLayout.startAnimation(alphaAnimation(0.0f, 0.6f, 250));
		}

		expanded = !expanded;
	}

	private Animation alphaAnimation(float from, float to, int duration) {
		Animation alphaAnimation = new AlphaAnimation(from, to);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setFillAfter(true);
		return alphaAnimation;
	}

	private Animation translateAnimation(float from, float to, int duration, TranslateAnimation.AnimationListener listener) {
		Animation translateAnimation = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_SELF, from,
				TranslateAnimation.RELATIVE_TO_SELF, to
				, 0, 0.0f, 0, 0.0f);
		translateAnimation.setAnimationListener(listener);
		translateAnimation.setDuration(duration);
		translateAnimation.setFillAfter(true);
		return translateAnimation;
	}

	// ============================================================
	// Inner and Anonymous Classes
	// ============================================================

	TranslateAnimation.AnimationListener translateON = new TranslateAnimation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			FrameLayout.LayoutParams menuParams = (FrameLayout.LayoutParams) menuLayout.getLayoutParams();
			menuParams.leftMargin = 0;
			menuParams.width = scaleWidth;
			menuLayout.setLayoutParams(menuParams);

			FrameLayout.LayoutParams shadowParams = (FrameLayout.LayoutParams) shadowLayout.getLayoutParams();
			shadowParams.leftMargin = 0;
			shadowParams.width = screenWidth;
			shadowLayout.setLayoutParams(shadowParams);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};

	TranslateAnimation.AnimationListener translateOFF = new TranslateAnimation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			FrameLayout.LayoutParams menuParams = (FrameLayout.LayoutParams) menuLayout.getLayoutParams();
			menuParams.leftMargin = -scaleWidth;
			menuParams.rightMargin = 0;
			menuParams.width = scaleWidth;
			menuLayout.setLayoutParams(menuParams);

			FrameLayout.LayoutParams shadowParams = (FrameLayout.LayoutParams) shadowLayout.getLayoutParams();
			shadowParams.leftMargin = -screenWidth;
			shadowParams.rightMargin = 0;
			shadowParams.width = screenWidth;
			shadowLayout.setLayoutParams(shadowParams);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};
}
