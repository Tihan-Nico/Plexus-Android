package com.plexus.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.account.activity.ProfileActivity;
import com.plexus.components.background.DialogInformation;
import com.plexus.model.account.User;
import com.plexus.model.notifications.PlexusNotification;
import com.plexus.posts.activity.PostDetailActivity;
import com.plexus.utils.MasterCipher;

import java.text.MessageFormat;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {

    String postid;
    Context mContext;
    List<PlexusNotification> mPlexusNotification;
    FirebaseUser firebaseUser;

    public NotificationAdapter(Context context, List<PlexusNotification> plexusNotification) {
        mContext = context;
        mPlexusNotification = plexusNotification;
    }

    @NonNull
    @Override
    public NotificationAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ImageViewHolder holder, final int position) {
        final PlexusNotification plexusNotification = mPlexusNotification.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.notification_timestamp.setReferenceTime(Long.parseLong(plexusNotification.getTimestamp()));
        getUserInfo(holder.profile_image, plexusNotification.getUserid());
        getNotificationDescription(plexusNotification, holder.notification_description, plexusNotification.getUserid());

        SharedPreferences prefs = mContext.getSharedPreferences("plexus", MODE_PRIVATE);
        postid = prefs.getString("postid", plexusNotification.getPostid());

        if (!plexusNotification.isNotificationViewed()) {
            holder.notification_background.setBackgroundColor(Color.parseColor("#D9CC775E"));
        }

        if (!plexusNotification.isNotificationRead()) {
            readNotifications();
        }

        holder.itemView.setOnClickListener(
                view -> {
                    if (plexusNotification.isNotificationViewed()) {
                        Intent intent;
                        if (plexusNotification.isIspost()) {
                            intent = new Intent(mContext, PostDetailActivity.class);
                            intent.putExtra("postid", plexusNotification.getPostid());
                            intent.putExtra("publisherid", plexusNotification.getUserid());

                        } else {
                            intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra("userid", plexusNotification.getUserid());

                        }
                        mContext.startActivity(intent);
                    } else {
                        Intent intent;
                        if (plexusNotification.isIspost()) {
                            intent = new Intent(mContext, PostDetailActivity.class);
                            intent.putExtra("postid", plexusNotification.getPostid());
                            intent.putExtra("publisherid", plexusNotification.getUserid());
                        } else {
                            intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra("userid", plexusNotification.getUserid());
                        }
                        viewedNotification(plexusNotification.getId());
                        mContext.startActivity(intent);
                    }
                });

        holder.notification_menu.setOnClickListener(v -> DialogInformation.showNotificationBottomSheet(mContext, plexusNotification, plexusNotification.getUserid()));

    }

    @Override
    public int getItemCount() {
        return mPlexusNotification == null ? 0 : mPlexusNotification.size();
    }

    private void viewedNotification(String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Notification").child(id);
        databaseReference.child("notificationViewed").setValue(true);
    }

    private void readNotifications() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Notification");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlexusNotification plexusNotification = snapshot.getValue(PlexusNotification.class);
                    if (plexusNotification != null) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("notificationRead", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserInfo(ImageView imageView, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Glide.with(mContext).asBitmap().load(MasterCipher.decrypt(user.getImageurl())).into(imageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getNotificationDescription(PlexusNotification plexusNotification, TextView notification_description, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (plexusNotification.isFollower()) {
                            String sourceString = "<b>" + MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())) + "</b> " + "started following you.";
                            notification_description.setText(Html.fromHtml(sourceString));
                        } else if (plexusNotification.isComment()) {
                            String sourceString = "<b>" + MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())) + "</b> " + "commented on your post.";
                            notification_description.setText(Html.fromHtml(sourceString));
                        } else if (plexusNotification.isReaction()) {
                            String sourceString = "<b>" + MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())) + "</b> " + "liked your post.";
                            notification_description.setText(Html.fromHtml(sourceString));
                        } else if (plexusNotification.isShared()) {
                            String sourceString = "<b>" + MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())) + "</b> " + "shared your post.";
                            notification_description.setText(Html.fromHtml(sourceString));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView notification_description;
        RelativeTimeTextView notification_timestamp;
        ImageView notification_menu, notification_type_image, profile_image;
        RelativeLayout notification_background;

        public ImageViewHolder(View itemView) {
            super(itemView);
            notification_background = itemView.findViewById(R.id.notification_background);
            profile_image = itemView.findViewById(R.id.profile_image);
            notification_type_image = itemView.findViewById(R.id.notification_type_image);
            notification_menu = itemView.findViewById(R.id.notification_menu);
            notification_description = itemView.findViewById(R.id.notification_description);
            notification_timestamp = itemView.findViewById(R.id.notification_timestamp);
        }
    }
}
