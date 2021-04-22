package com.plexus.utils;

import android.content.ContentResolver;
import android.graphics.Color;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.plexus.R;
import com.plexus.model.account.User;
import com.plexus.model.notifications.PlexusNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AccountUtil {

    public static FirebaseUser firebaseUser;

    public static void updateAccount() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy").child("Posts");

                HashMap<String, Object> deviceInfoMap = new HashMap<>();
                deviceInfoMap.put("name", MasterCipher.decrypt(user.getName()));
                deviceInfoMap.put("surname", MasterCipher.decrypt(user.getSurname()));
                deviceInfoMap.put("bio", MasterCipher.decrypt(user.getBio()));
                deviceInfoMap.put("country", MasterCipher.decrypt(MasterCipher.decrypt(user.getCountry())));
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

    public static void getUnreadNotifications(BottomNavigationView bottomNavigationView) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Notification");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlexusNotification plexusNotification = snapshot.getValue(PlexusNotification.class);
                    if (!plexusNotification.isNotificationRead()) {
                        unread++;
                    }
                }

                BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.notifications_nav);
                if ((unread == 0)) {
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

    public static void addLoginDetails(double latitude, double longitude, ContentResolver contentResolver) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Security").child("Login Activity");
        String ID = databaseReference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ID);
        hashMap.put("device_name", Settings.Global.getString(contentResolver, "device_name"));
        hashMap.put("device_login_time", new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(new Date()));
        hashMap.put("device_latitude", latitude);
        hashMap.put("device_longitude", longitude);
        hashMap.put("device_token", FirebaseInstanceId.getInstance().getToken());

        databaseReference.child(ID).setValue(hashMap);
    }

    public static void profileActivity(String profileid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Activity Log");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("title", "You started to follow someone.");
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("isFollow", true);
        hashMap.put("userid", profileid);

        reference.child(id).setValue(hashMap);
    }

}
