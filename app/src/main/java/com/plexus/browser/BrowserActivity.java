package com.plexus.browser;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.plexus.R;

public class BrowserActivity extends AppCompatActivity {

    WebView webView;

    String url;

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = findViewById(R.id.webView);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        init();
    }

    private void init(){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));

        int colorInt = Color.parseColor("#242526");
        builder.setToolbarColor(colorInt);

        /*builder.addMenuItem("Share via Plexus", "");
        builder.addMenuItem("Send via Konnect", "");
        builder.addMenuItem("Save link", "");*/

        webViewDark();
    }

    private void webViewDark(){
        if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)){
            WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
    }
}
