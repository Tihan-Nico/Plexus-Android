package com.plexus.settings.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.plexus.R;
import com.plexus.settings.helper.license.Library;
import com.plexus.settings.helper.license.LicenseAdapter;
import com.plexus.settings.helper.license.Licenses;

import java.util.ArrayList;
import java.util.List;

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

public class LicensesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_about_licenses);

        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        List<Library> licenses = new ArrayList<>();

        licenses.add(Licenses.fromGitHubApacheV2("Square/OkHttp"));
        licenses.add(Licenses.fromGitHubApacheV2("JakeWharton/ButterKnife"));
        licenses.add(Licenses.fromGitHubApacheV2("Google/Gson"));
        licenses.add(Licenses.fromGitHubApacheV2("Google/Volley"));
        licenses.add(Licenses.fromGitHubApacheV2("Square/OkHttp"));
        licenses.add(Licenses.fromGitHubApacheV2("Firebase/Firebase-android-sdk"));
        licenses.add(Licenses.fromGitHubApacheV2("Hdodenhof/CircleImageView"));
        licenses.add(Licenses.fromGitHubMIT("Facebook/Fresco"));
        licenses.add(Licenses.fromGitHubBSD("Bumptech/Glide"));
        licenses.add(Licenses.fromGitHubBSD("Facebook/Shimmer-Android"));
        licenses.add(Licenses.fromGitHubBSD("Bumptech/Glide"));
        licenses.add(Licenses.fromGitHubApacheV2("ReactiveX/RxAndroid", "2.x/" + Licenses.FILE_AUTO));
        licenses.add(Licenses.fromGitHubApacheV2("ReactiveX/RxJava", "2.x/" + Licenses.FILE_AUTO));

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new LicenseAdapter(licenses));

    }
}
