package com.plexus.account.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.account.activity.ProfileActivity;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;

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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    boolean isFragment;
    FirebaseUser firebaseUser;
    private final Context mContext;
    private final List<User> mUsers;

    public UserAdapter(Context context, List<User> users, boolean isFragment) {
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull final UserAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
        holder.about.setText(MasterCipher.decrypt(user.getBio()));
        holder.image_profile.setImageURI(MasterCipher.decrypt(user.getImageurl()));

        if (user.isVerified()) {
            holder.verified.setVisibility(View.VISIBLE);
        } else {
            holder.verified.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(
                view -> {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("userid", user.getId());
                    mContext.startActivity(intent);
                });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView about;
        public TextView fullname;
        public SimpleDraweeView image_profile;
        private final ImageView verified;

        public ImageViewHolder(View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.fullname);
            about = itemView.findViewById(R.id.about);
            image_profile = itemView.findViewById(R.id.image_profile);
            verified = itemView.findViewById(R.id.verified);
        }
    }
}
