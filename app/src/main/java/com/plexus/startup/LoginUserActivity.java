package com.plexus.startup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.tntkhang.gmailsenderlibrary.GMailSender;
import com.github.tntkhang.gmailsenderlibrary.GmailListener;
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
import com.plexus.utils.MasterCipher;

import java.text.MessageFormat;
import java.util.HashMap;

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

public class LoginUserActivity extends AppCompatActivity {

    private ImageView profile_image;
    private TextView fullname;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_loged_in_continue);

        profile_image = findViewById(R.id.profile_image);
        fullname = findViewById(R.id.fullname);
        next = findViewById(R.id.next);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Glide.with(getApplication()).load(MasterCipher.decrypt(user.getImageurl())).into(profile_image);
                        fullname.setText(MessageFormat.format("You are logged in as {0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        next.setOnClickListener(
                v -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    updateUser();
                    chatPrivacy(firebaseUser.getUid());
                    postPrivacy(firebaseUser.getUid());
                });

    }

    @SuppressLint("HardwareIds")
    private void chatPrivacy(String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Privacy").child("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.exists()){
                        //Do nothing
                    } else {
                        addChat(userID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addChat(String userID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Privacy").child("Chat");
        HashMap<String, Object> deviceInfoMap = new HashMap<>();
        deviceInfoMap.put("screenshots_enabled", "Enabled");
        deviceInfoMap.put("last_seen_enabled", "Enabled");

        reference.updateChildren(deviceInfoMap);
    }

    @SuppressLint("HardwareIds")
    private void postPrivacy(String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Privacy").child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.exists()){
                        //Do nothing
                    } else {
                        addPost(userID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addPost(String userID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Privacy").child("Posts");

        HashMap<String, Object> deviceInfoMap = new HashMap<>();
        deviceInfoMap.put("allow_screenshot", true);
        deviceInfoMap.put("allow_download", true);

        reference.updateChildren(deviceInfoMap);
    }

    private void sendLoginEmail(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                GMailSender.withAccount("tihannicopaxton2@gmail.com", "Chocolates123")
                        .withTitle("Plexus Login")
                        .withBody("Login Notification"
                                + "\n"
                                + "\n Users User ID: " + firebaseUser.getUid()
                                + "\n Users Email: " + firebaseUser.getEmail()
                                + "\n Users Full Name: " + user.getName() + " " + user.getSurname()
                        )
                        .withSender(getApplicationContext().getString(R.string.app_name))
                        .toEmailAddress(firebaseUser.getEmail())
                        .withListenner(new GmailListener() {
                            @Override
                            public void sendSuccess() {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void sendFail(String err) {
                                Toast.makeText(getApplicationContext(), "Fail: " + err, Toast.LENGTH_SHORT).show();
                            }
                        }).send();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> user = new HashMap<>();
        user.put("typing", "nobody");
        reference.updateChildren(user);

    }
}
