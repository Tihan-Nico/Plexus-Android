package com.plexus.account.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.account.ProfileLogger;
import com.plexus.model.account.User;

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

public class ProfileActivityAdapter extends RecyclerView.Adapter<ProfileActivityAdapter.ImageViewHolder> {

    private Context mContext;
    private List<ProfileLogger> profileLoggerList;
    private FirebaseUser firebaseUser;

    public ProfileActivityAdapter(Context context, List<ProfileLogger> profileActivities) {
        mContext = context;
        profileLoggerList = profileActivities;
    }

    @NonNull
    @Override
    public ProfileActivityAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_activity_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileActivityAdapter.ImageViewHolder holder, final int position) {

        final ProfileLogger profileLogger = profileLoggerList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.title.setText(profileLogger.getTitle());
        holder.timestamp.setReferenceTime(Long.parseLong(profileLogger.getTimestamp()));

        getUserInfo(holder.image);

        if (profileLogger.isLike()) {
            holder.image.setImageResource(R.drawable.heart);
        } else if (profileLogger.isComment()) {
            holder.image.setImageResource(R.drawable.comment_multiple_outline);
        } else if (profileLogger.isFollow()) {
            holder.image.setImageResource(R.drawable.account_supervisor_outline);
        } else if (profileLogger.isPost()) {
            holder.image.setImageResource(R.drawable.post_outline);
        } else if (profileLogger.isStory()) {
            holder.image.setImageResource(R.drawable.cards_outline);
        }
    }

    @Override
    public int getItemCount() {
        return profileLoggerList.size();
    }

    private void getUserInfo(final SimpleDraweeView imageView) {
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        imageView.setImageURI(user.getImageurl());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView image;
        TextView title;
        RelativeTimeTextView timestamp;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.timestamp);

        }
    }
}
