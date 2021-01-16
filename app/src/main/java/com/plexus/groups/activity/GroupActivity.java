package com.plexus.groups.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.groups.adapter.GroupPostAdapter;
import com.plexus.model.group.Group;
import com.plexus.model.group.GroupPosts;
import com.plexus.model.group.GroupSettings;
import com.plexus.posts.activity.CreatePostActivity;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    ImageView back, settings;
    SimpleDraweeView group_cover;
    TextView group_title, group_name, group_info, text_upload;
    Button group_edit;
    View group_setup;
    RecyclerView recyclerView;
    LinearLayout add_posts, image_upload, camera_upload, voice_note_upload;

    String groupID;
    String userID;
    String coverUrl;

    List<GroupPosts> groupsPostList;
    GroupPostAdapter groupPostAdapter;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        back = findViewById(R.id.back);
        settings = findViewById(R.id.settings);
        group_cover = findViewById(R.id.group_cover);
        group_title = findViewById(R.id.group_title);
        group_name = findViewById(R.id.group_name);
        group_info = findViewById(R.id.group_info);
        group_edit = findViewById(R.id.group_edit);
        group_setup = findViewById(R.id.group_setup);
        recyclerView = findViewById(R.id.recycler_view);
        add_posts = findViewById(R.id.add_posts);
        image_upload = findViewById(R.id.image_upload);
        camera_upload = findViewById(R.id.camera_upload);
        voice_note_upload = findViewById(R.id.voice_note_upload);
        text_upload = findViewById(R.id.text_upload);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        groupID = intent.getStringExtra("group_id");
        userID = intent.getStringExtra("user_id");
        coverUrl = intent.getStringExtra("cover_url");

        groupsPostList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupPostAdapter = new GroupPostAdapter(this, groupsPostList);
        recyclerView.setAdapter(groupPostAdapter);

        back.setOnClickListener(view -> finish());
        settings.setOnClickListener(view -> startActivity(new Intent(GroupActivity.this, GroupSettingsActivity.class)));
        group_edit.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), GroupEditActivity.class);
            intent1.putExtra("groupID", groupID);
            startActivity(intent1);
        });
        group_setup.setVisibility(View.GONE);
        group_edit.setVisibility(View.GONE);

        Glide.with(getApplicationContext()).asBitmap().load(coverUrl).into(group_cover);

        init();
    }

    private void init() {
        getGroupData(groupID);
        getGroupSettings();
        addPostsToGroup();
        getGroupMemberCount(groupID);
        getGroupAdmin(groupID);
        getGroupPosts();
        setupGroup();
    }

    private void getGroupData(String groupID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                group_title.setText(MasterCipher.decrypt(group.getName()));
                group_name.setText(MasterCipher.decrypt(group.getName()));
                Glide.with(getApplicationContext()).asBitmap().load(MasterCipher.decrypt(group.getCoverImageUrl())).into(group_cover);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getGroupSettings() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID).child("Settings").child("Posting");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                GroupSettings groupSettings = snapshot.getValue(GroupSettings.class);
                if (snapshot.exists()) {
                    if (!groupSettings.isPostApproval()) {
                        add_posts.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void addPostsToGroup() {

        image_upload.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
            intent.putExtra("group", true);
            intent.putExtra("isUploadImage", true);
            intent.putExtra("groupName", group_name.getText().toString());
            intent.putExtra("group_id", groupID);
            startActivity(intent);
        });

        voice_note_upload.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
            intent.putExtra("group", true);
            intent.putExtra("isRecordingUpload", true);
            intent.putExtra("groupName", group_name.getText().toString());
            intent.putExtra("group_id", groupID);
            startActivity(intent);
        });

        camera_upload.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
            intent.putExtra("group", true);
            intent.putExtra("isUploadCamera", true);
            intent.putExtra("groupName", group_name.getText().toString());
            intent.putExtra("group_id", groupID);
            startActivity(intent);
        });

        text_upload.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
            intent.putExtra("group", true);
            intent.putExtra("isText", true);
            intent.putExtra("groupName", group_name.getText().toString());
            intent.putExtra("group_id", groupID);
            startActivity(intent);
        });

    }

    private void getGroupMemberCount(String groupID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID).child("Members");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 1) {
                    group_info.setText(MessageFormat.format("{0} Member", snapshot.getChildrenCount()));
                } else if (snapshot.getChildrenCount() > 2) {
                    group_info.setText(MessageFormat.format("{0} Members", snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getGroupAdmin(String groupID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID).child("Admins");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (userID.equals(firebaseUser.getUid())) {
                    group_setup.setVisibility(View.VISIBLE);
                    group_edit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setupGroup() {
        LinearLayout invite_friends = group_setup.findViewById(R.id.invite_friends);
        LinearLayout edit_description = group_setup.findViewById(R.id.edit_description);
        LinearLayout add_group_cover = group_setup.findViewById(R.id.add_group_cover);

        invite_friends.setOnClickListener(view -> shareDeepLink(generateDeepLinkUrl()));

        edit_description.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GroupEditActivity.class);
            intent.putExtra("add_description", true);
            startActivity(intent);
        });

        add_group_cover.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GroupEditActivity.class);
            intent.putExtra("add_cover", true);
            startActivity(intent);
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                /*if (group.getAbout().equals("")){
                    edit_description.setVisibility(View.GONE);
                }

                if (group.getCoverImageUrl().equals("")){
                    add_group_cover.setVisibility(View.GONE);
                }*/

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID).child("Members");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 2) {
                            invite_friends.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        if (invite_friends.getVisibility() == View.GONE && edit_description.getVisibility() == View.GONE && add_group_cover.getVisibility() == View.GONE) {
            group_setup.setVisibility(View.GONE);
        }

    }

    private void getGroupPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                groupsPostList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {
                        GroupPosts groupPosts = snapshot.getValue(GroupPosts.class);
                        if (groupPosts.getPublisher().equals(firebaseUser.getUid())) {
                            groupsPostList.add(groupPosts);
                        }
                    }
                }
                groupPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private String generateDeepLinkUrl() {
        return "https://plexus.dev/groups?id=" + groupID;
    }

    private void shareDeepLink(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Share Group via"));
    }
}
