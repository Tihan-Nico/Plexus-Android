package com.plexus.search.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.posts.activity.HashTagViewActivity;

import java.text.MessageFormat;
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

public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.ViewHolder> {

    List<String> mTags;
    List<String> post_counts;
    private final Context mContext;

    public HashtagAdapter(Context context, List<String> mTags, List<String> post_counts) {
        mContext = context;
        this.mTags = mTags;
        this.post_counts = post_counts;

    }

    @NonNull
    @Override
    public HashtagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_trending, parent, false);
        return new HashtagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.hashtag.setText(MessageFormat.format("#{0}", mTags.get(position)));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, HashTagViewActivity.class);
            intent.putExtra("hashtag", mTags.get(position));
            mContext.startActivity(intent);
        });

        getPostsCount(position, holder.posts_count);

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public void hashtagFilter(List<String> filterHashTags, List<String> filterHashTagCounts) {
        this.mTags = filterHashTags;
        this.post_counts = filterHashTagCounts;

        notifyDataSetChanged();
    }

    private void getPostsCount(int position, TextView textView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Hashtags").child(mTags.get(position)).child("Posts");
        reference.addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        textView.setText(dataSnapshot.getChildrenCount() + " posts");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView hashtag, posts_count, trending_count;
        RelativeLayout hashtag_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            hashtag = itemView.findViewById(R.id.hashtag);
            posts_count = itemView.findViewById(R.id.posts_count);

        }
    }

}
