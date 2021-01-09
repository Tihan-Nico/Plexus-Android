package com.plexus.account.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.plexus.settings.activity.manage_account.ChangeEmailActivity;
import com.plexus.settings.activity.manage_account.login_management.LoginManagementActivity;
import com.plexus.settings.activity.manage_account.two_factor.TwoFactorAuthentication;
import com.plexus.settings.activity.privacy.lockscreen.ChangePasswordActivity;
import com.plexus.utils.MasterCipher;

import java.text.MessageFormat;

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

public class ManageAccountActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private SimpleDraweeView profile_image;
    public LinearLayout email_change, manage_info, change_password, login_management, two_factor;
    private TextView email, fullname;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_manage_account);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        profile_image = findViewById(R.id.profile_image);
        email_change = findViewById(R.id.email_change);
        manage_info = findViewById(R.id.manage_info);
        change_password = findViewById(R.id.change_password);
        login_management = findViewById(R.id.login_management);
        two_factor = findViewById(R.id.two_factor);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                email.setText(firebaseUser.getEmail());
                profile_image.setImageURI(MasterCipher.decrypt(user.getImageurl()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        manage_info.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ManagePlexusInfoActivity.class)));

        login_management.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginManagementActivity.class)));

        change_password.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class)));

        email_change.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ChangeEmailActivity.class)));

        two_factor.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), TwoFactorAuthentication.class)));

    }

}
