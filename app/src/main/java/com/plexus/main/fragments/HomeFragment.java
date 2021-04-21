package com.plexus.main.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import com.plexus.model.posts.Story;
import com.plexus.posts.adapter.PostAdapter;
import com.plexus.story.adapter.StoryAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static android.content.Context.MODE_PRIVATE;

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

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView recyclerView_story;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private List<String> followingList;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private static final float TOOLBAR_ELEVATION = 14f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = getContext().getSharedPreferences("plexus", MODE_PRIVATE);

        toolbar = getActivity().findViewById(R.id.toolbar);

        recyclerView = view.findViewById(R.id.recycler_view_post);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList, false);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        CompositeDisposable compositeDisposable = new CompositeDisposable();

        checkFollowing();

        init();
    }

    private void init(){
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            // Keeps track of the overall vertical offset in the list
            int verticalOffset;

            // Determines the scroll UP/DOWN direction
            boolean scrollingUp;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (scrollingUp) {
                        if (verticalOffset > toolbar.getHeight()) {
                            toolbarAnimateHide();
                        } else {
                            toolbarAnimateShow(verticalOffset);
                        }
                    } else {
                        if (toolbar.getTranslationY() < toolbar.getHeight() * -0.6 && verticalOffset > toolbar.getHeight()) {
                            toolbarAnimateHide();
                        } else {
                            toolbarAnimateShow(verticalOffset);
                        }
                    }
                }
            }

            @Override
            public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                verticalOffset += dy;
                scrollingUp = dy > 0;
                int toolbarYOffset = (int) (dy - toolbar.getTranslationY());
                toolbar.animate().cancel();
                if (scrollingUp) {
                    if (toolbarYOffset < toolbar.getHeight()) {
                        if (verticalOffset > toolbar.getHeight()) {
                            toolbarSetElevation(TOOLBAR_ELEVATION);
                        }
                        toolbar.setTranslationY(-toolbarYOffset);
                    } else {
                        toolbarSetElevation(0);
                        toolbar.setTranslationY(-toolbar.getHeight());
                    }
                } else {
                    if (toolbarYOffset < 0) {
                        if (verticalOffset <= 0) {
                            toolbarSetElevation(0);
                        }
                        toolbar.setTranslationY(0);
                    } else {
                        if (verticalOffset > toolbar.getHeight()) {
                            toolbarSetElevation(TOOLBAR_ELEVATION);
                        }
                        toolbar.setTranslationY(-toolbarYOffset);
                    }
                }
            }
        });
    }

    private void toolbarSetElevation(float elevation) {
        toolbar.setElevation(elevation);
    }

    private void toolbarAnimateShow(final int verticalOffset) {
        toolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        toolbarSetElevation(verticalOffset == 0 ? 0 : TOOLBAR_ELEVATION);
                    }
                });
    }

    private void toolbarAnimateHide() {
        toolbar.animate()
                .translationY(-toolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toolbarSetElevation(0);
                    }
                });
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followingList.add(snapshot.getKey());
                }
                new readPosts().execute();
                new readStory().execute();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class readPosts extends AsyncTask<String, Void, DatabaseReference> {
        @Override
        protected DatabaseReference doInBackground(String... strings) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post post = snapshot.getValue(Post.class);
                        for (String id : followingList) {
                            if (post.getPublisher().equals(id)) {
                                postList.add(post);
                            }
                        }
                        if (post.getPublisher().equals(firebaseUser.getUid())) {
                            postList.add(post);
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {

                }
            });

            return reference;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class readStory extends AsyncTask<String, Void, DatabaseReference> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected DatabaseReference doInBackground(String... strings) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
            reference.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                            long timecurrent = System.currentTimeMillis();
                            storyList.clear();
                            storyList.add(new Story("", 0, 0, "", "", firebaseUser.getUid()));
                            for (String id : followingList) {
                                int countStory = 0;
                                Story story = null;
                                for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
                                    story = snapshot.getValue(Story.class);
                                    if (story != null && timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                                        countStory++;
                                    }
                                }
                                if (countStory > 0) {
                                    storyList.add(story);
                                }
                            }
                            storyAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NotNull DatabaseError databaseError) {
                        }
                    });

            return reference;
        }

        @Override
        protected void onPostExecute(DatabaseReference databaseReference) {
            super.onPostExecute(databaseReference);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}
