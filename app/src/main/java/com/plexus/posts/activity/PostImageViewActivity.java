package com.plexus.posts.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.plexus.account.activity.FollowersActivity;
import com.plexus.components.components.ImageView.Constants;
import com.plexus.components.components.ImageView.PhotoView;
import com.plexus.model.posts.Post;
import com.plexus.posts.activity.comment.CommentActivity;

import java.util.HashMap;
import java.util.Objects;

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

public class PostImageViewActivity extends AppCompatActivity {

    RelativeLayout parent;
    PhotoView photoView;
    Bitmap bitmap;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    String profileid;
    String postid;
    String publisherid;
    String filename;
    boolean showPostDetails = true;
    private ImageView back, like;
    private TextView like_count, comment_count;
    private Toolbar toolbar;
    private LinearLayout weight, likes, comments, message;
    private final String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_image_view_fullscreen);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("posts/");

        SharedPreferences prefs = PostImageViewActivity.this.getSharedPreferences("plexus", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        photoView = findViewById(R.id.image_view);
        parent = findViewById(R.id.background);
        back = findViewById(R.id.back);
        toolbar = findViewById(R.id.toolbar);
        weight = findViewById(R.id.weight);
        like = findViewById(R.id.like);
        like_count = findViewById(R.id.like_count);
        comment_count = findViewById(R.id.comment_count);
        likes = findViewById(R.id.likes);
        comments = findViewById(R.id.comments);
        message = findViewById(R.id.message);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);

                        if (bitmap != null) {
                            parent.setBackground(
                                    new BitmapDrawable(
                                            PostImageViewActivity.this.getResources(),
                                            Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true)))); // ));
                            photoView.setImageBitmap(bitmap);
                        } else {
                            Glide.with(PostImageViewActivity.this)
                                    .asBitmap()
                                    .load(post.getPostimage())
                                    .error(R.mipmap.ic_launcher)
                                    .listener(
                                            new RequestListener<Bitmap>() {
                                                @Override
                                                public boolean onLoadFailed(
                                                        @Nullable GlideException e,
                                                        Object model,
                                                        Target<Bitmap> target,
                                                        boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(
                                                        Bitmap resource,
                                                        Object model,
                                                        Target<Bitmap> target,
                                                        DataSource dataSource,
                                                        boolean isFirstResource) {
                                                    if (Build.VERSION.SDK_INT >= 16) {
                                                        parent.setBackground(
                                                                new BitmapDrawable(
                                                                        PostImageViewActivity.this.getResources(),
                                                                        Constants.fastblur(
                                                                                Bitmap.createScaledBitmap(resource, 50, 50, true)))); // ));
                                                    } else {
                                                        onPalette(Palette.from(resource).generate());
                                                    }
                                                    photoView.setImageBitmap(resource);
                                                    return false;
                                                }
                                            })
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(photoView);
                        }

                        isLiked(post.getPostid(), like);
                        nrLikes(like_count, post.getPostid());
                        getComments(post.getPostid(), comment_count);

                        like.setOnClickListener(
                                view -> {
                                    if (like.getTag().equals("like")) {
                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Likes")
                                                .child(post.getPostid())
                                                .child(firebaseUser.getUid())
                                                .setValue(true);
                                        addNotification(post.getPostid());
                                    } else {
                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Likes")
                                                .child(post.getPostid())
                                                .child(firebaseUser.getUid())
                                                .removeValue();
                                        FirebaseDatabase.getInstance()
                                                .getReference("Users")
                                                .child(publisherid)
                                                .child("Notifications")
                                                .child(postid)
                                                .removeValue();
                                    }
                                });

                        comments.setOnClickListener(
                                view -> {
                                    Intent intent = new Intent(PostImageViewActivity.this, CommentActivity.class);
                                    intent.putExtra("postid", post.getPostid());
                                    intent.putExtra("publisherid", post.getPublisher());
                                    PostImageViewActivity.this.startActivity(intent);
                                });

                        likes.setOnClickListener(
                                view -> {
                                    Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
                                    intent.putExtra("id", post.getPostid());
                                    intent.putExtra("title", "likes");
                                    getApplicationContext().startActivity(intent);
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        back.setOnClickListener(view1 -> finish());

        photoView.setOnClickListener(
                view13 -> {
                    if (showPostDetails == true) {
                        weight.setVisibility(View.GONE);
                        toolbar.setVisibility(View.GONE);
                        showPostDetails = false;
                    } else {
                        weight.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);
                        showPostDetails = true;
                    }
                });
    }

    private void addNotification(String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid).child("Notifications");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("reaction", true);

        reference.child(id).setValue(hashMap);
    }

    private void deleteNotifications(final String postid, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Notification");
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(snapshot.child("postid").getValue(), postid)) {
                                snapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void nrLikes(final TextView like_count, String postId) {
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        like_count.setText(dataSnapshot.getChildrenCount() + " Likes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getComments(String postId, final TextView comment_count) {
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        comment_count.setText(dataSnapshot.getChildrenCount() + " Comments");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void isLiked(final String postid, final ImageView imageView) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        assert firebaseUser != null;
                        if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                            imageView.setImageResource(R.drawable.liked);
                            imageView.setTag("liked");
                        } else {
                            imageView.setImageResource(R.drawable.like);
                            imageView.setTag("like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public void onPalette(Palette palette) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) photoView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }
}
