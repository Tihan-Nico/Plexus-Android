package com.plexus.groups.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.plexus.groups.adapter.GroupAdapter;
import com.plexus.groups.adapter.GroupPostAdapter;
import com.plexus.model.group.Group;
import com.plexus.model.group.GroupPosts;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AllGroupActivity extends AppCompatActivity {

    ImageView back, create_group;
    RecyclerView recycler_view, group_posts;

    List<Group> groupList;
    List<String> myGroupLists;
    GroupAdapter groupAdapter;
    List<GroupPosts> groupsPostList;
    GroupPostAdapter groupPostAdapter;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_groups);

        back = findViewById(R.id.back);
        recycler_view = findViewById(R.id.recycler_view);
        group_posts = findViewById(R.id.group_posts);
        create_group = findViewById(R.id.create_group);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        myGroupLists = new ArrayList<>();

        recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view.setLayoutManager(linearLayoutManager);
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groupList);
        recycler_view.setAdapter(groupAdapter);

        group_posts.setHasFixedSize(true);
        group_posts.setLayoutManager(new LinearLayoutManager(this));
        groupsPostList = new ArrayList<>();
        groupPostAdapter = new GroupPostAdapter(this, groupsPostList);
        group_posts.setAdapter(groupPostAdapter);

        back.setOnClickListener(view -> finish());

        init();
    }

    private void init(){
        myGroups();
    }

    private void myGroups() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Groups");
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myGroupLists.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            myGroupLists.add(snapshot.getKey());
                        }
                        readGroups();
                        getGroupPosts();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void readGroups() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        groupList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Group group = snapshot.getValue(Group.class);
                            for (String id : myGroupLists) {
                                groupList.add(group);
                            }
                        }
                        groupAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getGroupPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                groupsPostList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupPosts groupPosts = snapshot.getValue(GroupPosts.class);
                    for (String id : myGroupLists) {
                        if (groupPosts.getPublisher().equals(id)) {
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

}
