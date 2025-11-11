package com.digitalorder.store;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash Activity - شاشة البداية
 * Version: 1.0.4
 * 
 * تعرض شعار التطبيق لمدة 2 ثانية ثم تنتقل للشاشة الرئيسية
 * 
 * التحديثات في v1.0.1:
 * - إصلاح memory leak بإلغاء Handler في onDestroy
 * - منع فتح MainActivity إذا تم إغلاق Activity
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // إنشاء Handler و Runnable
        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            // التحقق من أن Activity لا تزال نشطة
            if (!isFinishing() && !isDestroyed()) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        
        // الانتقال للشاشة الرئيسية بعد فترة الانتظار
        handler.postDelayed(runnable, SPLASH_DURATION);
    }

    /**
     * تنظيف Handler عند إغلاق Activity لمنع memory leak
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
    }
}
