package com.plexus.settings.activity.privacy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RestrictAccountDetailsActivity extends AppCompatActivity {

    private SimpleDraweeView profile_image;
    private ImageView back;
    private FirebaseUser firebaseUser;
    private Button continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_restrict_accounts_details);

        profile_image = findViewById(R.id.profile_image);
        back = findViewById(R.id.back);
        continue_btn = findViewById(R.id.continue_btn);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean("started_previously_restricted", false);
        if(previouslyStarted) {
            startActivity(new Intent(getApplicationContext(), RestrictAccountsActivity.class));
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                profile_image.setImageURI(user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(v -> finish());

        continue_btn.setOnClickListener(view -> {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("started_previously_restricted", true);
            edit.apply();
            startActivity(new Intent(getApplicationContext(), RestrictAccountsActivity.class));
        });

    }

}
