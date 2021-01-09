package com.plexus.account.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;

import java.text.MessageFormat;
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

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;

    public BlockedUsersAdapter(Context mContext, List<User> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.image_profile.setImageURI(MasterCipher.decrypt(user.getImageurl()));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Blocked Users").child(user.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.platform.setText(user.getBlocked_platform());
                holder.timestamp.setText(user.getBlocked_timestamp());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.unblock.setOnClickListener(v -> unblockUser(user.getId()));

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView fullname, unblock, platform, timestamp;
        public SimpleDraweeView image_profile;

        public ViewHolder(View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            unblock = itemView.findViewById(R.id.unblock);
            timestamp = itemView.findViewById(R.id.timestamp);
            platform = itemView.findViewById(R.id.platform);
        }
    }

    private void unblockUser(String profileid) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", profileid);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Blocked Users").orderByChild("uid").equalTo(profileid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            dataSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(mContext, "User is unblocked successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {

                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
