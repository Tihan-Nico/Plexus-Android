package com.plexus.net;

import android.os.Build;

import com.plexus.BuildConfig;

public class StandardUserAgentInterceptor extends UserAgentInterceptor {

    public StandardUserAgentInterceptor() {
        super("Plexus-Android/" + BuildConfig.VERSION_NAME + " Android/" + Build.VERSION.SDK_INT);
    }
}
