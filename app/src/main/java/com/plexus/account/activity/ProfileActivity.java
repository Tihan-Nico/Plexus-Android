package com.plexus.account.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.plexus.R;
import com.plexus.account.FollowersActivity;
import com.plexus.account.adapters.ProfilePostAdapter;
import com.plexus.components.Constants;
import com.plexus.components.components.PlexusRecyclerView;
import com.plexus.components.components.bottomsheet.adapter.SheetOptionsAdapter;
import com.plexus.components.components.bottomsheet.model.SheetOptions;
import com.plexus.model.Token;
import com.plexus.model.account.User;
import com.plexus.model.posts.Post;
import com.plexus.notifications.fcm.FirebaseNotificationHelper;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

public class ProfileActivity extends AppCompatActivity {

    public static final String[] titles = new String[]{"Report User",
            "Block", "Profile Link"};
    public static final Integer[] images = {R.drawable.email_outline,
            R.drawable.block_helper, R.drawable.link_variant};
    public ImageView verified, menu;
    StorageReference storageReference;
    TextView followers, following;
    String profileid;
    PlexusRecyclerView recycler_view;
    View profile_empty_state;
    DatabaseReference databaseReference;
    Intent intent;
    private SimpleDraweeView profile_cover;
    private TextView posts, fullname, bio, username;
    private Button follow;
    private FirebaseUser firebaseUser;
    private boolean blocked = false;
    private List<Post> postList;
    private View disabled;
    private View show_account_private;
    private SimpleDraweeView image_profile;
    private BottomSheetDialog profile_sheet;
    private ProfilePostAdapter profilePostAdapter;
    private ArrayList<SheetOptions> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("avatars/covers");

