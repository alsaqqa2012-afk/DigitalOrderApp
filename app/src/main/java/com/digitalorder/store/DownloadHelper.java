package com.digitalorder.store;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

/**
 * Helper class for handling file downloads
 * Version: 2.0.0
 * 
 * التحديثات في v1.0.5:
 * - إضافة التحقق من الصلاحيات قبل التحميل
 * - استخدام string resources بدلاً من النصوص المباشرة
 * - إضافة معالجة أفضل للأخطاء مع logging
 * - إضافة التحقق من null للـ cookies
 */
public class DownloadHelper {
    
    private static final String TAG = "DownloadHelper";
    
    /**
     * Download file using DownloadManager
     */
    public static void downloadFile(Context context, String url, String userAgent, 
                                   String contentDisposition, String mimeType) {
        try {
            // التحقق من الصلاحيات للأجهزة القديمة (Android 9 وما قبل)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ContextCompat.checkSelfPermission(context, 
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, R.string.storage_permission_required, 
                        Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Storage permission not granted");
                    return;
                }
            }
            
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);
            
            // Get cookies and add if not null
            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null && !cookies.isEmpty()) {
                request.addRequestHeader("cookie", cookies);
            }
            request.addRequestHeader("User-Agent", userAgent);
            
            // Set notification
            request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            
            // Set file name and destination
            String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            
            // Enqueue download
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
                Toast.makeText(context, 
                    context.getString(R.string.downloading_file, fileName), 
                    Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Download started: " + fileName);
            } else {
                Log.e(TAG, "DownloadManager service is null");
                Toast.makeText(context, R.string.download_failed, Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception during download", e);
            Toast.makeText(context, R.string.storage_permission_required, 
                Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid download URL or parameters", e);
            Toast.makeText(context, R.string.download_failed, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during download", e);
            Toast.makeText(context, R.string.download_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
