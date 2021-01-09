package com.plexus.posts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.posts.Hashtag;
import com.plexus.model.posts.Post;
import com.plexus.model.account.User;
import com.plexus.posts.adapter.PostAdapter;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HashTagViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView title, post_count, created_by;
    ImageView back;

    String hashtag;
    PostAdapter postAdapter;
    List<Post> postList;
    private List<String> myHashtagPosts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_hashtag_detail);

        Intent intent = getIntent();
        hashtag = intent.getStringExtra("hashtag");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(HashTagViewActivity.this, postList, false);
        recyclerView.setAdapter(postAdapter);

        title = findViewById(R.id.hashtag_name);
        post_count = findViewById(R.id.post_count);
        created_by = findViewById(R.id.created_by);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        title.setText(MessageFormat.format("#{0}", hashtag));

        getHashtagInformation();
        hashtagPosts();

    }

    private void getHashtagInformation(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Hashtags").child(hashtag);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Hashtag hashTag = snapshot.getValue(Hashtag.class);
                userName(hashTag.getCreatedBy(), created_by);

                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Hashtags").child(hashtag).child("Posts");
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() == 1){
                            post_count.setText(MessageFormat.format("{0} person is talking about this", snapshot.getChildrenCount()));
                        } else {
                            post_count.setText(MessageFormat.format("{0} people are talking about this", snapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userName(String profileid, TextView textView){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                textView.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hashtagPosts(){
        myHashtagPosts = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hashtags").child(hashtag).child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    myHashtagPosts.add(snapshot.getKey());
                }
                readPost();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : myHashtagPosts) {
                        if (post.getPostid().equals(id)) {
                            postList.add(post);
                        }
                    }
                }
                Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
