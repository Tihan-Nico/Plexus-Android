package com.plexus.posts.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
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
import com.plexus.model.account.User;
import com.plexus.model.posts.Comment;
import com.plexus.model.posts.Post;
import com.plexus.posts.adapter.CommentAdapter;
import com.plexus.posts.adapter.PostAdapter;
import com.plexus.utils.MasterCipher;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class PostDetailActivity extends AppCompatActivity {

    String postid;
    String publisherid;
    RecyclerView recyclerView, recyclerViewComments;
    Post post_;
    EditText addcomment;
    ImageView image, back, post;
    FirebaseUser firebaseUser;
    Uri mImageUri = null;
    private PostAdapter postAdapter;
    private CommentAdapter commentAdapter;
    private List<Post> postList;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);

        addcomment = findViewById(R.id.message);
        image = findViewById(R.id.image);
        post = findViewById(R.id.send);
        back = findViewById(R.id.back);

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        Uri data = getIntent().getData();
        if (data != null) {
            postid = data.getQueryParameter("id");
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(PostDetailActivity.this, postList, true);
        recyclerView.setAdapter(postAdapter);

        recyclerViewComments = findViewById(R.id.recycler_view_comments);
        recyclerViewComments.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagerComments = new LinearLayoutManager(this);
        recyclerViewComments.setLayoutManager(mLayoutManagerComments);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postid, post_);
        recyclerViewComments.setAdapter(commentAdapter);

        back.setOnClickListener(v -> finish());

        image.setOnClickListener(v -> CropImage.activity().setAspectRatio(9, 16).start(PostDetailActivity.this));

        addcomment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                post.setVisibility(View.VISIBLE);
            } else {
                post.setVisibility(View.GONE);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (publisherid.equals(firebaseUser.getUid())) {
                            addcomment.setHint("Comment on your post");
                        } else {
                            addcomment.setHint("Comment on " + MasterCipher.decrypt(user.getName()) + " " + MasterCipher.decrypt(user.getSurname()) + " post");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        post.setOnClickListener(
                view -> {
                    String comment = addcomment.getText().toString();
                    if (addcomment.getText().toString().equals("")) {
                        Toast.makeText(PostDetailActivity.this, "You can't send empty comments", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        addComment(comment);
                        profileActivity();

                        if (publisherid.equals(firebaseUser.getUid())) {
                            /*
                             *Notification doesn't get sent to the user
                             * if the post equals to the publisher ID....
                             */

                        } else {
                            sendFunctionNotification("comment");
                        }

                    }
                });

        readComments();

        readPost();

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    private void addComment(String comment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", MasterCipher.encrypt(comment));
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);
        hashMap.put("type", "text");
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

        reference.child(commentid).setValue(hashMap);
        addNotification();

        addcomment.setText("");
    }

    private void sendImage(Uri uri) throws IOException {
        String filePath = "posts/comments/" + "image_" + System.currentTimeMillis();

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePath);
        storageReference.putBytes(data).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String downloadUri = uriTask.getResult().toString();

            if (uriTask.isSuccessful()) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
                String commentid = databaseReference.push().getKey();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("comment", MasterCipher.encrypt(downloadUri));
                hashMap.put("publisher", firebaseUser.getUid());
                hashMap.put("commentid", commentid);
                hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
                hashMap.put("type", "image");

                databaseReference.child(commentid).setValue(hashMap);

            }

        }).addOnFailureListener(e -> {
        });
    }

    private void profileActivity() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Activity Log");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("title", "You commented on a post");
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("isPost", true);

        reference.child(id).setValue(hashMap);
    }

    private void sendFunctionNotification(String type) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
                String notificationId = notificationRef.push().getKey();

                HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("from", firebaseUser.getUid());
                notificationData.put("type", type);
                notificationData.put("name", MasterCipher.decrypt(user.getName()));
                notificationData.put("profile_image", MasterCipher.decrypt(user.getImageurl()));
                notificationRef.child(notificationId).setValue(notificationData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification() {
        DatabaseReference reference =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(firebaseUser.getUid())
                        .child("Notification");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("comment", true);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("isSeen", false);

        reference.push().setValue(hashMap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            try {
                sendImage(mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void readComments() {
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        commentList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comment comment = snapshot.getValue(Comment.class);
                            commentList.add(comment);
                        }

                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
