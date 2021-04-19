package com.plexus.main.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.plexus.R;
import com.plexus.account.FollowersActivity;
import com.plexus.account.activity.AccountStatusActivity;
import com.plexus.account.activity.EditProfileActivity;
import com.plexus.account.activity.FollowingActivity;
import com.plexus.account.activity.settings.ProfileLogActivity;
import com.plexus.account.adapters.ProfilePostAdapter;
import com.plexus.components.components.bottomsheet.adapter.SheetOptionsAdapter;
import com.plexus.components.components.bottomsheet.model.SheetOptions;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.model.account.User;
import com.plexus.model.posts.Post;
import com.plexus.posts.activity.saved_posts.SavedPostsActivity;
import com.plexus.qr.activity.QrGetLinkActivity;
import com.plexus.settings.activity.privacy.PrivacyActivity;
import com.plexus.story.activity.AddStoryActivity;
import com.plexus.utils.MasterCipher;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
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

public class ProfileFragment extends Fragment {

    TextView posts, fullname, bio, username;
    TextView followers, following;
    MaterialButton edit_details;
    LinearLayout lin_add_story, lin_edit_profile, lin_logs, lin_more;
    RecyclerView recycler_view;
    private SimpleDraweeView profile_cover, image_profile;
    private BottomSheetDialog profile_sheet, profile_image_sheet, profile_cover_sheet;

    private FirebaseUser firebaseUser;
    StorageReference storageReference;

    public static final String[] titles = new String[]{"Account Privacy", "Account Status", "Archive"};
    public static final Integer[] images = {R.drawable.lock_outline, R.drawable.error, R.drawable.archive_outline};

    public static final String[] profile_image_titles = new String[]{"View Profile Picture", "Select Profile Picture"};
    public static final String[] profile_image_images = {};

    public static final String[] profile_cover_titles = new String[]{"View Profile Cover", "Upload Cover Photo", "Select Plexus Covers"};
    public static final String[] profile_cover_images = {};

    ArrayList<SheetOptions> rowItems;
    private List<Post> postList;
    private ProfilePostAdapter profilePostAdapter;

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_view, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("avatars/covers");

        image_profile = view.findViewById(R.id.profile_image);
        profile_cover = view.findViewById(R.id.image_cover);
        edit_details = view.findViewById(R.id.edit_details);
        username = view.findViewById(R.id.username);
        posts = view.findViewById(R.id.posts);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.about);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        recycler_view = view.findViewById(R.id.recycler_view);
        lin_add_story = view.findViewById(R.id.lin_add_story);
        lin_edit_profile = view.findViewById(R.id.lin_edit_profile);
        lin_logs = view.findViewById(R.id.lin_logs);
        lin_more = view.findViewById(R.id.lin_more);

        init();

        return view;
    }

    private void init(){
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recycler_view.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        profilePostAdapter = new ProfilePostAdapter(getContext(), postList);
        recycler_view.setAdapter(profilePostAdapter);

        profile_sheet = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        profile_sheet.setContentView(R.layout.sheet_layout);
        ListView listView = profile_sheet.findViewById(R.id.listview);

        rowItems = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            SheetOptions item = new SheetOptions(titles[i], images[i]);
            rowItems.add(item);
        }

        SheetOptionsAdapter optionsAdapter = new SheetOptionsAdapter(getContext(), rowItems);
        listView.setAdapter(optionsAdapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (position == 0) {
                startActivity(new Intent(getContext(), ProfileLogActivity.class));
                profile_sheet.dismiss();
            }

            if (position == 1) {
                startActivity(new Intent(getContext(), PrivacyActivity.class));
                profile_sheet.dismiss();
            }

            if (position == 2) {
                startActivity(new Intent(getContext(), AccountStatusActivity.class));
                profile_sheet.dismiss();
            }

            if (position == 4) {
                startActivity(new Intent(getContext(), SavedPostsActivity.class));
                profile_sheet.dismiss();
            }

            if (position == 5) {
                Intent intent = new Intent(getContext(), QrGetLinkActivity.class);
                intent.putExtra("type", "profile");
                intent.putExtra("id", firebaseUser.getUid());
                startActivity(intent);
            }

        });

        lin_add_story.setOnClickListener(v -> startActivity(new Intent(PlexusDependencies.getApplication(), AddStoryActivity.class)));

        lin_more.setOnClickListener(v -> profile_sheet.show());

        lin_edit_profile.setOnClickListener(v -> startActivity(new Intent(PlexusDependencies.getApplication(), EditProfileActivity.class)));

        lin_logs.setOnClickListener(v -> startActivity(new Intent(PlexusDependencies.getApplication(), ProfileLogActivity.class)));

        profile_cover.setOnClickListener(v -> CropImage.activity().start(requireContext(), this));

        following.setOnClickListener(v -> {
            Intent intent = new Intent(PlexusDependencies.getApplication(), FollowingActivity.class);
            intent.putExtra("profileid", firebaseUser.getUid());
            startActivity(intent);
        });

        followers.setOnClickListener(v -> {
            Intent intent = new Intent(PlexusDependencies.getApplication(), FollowersActivity.class);
            intent.putExtra("profileid", firebaseUser.getUid());
            startActivity(intent);
        });

        getFollowers(following, followers);
        myFotos();
        getNrPosts();
    }

    private void myFotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid())) {
                        postList.add(post);
                    }

                }
                Collections.reverse(postList);
                profilePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid())) {
                        i++;
                    }
                }
                posts.setText(MessageFormat.format("{0}", i));
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(TextView following, TextView followers) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                followers.setText(MessageFormat.format("{0}", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                following.setText(MessageFormat.format("{0}", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    /*
     * Get user information from Firebase or anything related to Firebase.
     */

    private void userInformation() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                username.setText(MasterCipher.decrypt(user.getUsername()));
                bio.setText(MasterCipher.decrypt(user.getBio()));

                image_profile.setImageURI(MasterCipher.decrypt(user.getImageurl()));
                profile_cover.setImageURI(MasterCipher.decrypt(user.getProfile_cover()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private String generateDeepLinkUrl(String pushID) {
        return "https://plexus.dev/profile=" + pushID;
    }

    private void shareDeepLink(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Share Profile via"));
    }

    @Override
    public void onStart() {
        super.onStart();
        userInformation();
    }
}
