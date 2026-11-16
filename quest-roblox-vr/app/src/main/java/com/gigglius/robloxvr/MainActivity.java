package com.gigglius.robloxvr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Screenshot-driven classic Roblox 2014 dashboard for the BimBam Quest port.
 * The dashboard is kept separate from the original native in-game engine menus.
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
        dashboard.setWebViewClient(new WebViewClient());
        dashboard.setBackgroundColor(0xFFFFFFFF);
        setContentView(dashboard);
        dashboard.loadUrl("file:///android_asset/classic_dashboard.html");
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
