package com.plexus.settings.activity.privacy.lockscreen;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.settings.authentication.ReAuthenticationDialog;
import com.plexus.startup.LoginActivity;

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

public class ChangePasswordActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private ImageView back;
    private TextView email;
    private EditText password;
    private Button change_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_change_password);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.back);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        change_password = findViewById(R.id.change_password);

        back.setOnClickListener(v -> finish());
        email.setText(firebaseUser.getEmail());

        change_password.setOnClickListener(v -> {
            String change = password.getText().toString();
            firebaseUser.updatePassword(change).addOnCompleteListener(task -> {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReAuthenticationDialog reAuthenticationDialog = new ReAuthenticationDialog();
        reAuthenticationDialog.setCancelable(false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        reAuthenticationDialog.show(ft, ReAuthenticationDialog.TAG);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
