package com.plexus.messaging.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.plexus.messaging.activity.MessageUserActivity;
import com.plexus.model.account.User;
import com.plexus.model.messaging.Chatlist;
import com.plexus.model.messaging.Message;
import com.plexus.utils.MasterCipher;
import com.vanniktech.emoji.EmojiTextView;

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

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.ViewHolder> {

    Context mContext;
    List<User> mUsers;
    boolean ischat;
    FirebaseUser firebaseUser;
    String theLastMessage;

    public ChatlistAdapter(Context mContext, List<User> mUsers, boolean ischat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);

        new Thread(() -> holder.profile_image.setImageURI(MasterCipher.decrypt(user.getImageurl()))).start();
        holder.username.setText(String.format("%s %s", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (ischat) {
            lastMessage(user.getId(), holder.last_msg);
            getTimestamps(user.getId(), holder.timestamp);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("plexus", MODE_PRIVATE).edit();
            editor.putString("userid", user.getId());
            editor.apply();

            Intent intent = new Intent(mContext, MessageUserActivity.class);
            intent.putExtra("userid", user.getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void getTimestamps(final String userid, final TextView timestamp) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com/").getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chatlist chatlist = dataSnapshot.getValue(Chatlist.class);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {

                        timestamp.setText(String.format("%s %s", chatlist.getDate(), chatlist.getTime()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com/").getReference("Chats").child(firebaseUser.getUid()).child("Messages");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (firebaseUser != null && message != null) {
                        if (message.getReceiver().equals(firebaseUser.getUid()) && message.getSender().equals(userid) ||
                                message.getReceiver().equals(userid) && message.getSender().equals(firebaseUser.getUid())) {
                            switch (message.getType()) {
                                case "image":
                                    theLastMessage = "Sent a photo";
                                    break;
                                case "file":
                                    theLastMessage = "Sent a file";
                                    break;
                                case "voice_note":
                                    theLastMessage = "Sent a voice note";
                                    break;
                                case "audio":
                                    theLastMessage = "Sent a audio file";
                                    break;
                                case "video":
                                    theLastMessage = "Sent a video";
                                    break;
                                default:
                                    theLastMessage = message.getMessage();
                                    break;
                            }
                        }
                    }
                }

                if ("default".equals(theLastMessage)) {
                    last_msg.setText("No Message");
                } else {
                    last_msg.setText(theLastMessage);
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, timestamp;
        public SimpleDraweeView profile_image;
        private final EmojiTextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.fullname);
            timestamp = itemView.findViewById(R.id.timestamp);
            profile_image = itemView.findViewById(R.id.profile_image);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }
}