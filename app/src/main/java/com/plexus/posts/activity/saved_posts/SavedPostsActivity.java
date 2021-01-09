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
import com.plexus.model.posts.SavedPostsCollection;
import com.plexus.posts.adapter.saves.SavedPostAdapter;
import com.plexus.posts.adapter.saves.SavedPostCollectionAdapter;

import org.jetbrains.annotations.NotNull;

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

public class SavedPostsActivity extends AppCompatActivity {

    RecyclerView recyclerView, collections;
    ImageView back;
    private SavedPostAdapter savedPostAdapter;
    SavedPostCollectionAdapter savedPostsCollectionAdapter;
    private List<Post> postList_saves;
    private List<String> mySaves;
    private List<SavedPostsCollection> savedPostsCollectionList;
    private FirebaseUser firebaseUser;
    TextView create_collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_saves_activity);

        recyclerView = findViewById(R.id.recents);
        collections = findViewById(R.id.collections);
        back = findViewById(R.id.back);
        create_collection = findViewById(R.id.create_collection);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        back.setOnClickListener(v -> finish());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SavedPostsActivity.this));
        postList_saves = new ArrayList<>();
        savedPostAdapter = new SavedPostAdapter(this, postList_saves);
        recyclerView.setAdapter(savedPostAdapter);

        collections.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        collections.setLayoutManager(mLayoutManager);
        savedPostsCollectionList = new ArrayList<>();
        savedPostsCollectionAdapter = new SavedPostCollectionAdapter(this, savedPostsCollectionList);
        collections.setAdapter(savedPostsCollectionAdapter);

        create_collection.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreateCollectionsSavesActivity.class)));

        mySaves();
        getCollections();

    }

    private void mySaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Recent");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
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

    private void getCollections(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                savedPostsCollectionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SavedPostsCollection savedPostsCollection = snapshot.getValue(SavedPostsCollection.class);
                    savedPostsCollectionList.add(savedPostsCollection);
                }
                Collections.reverse(savedPostsCollectionList);
                savedPostsCollectionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }
}
