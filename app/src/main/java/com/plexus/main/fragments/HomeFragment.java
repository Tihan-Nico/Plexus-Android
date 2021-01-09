package com.plexus.main.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding4.view.RxView;
import com.plexus.R;
import com.plexus.groups.activity.AllGroupActivity;
import com.plexus.information_centre.Covid_Information;
import com.plexus.startup.LoginActivity;
import com.plexus.model.posts.Post;
import com.plexus.model.posts.Story;
import com.plexus.posts.activity.CreatePostActivity;
import com.plexus.posts.adapter.PostAdapter;
import com.plexus.settings.activity.SettingsActivity;
import com.plexus.settings.activity.privacy.PrivacyActivity;
import com.plexus.story.adapter.StoryAdapter;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

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

    LinearLayout image_upload, camera_upload, voice_note_upload;
    TextView text_upload;
    RecyclerView recyclerView;
    SimpleDraweeView profile_image;
    RecyclerView recyclerView_story;
    BottomSheetDialog bottomSheetDialog;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private List<String> followingList;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = getContext().getSharedPreferences("plexus", MODE_PRIVATE);

        profile_image = view.findViewById(R.id.profile_image);
        voice_note_upload = view.findViewById(R.id.voice_note_upload);
        image_upload = view.findViewById(R.id.image_upload);
        camera_upload = view.findViewById(R.id.camera_upload);
        text_upload = view.findViewById(R.id.text_upload);

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

        //Account Manage Sheet
        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_feed);
        SimpleDraweeView profile_image_sheet = bottomSheetDialog.findViewById(R.id.profile_image);
        TextView fullname = bottomSheetDialog.findViewById(R.id.fullname);
        ImageView logout = bottomSheetDialog.findViewById(R.id.logout);
        LinearLayout privacy_main = bottomSheetDialog.findViewById(R.id.privacy);
        LinearLayout information_centre = bottomSheetDialog.findViewById(R.id.information_centre);
        LinearLayout groups = bottomSheetDialog.findViewById(R.id.groups);
        TextView email = bottomSheetDialog.findViewById(R.id.email);
        LinearLayout settings = bottomSheetDialog.findViewById(R.id.settings);

        CompositeDisposable compositeDisposable = new CompositeDisposable();

        Disposable privacy_sheet = RxView.clicks(privacy_main).subscribe(unit -> {
            startActivity(new Intent(getContext(), PrivacyActivity.class));
            bottomSheetDialog.dismiss();
        });
        compositeDisposable.add(privacy_sheet);

        Disposable settings_sheet = RxView.clicks(settings).subscribe(unit -> {
            startActivity(new Intent(getContext(), SettingsActivity.class));
            bottomSheetDialog.dismiss();
        });
        compositeDisposable.add(settings_sheet);

        Disposable logout_sheet = RxView.clicks(logout).subscribe(unit -> {
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });
        compositeDisposable.add(logout_sheet);

        Disposable group_sheet = RxView.clicks(groups).subscribe(unit -> {
            startActivity(new Intent(getActivity(), AllGroupActivity.class));
            bottomSheetDialog.dismiss();
        });
        compositeDisposable.add(group_sheet);

        Disposable a = RxView.clicks(profile_image).subscribe(unit -> bottomSheetDialog.show());
        compositeDisposable.add(a);

        Disposable information = RxView.clicks(information_centre).subscribe(unit -> startActivity(new Intent(getActivity(), Covid_Information.class)));
        compositeDisposable.add(information);

        Disposable voice_note = RxView.clicks(voice_note_upload).subscribe(unit -> {
            Intent intent = new Intent(getContext(), CreatePostActivity.class);
            intent.putExtra("isRecordingUpload", true);
            startActivity(intent);
        });
        compositeDisposable.add(voice_note);

        Disposable image = RxView.clicks(image_upload).subscribe(unit -> {
            Intent intent = new Intent(getContext(), CreatePostActivity.class);
            intent.putExtra("isUploadImage", true);
            startActivity(intent);
        });
        compositeDisposable.add(image);

        Disposable camera = RxView.clicks(camera_upload).subscribe(unit -> {
            Intent intent = new Intent(getContext(), CreatePostActivity.class);
            intent.putExtra("isUploadCamera", true);
            startActivity(intent);
        });
        compositeDisposable.add(camera);

        Disposable text = RxView.clicks(text_upload).subscribe(unit ->{
            Intent intent = new Intent(getContext(), CreatePostActivity.class);
            intent.putExtra("isText", true);
            startActivity(intent);
        });
        compositeDisposable.add(text);

        checkFollowing();
        getUserData(fullname, profile_image_sheet, email);

    }

    private void getUserData(TextView fullname, SimpleDraweeView profile_image_sheet, TextView email){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHandler.post(() -> {
                    String image = dataSnapshot.child("imageurl").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String surname = dataSnapshot.child("surname").getValue(String.class);

                    fullname.setText(MasterCipher.decrypt(name)  + " " + MasterCipher.decrypt(surname));
                    profile_image_sheet.setImageURI(MasterCipher.decrypt(image));
                    profile_image.setImageURI(MasterCipher.decrypt(image));
                    email.setText(firebaseUser.getEmail());
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                        if (post.getPublisher().equals(firebaseUser.getUid())){
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
}
