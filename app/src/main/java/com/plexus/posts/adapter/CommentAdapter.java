package com.plexus.posts.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.plexus.main.activity.MainActivity;
import com.plexus.model.account.User;
import com.plexus.model.posts.Comment;
import com.plexus.model.posts.Post;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<Comment> mComment;
    private final String postid;
    private final Post post;

    private Dialog deleteDialog;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context context, List<Comment> comments, String postid, Post post) {
        mContext = context;
        mComment = comments;
        this.postid = postid;
        this.post = post;
    }

    @NonNull
    @Override
    public CommentAdapter.ImageViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(position);
        String type = mComment.get(position).getType();
        holder.comment.setText(MasterCipher.decrypt(comment.getComment()));

        deleteDialog = new Dialog(mContext);
        deleteDialog.setContentView(R.layout.dialog_post_delete);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        getUserInfo(holder.image_profile, holder.username, comment.getPublisher(), holder.presence);

        holder.timestamp.setReferenceTime(Long.parseLong(comment.getTimestamp()));

        if (type.equals("text")) {
            holder.comment.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
        } else if (type.equals("image")) {
            holder.comment.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageURI(MasterCipher.decrypt(comment.getComment()));
        }

        holder.username.setOnClickListener(
                view -> {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", comment.getPublisher());
                    mContext.startActivity(intent);
                });

        holder.image_profile.setOnClickListener(
                view -> {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", comment.getPublisher());
                    mContext.startActivity(intent);
                });

        holder.itemView.setOnLongClickListener(
                view -> {
                    if (comment.getPublisher().equals(firebaseUser.getUid())) {

                        TextView title = deleteDialog.findViewById(R.id.title);
                        title.setText("Are you sure you want to delete this comment?");

                        TextView warning = deleteDialog.findViewById(R.id.warning);
                        warning.setText("Once deleted this action can't be undone. So be sure that you want to delete this comment.");

                        Button delete = deleteDialog.findViewById(R.id.delete);
                        delete.setOnClickListener(
                                view12 -> {
                                    FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(comment.getCommentid()).removeValue();
                                    deleteDialog.dismiss();
                                });

                        Button cancel = deleteDialog.findViewById(R.id.cancel);
                        cancel.setOnClickListener(view1 -> deleteDialog.dismiss());
                        deleteDialog.show();
                    } else if (post.getPublisher().equals(firebaseUser.getUid())) {
                        TextView title = deleteDialog.findViewById(R.id.title);
                        title.setText("Are you sure you want to delete this comment?");

                        TextView warning = deleteDialog.findViewById(R.id.warning);
                        warning.setText("Once deleted this action can't be undone. So be sure that you want to delete this comment.");

                        Button delete = deleteDialog.findViewById(R.id.delete);
                        delete.setOnClickListener(
                                view12 -> {
                                    FirebaseDatabase.getInstance().getReference("Comments").child(post.getPostid()).child(comment.getCommentid()).removeValue();
                                    deleteDialog.dismiss();
                                });

                        Button cancel = deleteDialog.findViewById(R.id.cancel);
                        cancel.setOnClickListener(view1 -> deleteDialog.dismiss());
                        deleteDialog.show();
                    }
                    return true;
                });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    private void getUserInfo(final SimpleDraweeView imageView, final TextView username, String publisherid, final ImageView presence) {
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        imageView.setImageURI(MasterCipher.decrypt(user.getImageurl()));
                        username.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                });
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView presence;
        public TextView username, comment;
        private final RelativeTimeTextView timestamp;
        private final SimpleDraweeView image;
        private final SimpleDraweeView image_profile;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.fullname);
            comment = itemView.findViewById(R.id.comment);
            timestamp = itemView.findViewById(R.id.timestamp);
            image = itemView.findViewById(R.id.image);
            presence = itemView.findViewById(R.id.presence_online);
        }
    }
}
