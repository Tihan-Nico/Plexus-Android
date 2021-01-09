package com.plexus.account.activity.settings;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.account.ProfileLogger;
import com.plexus.account.adapters.ProfileActivityAdapter;

import java.util.ArrayList;
import java.util.Collections;
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

public class ProfileLogActivity extends AppCompatActivity {

    private ImageView back;
    private RecyclerView recyclerView;
    private ProfileActivityAdapter profileActivityAdapter;
    private List<ProfileLogger> profileLoggerList;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_profile_activity);

        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recycler_view);

        back.setOnClickListener(v -> finish());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ProfileLogActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        profileLoggerList = new ArrayList<>();
        profileActivityAdapter = new ProfileActivityAdapter(ProfileLogActivity.this, profileLoggerList);
        recyclerView.setAdapter(profileActivityAdapter);

        readLog();

    }

    private void readLog(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Activity Log");

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profileLoggerList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ProfileLogger profileLogger = snapshot.getValue(ProfileLogger.class);
                            profileLoggerList.add(profileLogger);
                        }

                        Collections.reverse(profileLoggerList);
                        profileActivityAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

}
