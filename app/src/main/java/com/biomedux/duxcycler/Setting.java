// ============================================================
// FileName		: Setting.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Setting {

    // ============================================================
    // Constants
    // ============================================================

    public final static int VIEWPORT_WIDTH			= 2960;
    public final static int VIEWPORT_HEIGHT			= 1440;

    // ============================================================
    // Fields
    // ============================================================

    private int deviceWidth;
    private int deviceHeight;

    private View decorView;
    private int option;

    // ============================================================
    // Constructors
    // ============================================================

    public Setting(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        deviceWidth = metrics.widthPixels;
        deviceHeight = metrics.heightPixels;

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        decorView = activity.getWindow().getDecorView();
        option = decorView.getSystemUiVisibility();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            option |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            option |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        hideNavigation();
    }

    // ============================================================
    // Getter & Setter
    // ============================================================

    public int getDeviceWidth() {
        return deviceWidth;
    }

    public int getDeviceHeight() {
        return deviceHeight;
    }

    public float getScaleX() {
        return (float) VIEWPORT_WIDTH / (float) deviceWidth;
    }

    public float getScaleY() {
        return (float) VIEWPORT_HEIGHT / (float) deviceHeight;
    }

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    // ============================================================
    // Methods
    // ============================================================

    public void hideNavigation() {
        decorView.setSystemUiVisibility(option);
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}
