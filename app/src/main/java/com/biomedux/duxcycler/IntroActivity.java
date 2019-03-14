// ============================================================
// FileName		: IntroActivity.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class IntroActivity extends Activity {

    // ============================================================
    // Constants
    // ============================================================

    // ============================================================
    // Fields
    // ============================================================

    private Handler handler = new Handler();
    private Setting setting;

    // ============================================================
    // Constructors
    // ============================================================

    // ============================================================
    // Getter & Setter
    // ============================================================

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setting = new Setting(this);
        setContentView(R.layout.activity_intro);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1500);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            setting.hideNavigation();
    }

    // ============================================================
    // Methods
    // ============================================================

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

    };
}
