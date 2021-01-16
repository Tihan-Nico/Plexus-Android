package com.plexus.messaging.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.plexus.messaging.adapter.MessageAdapter;
import com.plexus.model.account.Privacy;
import com.plexus.model.account.User;
import com.plexus.model.messaging.Message;
import com.plexus.utils.MasterCipher;
import com.plexus.utils.SoundHelper;
import com.plexus.utils.TimeUtils;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.plexus.Plexus.TAG;

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

public class MessageUserActivity extends AppCompatActivity {

    public static boolean running = false;
    public static String userid;
    ImageView send_message, back, emoji;
    LinearLayout linearLayout;
    MessageAdapter messageAdapter;
    List<Message> mMessage;
    RecyclerView recyclerView;
    ValueEventListener seenListener;
    EmojiPopup emojiPopup;
    View main_activity_root_view;
    Intent intent;
    private TextView fullname, online;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private EmojiEditText message;
    private SimpleDraweeView profile_image;
    private SoundHelper mSound;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        running = true;
        mSound = new SoundHelper(this);

        linearLayout = findViewById(R.id.linear);
        online = findViewById(R.id.online);
        back = findViewById(R.id.back);
        profile_image = findViewById(R.id.profile_image);
        recyclerView = findViewById(R.id.recycler_view);
        main_activity_root_view = findViewById(R.id.main_activity_root_view);
        emoji = findViewById(R.id.emoji);

        messageAdapter = new MessageAdapter(MessageUserActivity.this, mMessage);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        fullname = findViewById(R.id.fullname);
        send_message = findViewById(R.id.send_message);
        message = findViewById(R.id.message);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        send_message.setOnClickListener(v -> {
            String msg = Objects.requireNonNull(message.getText()).toString();
            if (!msg.equals("")) {
                sendMessage(fuser.getUid(), userid, msg);
                sendFunctionNotification("message", msg);
            } else {
                Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            message.setText("");
            message.clearFocus();
            hideKeybaord(main_activity_root_view);
        });

        back.setOnClickListener(v -> finish());

        message.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                send_message.setVisibility(View.VISIBLE);
            } else {
                send_message.setVisibility(View.GONE);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    fullname.setText(MasterCipher.decrypt(String.format("%s %s", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname()))));
                }
                if (user != null) {
                    profile_image.setImageURI(MasterCipher.decrypt(user.getImageurl()));

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.exists()) {
                            if (user.getOnline_presence().equals("Online")) {
                                online.setText("Online");
                            } else {
                                online.setText(TimeUtils.getFormattedTimestamp(MessageUserActivity.this, user.getOnline()));
                            }
                        }
                    }
                }

                readMessages(fuser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        emoji.setOnClickListener(view -> emojiPopup.toggle());

        setUpEmojiPopup();
    }

    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(main_activity_root_view)
                .setOnEmojiBackspaceClickListener(ignore -> Log.d(TAG, "Clicked on Backspace"))
                .setOnEmojiClickListener((ignore, ignore2) -> Log.d(TAG, "Clicked on emoji"))
                .setOnEmojiPopupShownListener(() -> emoji.setImageResource(R.drawable.ic_keyboard))
                .setOnSoftKeyboardOpenListener(ignore -> Log.d(TAG, "Opened soft keyboard"))
                .setOnEmojiPopupDismissListener(() -> emoji.setImageResource(R.drawable.ic_outline_emoji_emotions_24))
                .setOnSoftKeyboardCloseListener(() -> Log.d(TAG, "Closed soft keyboard"))
                .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                .setPageTransformer((page, position) -> {
                })
                .build(message);
    }

    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    /*
      Sending messages and attachments should only be
      added below this comment section. This is to
      keep the code clean and well placed for future
      updates.
     */
    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com").getReference("Chats").child(userid).child("Messages");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null && message.getReceiver().equals(fuser.getUid()) && message.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com").getReference("Chats").child(fuser.getUid()).child("Messages");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null && message.getReceiver().equals(fuser.getUid()) && message.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendFunctionNotification(String type, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
                String notificationId = notificationRef.push().getKey();

                HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("from", fuser.getUid());
                notificationData.put("type", type);
                notificationData.put("name", MasterCipher.decrypt(user.getName()));
                notificationData.put("profile_image", MasterCipher.decrypt(user.getImageurl()));
                notificationData.put("message", message);
                notificationRef.child(notificationId).setValue(notificationData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com").getReference();
        String id = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("type", "text");
        hashMap.put("isseen", false);
        reference.child("Chats").child(receiver).child("Messages").child(id).setValue(hashMap);
        reference.child("Chats").child(sender).child("Messages").child(id).setValue(hashMap);

        addToChatlist();
        mSound.playSound();

    }

    private void readMessages(final String myid, final String userid) {
        mMessage = new ArrayList<>();

        reference = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com").getReference("Chats").child(myid).child("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessage.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    assert message != null;
                    if (message.getReceiver().equals(myid) && message.getSender().equals(userid) ||
                            message.getReceiver().equals(userid) && message.getSender().equals(myid)) {
                        mMessage.add(message);
                    }

                    messageAdapter = new MessageAdapter(MessageUserActivity.this, mMessage);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToChatlist() {
        final DatabaseReference chatRef = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com").getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com").getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());
    }


    /*
      This section of code is only for the privacy and background
      tasks fetched from the Plexus backend and should be kept on
      private at all times
     */
    private void getPrivacySettings() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Privacy").child("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Privacy privacy = dataSnapshot.getValue(Privacy.class);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
                        Handler handler = new Handler();
                        if (privacy.getLast_seen_enabled().equals("Disabled")) {
                            handler.post(() -> online.setVisibility(View.GONE));
                        } else {
                            handler.post(() -> online.setVisibility(View.VISIBLE));
                        }

                        if (privacy.getScreenshot_enabled().equals("Disabled")) {
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        seenMessage(userid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        seenMessage(userid);
    }
}
