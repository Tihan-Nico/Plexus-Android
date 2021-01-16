package com.plexus.settings.activity.notices;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plexus.Plexus;
import com.plexus.R;
import com.plexus.model.settings.Notices;
import com.plexus.settings.adapters.NoticesAdapter;

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

public class NoticesActivity extends AppCompatActivity {

    private static final String URL = "https://raw.githubusercontent.com/PlexusInc/notices/master/Notices.json";
    ImageView back;
    private RecyclerView recyclerView;
    private NoticesAdapter noticesAdapter;
    private List<Notices> noticesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_notices);

        recyclerView = findViewById(R.id.recycler_view);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        noticesList = new ArrayList<>();
        noticesAdapter = new NoticesAdapter(getApplicationContext(), noticesList);
        recyclerView.setAdapter(noticesAdapter);

        fetchNotices();

    }

    private void fetchNotices() {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                response -> {
                    if (response == null) {
                        Toast.makeText(getApplicationContext(), "Couldn't fetch updates!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<Notices> items = new Gson().fromJson(response.toString(), new TypeToken<List<Notices>>() {
                    }.getType());

                    noticesList.clear();
                    noticesList.addAll(items);

                    // refreshing recycler view
                    noticesAdapter.notifyDataSetChanged();
                }, error -> {
            // error in getting json
            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        Plexus.getInstance().addToRequestQueue(request);
    }

}
