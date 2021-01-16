package com.plexus.settings.activity.version;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.plexus.BuildConfig;
import com.plexus.R;

import org.jsoup.Jsoup;

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

public class VersionActivity extends AppCompatActivity {

    String currentVersion;
    TextView current_version, up_to_date;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_version);

        current_version = findViewById(R.id.current_version);
        up_to_date = findViewById(R.id.up_to_date);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            current_version.setText("Current Version " + currentVersion);
            up_to_date.setText("Your app is up to date");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        VersionChecker checker = new VersionChecker();
        checker.execute();

    }

    @SuppressLint("StaticFieldLeak")
    public class VersionChecker extends AsyncTask<String, String, String> {
        private String newVersion;

        @Override
        protected String doInBackground(String... params) {
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName())
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newVersion;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String appVersionName = BuildConfig.VERSION_NAME;
            if (!appVersionName.equals(newVersion)) {
                current_version.setText("Update Now");
                up_to_date.setText("There is a new version available on Play Store");
            }
        }
    }
}
