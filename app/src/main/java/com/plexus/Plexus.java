package com.plexus;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.account.activity.UserBannedActivity;
import com.plexus.components.locale_changer.LocaleChanger;
import com.plexus.main.activity.MainActivity;
import com.plexus.model.account.Banned;
import com.plexus.model.account.User;
import com.plexus.notifications.NotificationChannels;
import com.plexus.utils.AppStartup;
import com.plexus.utils.MasterCipher;
import com.plexus.utils.UpdateCheck;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.logging.Log;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.twitter.TwitterEmojiProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
    public static final List<Locale> SUPPORTED_LOCALES =
            Arrays.asList(
                    new Locale("en", "US"),
                    new Locale("af", "ZA"),
                    new Locale("fr", "FR")
            );
    private static Plexus mInstance;
    private RequestQueue mRequestQueue;
    private FirebaseUser firebaseUser;

    public static synchronized Plexus getInstance() {
        return mInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        AppStartup.getInstance().onApplicationCreate();
        super.onCreate();
        EmojiManager.install(new TwitterEmojiProvider());
        Fresco.initialize(this);
        LocaleChanger.initialize(getApplicationContext(), SUPPORTED_LOCALES);
        mInstance = this;

        AppStartup.getInstance()
                .addBlocking("first-launch", this::initializeFirstEverAppLaunch)
                .addPostRender(() -> NotificationChannels.create(this)).execute();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            checkIfBanned();
            status();
        }
    }

    private void status() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online_presence", "Online");

        databaseReference.updateChildren(hashMap);
    }

    private void checkIfBanned() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banned").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Banned banned = dataSnapshot.getValue(Banned.class);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
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

    private void initializeFirstEverAppLaunch() {
        try {
            if (UpdateCheck.appWasUpdated(this)){
                showUpdate();
            } else {
                Log.i(TAG, "First ever app launch!");
                AppInitialization.onFirstEverAppLaunch(this);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showUpdate() {
        BottomSheetDialog update_sheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        update_sheet.setContentView(R.layout.sheet_update_settings);
        update_sheet.setCancelable(false);

        Button continue_update = update_sheet.findViewById(R.id.continue_btn);

        continue_update.setOnClickListener(v -> {
            updateAccount(firebaseUser.getUid());
            update_sheet.dismiss();
        });

        update_sheet.show();
    }

    private void updateAccount(String userID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                HashMap<String, Object> deviceInfoMap = new HashMap<>();
                deviceInfoMap.put("name", MasterCipher.decrypt(user.getName()));
                deviceInfoMap.put("surname", MasterCipher.decrypt(user.getSurname()));
                deviceInfoMap.put("bio", MasterCipher.decrypt(user.getBio()));
                deviceInfoMap.put("country", MasterCipher.decrypt(user.getCountry()));
                deviceInfoMap.put("username", MasterCipher.decrypt(user.getUsername()));
                deviceInfoMap.put("imageurl", MasterCipher.decrypt(user.getImageurl()));
                deviceInfoMap.put("profile_cover", MasterCipher.decrypt(user.getProfile_cover()));
                deviceInfoMap.put("website", MasterCipher.decrypt(user.getWebsite()));

                reference.updateChildren(deviceInfoMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleChanger.onConfigurationChanged();
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
