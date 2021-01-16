package com.plexus.account.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.components.components.ImageView.Constants;
import com.plexus.components.components.socials.PlexusSocialTextView;
import com.plexus.model.posts.Post;
import com.plexus.model.account.User;
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

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ImageViewHolder> {

    Context mContext;
    List<Post> mPosts;
    Bitmap bitmap;

    public ProfilePostAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ProfilePostAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

    @SuppressLint("GetInstance")
    @Override
    public void onBindViewHolder(@NonNull final ProfilePostAdapter.ImageViewHolder holder, final int position) {
        final Post post = mPosts.get(position);
        String type = mPosts.get(position).getType();
        holder.description.setText(MasterCipher.decrypt(post.getDescription()));

        switch (type) {
            case "image":
            case "profile_image":
                holder.description.setVisibility(View.GONE);
                Glide.with(mContext).asBitmap().load(MasterCipher.decrypt(post.getPostimage())).into(holder.post_image);
                holder.profile_image.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                break;
            case "video":
                holder.description.setVisibility(View.GONE);
                Glide.with(mContext).asBitmap().load(MasterCipher.decrypt(post.getVideoURL())).into(holder.post_image);
                holder.profile_image.setVisibility(View.GONE);
                holder.play.setVisibility(View.VISIBLE);
                break;
            case "audio":
                holder.description.setVisibility(View.GONE);
                Glide.with(mContext).asBitmap().load(MasterCipher.decrypt(post.getAudioURL())).into(holder.post_image);
                holder.profile_image.setVisibility(View.VISIBLE);
                holder.play.setVisibility(View.VISIBLE);
                break;
            default:
                holder.description.setVisibility(View.VISIBLE);
                holder.post_image.setImageResource(R.drawable.gradient_yellow);
                holder.profile_image.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                break;
        }

        holder.post_image.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PostDetailActivity.class);
            intent.putExtra("postid", post.getPostid());
            intent.putExtra("publisherid", post.getPublisher());
            mContext.startActivity(intent);
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher());
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (bitmap != null) {
                            holder.post_image.setBackground(new BitmapDrawable(mContext.getResources(), Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true)))); // ));
                            holder.profile_image.setImageBitmap(bitmap);
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
                                                public boolean onResourceReady(
                                                        Bitmap resource,
                                                        Object model,
                                                        Target<Bitmap> target,
                                                        DataSource dataSource,
                                                        boolean isFirstResource) {
                                                    if (Build.VERSION.SDK_INT >= 16) {
                                                        holder.post_image.setBackground(
                                                                new BitmapDrawable(
                                                                        mContext.getResources(),
                                                                        Constants.fastblur(
                                                                                Bitmap.createScaledBitmap(resource, 50, 50, true)))); // ));
                                                    } else {
                                                        onPalette(Palette.from(resource).generate(), holder.profile_image);
                                                    }
                                                    holder.profile_image.setImageBitmap(resource);
                                                    return false;
                                                }
                                            })
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(holder.profile_image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image, play, profile_image;
        public PlexusSocialTextView description;

        public ImageViewHolder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.image_post);
            description = itemView.findViewById(R.id.description);
            play = itemView.findViewById(R.id.play);
            profile_image = itemView.findViewById(R.id.profile_image);

        }
    }

    public void onPalette(Palette palette, ImageView photoView) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) photoView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }

}