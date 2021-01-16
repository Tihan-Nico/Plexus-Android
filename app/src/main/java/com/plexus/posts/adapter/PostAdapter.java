package com.plexus.posts.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.github.tntkhang.gmailsenderlibrary.GMailSender;
import com.github.tntkhang.gmailsenderlibrary.GmailListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.components.background.PlexusDelete;
import com.plexus.components.components.ImageView.Constants;
import com.plexus.components.components.socials.PlexusSocialTextView;
import com.plexus.model.Token;
import com.plexus.model.posts.Post;
import com.plexus.model.posts.SavedPostsCollection;
import com.plexus.model.account.User;
import com.plexus.notifications.fcm.FirebaseNotificationHelper;
import com.plexus.posts.activity.PostDetailActivity;
import com.plexus.posts.activity.PostImageViewActivity;
import com.plexus.posts.activity.PostVideoViewActivity;
import com.plexus.posts.activity.comment.CommentActivity;
import com.plexus.posts.activity.saved_posts.CreateCollectionsSavesActivity;
import com.plexus.posts.adapter.saves.CollectionSheetAdapter;
import com.plexus.account.activity.FollowersActivity;
import com.plexus.account.activity.ProfileActivity;
import com.plexus.utils.MasterCipher;
import com.plexus.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context mContext;
    List<Post> mPosts;
    private String profileid;
    private String postid;
    String appCode = "plexusinc";
    private FirebaseUser firebaseUser;
    BottomSheetDialog sheetPost, editPost, shareSheet, save_collection_sheet;
    Bitmap bitmap;
    boolean isDetailView;
    MediaPlayer mediaPlayer = new MediaPlayer();

    //Save Sheet
    private CollectionSheetAdapter collectionSheetAdapter;
    private List<SavedPostsCollection> savedPostsCollectionList;

    public PostAdapter(Context context, List<Post> posts, boolean isDetailView) {
        mContext = context;
        mPosts = posts;
        this.isDetailView = isDetailView;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("GetInstance")
    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPosts.get(position);
        String type = mPosts.get(position).getType();

        holder.timestamp.setReferenceTime(Long.parseLong(post.getTimestamp()));

        publisherInfo(holder.image_profile, holder.publisher, post.getPublisher());
        isLiked(post.getPostid(), holder.like);
        nrLikes(holder.like_count, post.getPostid());
        getComments(post.getPostid(), holder.comment_count);

        SharedPreferences prefs = mContext.getSharedPreferences("plexus", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        postid = prefs.getString("postid", "none");

        if (post.isShared()) {
            switch (type) {
                case "image":
                    holder.image.setVisibility(View.VISIBLE);
                    holder.video.setVisibility(View.GONE);
                    holder.profile_view.setVisibility(View.GONE);
                    holder.post_voice_note.setVisibility(View.GONE);
                    getPostInformationImage(post, holder.post_image);
                    holder.shared.setVisibility(View.VISIBLE);
                    break;
                case "video":
                    holder.video.setVisibility(View.VISIBLE);
                    holder.profile_view.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    holder.post_voice_note.setVisibility(View.GONE);
                    getPostInformationVideo(post, holder.post_video);
                    holder.shared.setVisibility(View.VISIBLE);
                    break;
                case "profile_image":
                    holder.profile_view.setVisibility(View.VISIBLE);
                    holder.video.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    holder.post_voice_note.setVisibility(View.GONE);
                    getPostInformationImage(post, holder.profile_image_update);
                    coverProfileUpdateBackground(holder.profile_cover_blur, holder.profile_image_update, post);
                    holder.shared.setVisibility(View.VISIBLE);
                    break;
                case "text":
                    holder.post_voice_note.setVisibility(View.GONE);
                    holder.video.setVisibility(View.GONE);
                    holder.profile_view.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    holder.shared.setVisibility(View.VISIBLE);
                    break;
                case "audio":
                    holder.post_voice_note.setVisibility(View.VISIBLE);
                    holder.video.setVisibility(View.GONE);
                    holder.profile_view.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    getPostInformationAudio(holder.post_voice_note, post);
                    holder.shared.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            switch (type) {
                case "image":
                    holder.image.setVisibility(View.VISIBLE);
                    holder.video.setVisibility(View.GONE);
                    holder.profile_view.setVisibility(View.GONE);
                    holder.post_voice_note.setVisibility(View.GONE);
                    holder.post_image.setImageURI(MasterCipher.decrypt(post.getPostimage()));
                    holder.shared.setVisibility(View.GONE);
                    break;
                case "video":
                    holder.video.setVisibility(View.VISIBLE);
                    holder.profile_view.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    holder.post_voice_note.setVisibility(View.GONE);
                    Glide.with(mContext).asBitmap().load(MasterCipher.decrypt(post.getVideoURL())).into(holder.post_video);
                    holder.shared.setVisibility(View.GONE);
                    break;
                case "profile_image":
                    holder.profile_view.setVisibility(View.VISIBLE);
                    holder.video.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    holder.post_voice_note.setVisibility(View.GONE);
                    coverProfileUpdateBackground(holder.profile_cover_blur, holder.profile_image_update, post);
                    holder.shared.setVisibility(View.GONE);
                    break;
                case "text":
                    holder.post_voice_note.setVisibility(View.GONE);
                    holder.video.setVisibility(View.GONE);
                    holder.profile_view.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    holder.shared.setVisibility(View.GONE);
                    break;
                case "audio":
                    holder.post_voice_note.setVisibility(View.VISIBLE);
                    holder.video.setVisibility(View.GONE);
                    holder.profile_view.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    attachmentVoiceNote(holder.post_voice_note, post.getPublisher(), post);
                    holder.shared.setVisibility(View.GONE);
                    break;
            }
        }

        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(MasterCipher.decrypt(post.getDescription()));
        }

        if (post.isShared()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(post.getShared_postid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        holder.shared.setVisibility(View.VISIBLE);
                        if (holder.description_shared.getText().toString().equals("")) {
                            holder.description_shared.setVisibility(View.GONE);
                        } else {
                            holder.description_shared.setVisibility(View.VISIBLE);
                        }
                        getPostInformationDescription(post, holder.description_shared);
                        sharedPublisherInfo(holder.image_profile_shared, holder.publisher_shared, post.getShared_userid());
                    } else {
                        holder.post_non_existent.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        holder.more.setOnClickListener(v -> moreSheet(post));

        //Share Sheet
        shareSheet = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        shareSheet.setContentView(R.layout.post_share_sheet);
        SimpleDraweeView image_profile = shareSheet.findViewById(R.id.image_profile);
        EditText description = shareSheet.findViewById(R.id.description);
        Button share_now = shareSheet.findViewById(R.id.share_now);
        LinearLayout share_ext = shareSheet.findViewById(R.id.share_ext);
        TextView publisher = shareSheet.findViewById(R.id.publisher);

        shareSheetUserInfo(image_profile, publisher);

        holder.share.setOnClickListener(v -> shareSheet.show());

        share_now.setOnClickListener(v -> {
            sharePost(description.getText().toString(), firebaseUser.getUid(), String.valueOf(System.currentTimeMillis()), post.getType(), post.getPostid(), post.getPublisher(), post);
            shareSheet.dismiss();
        });

        holder.like.setOnClickListener(
                view -> {
                    if (holder.like.getTag().equals("like")) {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Likes")
                                .child(post.getPostid())
                                .child(firebaseUser.getUid())
                                .setValue(true);

                        profileActivity();

                        if (post.getPublisher().equals(firebaseUser.getUid())) {
                            /*
                             *Notification doesn't get sent to the user
                             * if the post equals to the publisher ID....
                             */

                        } else {
                            addLikeNotification(post.getPublisher(), post.getPostid());
                            sendNotification("liked your post", "com.plexus.plexus_POST_TARGET_NOTIFICATION", post.getPostid(), "like");
                        }
                    } else {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Likes")
                                .child(post.getPostid())
                                .child(firebaseUser.getUid())
                                .removeValue();
                        FirebaseDatabase.getInstance().getReference("Users").child(profileid).child("Notifications").child(post.getPostid()).removeValue();
                    }
                });

        holder.image_profile.setOnClickListener(
                view -> {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("userid", post.getPublisher());
                    mContext.startActivity(intent);
                });

        holder.publisher.setOnClickListener(
                view -> {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("userid", post.getPublisher());
                    mContext.startActivity(intent);
                });

        if (isDetailView){
            // Disables the abilty to click on the post layout
        } else {
            holder.post_layout.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            });
        }

        holder.comment_count.setOnClickListener(
                view -> {
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra("postid", post.getPostid());
                    intent.putExtra("publisherid", post.getPublisher());
                    mContext.startActivity(intent);
                });

        if (post.isShared()){
            holder.post_image.setOnClickListener(
                    view -> {
                        Intent intent = new Intent(mContext, PostImageViewActivity.class);
                        intent.putExtra("postid", post.getShared_postid());
                        intent.putExtra("publisherid", post.getShared_userid());
                        mContext.startActivity(intent);
                    });
            holder.post_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostVideoViewActivity.class);
                    intent.putExtra("publisher", post.getShared_userid());
                    intent.putExtra("downloadLink", post.getVideoURL());
                    intent.putExtra("description", post.getDescription());
                    mContext.startActivity(intent);
                }
            });

        } else {
            holder.post_image.setOnClickListener(
                    view -> {
                        Intent intent = new Intent(mContext, PostImageViewActivity.class);
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent);
                    });

            holder.post_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostVideoViewActivity.class);
                    intent.putExtra("publisher", post.getPublisher());
                    intent.putExtra("downloadLink", MasterCipher.decrypt(post.getVideoURL()));
                    intent.putExtra("description", MasterCipher.decrypt(post.getDescription()));
                    mContext.startActivity(intent);
                }
            });

        }

        holder.profile_image_update.setOnClickListener(
                view -> {
                    Intent intent = new Intent(mContext, PostImageViewActivity.class);
                    intent.putExtra("postid", post.getPostid());
                    intent.putExtra("publisherid", post.getPublisher());
                    mContext.startActivity(intent);
                });

        holder.like_count.setOnClickListener(
                view -> {
                    Intent intent = new Intent(mContext, FollowersActivity.class);
                    intent.putExtra("id", post.getPostid());
                    intent.putExtra("title", "likes");
                    mContext.startActivity(intent);
                });

        holder.description.setOnMentionClickListener((view, text) -> {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            intent.putExtra("profileid", publisher.getId());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    /**
     * Voice note attachment for posts. The code below fetches the id
     * and the data from the view and the server.
     */

    private void attachmentVoiceNote(View post_voice_note, String profileid, Post post){
        ImageView play_voice_note = post_voice_note.findViewById(R.id.voice_note_play);
        TextView voice_time = post_voice_note.findViewById(R.id.voice_time);
        TextView voice_hashtag = post_voice_note.findViewById(R.id.voice_hashtag);
        ImageView voice_background = post_voice_note.findViewById(R.id.voice_background);
        ImageView voice_profile_image = post_voice_note.findViewById(R.id.voice_profile_image);

        coverBackground(voice_background, voice_profile_image, profileid);

        play_voice_note.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()){
                if(mediaPlayer!=null){
                    stopVoiceNote();
                    // Changing button image to play button
                    play_voice_note.setImageResource(R.drawable.play);
                }
            }else{
                // Resume audio
                if(mediaPlayer!=null){
                    try {
                        playVoiceNote(post, voice_time);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Changing button image to pause button
                    play_voice_note.setImageResource(R.drawable.pause);
                }
            }

            mediaPlayer.setOnCompletionListener(mp -> play_voice_note.setImageResource(R.drawable.play));

        });
    }

    private void playVoiceNote(Post posts, TextView textView) throws IOException {
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        mediaPlayer.setDataSource(mContext, Uri.parse(MasterCipher.decrypt(posts.getAudioURL())));
        mediaPlayer.prepare();
        mediaPlayer.start();

        long totalDuration = mediaPlayer.getDuration();
        textView.setText(TimeUtils.msToString(totalDuration));

    }

    public void stopVoiceNote() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void coverBackground(ImageView parent, ImageView imageView, String profileid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(profileid);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (bitmap != null) {
                            parent.setBackground(new BitmapDrawable(mContext.getResources(), Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true)))); // ));
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(MasterCipher.decrypt(user.getImageurl()))
                                    .error(R.mipmap.ic_launcher)
                                    .listener(new RequestListener<Bitmap>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                            if (Build.VERSION.SDK_INT >= 16) {
                                                parent.setBackground(new BitmapDrawable(
                                                        mContext.getResources(),
                                                        Constants.fastblur(Bitmap.createScaledBitmap(resource, 50, 50, true)))); // ));
                                            } else {
                                                onPalette(Palette.from(resource).generate(), imageView);
                                            }
                                            imageView.setImageBitmap(resource);
                                            return false;
                                        }
                                    })
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void coverProfileUpdateBackground(ImageView parent, ImageView imageView, Post post) {

        if (bitmap != null) {
            parent.setBackground(new BitmapDrawable(mContext.getResources(), Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true)))); // ));
            imageView.setImageBitmap(bitmap);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(MasterCipher.decrypt(post.getPostimage()))
                    .error(R.mipmap.ic_launcher)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (Build.VERSION.SDK_INT >= 16) {
                                parent.setBackground(new BitmapDrawable(
                                        mContext.getResources(),
                                        Constants.fastblur(Bitmap.createScaledBitmap(resource, 50, 50, true)))); // ));
                            } else {
                                onPalette(Palette.from(resource).generate(), imageView);
                            }
                            imageView.setImageBitmap(resource);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }

    public void onPalette(Palette palette, ImageView photoView) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) photoView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }

    /**
     * The code below adds the notification data to the database
     * then the Server functions reads the trigger and sends a notification
     * to the users device.
     */

    private void sendNotification(String message, String click_action, String postid, String type){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(profileid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);

                            FirebaseNotificationHelper.initialize(com.plexus.components.Constants.FCM_KEY)
                                    .defaultJson(false, getJsonBody(MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getImageurl()), message, click_action, postid, type))
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

    private String getJsonBody(String name, String imageUrl, String message, String click_action, String postid, String type) {

        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put("title", "Plexus");
            jsonObjectData.put("body", name + " " + message);
            jsonObjectData.put("click_action", click_action);
            jsonObjectData.put("from_user_id", firebaseUser.getUid());
            jsonObjectData.put("imageurl", imageUrl);
            jsonObjectData.put("postid", postid);
            jsonObjectData.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObjectData.toString();
    }

    private void sharePost(String share_description, String publisher, String timestamp, String type, String shared_post_id, String shared_post_publisher, Post post) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        String postid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postid", postid);
        hashMap.put("description", MasterCipher.encrypt(share_description));
        hashMap.put("publisher", publisher);
        hashMap.put("timestamp", timestamp);
        hashMap.put("shared_postid", shared_post_id);
        hashMap.put("shared_userid", shared_post_publisher);
        hashMap.put("type", type);
        hashMap.put("shared", true);

        reference.child(postid).setValue(hashMap);

        addShareNotification(shared_post_publisher, shared_post_id);
        addShareToPost(post, postid);
        sendNotification("shared your post", "com.plexus.plexus_POST_TARGET_NOTIFICATION", shared_post_id, "shared");
    }

    private void addShareNotification(String profileid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid).child("Notification");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("notificationRead", false);
        hashMap.put("notificationViewed", false);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("shared", true);

        reference.child(id).setValue(hashMap);
    }

    private void addLikeNotification(String profileid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid).child("Notification");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("notificationRead", false);
        hashMap.put("notificationViewed", false);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("reaction", true);

        reference.child(id).setValue(hashMap);
    }

    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Notification");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue().addOnCompleteListener(task -> Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * @param like_count
     * @param postId
     */

    private void nrLikes(final TextView like_count, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        like_count.setText(dataSnapshot.getChildrenCount() + " Likes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void getComments(String postId, final TextView comment_count) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        comment_count.setText(dataSnapshot.getChildrenCount() + " Comments");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void addShareToPost(Post post, String postid) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Posts")
                .child(post.getPostid())
                .child("Shares")
                .child(postid)
                .setValue(true);
    }

    private void isSavedInCollection(Post post) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections");
        ref.child("Collections").orderByChild(post.getPostid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(mContext, "Saved In Memes", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Not Saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(final String postid, final ImageView imageView) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
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
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void profileActivity(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Activity Log");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("title", "You liked a post");
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("isLike", true);

        reference.child(id).setValue(hashMap);
    }

    private void isSaved(final String postid, final ImageView imageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Recent");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.bookmark_multiple);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.bookmark_multiple_outline);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void checkCollections(ImageView imageView, Post post) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    saveCollectionSheet(post);
                    savePost(imageView, post);
                    sheetPost.dismiss();
                } else {
                    savePost(imageView, post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editPost(final String postid) {
        sheetPost.dismiss();

        editPost = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        editPost.setContentView(R.layout.sheet_edit_post);
        EditText caption_edit = editPost.findViewById(R.id.caption);
        Button save_edit = editPost.findViewById(R.id.save_edit);

        save_edit.setOnClickListener(
                view -> {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("description", MasterCipher.encrypt(caption_edit.getText().toString()));

                    FirebaseDatabase.getInstance().getReference("Posts").child(postid).updateChildren(hashMap);

                    editPost.dismiss();
                });

        editPost.show();
    }

    private void moreSheet(Post post){
        sheetPost = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        sheetPost.setContentView(R.layout.sheet_post);
        LinearLayout edit_post = sheetPost.findViewById(R.id.edit_post);
        LinearLayout delete_posts = sheetPost.findViewById(R.id.delete_posts);
        LinearLayout save_post = sheetPost.findViewById(R.id.save_post);
        ImageView save = sheetPost.findViewById(R.id.save);
        LinearLayout report_post = sheetPost.findViewById(R.id.report_post);
        LinearLayout copy_link = sheetPost.findViewById(R.id.copy_link);
        View line2 = sheetPost.findViewById(R.id.line2);
        View line3 = sheetPost.findViewById(R.id.line3);

        delete_posts.setOnClickListener(v -> {
            PlexusDelete.deletePost(sheetPost, postid, firebaseUser.getUid(), mContext);
        });

        report_post.setOnClickListener(v -> reportPost());

        edit_post.setOnClickListener(v -> {
            editPost(post.getPostid());
        });

        isSaved(post.getPostid(), save);

        delete_posts.setOnClickListener(v -> deletePost(post.getPostid()));

        save_post.setOnClickListener(v -> {
            checkCollections(save, post);
        });

        if (!post.getPublisher().equals(firebaseUser.getUid())){
            edit_post.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            delete_posts.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Uri postLink= Uri.parse(child.getKey());
                    Uri.Builder builder = new Uri.Builder()
                            .scheme("https")
                            .authority(appCode + ".page.link")
                            .appendPath("post")
                            .appendQueryParameter("post", postLink.toString());
                    Uri dynamicLink = builder.build();

                    copy_link.setOnClickListener(v -> {
                        try {
                            URL url = new URL(URLDecoder.decode(dynamicLink.toString(), "UTF-8"));
                            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("post", url.toString());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(mContext, "Post Link Copied", Toast.LENGTH_SHORT).show();
                        } catch (MalformedURLException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        sheetPost.dismiss();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sheetPost.show();
    }

    private void savePost(ImageView imageView, Post post){
        if (imageView.getTag().equals("save")){
            FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Recent")
                    .child(post.getPostid()).setValue(true);
        } else {
            FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Recent")
                    .child(post.getPostid()).removeValue();
        }
    }

    private void saveCollectionSheet(Post post){
        save_collection_sheet = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        save_collection_sheet.setContentView(R.layout.sheet_save_post);

        LinearLayout create_collection = save_collection_sheet.findViewById(R.id.create_collection);
        RecyclerView recyclerView = save_collection_sheet.findViewById(R.id.recycler_view);

        create_collection.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, CreateCollectionsSavesActivity.class)));

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        savedPostsCollectionList = new ArrayList<>();
        collectionSheetAdapter = new CollectionSheetAdapter(mContext, savedPostsCollectionList, post.getPostid());
        recyclerView.setAdapter(collectionSheetAdapter);

        getCollections();
        isSavedInCollection(post);

        save_collection_sheet.show();

    }

    /**
     * The section below fetches user and post information from the database for the certain post
     * and the share sheet.
     */
    private void publisherInfo(final SimpleDraweeView image_profile, final TextView publisher, final String userid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image_shared_db = dataSnapshot.child("imageurl").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);
                String surname = dataSnapshot.child("surname").getValue(String.class);

                image_profile.setImageURI(MasterCipher.decrypt(image_shared_db));
                publisher.setText(String.format("%s %s", MasterCipher.decrypt(name), MasterCipher.decrypt(surname)));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getCollections() {
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
                collectionSheetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void deletePost(String postid){
        sheetPost.dismiss();

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_post_delete);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button delete = dialog.findViewById(R.id.delete);
        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {
            final String id = postid;
            FirebaseDatabase.getInstance().getReference("Posts")
                    .child(postid).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            deleteNotifications(id, firebaseUser.getUid());
                            dialog.dismiss();
                        }
                    });
        });

        dialog.show();
    }

    private void reportPost(){
        BottomSheetDialog report_post = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        report_post.setContentView(R.layout.report_sheet);
        ImageView back = report_post.findViewById(R.id.back);
        Button send_report = report_post.findViewById(R.id.report_post);
        LinearLayout unfollow = report_post.findViewById(R.id.unfollow_user);
        LinearLayout block_user = report_post.findViewById(R.id.block_user);
        EditText editText = report_post.findViewById(R.id.report);
        TextView block_name = report_post.findViewById(R.id.block_name);
        TextView unfollow_name = report_post.findViewById(R.id.unfollow_name);

        block_user.setOnClickListener(v -> blockUser(profileid));

        back.setOnClickListener(v -> report_post.dismiss());

        send_report.setOnClickListener(v -> {
            try {
                sendMail();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(profileid);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        block_name.setText(MessageFormat.format("Block {0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                        unfollow_name.setText(MessageFormat.format("Unfollow {0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

        unfollow.setOnClickListener(v -> {
            unfollowUser();
        });

        report_post.show();

    }

    private void blockUser(String profileid) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", profileid);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Blocked Users").child(profileid).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(mContext, "User was blocked successfully.", Toast.LENGTH_SHORT).show();

                });
    }

    private void unfollowUser(){
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
    }

    private void sendMail() throws PackageManager.NameNotFoundException {

        String serviceType = Context.TELEPHONY_SERVICE;
        PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            TelephonyManager m_telephonyManager = (TelephonyManager) mContext.getSystemService(serviceType);
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            GMailSender.withAccount("tihannicopaxton2@gmail.com", "Chocolates123")
                                    .withTitle("Post Report - Plexus Android")
                                    .withBody("Post Report"
                                            + "\n"
                                            + "\n Plexus User Information"
                                            + "\n"
                                            + "\n Users User ID: " + firebaseUser.getUid()
                                            + "\n Users Email: " + firebaseUser.getEmail()
                                            + "\n Users Full Name: " + user.getName() + " " + user.getSurname()
                                            + "\n Plexus Post ID: " + child.getKey()
                                            + "\n Plexus Post Link: " + child.getRef().toString()
                                            + "\n"
                                            + "\n Plexus Application Information"
                                            + "\n"
                                            + "\n APP Package Name: " + mContext.getPackageName()
                                            + "\n APP Version Name: " + pInfo.versionName
                                            + "\n APP Version Code: " + pInfo.versionCode
                                            + "\n"
                                            + "\n Mobile Operator Information"
                                            + "\n"
                                            + "\n Device ID: " + m_telephonyManager.getDeviceId()
                                            + "\n Subscriber ID: " + m_telephonyManager.getSubscriberId()
                                            + "\n Network Operator: " + m_telephonyManager.getNetworkOperator()
                                            + "\n Sim Operator Name: " + m_telephonyManager.getSimOperatorName()
                                            + "\n"
                                            + "\n Device Information"
                                            + "\n"
                                            + "\n OS Version: " + System.getProperty("os.version") + " (" + android.os.Build.VERSION.INCREMENTAL + ")"
                                            + "\n OS API Level: " + android.os.Build.VERSION.SDK
                                            + "\n Device: " + android.os.Build.DEVICE
                                            + "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")"
                                            + "\n Manufacturer: " + android.os.Build.MANUFACTURER
                                            + "\n Other TAGS: " + android.os.Build.TAGS
                                    )
                                    .withSender(mContext.getString(R.string.app_name))
                                    .toEmailAddress("plexusincsa@gmail.com") // one or multiple addresses separated by a comma
                                    .withListenner(new GmailListener() {
                                        @Override
                                        public void sendSuccess() {
                                            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void sendFail(String err) {
                                            Toast.makeText(mContext, "Fail: " + err, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .send();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void shareSheetUserInfo(final SimpleDraweeView image_profile, final TextView name) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String image_shared_db = dataSnapshot.child("imageurl").getValue(String.class);
                        String name_db = dataSnapshot.child("name").getValue(String.class);
                        String surname = dataSnapshot.child("surname").getValue(String.class);

                        image_profile.setImageURI(MasterCipher.decrypt(image_shared_db));
                        name.setText(String.format("%s %s", MasterCipher.decrypt(name_db), MasterCipher.decrypt(surname)));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

    private void sharedPublisherInfo(final SimpleDraweeView image_profile_shared, final TextView publisher_shared, final String userid_shared) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid_shared);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String image_shared_db = dataSnapshot.child("imageurl").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String surname = dataSnapshot.child("surname").getValue(String.class);

                        image_profile_shared.setImageURI(MasterCipher.decrypt(image_shared_db));
                        publisher_shared.setText(String.format("%s %s", MasterCipher.decrypt(name), MasterCipher.decrypt(surname)));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getPostInformationDescription(Post post, TextView description) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getShared_postid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String description_db = snapshot.child("description").getValue(String.class);
                description.setText(MasterCipher.decrypt(description_db));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostInformationAudio(View view, Post post) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getShared_postid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                attachmentVoiceNote(view, post.getShared_userid(), post);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostInformationVideo(Post post, ImageView post_image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getShared_postid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).asBitmap().load(MasterCipher.decrypt(post.getVideoURL())).into(post_image);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostInformationImage(Post post, ImageView post_image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getShared_postid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).asBitmap().load(MasterCipher.decrypt(post.getPostimage())).into(post_image);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView follow, like, comment, save, more, message, presence, share, profile_cover_blur, profile_image_update, post_video;
        public TextView publisher, like_count, comment_count, publisher_shared;
        public PlexusSocialTextView description, description_shared;
        public RelativeTimeTextView timestamp;
        public LinearLayout post_layout, shared;
        RelativeLayout profile_view;
        SimpleDraweeView image_profile, post_image, image_profile_shared;
        View post_non_existent, post_voice_note;
        ConstraintLayout image, video;

        public ViewHolder(View itemView) {
            super(itemView);

            post_layout = itemView.findViewById(R.id.post_layout);
            shared = itemView.findViewById(R.id.shared);
            follow = itemView.findViewById(R.id.follow);
            image_profile = itemView.findViewById(R.id.image_profile);
            image_profile_shared = itemView.findViewById(R.id.image_profile_shared);
            post_video = itemView.findViewById(R.id.post_video);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            like_count = itemView.findViewById(R.id.like_count);
            comment_count = itemView.findViewById(R.id.comment_count);
            save = itemView.findViewById(R.id.save);
            timestamp = itemView.findViewById(R.id.timestamp);
            publisher = itemView.findViewById(R.id.publisher);
            publisher_shared = itemView.findViewById(R.id.publisher_shared);
            profile_image_update = itemView.findViewById(R.id.profile_image_update);
            description = itemView.findViewById(R.id.description);
            description_shared = itemView.findViewById(R.id.description_shared);
            message = itemView.findViewById(R.id.message);
            presence = itemView.findViewById(R.id.presence_online);
            more = itemView.findViewById(R.id.menu);
            share = itemView.findViewById(R.id.share);
            post_non_existent = itemView.findViewById(R.id.post_non_existent);
            post_voice_note = itemView.findViewById(R.id.post_voice_note);
            profile_cover_blur = itemView.findViewById(R.id.profile_cover_blur);
            profile_view = itemView.findViewById(R.id.profile_view);
            image = itemView.findViewById(R.id.image);
            video = itemView.findViewById(R.id.video);

        }
    }

}
