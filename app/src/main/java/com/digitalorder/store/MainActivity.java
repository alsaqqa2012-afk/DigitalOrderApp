package com.digitalorder.store;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * Main Activity - الشاشة الرئيسية
 * Version: 1.0.7
 * 
 * نسخة محسّنة مع ميزات احترافية:
 * - نظام صلاحيات متقدم
 * - دعم الوضع الليلي
 * - قائمة خيارات محسّنة
 * - رسوم متحركة سلسة
 * - مشاركة المحتوى
 * - تحسينات في الأداء
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout;
    private TextView errorText;
    private MaterialButton retryButton;
    private String websiteUrl;
    private long backPressedTime;
    private Toast backToast;
    private PermissionManager permissionManager;
    private SharedPreferences preferences;
    private ActivityResultLauncher<String> permissionLauncher;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity onCreate started");
        
        try {
        
        // تطبيق الوضع الليلي من الإعدادات
        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        applyTheme();
        
        setContentView(R.layout.activity_main);

        // Initialize permission manager
        permissionManager = new PermissionManager(this);
        
        // تسجيل launcher للصلاحيات
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> permissionManager.handlePermissionResult(granted)
        );
        permissionManager.registerPermissionLauncher(permissionLauncher);

        // Initialize views

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        errorLayout = findViewById(R.id.errorLayout);
        errorText = findViewById(R.id.errorText);
        retryButton = findViewById(R.id.retryButton);

        websiteUrl = getString(R.string.website_url);

        // Configure WebView
        setupWebView();

        // Configure SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            webView.reload();
        });

        // Configure retry button
        retryButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                loadWebsite();
            } else {
                Snackbar.make(v, R.string.no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, view -> {
                        if (isNetworkAvailable()) {
                            loadWebsite();
                        }
                    })
                    .show();
            }
        });

        // Setup back press handler
        setupBackPressHandler();

        // Restore WebView state or load website
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            // Load website
            if (isNetworkAvailable()) {
                loadWebsite();
            } else {
                showError(getString(R.string.no_internet));
            }
        }
        
            // إعداد Bottom Navigation
            setupBottomNavigation();
            
            Log.d(TAG, "MainActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "حدث خطأ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * إنشاء قائمة الخيارات
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * معالجة اختيار عنصر من القائمة
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_refresh) {
            webView.reload();
            return true;
        } else if (id == R.id.action_share) {
            shareCurrentPage();
            return true;
        } else if (id == R.id.action_settings) {
            openSettings();
            return true;
        } else if (id == R.id.action_home) {
            webView.loadUrl(websiteUrl);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * تطبيق الثيم (فاتح أو داكن)
     */
    private void applyTheme() {
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * إعداد WebView بجميع الإعدادات المطلوبة
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        
        // Enable DOM storage
        webSettings.setDomStorageEnabled(true);
        
        // Enable database
        webSettings.setDatabaseEnabled(true);
        
        // Enable zoom controls
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        
        // Enable responsive layout
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        
        // Enable caching for better performance
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Enable mixed content in compatibility mode only
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        
        // Set user agent
        webSettings.setUserAgentString(webSettings.getUserAgentString() + " DigitalOrderApp/1.0.6");

        // Set WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    showError(getString(R.string.error_loading));
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.cancel();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                showError(getString(R.string.ssl_error));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                
                // Handle external links
                if (url.startsWith("tel:") || url.startsWith("mailto:") || 
                    url.startsWith("whatsapp:") || url.startsWith("sms:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, R.string.no_app_to_open, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                
                // Load internal links in WebView
                if (url.contains("digitalorder.store")) {
                    return false;
                }
                
                // Open external links in browser
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, R.string.no_app_to_open, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Set WebChromeClient for progress
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        
        // Set DownloadListener for file downloads
        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            // طلب الصلاحية قبل التحميل
            permissionManager.requestStoragePermission(new PermissionManager.PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    DownloadHelper.downloadFile(MainActivity.this, url, userAgent, 
                        contentDisposition, mimeType);
                }

                @Override
                public void onPermissionDenied() {
                    Snackbar.make(findViewById(android.R.id.content), 
                        R.string.storage_permission_required, 
                        Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     * تحميل الموقع
     */
    private void loadWebsite() {
        errorLayout.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(websiteUrl);
    }

    /**
     * عرض رسالة خطأ
     */
    private void showError(String message) {
        webView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        errorText.setText(message);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * التحقق من توفر الإنترنت
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null) {
            return false;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        } else {
            @SuppressWarnings("deprecation")
            android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    /**
     * مشاركة الصفحة الحالية
     */
    private void shareCurrentPage() {
        String currentUrl = webView.getUrl();
        if (currentUrl != null && !currentUrl.isEmpty()) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentUrl);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
        }
    }

    /**
     * فتح شاشة الإعدادات
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * إعداد معالجة زر الرجوع
     */
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    // Double press to exit
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        if (backToast != null) {
                            backToast.cancel();
                        }
                        finish();
                    } else {
                        backToast = Toast.makeText(MainActivity.this, R.string.exit_message, Toast.LENGTH_SHORT);
                        backToast.show();
                        backPressedTime = System.currentTimeMillis();
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) {
            webView.saveState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (webView != null && savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
    }

    /**
     * إعداد Bottom Navigation
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                if (itemId == R.id.nav_home) {
                    webView.loadUrl("https://digitalorder.store/");
                    return true;
                } else if (itemId == R.id.nav_shop) {
                    webView.loadUrl("https://digitalorder.store/shop/");
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    webView.loadUrl("https://digitalorder.store/cart/");
                    return true;
                } else if (itemId == R.id.nav_account) {
                    webView.loadUrl("https://digitalorder.store/my-account/");
                    return true;
                }
                
                return false;
            });
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (backToast != null) {
            backToast.cancel();
            backToast = null;
        }
        
        if (webView != null) {
            webView.stopLoading();
            webView.onPause();
            webView.removeAllViews();
            webView.clearHistory();
            webView.clearCache(true);
            webView.loadUrl("about:blank");
            webView.pauseTimers();
            webView.destroy();
            webView = null;
        }
    }
}
