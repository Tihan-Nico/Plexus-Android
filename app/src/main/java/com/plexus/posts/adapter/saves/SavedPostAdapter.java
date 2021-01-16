package com.plexus.posts.adapter.saves;

import android.content.Context;
import android.content.Intent;
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
import com.plexus.model.account.User;
import com.plexus.model.posts.Post;
import com.plexus.posts.activity.PostDetailActivity;
import com.plexus.utils.MasterCipher;

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

public class SavedPostAdapter extends RecyclerView.Adapter<SavedPostAdapter.ViewHolder> {

    FirebaseUser firebaseUser;
    private final Context mContext;
    private final List<Post> mPost;

    public SavedPostAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPost = posts;
    }

    @NonNull
    @Override
    public SavedPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_saves_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedPostAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);
        String type = mPost.get(position).getType();

        String description = MasterCipher.decrypt(post.getDescription());
        holder.post_description.setText(description);

        holder.post_image.setImageURI(MasterCipher.decrypt(post.getPostimage()));

        getUserInfo(post.getPublisher(), holder.post_publisher_name);

        if (type.equals("image")) {
            holder.post_type.setText("Image");
        } else if (type.equals("profile_image")) {
            holder.post_type.setText("Profile Image");
        } else {
            holder.post_type.setText("Text");
        }

        holder.saved_timestamp.setReferenceTime(Long.parseLong(post.getTimestamp()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PostDetailActivity.class);
            intent.putExtra("postid", post.getPostid());
            intent.putExtra("publisherid", post.getPublisher());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    private void getUserInfo(String profileid, TextView full_name) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                full_name.setText(String.format("%s %s", user.getName(), user.getSurname()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView post_image;
        public TextView post_description, post_type, post_publisher_name;
        public RelativeTimeTextView saved_timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            post_description = itemView.findViewById(R.id.post_description);
            post_type = itemView.findViewById(R.id.post_type);
            post_publisher_name = itemView.findViewById(R.id.post_publisher_name);
            saved_timestamp = itemView.findViewById(R.id.saved_timestamp);
        }
    }

}
