package com.plexus;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.plexus.model.account.Banned;
import com.plexus.account.activity.UserBannedActivity;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.twitter.TwitterEmojiProvider;

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

public class Plexus extends Application {

  public static final String TAG = Plexus.class.getSimpleName();
  private static Plexus mInstance;
  private RequestQueue mRequestQueue;
  private FirebaseUser firebaseUser;
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  public static synchronized Plexus getInstance() {
        return mInstance;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onCreate() {
      super.onCreate();
      FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
              .setPersistenceEnabled(true)
              .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
              .build();
      db.setFirestoreSettings(settings);
      EmojiManager.install(new TwitterEmojiProvider());
      Fresco.initialize(this);
      mInstance = this;

      firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
      if (firebaseUser != null) {
          checkIfBanned();
          status();
      }
  }

  private void status(){
      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
      HashMap<String, Object> hashMap = new HashMap<>();
      hashMap.put("online_presence", "Online");

      databaseReference.updateChildren(hashMap);
  }

  private void checkIfBanned(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banned").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Banned banned = dataSnapshot.getValue(Banned.class);
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if(dataSnapshot1.exists()){
                        if (banned != null && banned.isBanned()) {
                            startActivity(new Intent(getApplicationContext(), UserBannedActivity.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
  }

  public RequestQueue getRequestQueue() {
      if (mRequestQueue == null) {
          mRequestQueue = Volley.newRequestQueue(getApplicationContext());
      }
      return mRequestQueue;
  }

  public <T> void addToRequestQueue(Request<T> req) {
      req.setTag(TAG);
      getRequestQueue().add(req);
  }
}
