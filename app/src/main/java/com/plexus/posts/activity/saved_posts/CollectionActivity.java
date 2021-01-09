package com.plexus.posts.activity.saved_posts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.plexus.model.posts.Post;
import com.plexus.posts.adapter.saves.SavedPostAdapter;

import org.jetbrains.annotations.NotNull;

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

public class CollectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SavedPostAdapter savedPostAdapter;
    List<Post> postList_saves;
    FirebaseUser firebaseUser;
    ImageView back;
    TextView collection_name;
    String id, name;
    private List<String> mySaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_saves_collection);

        recyclerView = findViewById(R.id.recycler_view);
        collection_name = findViewById(R.id.collection_name);
        back = findViewById(R.id.back);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("collection_name");

        back.setOnClickListener(v -> finish());

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagers = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManagers);
        postList_saves = new ArrayList<>();
        savedPostAdapter = new SavedPostAdapter(this, postList_saves);
        recyclerView.setAdapter(savedPostAdapter);

        collection_name.setText(name);

        mySaves();

    }

    private void mySaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child(id).child("Collection Saves");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    for (String id : mySaves) {
                        if (post.getPostid().equals(id)) {
                            postList_saves.add(post);
                        }
                    }
                }
                savedPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

}
