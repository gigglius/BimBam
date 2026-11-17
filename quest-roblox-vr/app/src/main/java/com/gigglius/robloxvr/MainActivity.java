package com.gigglius.robloxvr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;
import java.util.Locale;

/**
 * Screenshot-driven classic Roblox 2014 dashboard for the BimBam Quest port.
 * Dashboard images are served directly from packaged Android drawable resources.
 */
public class MainActivity extends Activity {
    private WebView dashboard;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        dashboard = new WebView(this);
        WebSettings settings = dashboard.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDefaultTextEncodingName("UTF-8");
        dashboard.setWebChromeClient(new WebChromeClient());
        dashboard.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return serveDashboardResource(request.getUrl().getPath());
            }

            @SuppressWarnings("deprecation")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                try {
                    android.net.Uri uri = android.net.Uri.parse(url);
                    if ("bimbam.local".equals(uri.getHost())) {
                        return serveDashboardResource(uri.getPath());
                    }
                } catch (Exception ignored) { }
                return super.shouldInterceptRequest(view, url);
            }
        });
        dashboard.setBackgroundColor(0xFFFFFFFF);
        setContentView(dashboard);
        dashboard.loadUrl("file:///android_asset/classic_dashboard.html");
    }

    private WebResourceResponse serveDashboardResource(String path) {
        if (path == null || !path.startsWith("/res/")) return null;
        String fileName = path.substring("/res/".length());
        int dot = fileName.lastIndexOf('.');
        if (dot <= 0) return null;
        String name = fileName.substring(0, dot).toLowerCase(Locale.ROOT);
        String ext = fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
        int resourceId = getResources().getIdentifier(name, "drawable", getPackageName());
        if (resourceId == 0) return null;
        try {
            InputStream stream = getResources().openRawResource(resourceId);
            String mime = "jpg".equals(ext) || "jpeg".equals(ext) ? "image/jpeg" : "image/png";
            return new WebResourceResponse(mime, null, stream);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override public void onBackPressed() {
        if (dashboard != null && dashboard.canGoBack()) dashboard.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (dashboard != null) {
            dashboard.loadUrl("about:blank");
            dashboard.destroy();
            dashboard = null;
        }
        super.onDestroy();
    }
}
