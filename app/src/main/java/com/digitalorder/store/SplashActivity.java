package com.digitalorder.store;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash Activity - شاشة البداية
 * Version: 2.0.0
 * 
 * تعرض شعار التطبيق لمدة 2 ثانية ثم تنتقل للشاشة الرئيسية
 * 
 * التحديثات في v1.0.7:
 * - إضافة logging لتتبع المشاكل
 * - معالجة أخطاء محسّنة
 * - التأكد من عدم حدوث crash
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "SplashActivity onCreate started");
            setContentView(R.layout.activity_splash);

            // إنشاء Handler و Runnable
            handler = new Handler(Looper.getMainLooper());
            runnable = () -> {
                try {
                    // التحقق من أن Activity لا تزال نشطة
                    if (!isFinishing() && !isDestroyed()) {
                        Log.d(TAG, "Starting MainActivity");
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d(TAG, "MainActivity started successfully");
                    } else {
                        Log.w(TAG, "Activity is finishing or destroyed, skipping MainActivity start");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error starting MainActivity: " + e.getMessage(), e);
                    // في حالة حدوث خطأ، نحاول مرة أخرى
                    finish();
                }
            };
            
            // الانتقال للشاشة الرئيسية بعد فترة الانتظار
            handler.postDelayed(runnable, SPLASH_DURATION);
            Log.d(TAG, "Handler scheduled for " + SPLASH_DURATION + "ms");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            // في حالة حدوث خطأ، ننتقل مباشرة للشاشة الرئيسية
            try {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception ex) {
                Log.e(TAG, "Fatal error: " + ex.getMessage(), ex);
                finish();
            }
        }
    }

    /**
     * تنظيف Handler عند إغلاق Activity لمنع memory leak
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
                handler = null;
                runnable = null;
            }
            Log.d(TAG, "SplashActivity destroyed");
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage(), e);
        }
    }
}
