package com.plexus.settings.activity.privacy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.account.Privacy;
import com.plexus.model.account.User;
import com.plexus.settings.activity.privacy.lockscreen.AppLockActivity;

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

public class PrivacyActivity extends AppCompatActivity {

    public LinearLayout blocked_accounts, lock_screen, restricted_accounts, last_seen, screenshot_view, post_privacy;
    ImageView back;
    private FirebaseUser firebaseUser;
    private SwitchMaterial set_account_private;
    private TextView last_seen_enabled, screenshot_enabled;
    private Dialog options_last_seen, options_screenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_privacy);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        lock_screen = findViewById(R.id.lock_screen);
        blocked_accounts = findViewById(R.id.blocked_accounts);
        restricted_accounts = findViewById(R.id.restricted_accounts);
        set_account_private = findViewById(R.id.set_account_private);
        last_seen = findViewById(R.id.last_seen);
        screenshot_view = findViewById(R.id.screenshot);
        last_seen_enabled = findViewById(R.id.last_seen_enabled);
        screenshot_enabled = findViewById(R.id.screenshot_enabled);
        post_privacy = findViewById(R.id.post_privacy);

        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        set_account_private.setChecked(false);

        set_account_private.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAccountPrivate();
            } else {
                setAccountPublic();
            }
        });

        post_privacy.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PostPrivacyActivity.class)));

        options_last_seen = new Dialog(PrivacyActivity.this);
        options_last_seen.setContentView(R.layout.dialog_privacy_chat);
        options_last_seen.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        options_last_seen.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        options_screenshot = new Dialog(PrivacyActivity.this);
        options_screenshot.setContentView(R.layout.dialog_privacy_chat);
        options_screenshot.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        options_screenshot.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final ListView screenshot = options_screenshot.findViewById(R.id.list);
        screenshot.setOnItemClickListener(
                (adapterView, view, position, id) -> {
                    String selectedFromList = (String) screenshot.getItemAtPosition(position);
                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy").child("Chat").child("screenshot_enabled").setValue(selectedFromList);
                    screenshot_enabled.setText(selectedFromList);
                    options_screenshot.dismiss();
                });

        screenshot_view.setOnClickListener(v -> options_screenshot.show());

        final ListView last_seen_list = options_last_seen.findViewById(R.id.list);
        last_seen_list.setOnItemClickListener(
                (adapterView, view, position, id) -> {
                    String selectedFromList = (String) last_seen_list.getItemAtPosition(position);
                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy").child("Chat").child("last_seen_enabled").setValue(selectedFromList);
                    last_seen_enabled.setText(selectedFromList);
                    options_last_seen.dismiss();
                });

        last_seen.setOnClickListener(v -> {
            options_last_seen.show();
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
                        runOnUiThread(() -> {
                            set_account_private.setChecked(user.isPrivate_account());

                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getChatPrivacy();

        lock_screen.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AppLockActivity.class)));

        blocked_accounts.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), BlockedAccountsActivity.class)));

        restricted_accounts.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RestrictAccountDetailsActivity.class)));

    }

    private void getChatPrivacy() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy").child("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Privacy privacy = dataSnapshot.getValue(Privacy.class);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
                        runOnUiThread(() -> {
                            last_seen_enabled.setText(privacy.getLast_seen_enabled());
                            screenshot_enabled.setText(privacy.getScreenshot_enabled());

                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAccountPrivate() {
        HashMap<String, Boolean> hashMap = new HashMap<>();
        hashMap.put("private_account", true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Privacy").setValue(hashMap);
    }

    private void setAccountPublic() {
        HashMap<String, Boolean> hashMap = new HashMap<>();
        hashMap.put("private_account", false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Privacy").setValue(hashMap);
    }

}
