package com.digitalorder.store;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.card.MaterialCardView;

/**
 * Settings Activity - شاشة الإعدادات
 * Version: 2.0.0
 * 
 * توفر إعدادات التطبيق:
 * - الوضع الليلي
 * - مسح الكاش
 * - معلومات التطبيق
 */
public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SwitchMaterial darkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }

        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Initialize views
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        MaterialCardView clearCacheCard = findViewById(R.id.clearCacheCard);
        MaterialCardView aboutCard = findViewById(R.id.aboutCard);

        // Load current settings
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);

        // Dark mode switch listener
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
            
            // Apply theme
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            
            // Recreate activity to apply theme
            recreate();
        });

        // Clear cache listener
        clearCacheCard.setOnClickListener(v -> showClearCacheDialog());

        // About listener
        aboutCard.setOnClickListener(v -> showAboutDialog());
    }

    /**
     * عرض حوار تأكيد مسح الكاش
     */
    private void showClearCacheDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.clear_cache)
            .setMessage(R.string.clear_cache_message)
            .setPositiveButton(R.string.clear, (dialog, which) -> {
                clearCache();
                Toast.makeText(this, R.string.cache_cleared, Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    /**
     * مسح الكاش
     */
    private void clearCache() {
        try {
            deleteCache(getCacheDir());
            deleteCache(getExternalCacheDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * حذف ملفات الكاش
     */
    private void deleteCache(java.io.File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    deleteCache(new java.io.File(dir, child));
                }
            }
        }
        if (dir != null) {
            dir.delete();
        }
    }

    /**
     * عرض معلومات التطبيق
     */
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.about_app)
            .setMessage(getString(R.string.about_message))
            .setPositiveButton(R.string.ok, null)
            .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
