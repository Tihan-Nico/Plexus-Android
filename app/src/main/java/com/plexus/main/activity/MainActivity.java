package com.plexus.main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.plexus.R;
import com.plexus.components.background.DialogInformation;
import com.plexus.services.LocationService;
import com.plexus.startup.PermissionActivity;
import com.plexus.main.fragments.HomeFragment;
import com.plexus.main.fragments.NotificationsFragment;
import com.plexus.main.fragments.ProfileFragment;
import com.plexus.main.fragments.SearchFragment;
import com.plexus.messaging.fragment.ChatlistFragment;
import com.plexus.model.notifications.PlexusNotification;
import com.plexus.model.Token;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    String profileid;
    private FirebaseUser firebaseUser;
    SharedPreferences prefs;
    Fragment selectedfragment = null;

    BottomSheetDialog update_sheet;

    //Permission Requests
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    public static boolean appWasUpdated(Context context) throws PackageManager.NameNotFoundException {
        //this code gets current version-code (after upgrade it will show new versionCode)
        PackageManager manager = context.getPackageManager();
        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
        int versionCode = info.versionCode;
        SharedPreferences prefs = context.getSharedPreferences("plexus", Context.MODE_PRIVATE);
        if (prefs.getInt("version", -1) > 0) {
            if (prefs.getInt("version", -1) != versionCode) {
                //save current versionCode: 1st-run after upgrade
                prefs.edit().putInt("version", versionCode).apply();

                return true;
            } //no need for else, because app version did not change...
        } else {
            //save current versionCode for 1st-run ever
            prefs.edit().putInt("version", versionCode).apply();
        }
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        prefs = getSharedPreferences("plexus", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("plexus", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
        switch (item.getItemId()) {
              case R.id.feed_nav:
                  selectedfragment = new HomeFragment();
                break;

            case R.id.message_nav:
                  selectedfragment = new ChatlistFragment();
                break;

            case R.id.notifications_nav:
                  selectedfragment = new NotificationsFragment();
                break;

            case R.id.profile_nav:
                  selectedfragment = new ProfileFragment();
                break;

            case R.id.watch_nav:
                  selectedfragment = new SearchFragment();
                break;
        }
        if (selectedfragment != null) {
              getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                      selectedfragment).commit();
        }

            return true;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean loggedInRecently = extras.getBoolean("loggedIn", false);
            if (loggedInRecently) {
                LocationService.LocationResult locationResult = new LocationService.LocationResult() {
                    @Override
                    public void gotLocation(Location location) {
                        addLoginDetails(location.getLatitude(), location.getLongitude());
                    }
                };

                LocationService locationService = new LocationService();
                locationService.getLocation(this, locationResult);
            }

        }


        updateToken(FirebaseInstanceId.getInstance().getToken());
        getUnreadNotifications();

        try {
            if (appWasUpdated(getApplicationContext())) {
                showUpdate();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!hasPermissions(this, PERMISSIONS)) {
            startActivity(new Intent(getApplicationContext(), PermissionActivity.class));
        }

        checkIfVerifiedBefore();
    }

    private void updateToken(String s) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(s);
        databaseReference.child(firebaseUser.getUid()).setValue(token);
    }

    private void showUpdate() {
        update_sheet = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialogTheme);
        update_sheet.setContentView(R.layout.sheet_update_settings);
        update_sheet.setCancelable(false);

        Button continue_update = update_sheet.findViewById(R.id.continue_btn);

        continue_update.setOnClickListener(v -> {
            updateAccount(firebaseUser.getUid());
            update_sheet.dismiss();
        });

        update_sheet.show();
    }

    private void updateAccount(String userID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Privacy").child("Posts");

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

    private void checkIfVerifiedBefore(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (!user.isVerifiedBefore()){
                    if (user.isVerified()){
                        DialogInformation.showVerified(MainActivity.this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUnreadNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Notification");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PlexusNotification plexusNotification = snapshot.getValue(PlexusNotification.class);
                    if (!plexusNotification.isNotificationRead()){
                        unread++;
                    }
                }

                BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.notifications_nav);
                if ((unread == 0)){
                    badge.setVisible(false);
                } else {
                    badge.setBadgeTextColor(Color.WHITE);
                    badge.setBackgroundColor(Color.parseColor("#f78361"));
                    badge.setNumber(unread);
                    badge.setVisible(true);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addLoginDetails(double latitude, double longitude){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Security").child("Login Activity");
        String ID = databaseReference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ID);
        hashMap.put("device_name", Settings.Global.getString(getContentResolver(), "device_name"));
        hashMap.put("device_login_time", new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(new Date()));
        hashMap.put("device_latitude", latitude);
        hashMap.put("device_longitude", longitude);
        hashMap.put("device_token", FirebaseInstanceId.getInstance().getToken());

        databaseReference.child(ID).setValue(hashMap);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!selectedfragment.equals(new HomeFragment())) {
            bottomNavigationView.setSelectedItemId(R.id.feed_nav);
            selectedfragment = new HomeFragment();
        } else {
            super.onBackPressed();
    }
  }
}
