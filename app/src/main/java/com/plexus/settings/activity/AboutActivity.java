package com.plexus.settings.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.plexus.R;
import com.plexus.components.locale_changer.base.LocaleChangerBaseActivity;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class AboutActivity extends LocaleChangerBaseActivity {

    private TextView open_source, legal, app_version;
    private ImageView back;

    static String VersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_about);

        legal = findViewById(R.id.legal);
        app_version = findViewById(R.id.app_version);
        open_source = findViewById(R.id.license);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        app_version.setText(VersionName(getApplicationContext()));

        open_source.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LicensesActivity.class)));

        String text = "Plexus and the Plexus logos are trademark of Plexus Inc. All rights reserved.";

        SpannableString ss = new SpannableString(text);
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ForegroundColorSpan plexus = new ForegroundColorSpan(Color.parseColor("#f78361"));
        ForegroundColorSpan plexus_2 = new ForegroundColorSpan(Color.parseColor("#f78361"));
        ForegroundColorSpan plexus_3 = new ForegroundColorSpan(Color.parseColor("#f78361"));
        ssb.setSpan(plexus, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(plexus_2, 15, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(plexus_3, 45, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        legal.setText(ssb);
    }
}