        image_profile = findViewById(R.id.profile_image);
        profile_cover = findViewById(R.id.image_cover);
        username = findViewById(R.id.username);
        posts = findViewById(R.id.posts);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.about);
        menu = findViewById(R.id.menu);
        verified = findViewById(R.id.verified);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        recycler_view = findViewById(R.id.recycler_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        profileid = intent.getStringExtra("userid");

        Uri data = getIntent().getData();
        if (data != null) {
            profileid = data.getQueryParameter("id");
        }

        init();

    }

    private void init(){

        recycler_view.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(ProfileActivity.this, 3);
        recycler_view.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        profilePostAdapter = new ProfilePostAdapter(ProfileActivity.this, postList);
        recycler_view.setAdapter(profilePostAdapter);
        recycler_view.setEmptyView(profile_empty_state);

        profile_sheet = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialogTheme);
        profile_sheet.setContentView(R.layout.sheet_layout);
        ListView listView = profile_sheet.findViewById(R.id.listview);

        rowItems = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            SheetOptions item = new SheetOptions(titles[i], images[i]);
            rowItems.add(item);
        }

        SheetOptionsAdapter optionsAdapter = new SheetOptionsAdapter(this, rowItems);
        listView.setAdapter(optionsAdapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (position == 0) {
                startActivity(new Intent(this, ProfileActivity.class));
                profile_sheet.dismiss();
            }

            if (position == 1) {
                if (blocked) {
                    unblockUser(profileid);
                } else {
                    blockUser(profileid);
                }
            }

            if (position == 2) {
                shareDeepLink(generateDeepLinkUrl());
            }

        });

        menu.setOnClickListener(v -> profile_sheet.show());

        if (profileid.equals(firebaseUser.getUid())) {
            follow.setVisibility(View.GONE);
        }

        follow.setOnClickListener(view1 -> {
            String follower = follow.getText().toString();
            switch (follower) {
                case "Edit Profile":
                    startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                    break;
                case "Follow":
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(firebaseUser.getUid())
                            .child("following")
                            .child(profileid)
                            .setValue(true);
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Follow")
                            .child(profileid)
                            .child("followers")
                            .child(firebaseUser.getUid())
                            .setValue(true);
                    profileActivity();
                    addNotification();
                    sendNotification();
                    break;
                case "Following":
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Follow")
                            .child(firebaseUser.getUid())
                            .child("following")
                            .child(profileid)
                            .removeValue();
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Follow")
                            .child(profileid)
                            .child("followers")
                            .child(firebaseUser.getUid())
                            .removeValue();
                    break;
            }
        });

        following.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), FollowingActivity.class);
            intent.putExtra("profileid", profileid);
            startActivity(intent);
        });
        followers.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
            intent.putExtra("profileid", profileid);
            startActivity(intent);
        });

        getFollowers(following, followers);
        myFotos();
        getNrPosts();
        checkFollow();
        userInformation();
        checkIfBlocked(profileid);
        blockedOrNot(profileid);
        isAccountPublic();

    }

    private void checkIfBlocked(String profileid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Blocked Users").orderByChild("uid").equalTo(profileid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.exists()) {
                                disabled.setVisibility(View.VISIBLE);
                                blocked = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void unblockUser(String profileid) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", profileid);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Blocked Users").orderByChild("uid").equalTo(profileid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getApplicationContext(), "User is unblocked successfully", Toast.LENGTH_SHORT).show();
                                        disabled.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener(e -> {

                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void blockUser(String profileid) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", profileid);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Blocked Users").child(profileid).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "User was blocked successfully.", Toast.LENGTH_SHORT).show();
                    disabled.setVisibility(View.VISIBLE);
                });
    }

    private void blockedOrNot(String profileid) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(profileid).child("Blocked Users").orderByChild("uid").equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.exists()) {
                                disabled.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void addNotification() {
        DatabaseReference reference =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(profileid)
                        .child("Notification");

        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("follower", true);
        hashMap.put("notificationRead", false);
        hashMap.put("notificationViewed", false);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

        reference.child(id).setValue(hashMap);
    }

    private void checkFollow() {
        DatabaseReference reference =
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Follow")
                        .child(firebaseUser.getUid())
                        .child("following");
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(profileid).exists()) {
                            follow.setText("Following");
                            DatabaseReference privacy_data = FirebaseDatabase.getInstance().getReference("Users").child(profileid).child("Privacy");
                            privacy_data.addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                if (dataSnapshot1.exists()) {
                                                    if (user.isPrivate_account()) {
                                                        show_account_private.setVisibility(View.GONE);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        } else {
                            follow.setText("Follow");
                            DatabaseReference privacy_data = FirebaseDatabase.getInstance().getReference("Users").child(profileid).child("Privacy");
                            privacy_data.addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                if (dataSnapshot1.exists()) {
                                                    if (user.isPrivate_account()) {
                                                        show_account_private.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void sendNotification() {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(profileid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);

                            FirebaseNotificationHelper.initialize(Constants.FCM_KEY)
                                    .defaultJson(false, getJsonBody(MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getImageurl())))
                                    .receiverFirebaseToken(token.getToken())
                                    .send();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getJsonBody(String name, String imageUrl) {

        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put("title", "Plexus");
            jsonObjectData.put("body", name + " " + "started following you");
            jsonObjectData.put("click_action", "com.plexus.plexus_PROFILE_TARGET_NOTIFICATION");
            jsonObjectData.put("from_user_id", firebaseUser.getUid());
            jsonObjectData.put("imageurl", imageUrl);
            jsonObjectData.put("type", "follower");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObjectData.toString();
    }

    private void profileActivity() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Activity Log");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("title", "You started to follow someone.");
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("isFollow", true);
        hashMap.put("userid", profileid);

        reference.child(id).setValue(hashMap);
    }

    private void myFotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                profilePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        i++;
                    }
                }
                posts.setText("" + i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(TextView following, TextView followers) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void userInformation() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                username.setText(MasterCipher.decrypt(user.getUsername()));
                bio.setText(MasterCipher.decrypt(user.getBio()));

                image_profile.setImageURI(MasterCipher.decrypt(user.getImageurl()));
                profile_cover.setImageURI(MasterCipher.decrypt(user.getProfile_cover()));

                if (user.isVerified()) {
                    verified.setVisibility(View.VISIBLE);
                } else {
                    verified.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void isAccountPublic() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(profileid).child("Privacy")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.exists()) {
                                if (profileid.equals(firebaseUser.getUid())) {
                                    show_account_private.setVisibility(View.GONE);
                                } else {
                                    if (user.isPrivate_account()) {
                                        show_account_private.setVisibility(View.VISIBLE);
                                    } else {
                                        show_account_private.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String generateDeepLinkUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("plexus.dev")
                .appendPath("profile")
                .appendQueryParameter("id", firebaseUser.getUid());
        return builder.build().toString();
    }

    private void shareDeepLink(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Share Group via"));
    }

}
