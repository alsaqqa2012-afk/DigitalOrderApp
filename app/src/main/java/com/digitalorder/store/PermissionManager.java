package com.digitalorder.store;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

/**
 * Permission Manager - إدارة الصلاحيات
 * Version: 2.0.0
 * 
 * يدير طلب الصلاحيات في وقت التشغيل بطريقة احترافية
 */
public class PermissionManager {
    
    private final Activity activity;
    private ActivityResultLauncher<String> permissionLauncher;
    private PermissionCallback callback;
    
    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }
    
    public PermissionManager(Activity activity) {
        this.activity = activity;
    }
    
    /**
     * تسجيل launcher للصلاحيات
     */
    public void registerPermissionLauncher(ActivityResultLauncher<String> launcher) {
        this.permissionLauncher = launcher;
    }
    
    /**
     * طلب صلاحية التخزين
     */
    public void requestStoragePermission(PermissionCallback callback) {
        this.callback = callback;
        
        // للأجهزة الحديثة (Android 10+) لا نحتاج صلاحية التخزين
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (callback != null) {
                callback.onPermissionGranted();
            }
            return;
        }
        
        // التحقق من الصلاحية
        if (ContextCompat.checkSelfPermission(activity, 
                Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_GRANTED) {
            if (callback != null) {
                callback.onPermissionGranted();
            }
        } else {
            // عرض توضيح للمستخدم قبل طلب الصلاحية
            showPermissionRationale();
        }
    }
    
    /**
     * عرض توضيح للمستخدم حول سبب الحاجة للصلاحية
     */
    private void showPermissionRationale() {
        new AlertDialog.Builder(activity)
            .setTitle(R.string.storage_permission_title)
            .setMessage(R.string.storage_permission_message)
            .setPositiveButton(R.string.ok, (dialog, which) -> {
                // طلب الصلاحية
                if (permissionLauncher != null) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            })
            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                if (callback != null) {
                    callback.onPermissionDenied();
                }
            })
            .setCancelable(false)
            .show();
    }
    
    /**
     * معالجة نتيجة طلب الصلاحية
     */
    public void handlePermissionResult(boolean granted) {
        if (callback != null) {
            if (granted) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
    }
    
    /**
     * التحقق من صلاحية التخزين
     */
    public boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return ContextCompat.checkSelfPermission(activity, 
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
